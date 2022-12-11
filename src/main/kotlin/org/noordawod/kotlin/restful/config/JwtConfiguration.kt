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

package org.noordawod.kotlin.restful.config

import kotlinx.serialization.ExperimentalSerializationApi
import org.noordawod.kotlin.restful.util.EncryptionAlgorithm

/**
 * Configuration for use in a JWT authorization token.
 *
 * @param algorithm the algorithm to be used for encrypting the JWT, for example: "HMAC256"
 * @param secret the secret string that will be used as a primary key for encrypting the JWT
 * @param duration how many minutes to add to the original expiration date when renewal
 * is applied
 * @param rearmThreshold if a client's JWT expires less than the number of minutes in this
 * [rearmThreshold], then it will be regenerated and sent to the client
 * @param rearmDuration how many minutes to add to the original expiration date when
 * regenerating the JWT
 */
@ExperimentalSerializationApi
@kotlinx.serialization.Serializable
data class JwtConfiguration(
  val algorithm: EncryptionAlgorithm,
  val secret: String,
  val duration: Int,
  val rearmThreshold: Int,
  val rearmDuration: Int
) {
  init {
    require(duration >= rearmThreshold) {
      "Token duration must not be less than rearm threshold"
    }
  }
}
