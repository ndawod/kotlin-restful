/*
 * The MIT License
 *
 * Copyright 2024 Noor Dawod. All rights reserved.
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

package org.noordawod.kotlin.restful

/**
 * The decoded JWT after it had passed verification.
 */
typealias Jwt = com.auth0.jwt.interfaces.DecodedJWT

/**
 * The JWT authorization access token type.
 */
typealias JwtAuthentication = String

/**
 * The JWT exception raised when decoding fails.
 */
typealias JwtDecodeException = com.auth0.jwt.exceptions.JWTDecodeException

/**
 * The JWT exception raised when decoding fails.
 */
typealias JwtCreateException = com.auth0.jwt.exceptions.JWTCreationException

/**
 * The JWT exception raised when decoding fails.
 */
typealias JwtVerifyException = com.auth0.jwt.exceptions.JWTVerificationException

/**
 * A function signature to create a JWT string that expires in a specific date.
 */
typealias JwtAuthenticationCreator = (
  id: String,
  subject: String?,
  issuer: String?,
  audience: Collection<String>?,
  expiresAt: java.util.Date,
) -> String

/**
 * A function signature that verifies a JWT string and returns the [Jwt].
 */
typealias JwtAuthenticationVerifier = (accessToken: String) -> Jwt
