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

import okio.BufferedSink
import okio.BufferedSource
import okio.buffer
import okio.sink
import okio.source

/**
 * Returns a new [BufferedSource] that buffers the input from this
 * [InputStream][java.io.InputStream].
 */
fun java.io.InputStream.bufferedInput(): BufferedSource = source().buffer()

/**
 * Returns a new [BufferedSink] that buffers the output from this
 * [OutputStream][java.io.OutputStream].
 */
fun java.io.OutputStream.bufferedOutput(): BufferedSink = sink().buffer()

/**
 * Returns a new [BufferedSink] that buffers writes of the encoded [model].
 *
 * @param model the native model to encode as JSON
 */
fun <T> java.io.OutputStream.jsonOutput(
  model: T,
  adapterProvider: JsonAdapterProvider<T>
) {
  bufferedOutput().use {
    adapterProvider(model).toJson(it, model)
    it.flush()
  }
}

/**
 * Returns a new [BufferedSink] that buffers writes of this encoded model of type [T].
 *
 * @param stream the output stream to write to
 */
fun <T> T.jsonOutput(
  stream: java.io.OutputStream,
  adapterProvider: JsonAdapterProvider<T>
) {
  stream.bufferedOutput().use {
    adapterProvider(this).toJson(it, this)
    it.flush()
  }
}
