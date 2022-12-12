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

package org.noordawod.kotlin.restful.repository.impl

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers
import kotlinx.serialization.ExperimentalSerializationApi
import org.noordawod.kotlin.core.config.JwtConfiguration
import org.noordawod.kotlin.restful.extension.createJwt
import org.noordawod.kotlin.restful.extension.verifyJwt
import org.noordawod.kotlin.restful.repository.AuthenticationInvalidException
import org.noordawod.kotlin.restful.repository.JwtAuthenticationRepository
import org.noordawod.kotlin.restful.undertow.handler.Jwt
import org.noordawod.kotlin.restful.undertow.handler.JwtAuthentication
import org.noordawod.kotlin.restful.undertow.handler.JwtAuthenticationHandler

/**
 * Authentication module for JSON Web Tokens (JWT).
 *
 * @param config the [JwtConfiguration] instance to use
 */
@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalSerializationApi
internal class JwtAuthenticationRepositoryImpl constructor(
  override val config: JwtConfiguration,
  val issuer: String
) : JwtAuthenticationRepository {
  override fun interceptor(next: HttpHandler, enforced: Boolean): HttpHandler =
    JwtAuthenticationHandler(
      next = next,
      creator = ::createAccessToken,
      verifier = ::verifyAccessToken,
      prependBearer = true,
      enforced = enforced,
      rearmThreshold = java.time.Duration.ofMinutes(config.rearmThreshold.toLong()),
      rearmDuration = java.time.Duration.ofMinutes(config.rearmDuration.toLong())
    )

  override fun getAccessToken(exchange: HttpServerExchange): Jwt? =
    exchange.getAttachment(JwtAuthenticationHandler.SERVER_JWT_ID)

  override fun setAccessToken(exchange: HttpServerExchange, jwt: JwtAuthentication): Jwt =
    verifyAccessToken(jwt).apply {
      exchange.responseHeaders.put(
        Headers.AUTHORIZATION,
        "${JwtAuthenticationHandler.BEARER_PREFIX}$jwt"
      )
    }

  override fun createAccessToken(
    id: String,
    subject: String,
    expiresAt: java.util.Date
  ): JwtAuthentication {
    try {
      val algorithm = config.algorithm.algorithm(config.secret)
      return algorithm.createJwt(id, subject, issuer, expiresAt)
    } catch (@Suppress("TooGenericExceptionCaught") error: Throwable) {
      throw AuthenticationInvalidException("Access token creation error.", error)
    }
  }

  override fun verifyAccessToken(jwt: JwtAuthentication): Jwt {
    try {
      val algorithm = config.algorithm.algorithm(config.secret)
      return algorithm.verifyJwt(issuer).verify(jwt)
    } catch (@Suppress("TooGenericExceptionCaught") error: Throwable) {
      throw AuthenticationInvalidException("Access token verification error.", error)
    }
  }

  private fun createAccessToken(
    id: String,
    subject: String,
    @Suppress("UNUSED_PARAMETER") issuer: String,
    expiresAt: java.util.Date
  ): JwtAuthentication = createAccessToken(id, subject, expiresAt)
}
