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

package org.noordawod.kotlin.restful.freemarker

/**
 * Defines a page to render with FreeMarker.
 */
interface FreeMarkerPage {
  /**
   * The URL path to access this page, excluding the leading "/".
   */
  val path: String

  /**
   * The URL path to access this page, including the leading "/".
   */
  val absolutePath: String get() = "/$path"

  /**
   * The file name in the templates directory, excluding the trailing extension.
   */
  val file: String

  /**
   * The file name in the templates directory, including the trailing extension.
   */
  val absoluteFile: String get() = "$file.ftl"
}

/**
 * Defines an HTML page to render with FreeMarker.
 */
interface FreeMarkerHtmlPage : FreeMarkerPage {
  /**
   * The file name with a suffix indicating this is an HTML page.
   */
  val pathWithExtension: String get() = if (path.isBlank()) "" else "$path.html"

  /**
   * The URL path to access this page, including the leading "/".
   */
  override val absolutePath: String get() = "/$pathWithExtension"

  /**
   * The file name in the templates directory, including the trailing extension.
   */
  override val absoluteFile: String get() = "$file.ftlh"
}
