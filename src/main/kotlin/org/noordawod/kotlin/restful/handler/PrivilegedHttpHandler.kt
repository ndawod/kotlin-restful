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

package org.noordawod.kotlin.restful.handler

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import org.noordawod.kotlin.core.logger.Logger
import org.noordawod.kotlin.restful.auth.Client
import org.noordawod.kotlin.restful.auth.Permissions
import org.noordawod.kotlin.restful.auth.Roles
import org.noordawod.kotlin.restful.auth.impersonate

/**
 * Extends [HttpHandler] to provide a [Client] and [Roles]. These are used by
 * [PrivilegedHttpHandler] to determine whether the incoming request is accessible
 * by the remote [Client].
 */
internal interface PrivilegedUserHttpHandler<ID : Any, R : Any> : HttpHandler {
  /**
   * Returns the required [Permissions] to access this action.
   *
   * @param exchange the HTTP I/O exchange
   */
  fun requiredPermissions(exchange: HttpServerExchange): Permissions

  /**
   * Returns the remote [Client] for the specified [exchange], null otherwise.
   *
   * For example, an operator signs in to the control panel and wants to modify the details
   * of an item belonging to another user in the system. The operator user is returned when
   * calling [client].
   *
   * @param exchange the HTTP I/O exchange
   */
  fun client(exchange: HttpServerExchange): Client<ID, R>?
}

/**
 * Thrown exception if the remote [Client] has insufficient permissions to perform an action.
 *
 * @param message human-friendly error message
 * @param klass the [HttpHandler] class trying to perform the action
 * @param host requested endpoint's host name
 * @param port requested endpoint's port
 * @param method requested endpoint's method
 * @param path requested endpoint's path
 * @param cause optional exception that caused this error
 */
@Suppress("MemberVisibilityCanBePrivate", "LongParameterList")
internal class InsufficientPermissionsException constructor(
  message: String,
  val klass: Class<out HttpHandler>,
  val host: String,
  val port: Int,
  val method: String,
  val path: String,
  cause: Throwable? = null,
) : Throwable(message, cause) {
  override fun toString(): String =
    "$message [class=${klass.simpleName} host=$host, port=$port, method=$method, path=$path]"
}

/**
 * An [HttpHandler] that ensures that a [remote user][Client] can perform the requested
 * action based on their [Roles].
 *
 * @param privilegedHandler HTTP handler to check for the user's privileges
 * @param logger the [Logger] instance to use
 * @param debugSession whether to debug the session details on every request
 * @param nextHandler HTTP handler to run if the user's privileges allow it
 */
internal class PrivilegedHttpHandler<ID : Any, R : Any> constructor(
  private val privilegedHandler: PrivilegedUserHttpHandler<ID, R>,
  private val logger: Logger,
  private val debugSession: Boolean,
  private val nextHandler: HttpHandler
) : HttpHandler {
  @Suppress("NestedBlockDepth")
  override fun handleRequest(exchange: HttpServerExchange) {
    val host = exchange.hostName
    val port = exchange.hostPort
    val method = exchange.requestMethod.toString()
    val path = exchange.requestPath

    // The permissions required in order to access this action.
    val requiredPermissions = privilegedHandler.requiredPermissions(exchange)
    if (debugSession) {
      val permissionsDebug = "${requiredPermissions.ifEmpty { "(empty)" }}"
      logger.info(
        TAG,
        "Required permissions for '$method $path': $permissionsDebug"
      )
    }

    // When there are no required permissions, just permit the action.
    if (requiredPermissions.isNotEmpty()) {
      // We must call this method as some set up work could be happening there.
      val client = privilegedHandler.client(exchange)
        ?: throw InsufficientPermissionsException(
          "Access denied for unknown users",
          privilegedHandler.javaClass,
          host,
          port,
          method,
          path
        )

      // System-wide administrators can do anything.
      if (!client.administrator) {
        // Anonymous users do not have any permissions, so fail early.
        if (client.anonymous) {
          throw InsufficientPermissionsException(
            "Access denied for anonymous users",
            privilegedHandler.javaClass,
            host,
            port,
            method,
            path
          )
        }

        // Evaluate the current client permissions including any impersonated ones.
        val clientPermissions = client.permissions.impersonate()
        if (debugSession) {
          logger.info(TAG, "Client permissions: $clientPermissions")
        }

        // We'll disallow access if the evaluated permissions aren't sufficient.
        if (!clientPermissions.containsAll(requiredPermissions)) {
          if (debugSession) {
            logger.warning(TAG, "Insufficient permissions for client: ${client.identifier}")
            logger.warning(TAG, "- Required: $requiredPermissions")
            logger.warning(TAG, "- Resolved: $clientPermissions")
            logger.warning(TAG, "- Missing:  ${requiredPermissions - clientPermissions}")
          }

          throw InsufficientPermissionsException(
            "Access denied",
            privilegedHandler.javaClass,
            host,
            port,
            method,
            path
          )
        }
      }
    }

    nextHandler.handleRequest(exchange)
  }

  companion object {
    private const val TAG: String = "PrivilegedHttpHandler"
  }
}
