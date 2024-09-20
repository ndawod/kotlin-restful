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

package org.noordawod.kotlin.restful.freemarker

/**
 * A collection of query parameters.
 *
 * Note: Don't use [Iterable] as it'll cause deserializers to fail when reading a list
 * from a file such as Yaml or JSON.
 */
typealias QueryParameters = Collection<String>

/**
 * A [Map] of query parameters indexed by a [String] key, and its associated values as a
 * [java.util.Deque] of [String] values.
 */
typealias QueryParametersMap = Map<String, java.util.Deque<String>>

/**
 * Base data model interface for FreeMarker provides support for retrieving and constructing
 * query parameters.
 */
interface FreeMarkerDataModel {
  /**
   * Allowed query parameters for the app. If the value is null, then all parameters
   * are automatically allowed (use with care).
   */
  val allowedQueryParameters: QueryParameters?

  /**
   * Query parameters in this page.
   */
  val queryParametersMap: QueryParametersMap

  /**
   * Generates the query part of a URL from the existing [queryParametersMap] detected for this
   * page.
   */
  fun query(): String = query(null, true)

  /**
   * Generates the query part of a URL from a combination of the existing [queryParametersMap]
   * detected for this page and the specified [params].
   *
   * @param params list of parameters as a [Map] of [String] to [Any]
   */
  fun query(params: Map<String, Any>): String = query(params, true)

  /**
   * Generates the query part of a URL from the specified [params]. If [append] is true, then
   * the specified [params] will be appended to the existing [queryParametersMap] detected
   * for this page. Otherwise, only [params] will comprise the final result.
   *
   * @param params list of parameters as a [Map] of [String] to [Any]
   * @param append whether to override the existing params or merge them to the existing ones
   */
  fun query(
    params: Map<String, Any>? = null,
    append: Boolean,
  ): String

  /**
   * Static functions, constants and other values.
   */
  companion object {
    /**
     * The [java.nio.charset.Charset] name this package shall use when encoding query parameters.
     */
    const val CHARSET_NAME: String = "UTF-8"

    /**
     * The [java.nio.charset.Charset] this package shall use when encoding query parameters.
     */
    val CHARSET: java.nio.charset.Charset = java.nio.charset.StandardCharsets.UTF_8
  }
}
