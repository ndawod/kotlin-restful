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

package org.noordawod.kotlin.restful.util

import com.auth0.jwt.algorithms.Algorithm
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Lists the three most used algorithms when creating a JWT, just for making things a
 * bit easier.
 *
 * @param method the encryption algorithm method
 * @param bits how many bits this encryption algorithm has
 */
@Suppress("UnderscoresInNumericLiterals", "MagicNumber", "MemberVisibilityCanBePrivate")
@Serializable(with = EncryptionAlgorithmSerializer::class)
enum class EncryptionAlgorithm(
  val method: String,
  val bits: Int,
) {
  /**
   * A moderate-length algorithm suitable for development.
   */
  HS256(
    method = "HMAC256",
    bits = 512,
  ),

  /**
   * A medium-length algorithm suitable for beta.
   */
  HS384(
    method = "HMAC384",
    bits = 1024,
  ),

  /**
   * An adequate-length algorithm suitable for production.
   */
  HS512(
    method = "HMAC512",
    bits = 1024,
  ),
  ;

  /**
   * Returns an [Algorithm] instance with the provided [secret].
   */
  fun algorithm(secret: String): Algorithm = generateImpl(
    algorithm = this,
    secret = secret,
    klass = String::class.java,
  )

  /**
   * Returns an [Algorithm] instance with the provided [secret].
   */
  fun algorithm(secret: ByteArray): Algorithm = generateImpl(
    algorithm = this,
    secret = secret,
    klass = ByteArray::class.java,
  )

  private fun <T> generateImpl(
    algorithm: EncryptionAlgorithm,
    secret: T,
    klass: Class<T>,
  ): Algorithm {
    val method = Algorithm::class.java.getMethod(
      algorithm.method,
      klass,
    )

    return method.invoke(null, secret) as Algorithm
  }
}

/**
 * Helper object for (de)serializing the [EncryptionAlgorithm] enum.
 */
class EncryptionAlgorithmSerializer : KSerializer<EncryptionAlgorithm> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
    serialName = "EncryptionAlgorithm",
    kind = PrimitiveKind.STRING,
  )

  override fun serialize(
    encoder: Encoder,
    value: EncryptionAlgorithm,
  ) {
    encoder.encodeString(value.name.uppercase(java.util.Locale.ENGLISH))
  }

  override fun deserialize(decoder: Decoder): EncryptionAlgorithm = EncryptionAlgorithm
    .valueOf(decoder.decodeString().uppercase(java.util.Locale.ENGLISH))
}
