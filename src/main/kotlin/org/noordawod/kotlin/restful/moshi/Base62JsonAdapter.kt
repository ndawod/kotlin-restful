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

package org.noordawod.kotlin.restful.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import org.noordawod.kotlin.security.ByteUtils

/**
 * Used to [annotate][Annotation] those properties of a class that need to be converted from or to
 * Base62 using [Base62JsonAdapter].
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@kotlin.annotation.Target(AnnotationTarget.FIELD)
@MustBeDocumented
@JsonQualifier
annotation class Base62Json

/**
 * A [JsonAdapter] to convert from a [ByteArray] to Base62, and vice versa.
 *
 * @param rethrowOnError whether to rethrow the [Exception] when encoding/decoding Base62 data
 */
class Base62JsonAdapter constructor(
  @Suppress("MemberVisibilityCanBePrivate")
  val rethrowOnError: Boolean
) : JsonAdapter<ByteArray>() {
  @Suppress("ReturnCount")
  @Throws(IllegalArgumentException::class)
  override fun fromJson(reader: JsonReader): ByteArray? {
    if (!reader.hasNext()) {
      return null
    }

    val value: String? = try {
      reader.nextString()
    } catch (ignored: java.io.IOException) {
      // We don't want to crash when read data is invalid.
      null
    }
    if (value.isNullOrBlank()) {
      return null
    }

    return if (rethrowOnError) {
      ByteUtils.fromBase62(value)
    } else {
      try {
        return ByteUtils.fromBase62(value)
      } catch (ignored: IllegalArgumentException) {
        // We don't want to crash when read data is invalid.
      }
      null
    }
  }

  override fun toJson(writer: JsonWriter, value: ByteArray?) {
    val json: String? = when {
      null == value -> null
      rethrowOnError -> ByteUtils.toBase62(value)
      else -> {
        try {
          ByteUtils.toBase62(value)
        } catch (ignored: IllegalArgumentException) {
          // We don't want to crash when value is invalid.
          null
        }
      }
    }
    writer.value(json)
  }
}

/**
 * A helper extension function to allow regular concatenation of [Moshi.Builder].
 */
fun Moshi.Builder.addBase62JsonAdapter(rethrowOnError: Boolean): Moshi.Builder =
  add(ByteArray::class.java, Base62Json::class.java, Base62JsonAdapter(rethrowOnError))
