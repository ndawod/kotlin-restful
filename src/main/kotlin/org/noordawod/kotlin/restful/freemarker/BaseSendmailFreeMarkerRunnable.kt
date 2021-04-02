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

package org.noordawod.kotlin.restful.freemarker

/**
 * A [BaseByteArrayFreeMarkerRunnable] that orchestrates preparing an email message based on
 * a FreeMarker template, and sending the email. The output is considered to be UTF-8 always.
 *
 * Note: after the method [sendEmail] is executed, the contents of the [bytes] buffer is
 * emptied.
 *
 * @param T type of the data model
 * @param config configuration for FreeMarker
 * @param basePath where template files reside, excluding the trailing slash
 * @param bufferSize initial buffer size, defaults to [DEFAULT_BUFFER_SIZE]
 */
abstract class BaseSendmailFreeMarkerRunnable<T : Any> constructor(
  config: FreeMarkerConfiguration,
  basePath: String,
  bufferSize: Int = DEFAULT_BUFFER_SIZE
) : BaseByteArrayFreeMarkerRunnable<T>(config, basePath, bufferSize) {
  /**
   * Perform the sendmail operation.
   *
   * @param contents the FreeMarker+[model] output
   */
  abstract fun sendEmail(contents: String)

  override fun run() {
    super.run()
    bytes.use {
      sendEmail(it.toString(FreeMarkerDataModel.CHARSET_NAME))
    }
  }
}
