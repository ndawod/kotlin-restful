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

package org.noordawod.kotlin.restful.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import org.noordawod.kotlin.core.extension.MILLIS_IN_1_SECOND

/**
 * A Moshi-compatible [JsonAdapter] to convert a [OffsetDateTime][java.time.OffsetDateTime]
 * from and to a numeric representation (since the UNIX epoch).
 *
 * @param usingSeconds whether this [JsonAdapter] should use seconds to denote a
 * [OffsetDateTime][java.time.OffsetDateTime] when true, or milliseconds when false
 * @param zeroAsNull whether this [JsonAdapter] should use the digit 0 to denote a null
 * when true, or null itself when false
 */
class OffsetDateTimeAdapter(
  @Suppress("MemberVisibilityCanBePrivate")
  val usingSeconds: Boolean = true,

  @Suppress("MemberVisibilityCanBePrivate")
  val zeroAsNull: Boolean = false,
) : JsonAdapter<java.time.OffsetDateTime>() {
  override fun fromJson(reader: JsonReader): java.time.OffsetDateTime? =
    if (reader.hasNext()) {
      val value = reader.nextLong()
      val milliseconds = if (usingSeconds) MILLIS_IN_1_SECOND * value else value
      java.time.OffsetDateTime.of(
        java.time.Instant.ofEpochMilli(milliseconds)
          .atZone(java.time.ZoneOffset.UTC)
          .toLocalDateTime(),
        java.time.ZoneOffset.UTC,
      )
    } else {
      null
    }

  override fun toJson(writer: JsonWriter, value: java.time.OffsetDateTime?) {
    val time: Long? = if (null == value) {
      if (zeroAsNull) 0L else null
    } else {
      val milliseconds = value.toInstant().toEpochMilli()
      if (usingSeconds) milliseconds / MILLIS_IN_1_SECOND else milliseconds
    }
    writer.value(time)
  }
}

/**
 * A helper extension function to allow regular concatenation of [Moshi.Builder].
 */
fun Moshi.Builder.addOffsetDateTimeAdapter(
  usingSeconds: Boolean,
  zeroAsNull: Boolean,
): Moshi.Builder = add(
  java.time.OffsetDateTime::class.java,
  OffsetDateTimeAdapter(usingSeconds, zeroAsNull),
)
