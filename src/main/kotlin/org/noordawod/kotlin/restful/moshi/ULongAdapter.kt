/*
 * COPYRIGHT (C) PAPER KITE SYSTEMS I.K.E. ALL RIGHTS RESERVED.
 * UNAUTHORIZED DUPLICATION, MODIFICATION OR PUBLICATION IS PROHIBITED.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF PAPER KITE SYSTEMS I.K.E.
 * THE COPYRIGHT NOTICE ABOVE DOES NOT EVIDENCE ANY ACTUAL OR INTENDED
 * PUBLICATION OF SUCH SOURCE CODE.
 */

package org.noordawod.kotlin.restful.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

/**
 * A simple Moshi adapter to encode/decode [ULong] numbers.
 */
object ULongAdapter : JsonAdapter<ULong>() {
  override fun fromJson(reader: JsonReader): ULong? =
    if (reader.hasNext()) reader.nextLong().toULong() else null

  override fun toJson(
    writer: JsonWriter,
    value: ULong?,
  ) {
    writer.value(value?.toLong())
  }
}
