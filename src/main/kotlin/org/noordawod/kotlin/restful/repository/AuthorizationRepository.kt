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

package org.noordawod.kotlin.restful.repository

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import org.noordawod.kotlin.core.config.JwtConfiguration
import org.noordawod.kotlin.restful.undertow.handler.Jwt
import org.noordawod.kotlin.restful.undertow.handler.JwtAuthentication

/**
 * A generic exception for all problems with authentication.
 *
 * @param message a short explanation of what happened
 * @param cause a [Throwable], if any, that caused the problem
 */
open class AuthenticationException(
  message: String,
  cause: Throwable? = null,
) : RuntimeException(message, cause)

/**
 * An exception for when an authentication is invalid or erroneous.
 *
 * @param message a short explanation of why it's invalid or erroneous
 * @param cause a [Throwable], if any, associated with the error
 */
open class AuthenticationInvalidException(
  message: String,
  cause: Throwable? = null,
) : AuthenticationException(message, cause)

/**
 * An exception for when an authentication cannot be created.
 *
 * @param message a short explanation of why it cannot be created
 * @param cause a [Throwable], if any, associated with the error
 */
open class AuthenticationCreationException(
  message: String,
  cause: Throwable? = null,
) : AuthenticationException(message, cause)

/**
 * A contract that describes methods and a configuration that facilitates authentication
 * of users in the system.
 */
interface JwtAuthenticationRepository {
  /**
   * Returns the [configuration][JwtConfiguration] associated with this
   * [JwtAuthenticationRepository].
   */
  val config: JwtConfiguration

  /**
   * Returns an [HttpHandler] that can intercept an HTTP request and check the validity of
   * a client-supplied access token. If the request is valid, then execution continues to the
   * [next provider][next].
   *
   * @param next the next [HttpHandler] to execute when the access token is valid
   * @param enforced whether an access token is required, or not
   */
  fun interceptor(next: HttpHandler, enforced: Boolean): HttpHandler

  /**
   * Returns the access token embedded in the [exchange request][exchange], null if none
   * was detected.
   *
   * @param exchange the [HttpServerExchange] to inspect looking for access token
   */
  fun getAccessToken(exchange: HttpServerExchange): Jwt?

  /**
   * Sets a new access token in the [exchange response][exchange], and returns it encoded
   * as a [Jwt] instance.
   *
   * @param exchange the [HttpServerExchange] to set a new access token in
   * @param jwt the access token to set
   */
  @Throws(AuthenticationInvalidException::class)
  fun setAccessToken(exchange: HttpServerExchange, jwt: JwtAuthentication): Jwt

  /**
   * Creates and returns a new [String] token from the provided arguments, or throws an
   * [AuthenticationInvalidException] on error.
   *
   * The JWT's "iat" (issued at) property is set to the time when the JWT is created.
   *
   * @param id the value used in JWT's "jti" (JWT ID) property
   * @param subject the value used in JWT's "sub" (subject) property
   * @param expiresAt the value used in JWT's "exp" (expiresAt) property
   */
  @Throws(AuthenticationInvalidException::class)
  fun createAccessToken(
    id: String,
    subject: String,
    expiresAt: java.util.Date,
  ): JwtAuthentication

  /**
   * Verifies the validity of the provided [access token][jwt] and returns its decoded
   * parts, or throws an [AuthenticationInvalidException] if it's invalid.
   *
   * @param jwt the access token to verify
   */
  @Throws(AuthenticationInvalidException::class)
  fun verifyAccessToken(jwt: JwtAuthentication): Jwt
}
