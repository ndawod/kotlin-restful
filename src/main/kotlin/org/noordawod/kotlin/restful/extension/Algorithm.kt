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
import com.auth0.jwt.exceptions.IncorrectClaimException
import org.noordawod.kotlin.core.extension.mutableListWith
import org.noordawod.kotlin.core.extension.trimOrNull
import org.noordawod.kotlin.restful.JwtCreateException
import org.noordawod.kotlin.restful.exception.AuthenticationCreationException
import org.noordawod.kotlin.restful.exception.AuthenticationInvalidException

/**
 * Generates a new JWT token using this [Algorithm] instance.
 *
 * The JWT's "iat" (issued at) property is set to the time when the JWT is created.
 *
 * @param id the value used in JWT's "jti" (JWT ID) property
 * @param subject the value used in JWT's "sub" (subject) property, optional
 * @param issuer the value used in JWT's "iss" (issuer) property, optional
 * @param audience the value used in JWT's "iss" (audience) property, optional
 * @param expiresAt the value used in JWT's "exp" (expiresAt) property
 */
@Throws(AuthenticationCreationException::class)
fun Algorithm.createJwt(
  id: String,
  subject: String?,
  issuer: String?,
  audience: Collection<String>?,
  expiresAt: java.util.Date,
): String {
  try {
    val jwt = JWT.create()
      .withJWTId(id)
      .withIssuedAt(java.util.Date())
      .withExpiresAt(expiresAt)

    val subjectNormalized = subject.trimOrNull()
    if (null != subjectNormalized) {
      jwt.withSubject(subject)
    }

    val issuerNormalized = issuer.trimOrNull()
    if (null != issuerNormalized) {
      jwt.withIssuer(issuerNormalized)
    }

    if (!audience.isNullOrEmpty()) {
      val audienceNormalized = mutableListWith<String>(audience.size)

      for (row in audience) {
        val entry = row.trimOrNull() ?: continue
        audienceNormalized.add(entry)
      }

      if (audienceNormalized.isNotEmpty()) {
        @Suppress("SpreadOperator")
        jwt.withAudience(*audienceNormalized.toTypedArray())
      }
    }

    return jwt.sign(this)
  } catch (error: JwtCreateException) {
    throw AuthenticationCreationException(
      message = "Creation of JWT access token failed.",
      cause = error,
    )
  }
}

/**
 * Generates a new JWT verifier using this [Algorithm] instance.
 *
 * @param issuer the expected issue field ("iss") of the JWT token, optional
 */
@Throws(AuthenticationInvalidException::class)
fun Algorithm.verifyJwt(issuer: String?): JWTVerifier {
  try {
    val jwt = JWT.require(this)

    val issuerNormalized = issuer.trimOrNull()
    if (null != issuerNormalized) {
      jwt.withIssuer(issuerNormalized)
    }

    return jwt.build()
  } catch (error: IncorrectClaimException) {
    throw AuthenticationInvalidException(
      message = "Verification of JWT access token failed.",
      cause = error,
    )
  }
}
