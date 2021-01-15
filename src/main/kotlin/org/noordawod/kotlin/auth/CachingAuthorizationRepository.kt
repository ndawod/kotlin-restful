/*
 * The MIT License
 *
 * Copyright 2020 Noor Dawod. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.noordawod.kotlin.auth

import org.apache.commons.collections4.map.LRUMap

/**
 * An implementation of [AuthorizationChecker] and [AuthorizationPersister].
 *
 * It has few internal LRU caches to speed up retrieval of [roles][Role] and
 * [permissions][Permissions], and will properly update both roles and permissions
 * when they’re changed, deleted or added.
 *
 * @param ID type of a [Client]’s unique identifier
 * @param R type of a [Role]’s unique identifier
 * @param persister the [AuthorizationPersister] to use
 * @param maxEntries maximum number of entries to keep in the LRU cache, defaults to
 * [DEFAULT_CACHE_ENTRIES]
 */
@Suppress("TooManyFunctions")
class CachingAuthorizationRepository<ID : Any, R : Any>(
  private val persister: AuthorizationPersister<ID, R>,
  maxEntries: Int = DEFAULT_CACHE_ENTRIES
) : AuthorizationChecker<ID, R>, AuthorizationPersister<ID, R> {
  private val clientRolesCache = LRUMap<ID, Collection<Role<R>>>(maxEntries)
  private val rolesCache = LRUMap<R, Role<R>>(maxEntries)

  @Suppress("ReturnCount")
  override fun has(client: Client<ID, R>, requiredPrivileges: Privileges): Boolean {
    // This map contains all known privileges of the client, based on defined roles
    // and any runtime privileges for the incoming request.
    val clientPrivileges: MutablePrivileges = LinkedHashMap(DEFAULT_ENTRIES)

    // Add all permissions defined by the different roles this client has.
    client.roles.forEach { role ->
      combinePrivileges(role.privileges, clientPrivileges)
    }

    // Add any additional privileges that are resolved at runtime based on the request.
    combinePrivileges(client.privileges, clientPrivileges)

    for (privilege in requiredPrivileges) {
      val resource = privilege.key
      val clientPermissions = clientPrivileges[resource]

      @Suppress("FoldInitializerAndIfToElvis")
      if (null == clientPermissions) {
        return false
      }

      val resourcePermissions = privilege.value
      if (!clientPermissions.containsAll(resourcePermissions)) {
        return false
      }
    }

    return true
  }

  @Synchronized
  override fun getRoles(permissions: Permissions?): Set<Role<R>> =
    // When restricting the list of roles, do not cache the result.
    if (permissions.isNullOrEmpty()) {
      if (rolesCache.isEmpty) {
        persister.getRoles().onEach {
          rolesCache[it.identifier] = it
        }
      }
      rolesCache.values.toSet()
    } else {
      persister.getRoles(permissions)
    }

  @Synchronized
  override fun getRoles(clientId: ID, permissions: Permissions?): Set<Role<R>> =
    // When restricting the list of roles, do not cache the result.
    if (permissions.isNullOrEmpty()) {
      clientRolesCache.getOrPut(clientId) {
        persister.getRoles(clientId)
      }.toSet()
    } else {
      persister.getRoles(clientId, permissions)
    }

  @Synchronized
  override fun getRole(roleId: R): Role<R>? {
    var role = rolesCache[roleId]
    if (null == role) {
      role = persister.getRole(roleId)
      if (null != role) {
        rolesCache[roleId] = role
      }
    }
    return role
  }

  @Synchronized
  override fun addRole(operator: ID, label: String, description: String?): Role<R> =
    persister.addRole(operator, label, description).apply {
      rolesCache[identifier] = this
    }

  @Synchronized
  override fun deleteRole(roleId: R) {
    persister.deleteRole(roleId)
    rolesCache.remove(roleId)
    clientRolesCache.clone().apply {
      clientRolesCache.clear()
      forEach { (clientId, roles) ->
        clientRolesCache[clientId] = roles.filterNot { it.identifier == roleId }
      }
    }
  }

  @Synchronized
  override fun updateRole(roleId: R, label: String, description: String?): Role<R>? =
    persister.updateRole(roleId, label, description)?.also { updatedRole ->
      rolesCache[roleId] = updatedRole
      clientRolesCache.forEach { (clientId, roles) ->
        val updatedRoles = ArrayList<Role<R>>(roles.size)
        roles.forEach { role ->
          updatedRoles.add(if (role.identifier == roleId) updatedRole else role)
        }
        clientRolesCache[clientId] = updatedRoles
      }
    }

  @Synchronized
  override fun addPrivileges(
    operator: ID,
    roleId: R,
    privileges: Privileges
  ) {
    persister.addPrivileges(operator, roleId, privileges)
  }

  @Synchronized
  override fun clearPrivileges(operator: ID, roleId: R) {
    persister.clearPrivileges(operator, roleId)
  }

  private fun combinePrivileges(from: Privileges?, to: MutablePrivileges) {
    from?.forEach { (resource, permissions) ->
      val resourcePermissions = (to[resource] ?: mutableSetOf()).toMutableSet()
      resourcePermissions.addAll(permissions)
      to[resource] = resourcePermissions
    }
  }

  companion object {
    internal const val DEFAULT_ENTRIES: Int = 100

    /**
     * Default maximum number of entries in the LRU cache.
     */
    const val DEFAULT_CACHE_ENTRIES: Int = 1000
  }
}
