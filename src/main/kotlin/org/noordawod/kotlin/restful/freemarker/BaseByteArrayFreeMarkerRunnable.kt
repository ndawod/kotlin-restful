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

@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.noordawod.kotlin.restful.freemarker

import org.noordawod.kotlin.core.util.CloseableByteArrayOutputStream

/**
 * A [BaseFreeMarkerRunnable] that prepare a memory-based, [ByteArray]-backed writer using
 * [CloseableByteArrayOutputStream]. The content is considered to be UTF-8 always.
 *
 * @param T type of the data model
 * @param config configuration for FreeMarker
 * @param basePath where template files reside, excluding the trailing slash
 * @param bufferSize initial buffer size, defaults to [DEFAULT_BUFFER_SIZE]
 */
abstract class BaseByteArrayFreeMarkerRunnable<T : Any>(
  config: FreeMarkerConfiguration,
  basePath: String,
  protected val bufferSize: Int = DEFAULT_BUFFER_SIZE
) : BaseFreeMarkerRunnable<T>(config, basePath) {
  /**
   * A memory-based, [ByteArray]-backed writer to use for preparing the content.
   */
  protected val bytes = CloseableByteArrayOutputStream(bufferSize)

  override fun prepareWriter(): java.io.BufferedWriter =
    java.io.BufferedWriter(
      java.io.OutputStreamWriter(bytes, FreeMarkerDataModel.CHARSET),
      bufferSize
    )
}
