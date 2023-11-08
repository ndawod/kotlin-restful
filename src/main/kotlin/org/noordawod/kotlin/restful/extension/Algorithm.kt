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

package org.noordawod.kotlin.restful.extension

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.noordawod.kotlin.restful.repository.AuthenticationCreationException
import org.noordawod.kotlin.restful.repository.AuthenticationInvalidException

/**
 * Generates a new JWT token using this [Algorithm] instance.
 *
 * The JWT's "iat" (issued at) property is set to the time when the JWT is created.
 *
 * @param id the value used in JWT's "jti" (JWT ID) property
 * @param subject the value used in JWT's "sub" (subject) property
 * @param issuer the value used in JWT's "iss" (issuer) property
 * @param expiresAt the value used in JWT's "exp" (expiresAt) property
 */
@Throws(AuthenticationCreationException::class)
fun Algorithm.createJwt(
  id: String,
  subject: String,
  issuer: String,
  expiresAt: java.util.Date,
): String {
  try {
    return JWT
      .create()
      .withJWTId(id)
      .withSubject(subject)
      .withIssuer(issuer)
      .withIssuedAt(java.util.Date())
      .withExpiresAt(expiresAt)
      .sign(this)
  } catch (@Suppress("TooGenericExceptionCaught") error: Throwable) {
    throw AuthenticationInvalidException("", error)
  }
}

/**
 * Generates a new JWT verifier using this [Algorithm] instance.
 *
 * @param issuer the expected issue field ("iss") of the JWT token
 */
@Throws(AuthenticationInvalidException::class)
fun Algorithm.verifyJwt(issuer: String): JWTVerifier {
  try {
    return JWT
      .require(this)
      .withIssuer(issuer)
      .build()
  } catch (@Suppress("TooGenericExceptionCaught") error: Throwable) {
    throw AuthenticationInvalidException("", error)
  }
}
