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

@file:Suppress("unused")

package org.noordawod.kotlin.restful.auth

/**
 * A remote user connecting to us and requesting to perform a certain operation.
 *
 * @param ID type of the unique [identifier] of this client
 * @param identifier a unique identifier for this client
 * @param administrator whether this client is an administrator
 * @param roles a list [Roles][Role] this client is associated with
 * @param privileges an optional, additional [privileges][Privileges] to consider
 * @param restrictions an optional set of restrictions to impose on this user
 */
@Suppress("MemberVisibilityCanBePrivate")
open class Client<ID : Any> constructor(
  val identifier: ID,
  val administrator: Boolean,
  roles: Roles<ID>?,
  privileges: Privileges?,
  restrictions: Permissions?
) {
  /**
   * Evaluates to true if this [user][Client] is anonymous, false otherwise.
   *
   * Note: an anonymous user has no [permissions].
   */
  val anonymous: Boolean

  /**
   * The list of [Roles][Role] this client is associated with.
   */
  val roles: Roles<ID>

  /**
   * An optional, additional [privileges][Privileges] to consider.
   */
  val privileges: Privileges

  /**
   * A set of [Permissions] evaluated based on [roles].
   *
   * Note: The set is already cleaned of all [restrictive permissions][restrictions].
   */
  val permissions: Permissions

  /**
   *  A set of restrictive [Permissions] associated with this client.
   */
  val restrictions: Permissions

  override fun toString(): String = "$identifier"

  final override fun equals(other: Any?): Boolean =
    other is Client<*> && other.identifier == identifier

  final override fun hashCode(): Int = toString().hashCode()

  init {
    val mutablePermissions = Permission.mutableSetOf()

    val mutableRoles = Role.mutableSetOf<ID>(roles?.size)
    if (!roles.isNullOrEmpty()) {
      mutableRoles.addAll(roles)

      roles.forEach { role ->
        mutablePermissions.withPrivileges(role.privileges)
      }
    }

    val mutablePrivileges = Permission.mutablePrivilegesOf(privileges?.size)
    if (!privileges.isNullOrEmpty()) {
      for (entry in privileges) {
        mutablePrivileges[entry.key] = entry.value.toMutableSet()
      }
    }
    mutablePermissions.withPrivileges(mutablePrivileges)

    val mutableRestrictions = Permission.mutableSetOf(restrictions?.size)
    if (!restrictions.isNullOrEmpty()) {
      mutableRestrictions.addAll(restrictions)
      mutablePermissions.removeAll(restrictions)
    }

    this.roles = mutableRoles
    this.privileges = mutablePrivileges
    this.restrictions = mutableRestrictions
    this.anonymous = mutablePermissions.isEmpty()
    this.permissions = mutablePermissions.toSet()
  }
}

@Suppress("NestedBlockDepth")
private fun MutablePermissions.withPrivileges(privileges: Privileges) {
  for (privilege in privileges) {
    for (permission in privilege.value) {
      if (!contains(permission)) {
        add(permission)
      }
    }
  }
}
