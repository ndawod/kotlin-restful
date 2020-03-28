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

@file:Suppress("unused", "MagicNumber", "MemberVisibilityCanBePrivate")

package org.noordawod.kotlin.restful.undertow

import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import io.undertow.server.ExchangeCompletionListener
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.AttachmentKey
import io.undertow.util.Headers
import java.time.Duration

/**
 * A function signature to create a JWT string that expires in a specific date.
 */
typealias JwtCreator = (subject: String, expiresAt: java.util.Date) -> String

/**
 * Holds the two most important pieces of authorization information: first is the
 * type of authorization (Basic, Bearer) and second is the JWT token itself.
 */
typealias JwtAuthorization = Pair<String, String>

/**
 * Performs simple authentication using a JSON Web Token mechanism.
 */
class JwtAuthenticationHandler constructor(
  private val next: HttpHandler,

  /**
   * A function to call with a future date to generate a new JWT.
   */
  val creator: JwtCreator,

  /**
   * A well-configured implementation of [JWTVerifier] to handle verification of a
   * client JWT.
   */
  val verifier: JWTVerifier,

  /**
   * Whether this [HttpHandler] should send the authorization header always to the client,
   * regardless if a rearm is needed or not.
   */
  val sendAlways: Boolean = false,

  /**
   * If a client's JWT expires less than the value of this [Duration], then it will be
   * auto-rearmed when the exchange finishes.
   */
  val rearmThreshold: Duration? = null,

  /**
   * If an auto-rearm is scheduled, this set the [Duration] in the future for the new
   * expiration time. Default is 2 more weeks.
   */
  val rearmDuration: Duration = Duration.ofDays(14)
) : HttpHandler {
  @Throws(JWTVerificationException::class)
  override fun handleRequest(exchange: HttpServerExchange) {
    // Extract the JWT from the header.
    val authorization = verifyAuthorizationHeader(exchange)

    // Attach it to this exchange and allow others to access it.
    exchange.putAttachment(CLIENT_JWT_ID, authorization.second)

    // Verify the JWT.
    val jwt = verifier.verify(authorization.second)

    // Attach it to this exchange and allow others to access it.
    exchange.putAttachment(SERVER_JWT_ID, jwt)

    // We'll listen to when the exchange is finished so we can either resend the header,
    // or rearm it if needed.
    exchange.addExchangeCompleteListener(JwtExchangeCompletionListener(authorization, jwt))

    next.handleRequest(exchange)
  }

  /**
   * We'll accept both a "Bearer" and a "Basic" authentication requests, and we'll
   * treat the latter as a "Bearer" since we're asked to use JWT.
   */
  @Throws(JWTVerificationException::class)
  private fun verifyAuthorizationHeader(exchange: HttpServerExchange): JwtAuthorization {
    // When no authentication header is supplied, fail abruptly.
    exchange.requestHeaders[Headers.AUTHORIZATION]?.firstOrNull()?.let { authorizationHeader ->
      if (authorizationHeader.startsWith(BASIC_PREFIX, ignoreCase = true)) {
        return BASIC_PREFIX to authorizationHeader.substring(BASIC_PREFIX.length)
      }
      if (authorizationHeader.startsWith(BEARER_PREFIX, ignoreCase = true)) {
        return BEARER_PREFIX to authorizationHeader.substring(BEARER_PREFIX.length)
      }
    }
    throw JWTVerificationException("Authorization token is invalid.")
  }

  private inner class JwtExchangeCompletionListener constructor(
    val authorization: JwtAuthorization,
    val jwt: DecodedJWT
  ) : ExchangeCompletionListener {
    override fun exchangeEvent(
      exchange: HttpServerExchange,
      nextListener: ExchangeCompletionListener.NextListener
    ) {
      try {
        exchangeEvent(exchange)
      } finally {
        nextListener.proceed()
      }
    }

    private fun exchangeEvent(exchange: HttpServerExchange) {
      // Specify the type in Kotlin as expiration date is never null.
      val expiresAt: java.util.Date = jwt.expiresAt

      // Prerequisite.
      val autoRenewMillis = rearmThreshold?.toMillis() ?: 0L
      val rearmDurationMillis = rearmDuration.toMillis() / 1_000L * 1_000L
      val expiresMillis = expiresAt.time

      // Needs to rearm or resent?
      if (expiresMillis - autoRenewMillis < System.currentTimeMillis()) {
        // Rearming client with a new JWT.
        sendHeader(
          exchange,
          authorization.first to creator(
            jwt.subject,
            java.util.Date(expiresMillis + rearmDurationMillis)
          )
        )
      } else if (sendAlways) {
        // Resending the same authorization to client.
        sendHeader(exchange, authorization)
      }
    }

    private fun sendHeader(exchange: HttpServerExchange, authorization: JwtAuthorization) {
      exchange.responseHeaders.put(
        Headers.AUTHORIZATION,
        "${authorization.first}${authorization.second}"
      )
    }
  }

  companion object {
    private const val BASIC_PREFIX: String = "Basic "
    private const val BEARER_PREFIX: String = "Bearer "

    /**
     * The attachment value for storing the user's identifier (JWT's "sub" property)
     * in a [HttpServerExchange].
     */
    val CLIENT_JWT_ID: AttachmentKey<String> = AttachmentKey.create(String::class.java)
    val SERVER_JWT_ID: AttachmentKey<DecodedJWT> = AttachmentKey.create(DecodedJWT::class.java)
  }
}
