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

package org.noordawod.kotlin.restful.config

import kotlinx.serialization.ExperimentalSerializationApi
import org.noordawod.kotlin.core.config.JwtConfiguration

/**
 * Configuration to encrypt and decrypt data with clients.
 *
 * @param sip private key for use in [SipHash][org.noordawod.kotlin.core.security.SipHash]
 * @param salt random salt to use in encryption
 * @param cipher the encryption cypher to use (normally a CBC one)
 * @param key the primary key in a block [cipher]
 * @param iv the IV portion of a block [cipher]
 * @param jwt the JWT configuration
 */
@ExperimentalSerializationApi
@kotlinx.serialization.Serializable
data class SecurityConfiguration constructor(
  val sip: String,
  val salt: String,
  val cipher: String,
  val key: String,
  val iv: String,
  val jwt: JwtConfiguration
)
