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
import org.noordawod.kotlin.core.extension.mutableSetWith

/**
 * An alias for a set of [roles][Role].
 */
typealias Roles<ID> = Set<Role<ID>>

/**
 * An alias for a mutable set of [roles][Role].
 */
typealias MutableRoles<ID> = MutableSet<Role<ID>>

/**
 * An alias for a set of [role identifiers][Role.identifier].
 */
typealias RolesIds<ID> = Set<ID>

/**
 * An alias for a map that maintains a [mutable set of permissions][MutablePermissions]
 * indexed by a [Role.identifier].
 */
typealias RolePermissionsMap<ID> =
  java.util.concurrent.ConcurrentHashMap<Role<ID>, MutablePermissions>

/**
 * An alias for [Pair] consisting of a [Roles] set and an [Int].
 */
typealias RolesCount = Pair<Roles<*>, Int>

/**
 * A role combines many [permissions][Permission] and [resources][Resource] to simplify
 * administration of users.
 *
 * Roles are defined in the database and an operator with enough privileges can manipulate
 * them in the control panel.
 *
 * @param R type of the [Role]'s unique identifier
 * @param identifier a unique identifier for this role
 * @param label a human-readable label (in English) for this role
 * @param description a short description of this role
 * @param privileges a map of [resources][Resource] and their [permissions][Permissions]
 */
open class Role<R> constructor(
  val identifier: R,
  val label: String,
  val description: String?,
  val privileges: Privileges
) {
  override fun toString(): String = "$identifier"

  final override fun equals(other: Any?): Boolean =
    other is Role<*> && other.identifier == identifier

  final override fun hashCode(): Int = identifier.hashCode()

  companion object {
    /**
     * Returns a [MutableRoles] having the provided side.
     *
     * If [size] is missing or invalid, the [default size][Constants.DEFAULT_LIST_CAPACITY]
     * is used.
     */
    fun <ID> mutableSetOf(size: Int? = null): MutableRoles<ID> {
      val normalizedSize = if (null == size || 1 > size) Constants.DEFAULT_LIST_CAPACITY else size
      return java.util.Collections.synchronizedSet(mutableSetWith(normalizedSize))
    }
  }
}
