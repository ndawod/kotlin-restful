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

package org.noordawod.kotlin.restful.repository

/**
 * Defines a repository to compress HTML-related files, like JavaScript, CSS and HTML.
 */
interface HtmlCompressorRepository {
  /**
   * Returns whether the compressor is engaged (enabled) or not.
   */
  val isEnabled: Boolean

  /**
   * Turns on/off the compressor.
   *
   * @param enabled true to turn on, false to turn off
   */
  fun setEnabled(enabled: Boolean)

  /**
   * Compresses the provided [html code][html] and returns the compressed result.
   *
   * @param html HTML code to compress
   */
  fun compress(html: String): String

  /**
   * Compresses the provided CSS [styles] and returns the compressed result.
   *
   * @param styles CSS styles to compress
   */
  fun compressCssStyles(styles: String): String

  /**
   * Compresses the provided JavaScript [source code][js] and returns the compressed result.
   *
   * @param js source code to compress
   */
  fun compressJavaScript(js: String): String
}
