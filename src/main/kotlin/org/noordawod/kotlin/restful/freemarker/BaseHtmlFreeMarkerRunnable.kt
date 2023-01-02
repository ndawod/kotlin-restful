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

import org.noordawod.kotlin.restful.Constants
import org.noordawod.kotlin.restful.repository.HtmlCompressorRepository

/**
 * A [BaseByteArrayFreeMarkerRunnable] that orchestrates preparing the contents of a
 * a FreeMarker template, and optionally compressing the resulting HTML.
 *
 * The output is considered to be UTF-8 always.
 *
 * Note: after the method [handleContents] is executed, the contents of the
 * [bytes] buffer is emptied.
 *
 * @param T type of the data model
 * @param config configuration for FreeMarker
 * @param basePath where template files reside, excluding the trailing slash
 * @param bufferSize initial buffer size, defaults to [DEFAULT_BUFFER_SIZE]
 * @param compressor the [HtmlCompressorRepository] instance to use
 */
abstract class BaseHtmlFreeMarkerRunnable<T : Any> constructor(
  config: FreeMarkerConfiguration,
  basePath: String = Constants.FTL_EMAIL_FOLDER,
  bufferSize: Int = DEFAULT_BUFFER_SIZE,
  protected val compressor: HtmlCompressorRepository? = null
) : BaseByteArrayFreeMarkerRunnable<T>(config, basePath, bufferSize) {
  /**
   * Perform the sendmail operation.
   *
   * @param contents the FreeMarker+[model] output
   */
  abstract fun handleContents(contents: String)

  override fun run() {
    super.run()

    bytes.use {
      var contents = it.toString(FreeMarkerDataModel.CHARSET)

      if (null != compressor) {
        contents = compressor.compress(contents)
      }

      handleContents(contents)
    }
  }
}
