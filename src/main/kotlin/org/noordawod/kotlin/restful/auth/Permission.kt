/*
 * The MIT License
 *
 * Copyright 2022 Noor Dawod. All rights reserved.
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

@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.noordawod.kotlin.restful.auth

import org.noordawod.kotlin.core.Constants
import org.noordawod.kotlin.core.extension.mutableListWith
import org.noordawod.kotlin.core.extension.mutableMapWith
import org.noordawod.kotlin.core.extension.mutableSetWith

/**
 * Signature of a unique identifier of a [Permission].
 */
typealias PermissionId = Int

/**
 * Signature of a set of [unique identifiers][PermissionId] of [permissions][Permission].
 */
typealias PermissionsIds = Set<PermissionId>

/**
 * Signature of a [immutable set][Set] of [permissions][Permission].
 */
typealias Permissions = Set<Permission>

/**
 * Signature of a [mutable set][MutableSet] of [permissions][Permission].
 */
typealias MutablePermissions = MutableSet<Permission>

/**
 * Signature of a unique identifier of a [Permission].
 */
typealias PermissionDecoder = (identifier: PermissionId) -> Permission?

/**
 * Defines a list of [resources][Resource] and a minimal [immutable set][Permissions] of
 * permissions.
 */
typealias Privileges = Map<out Resource, Permissions>

/**
 * Defines a list of [resources][Resource] and a minimal [mutable set][Permissions] of
 * permissions.
 */
typealias MutablePrivileges = MutableMap<Resource, MutableSet<out Permission>>

/**
 * Deep-merges this [Privileges] Map with another [privileges] Map, returns the merged
 * privileges.
 *
 * @param privileges the other [Privileges] to merge into this instance
 */
infix fun Privileges.mergeWith(privileges: Privileges): Privileges = toMutableMap().apply {
  privileges.forEach { (resource, permissions) ->
    val existingPermissions = (get(resource) ?: setOf()).toMutableSet()
    existingPermissions.addAll(permissions)
    put(resource, existingPermissions)
  }
}

/**
 * Converts these [PermissionsIds] to [Permissions], or null if the resulting set is empty.
 */
fun PermissionsIds.permissions(decoder: PermissionDecoder): Permissions? {
  val permissionsIds = Permission.mutableSetOf()
  for (permissionId in this) {
    val permission = decoder(permissionId)
    if (null != permission) {
      permissionsIds.add(permission)
    }
  }
  return if (permissionsIds.isEmpty()) null else permissionsIds
}

/**
 * Returns these [Permissions] with the impersonated ones, if found.
 *
 * Note: This function impersonates also the impersonated permissions, recursively, and does
 * no check for cyclic impersonations. Be extra careful with what you impersonate!
 */
fun Permissions.impersonate(): Permissions = impersonateImpl(Permission.mutableSetOf())

/**
 * Defines a permission for a [Resource] which can be accessed by a [Client].
 *
 * The list of permissions can grow dynamically based on the app's needs, but it's
 * recommended that they remain few in number.
 *
 * A [Client] requesting access to a [Resource] is considered anonymous until its list of
 * permissions are fetched. For that to happen, authorization must have taken place in the
 * past so that subsequent accesses would reveal the client's identity.
 *
 * A [Resource] is data that a [Client] requests to access or operate on. The required
 * permissions necessary to operate on a specific resource are defined in the database, and
 * an operator (via the control panel) may change those permissions at will.
 *
 * To make managing users simpler, [roles][Role] can be defined with pre-defined
 * permissions. Users may belong to zero or more roles, and may also have additional,
 * separate permissions that an operator can grant.
 */
interface Permission {
  /**
   * A unique identifier this permission.
   */
  val identifier: PermissionId

  /**
   * A human-friendly label (in English) for this permission.
   */
  val label: String

  /**
   * A short description of this permission.
   */
  val description: String

  /**
   * An optional set of [Permissions] that this permission can impersonate.
   */
  val impersonate: Permissions?

  companion object {
    /**
     * Returns a [MutablePermissions] having the provided side.
     *
     * If [size] is missing or invalid, the [default size][Constants.DEFAULT_LIST_CAPACITY]
     * is used.
     */
    fun mutableSetOf(size: Int? = null): MutablePermissions {
      val normalizedSize = if (null == size || 1 > size) Constants.DEFAULT_LIST_CAPACITY else size
      return java.util.Collections.synchronizedSet(mutableSetWith(normalizedSize))
    }

    /**
     * Returns a [MutablePrivileges] having the provided side.
     *
     * If [size] is missing or invalid, the [default size][Constants.DEFAULT_LIST_CAPACITY]
     * is used.
     */
    fun mutablePrivilegesOf(size: Int? = null): MutablePrivileges {
      val normalizedSize = if (null == size || 1 > size) Constants.DEFAULT_LIST_CAPACITY else size
      return java.util.Collections.synchronizedMap(mutableMapWith(normalizedSize))
    }

    /**
     * Cycles through the set of [Permissions] and ensures that the
     * [unique identifiers][Permission.identifier] are, indeed, unique.
     */
    @Suppress("NestedBlockDepth", "LongMethod", "MagicNumber")
    fun Permissions.haveUniqueIdentifiers(): Boolean {
      var result = true
      val permissionsById = groupBy { it.identifier }
      val sharedPermissionsIds = mutableListWith<Int>(permissionsById.size)
      forEach { permission ->
        if (!sharedPermissionsIds.contains(permission.identifier)) {
          val existingPermissions = permissionsById[permission.identifier]
          if (!existingPermissions.isNullOrEmpty() && 1 < existingPermissions.size) {
            if (result) {
              println("The following permissions have non-unique identifiers:")
              result = false
            }

            val permissionHexId = permission.identifier.toString(16)
            val permissionName = permission.javaClass.simpleName
            val otherPermissions = existingPermissions.filterNot { permission == it }

            println("- $permissionName (0x$permissionHexId) shared by: $otherPermissions")
          }
        }
      }
      return result
    }
  }
}

// Ensures that impersonated permissions are walked through so that inter-permissions are added.
private fun Permissions.impersonateImpl(permissions: MutablePermissions): Permissions {
  for (permission in this) {
    permissions.add(permission)
    permission.impersonate?.impersonateImpl(permissions)
  }
  return permissions
}
