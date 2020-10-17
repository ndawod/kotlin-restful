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

@file:Suppress("unused")

package org.noordawod.kotlin.restful.extension

import com.squareup.moshi.JsonAdapter
import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers
import io.undertow.util.StatusCodes
import okio.BufferedSink
import okio.BufferedSource
import okio.buffer
import okio.sink
import okio.source

/**
 * A signature for a callback function that provides a [JsonAdapter].
 */
typealias JsonAdapterProvider<T> = (T) -> JsonAdapter<T>

/**
 * Returns a new [BufferedSource] that buffers reads from this [HttpServerExchange]'s
 * [InputStream][java.io.InputStream].
 */
fun HttpServerExchange.bufferedInput(): BufferedSource = inputStream.source().buffer()

/**
 * Returns a new [BufferedSink] that buffers writes from this [HttpServerExchange]'s
 * [OutputStream][java.io.OutputStream].
 */
fun HttpServerExchange.bufferedOutput(): BufferedSink = outputStream.sink().buffer()

/**
 * Returns a new [BufferedSink] that buffers writes from this [HttpServerExchange]'s
 * [OutputStream][java.io.OutputStream].
 */
fun <T> HttpServerExchange.jsonOutput(model: T?, adapterProvider: JsonAdapterProvider<T>) {
  if (null == model) {
    statusCode = StatusCodes.NO_CONTENT
  } else {
    responseHeaders.put(Headers.CONTENT_TYPE, "application/json; charset=utf-8")
    bufferedOutput().use {
      adapterProvider(model).toJson(it, model)
      it.flush()
    }
  }
}

/**
 * Stores the request body of this [HttpServerExchange]'s [InputStream][java.io.InputStream]
 * in a [file], and returns the number of bytes written. If the request contains no body,
 * then 0 is returned.
 *
 * Note that if the [file] already exists in the file system, it will be overwritten.
 */
fun HttpServerExchange.binaryOutput(file: java.io.File): Long =
  binaryOutput(file, DEFAULT_BUFFER_SIZE)

/**
 * Stores the request body of this [HttpServerExchange]'s [InputStream][java.io.InputStream]
 * in a [file], and returns the number of bytes written. If the request contains no body,
 * then 0 is returned.
 *
 * Note that if the [file] already exists in the file system, it will be overwritten.
 */
fun HttpServerExchange.binaryOutput(file: java.io.File, bufferSize: Int): Long {
  val inputStream = this.inputStream
  var totalBytes = 0L
  var hasBytes: Boolean

  java.io.BufferedOutputStream(java.io.FileOutputStream(file), bufferSize).use { fileStream ->
    do {
      val buffer = ByteArray(bufferSize)
      val readBytes = inputStream.read(buffer, 0, bufferSize)
      hasBytes = 0 < readBytes
      if (hasBytes) {
        fileStream.write(buffer)
        totalBytes += readBytes
      }
    } while (!hasBytes)
  }

  return totalBytes
}
