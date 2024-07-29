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

@file:Suppress("unused", "MagicNumber", "MemberVisibilityCanBePrivate")

package org.noordawod.kotlin.restful.undertow.handler

import com.auth0.jwt.interfaces.DecodedJWT
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.AttachmentKey
import io.undertow.util.Headers
import org.noordawod.kotlin.restful.extension.deleteAccessToken
import org.noordawod.kotlin.restful.extension.setAccessToken
import org.noordawod.kotlin.restful.repository.AuthenticationException
import org.noordawod.kotlin.restful.repository.AuthenticationInvalidException

/**
 * The decoded JWT after it had passed verification.
 */
typealias Jwt = DecodedJWT

/**
 * A function signature to create a JWT string that expires in a specific date.
 */
typealias JwtAuthenticationCreator = (
  id: String,
  subject: String,
  issuer: String?,
  audience: Collection<String>?,
  expiresAt: java.util.Date,
) -> String

/**
 * A function signature that verifies a JWT string and returns the [Jwt].
 */
typealias JwtAuthenticationVerifier = (accessToken: String) -> Jwt

/**
 * The JWT authorization access token type.
 */
typealias JwtAuthentication = String

/**
 * Performs simple authentication using a JSON Web Token mechanism.
 *
 * @param next next [HTTP handler][HttpHandler] to execute if JWT access token is valid
 * @param creator function to call with a future date to generate a new JWT
 * @param verifier function to verify the validity of a JWT
 * @param sendAlways whether to resend JWT authorization header regardless if rearming is
 * @param deleteIfExpired whether to delete the authorization header if the detected
 * JWT access token has expired
 * @param prependBearer whether the "Bearer " string appears before the JWT access token
 * @param enforced whether to require the existence of an authorization header or not
 * @param rearmThreshold when the access token expires in less than the value of this
 * [Duration][java.time.Duration], then it will be auto-rearmed when the exchange finishes
 * @param rearmDuration when auto-rearm is scheduled, this set the
 * [Duration][java.time.Duration] in the future for the new expiration time
 */
@Suppress("LongParameterList")
class JwtAuthenticationHandler(
  private val next: HttpHandler,
  val creator: JwtAuthenticationCreator,
  val verifier: JwtAuthenticationVerifier,
  val sendAlways: Boolean = false,
  val deleteIfExpired: Boolean = false,
  val prependBearer: Boolean = false,
  val enforced: Boolean = true,
  val rearmThreshold: java.time.Duration = java.time.Duration.ofDays(1),
  val rearmDuration: java.time.Duration = java.time.Duration.ofDays(14),
) : HttpHandler {
  @Throws(AuthenticationException::class)
  override fun handleRequest(exchange: HttpServerExchange) {
    // Extract the JWT access token from the header.
    val accessToken = exchange.detectAccessToken()

    if (null != accessToken) {
      // The verification process may throw if the access token has the wrong claims.
      try {
        val jwt = verifier(accessToken)

        // Attach encoded JWT to this exchange and allow others to access it.
        exchange.putAttachment(CLIENT_JWT_ID, accessToken)

        // Attach resolved JWT to this exchange and allow others to access it.
        exchange.putAttachment(SERVER_JWT_ID, jwt)

        // Rearm the authorization header if the expiration date is near.
        exchange.possiblyRearm(
          accessToken = accessToken,
          jwt = jwt,
        )
      } catch (
        @Suppress("TooGenericExceptionCaught")
        error: Throwable,
      ) {
        if (deleteIfExpired) {
          exchange.deleteAccessToken()
        }

        if (enforced) {
          throw AuthenticationInvalidException(
            message = "Authorization access token is invalid: $accessToken.",
            cause = error,
          )
        }
      }
    } else if (enforced) {
      throw AuthenticationInvalidException("Authorization access token is missing.")
    }

    // On to the next handler.
    next.handleRequest(exchange)
  }

  private fun HttpServerExchange.detectAccessToken(): JwtAuthentication? {
    val headerValue = requestHeaders[Headers.AUTHORIZATION]?.firstOrNull()

    return when {
      null == headerValue -> null

      BEARER_PREFIX_LENGTH < headerValue.length &&
        BEARER_PREFIX.equals(
          other = headerValue.substring(
            startIndex = 0,
            endIndex = BEARER_PREFIX_LENGTH,
          ),
          ignoreCase = true,
        ) -> headerValue.substring(BEARER_PREFIX_LENGTH)

      else -> null
    }
  }

  private fun HttpServerExchange.possiblyRearm(
    accessToken: JwtAuthentication,
    jwt: Jwt,
  ) {
    val prefix = if (prependBearer) BEARER_PREFIX else ""
    val nowMillis = java.util.Date().time

    // The Java call may be null, so we need to guard against it.
    val expiresAt: java.util.Date = jwt.expiresAt ?: return
    val expiresMillis = expiresAt.time

    // Needs to rearm or resent?
    if (expiresMillis - rearmThreshold.toMillis() <= nowMillis) {
      try {
        val rearmedAccessToken = creator(
          jwt.id,
          jwt.subject,
          jwt.issuer,
          jwt.audience.ifEmpty { null },
          java.util.Date(nowMillis + rearmDuration.toMillis()),
        )

        // Rearming client with a new JWT.
        setAccessToken(
          prefix = prefix,
          accessToken = rearmedAccessToken,
        )
      } catch (
        @Suppress("TooGenericExceptionCaught")
        ignored: Throwable,
      ) {
        // NO-OP.
      }
    } else if (sendAlways) {
      // Resending the same authorization to client.
      setAccessToken(
        prefix = prefix,
        accessToken = accessToken,
      )
    }
  }

  companion object {
    /**
     * The JWT authorization type we support.
     */
    const val BEARER_PREFIX: String = "Bearer "

    /**
     * Length of [BEARER_PREFIX] in bytes.
     */
    const val BEARER_PREFIX_LENGTH: Int = BEARER_PREFIX.length

    /**
     * The attachment key to fetch the client-provided JWT from a [HttpServerExchange].
     */
    val CLIENT_JWT_ID: AttachmentKey<String> = AttachmentKey.create(String::class.java)

    /**
     * The attachment key to fetch the resolved [Jwt] from a [HttpServerExchange].
     */
    val SERVER_JWT_ID: AttachmentKey<Jwt> = AttachmentKey.create(Jwt::class.java)
  }
}

private fun HttpServerExchange.setAccessToken(
  prefix: String,
  accessToken: JwtAuthentication,
) {
  setAccessToken("$prefix$accessToken")
}
