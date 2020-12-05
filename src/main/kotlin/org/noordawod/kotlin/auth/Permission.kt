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
 * An alias for the unique identifier of an [Permission].
 */
typealias PermissionId = Int

/**
 * Signature of a [mutable set][Set] of [permissions][Permission].
 */
typealias Permissions = Set<Permission>

/**
 * Defines a permission for a [Resource] which can be accessed by a [Client].
 *
 * The list of permissions can grow dynamically based on the app’s needs, but it’s
 * recommended that they remain few in number.
 *
 * A [Client] requesting access to a [Resource] is considered anonymous until its list of
 * permissions are fetched. For that to happen, authorization must have taken place in the
 * past so that subsequent accesses would reveal the client’s identity.
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
}
