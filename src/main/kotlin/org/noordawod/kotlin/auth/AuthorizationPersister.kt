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
 * A contract that describes the persistence layer necessary to access and manipulate
 * [roles][Role].
 *
 * @param ID type of a [Client]’s unique identifier
 */
@Suppress("TooManyFunctions")
interface AuthorizationPersister<ID : Any, R : Any> {
  /**
   * Retrieves the list of [roles][Role] defined in the system. If no roles are found, then
   * an empty list is returned.
   *
   * @param permissions if provided, return only [roles][Role] having these permissions
   */
  fun getRoles(permissions: Permissions? = null): Set<Role<R>>

  /**
   * Retrieves the [roles][Role] of a [Client] identified by their
   * [unique identifier][clientId].
   *
   * @param clientId unique identifier of [Client]
   * @param permissions if provided, return only [roles][Role] having these permissions
   */
  fun getRoles(clientId: ID, permissions: Permissions? = null): Set<Role<R>>

  /**
   * Retrieves the details of a [Role] identified by its [unique identifier][roleId].
   *
   * @param roleId unique identifier of the [Role]
   */
  fun getRole(roleId: R): Role<R>?

  /**
   * Adds a new [Role] in the system. The [role’s unique identifier][Role.identifier]
   * is automatically generated to ensure its uniqueness.
   *
   * @param operator the operator (user) who’s adding the role
   * @param label the [Role]’s label
   * @param description the [Role]’s description
   */
  fun addRole(operator: ID, label: String, description: String?): Role<R>

  /**
   * Deletes a [Role] identified by its [unique identifier][roleId] from the system.
   *
   * @param operator the operator (user) who’s deleting the role
   * @param roleId unique identifier of the [Role]
   */
  fun deleteRole(operator: ID, roleId: R)

  /**
   * Updates a [Role]’s [label] and [description] in the system.
   *
   * @param operator the operator (user) who’s updating the role
   * @param roleId unique identifier of the [Role]
   * @param label the [Role]’s label
   * @param description the [Role]’s description
   */
  fun updateRole(operator: ID, roleId: R, label: String, description: String?): Role<R>?

  /**
   * Adds new [privileges] for a [Role] identified by its [unique identifier][roleId].
   *
   * @param operator the operator (user) who’s setting the privileges
   * @param roleId unique identifier of the [Role]
   * @param privileges the resources and their permissions to add for a role
   */
  fun setPrivileges(operator: ID, roleId: R, privileges: Privileges)

  /**
   * Deletes all [Permissions] associated with a [Role] identified by its
   * [unique identifier][roleId].
   *
   * @param operator the operator (user) who’s clearing the privileges
   * @param roleId unique identifier of the [Role]
   */
  fun clearPrivileges(operator: ID, roleId: R)
}
