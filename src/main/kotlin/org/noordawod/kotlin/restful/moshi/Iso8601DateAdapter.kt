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

private const val DATE_TIME_FORMAT: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

/**
 * A Moshi-compatible [JsonAdapter] to convert a [Date][java.util.Date] from and to
 * a string representation according to ISO 8601.
 *
 * Note: [dateFormat] is used as input to [SimpleDateFormat][java.text.SimpleDateFormat].
 *
 * @param dateFormat the format to use in [toJson] to encode date/time values, defaults to
 * `"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"`
 */
class Iso8601DateAdapter(dateFormat: String = DATE_TIME_FORMAT) : JsonAdapter<java.util.Date>() {
  private val dateTimeFormatter: java.text.DateFormat = java.text.SimpleDateFormat(dateFormat)

  override fun fromJson(reader: JsonReader): java.util.Date? = if (reader.hasNext()) {
    val value = reader.nextString()
    java.util.Date(MILLIS_IN_1_SECOND.times(java.time.OffsetDateTime.parse(value).toEpochSecond()))
  } else {
    null
  }

  override fun toJson(
    writer: JsonWriter,
    value: java.util.Date?,
  ) {
    val date: String? = if (null == value) {
      null
    } else {
      dateTimeFormatter.format(java.util.Date(value.time))
    }
    writer.value(date)
  }
}

/**
 * A helper extension function to allow regular concatenation of [Moshi.Builder].
 */
fun Moshi.Builder.addIso8601DateAdapter(dateFormat: String = DATE_TIME_FORMAT): Moshi.Builder = add(
  java.util.Date::class.java,
  Iso8601DateAdapter(dateFormat),
)

/**
 * A helper extension function to allow regular concatenation of [Moshi.Builder].
 */
fun Moshi.Builder.addLastIso8601DateAdapter(dateFormat: String = DATE_TIME_FORMAT): Moshi.Builder =
  addLast(
    java.util.Date::class.java,
    Iso8601DateAdapter(dateFormat),
  )
