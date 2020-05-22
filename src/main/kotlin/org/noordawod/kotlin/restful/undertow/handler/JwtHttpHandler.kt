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

package org.noordawod.kotlin.restful.undertow.handler

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import io.undertow.server.ExchangeCompletionListener
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.AttachmentKey
import io.undertow.util.Headers
import org.noordawod.kotlin.core.extension.MILLIS_IN_1_SECOND

/**
 * The exception that's thrown when a JWT fails verification.
 */
typealias JwtVerificationException = JWTVerificationException

/**
 * The decoded JWT after it had passed verification.
 */
typealias Jwt = DecodedJWT

/**
 * A function signature to create a JWT string that expires in a specific date.
 */
typealias JwtAuthenticationCreator = (subject: String, expiresAt: java.util.Date) -> String

/**
 * A function signature that verifies a JWT string and returns the [Jwt].
 */
typealias JwtAuthenticationVerifier = (subject: String) -> Jwt

/**
 * Holds the two most important pieces of authorization information: first is the
 * type of authorization (Basic, Bearer) and second is the JWT token itself.
 */
typealias JwtAuthentication = Pair<String, String>

/**
 * Performs simple authentication using a JSON Web Token mechanism.
 *
 * @property next next [HTTP handler][HttpHandler] to execute if JWT token is valid
 * @property creator function to call with a future date to generate a new JWT
 * @property verifier function to verify the validity of a JWT
 * @property sendAlways whether to resend JWT authorization header regardless if rearming is
 * required or not
 * @property rearmThreshold when the token expires in less than the value of this
 * [Duration][java.time.Duration], then it will be auto-rearmed when the exchange finishes
 * @property rearmDuration when auto-rearm is scheduled, this set the
 * [Duration][java.time.Duration] in the future for the new expiration time
 */
@Suppress("LongParameterList")
class JwtAuthenticationHandler constructor(
  private val next: HttpHandler,
  val creator: JwtAuthenticationCreator,
  val verifier: JwtAuthenticationVerifier,
  val sendAlways: Boolean = false,
  val rearmThreshold: java.time.Duration? = null,
  val rearmDuration: java.time.Duration = java.time.Duration.ofDays(14)
) : HttpHandler {
  override fun handleRequest(exchange: HttpServerExchange) {
    // Extract the JWT from the header.
    val authorization = verifyAuthorizationHeader(exchange)

    // Verify it.
    val jwt = verifier(authorization.second)

    // Attach encoded JWT to this exchange and allow others to access it.
    exchange.putAttachment(CLIENT_JWT_ID, authorization.second)

    // Attach resolved JWT to this exchange and allow others to access it.
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
  @Throws(JwtVerificationException::class)
  private fun verifyAuthorizationHeader(exchange: HttpServerExchange): JwtAuthentication {
    exchange.requestHeaders[Headers.AUTHORIZATION]?.firstOrNull()?.let {
      val authorizationHeaderLength = it.length
      if (it.startsWith(BASIC_PREFIX) && BASIC_PREFIX.length < authorizationHeaderLength) {
        return BASIC_PREFIX to it.substring(
          BASIC_PREFIX.length)
      }
      if (it.startsWith(BEARER_PREFIX) && BEARER_PREFIX.length < authorizationHeaderLength) {
        return BEARER_PREFIX to it.substring(
          BEARER_PREFIX.length)
      }
    }
    throw JwtVerificationException(
      "Authorization token is invalid."
    )
  }

  private inner class JwtExchangeCompletionListener constructor(
    val authentication: JwtAuthentication,
    val jwt: Jwt
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
      val rearmDurationMillis = rearmDuration.toMillis() / MILLIS_IN_1_SECOND * MILLIS_IN_1_SECOND
      val expiresMillis = expiresAt.time

      // Needs to rearm or resent?
      if (expiresMillis - autoRenewMillis < System.currentTimeMillis()) {
        // Rearming client with a new JWT.
        sendHeader(
          exchange,
          authentication.first to creator(
            jwt.subject,
            java.util.Date(expiresMillis + rearmDurationMillis)
          )
        )
      } else if (sendAlways) {
        // Resending the same authorization to client.
        sendHeader(exchange, authentication)
      }
    }

    private fun sendHeader(exchange: HttpServerExchange, authentication: JwtAuthentication) {
      exchange.responseHeaders.put(
        Headers.AUTHORIZATION,
        "${authentication.first}${authentication.second}"
      )
    }
  }

  companion object {
    private const val BASIC_PREFIX: String = "Basic "
    private const val BEARER_PREFIX: String = "Bearer "

    /**
     * The attachment key to fetch the client-provided JWT from a [HttpServerExchange].
     */
    val CLIENT_JWT_ID: AttachmentKey<String> = AttachmentKey.create(String::class.java)

    /**
     * The attachment key to fetch the resolved [Jwt] from a [HttpServerExchange].
     */
    val SERVER_JWT_ID: AttachmentKey<Jwt> = AttachmentKey.create(
      Jwt::class.java)
  }
}
