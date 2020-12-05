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

@file:Suppress("unused")

package org.noordawod.kotlin.auth

/**
 * Defines a contract to maintain a list of [permissions][Permission] and make it easy
 * to access and query.
 */
interface PermissionRepository {
  /**
   * Define all available [permissions][Permission] in the app. This is usually called once
   * when the app is starting up.
   *
   * It’s not possible to clear the list of permissions, but only to re-define them again. So
   * calling this method a second time would clear the previous list and define a totally new
   * one, as specified.
   *
   * @param permissions list of [permissions][Permission] to define
   */
  fun define(permissions: Collection<Permission>)

  /**
   * Define all available [permissions][Permission] in the app. This is usually called once
   * when the app is starting up.
   *
   * It’s not possible to clear the list of permissions, but only to re-define them again. So
   * calling this method a second time would clear the previous list and define a totally new
   * one, as specified.
   *
   * @param permissions list of [permissions][Permission] to define
   */
  fun define(permissions: Array<out Permission>)

  /**
   * Returns true if the specified [permission] is defined, false otherwise.
   *
   * @param permission the permission to check
   */
  fun isDefined(permission: Permission): Boolean

  /**
   * Returns true if a permission identified by its [permissionId] is defined, false otherwise.
   *
   * @param permissionId the unique permission identifier to check
   */
  fun isDefined(permissionId: PermissionId): Boolean

  /**
   * Returns a copy of all defined [permissions][Permission].
   */
  fun getAll(): Collection<Permission>

  /**
   * Retrieves a permission by its unique [identifier][permissionId]. If the permission isn’t found,
   * an Exception is thrown.
   *
   * @param permissionId the unique permission identifier to retrieve
   */
  fun get(permissionId: PermissionId): Permission

  /**
   * Retrieves a permission by its unique [identifier][permissionId], null if not found.
   *
   * @param permissionId the unique permission identifier to retrieve
   */
  fun getOrNull(permissionId: PermissionId): Permission?

  /**
   * Checks if [part1] and [part2] are both a [Permission] and their
   * [unique identifiers][Permission.identifier] are equal.
   *
   * @param part1 first element to check for equality
   * @param part2 second element to check for equality
   */
  fun equals(part1: Any, part2: Any): Boolean
}

/**
 * A [repository][PermissionRepository] implementation to deal with [permissions][Permission].
 *
 * This uses an in-memory [Map] to store and access all permissions.
 */
class PermissionRepositoryImpl : PermissionRepository {
  private val cache = java.util.concurrent.ConcurrentHashMap<PermissionId, Permission>(
    CachingAuthorizationRepository.DEFAULT_ENTRIES
  )

  override fun define(permissions: Collection<Permission>) {
    cache.clear()
    permissions.forEach {
      cache[it.identifier] = it
    }
  }

  override fun define(permissions: Array<out Permission>) {
    cache.clear()
    permissions.forEach {
      cache[it.identifier] = it
    }
  }

  override fun isDefined(permission: Permission): Boolean = isDefined(permission.identifier)

  override fun isDefined(permissionId: PermissionId): Boolean = cache.containsKey(permissionId)

  override fun getAll(): Collection<Permission> =
    ArrayList<Permission>(cache.size).apply {
      cache.forEach { (_, permission) ->
        add(permission)
      }
    }

  override fun get(permissionId: PermissionId): Permission =
    checkNotNull(getOrNull(permissionId)) { "Permission $permissionId is invalid." }

  override fun getOrNull(permissionId: PermissionId): Permission? = cache[permissionId]

  override fun equals(part1: Any, part2: Any): Boolean =
    part1 is Permission && part2 is Permission && part1.identifier == part2.identifier
}
