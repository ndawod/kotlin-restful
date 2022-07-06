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

package org.noordawod.kotlin.auth

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
 * A remote user connecting to us and requesting to perform a certain operation.
 *
 * @param ID type of the unique [identifier] of this client
 * @param R type of the unique identifier of [roles]
 * @param identifier a unique identifier for this client
 * @param roles a list [Roles][Role] this client is associated with
 * @param privileges additional privileges for this client based on the incoming request
 */
@Suppress("MemberVisibilityCanBePrivate")
open class Client<ID : Any, R : Any> constructor(
  val identifier: ID,
  val roles: Set<Role<R>>,
  val privileges: Privileges
) {
  override fun toString(): String = javaClass.simpleName +
    "[identifier=$identifier, roles=$roles], privileges=$privileges]"

  final override fun equals(other: Any?): Boolean =
    other is Client<*, *> && other.identifier == identifier

  final override fun hashCode(): Int = identifier.hashCode()
}
