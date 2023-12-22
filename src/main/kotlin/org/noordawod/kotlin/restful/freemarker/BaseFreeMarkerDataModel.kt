/*
 * The MIT License
 *
 * Copyright 2023 Noor Dawod. All rights reserved.
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

import org.noordawod.kotlin.core.extension.mutableListWith
import org.noordawod.kotlin.restful.extension.appendQueryParameter
import org.noordawod.kotlin.restful.util.QuerySeparator

/**
 * A base class that provides implementation for generating a URL query.
 */
abstract class BaseFreeMarkerDataModel : FreeMarkerDataModel {
  @Suppress("ComplexMethod", "NestedBlockDepth") // A bit long, but not complex.
  override fun query(
    params: Map<String, Any>?,
    append: Boolean,
  ): String {
    @Suppress("MagicNumber")
    val builder = StringBuilder(128)
    val sep = QuerySeparator()
    val overriddenKeys: MutableList<String> = if (params.isNullOrEmpty()) {
      mutableListOf()
    } else {
      mutableListWith(params.size)
    }

    val allowedQueryParameters = this.allowedQueryParameters

    // Add the new query parameters first.
    if (!params.isNullOrEmpty()) {
      params.forEach { (key, values) ->
        if (null == allowedQueryParameters || allowedQueryParameters.contains(key)) {
          if (values is String || values is Number || values is Boolean) {
            builder.appendQueryParameter(sep, key, listOf(values))
          } else if (values is Iterable<*> && values.iterator().hasNext()) {
            builder.appendQueryParameter(sep, key, values)
          } else {
            @Suppress("UseRequire")
            throw IllegalArgumentException("Received an unsupported value for parameter '$key'.")
          }
          overriddenKeys.add(key)
        }
      }
    }

    // Add this page's query parameters second, ignore those that were overridden.
    val queryParametersMapLocked = queryParametersMap
    if (append && queryParametersMapLocked.isNotEmpty()) {
      queryParametersMapLocked.forEach { (key, values) ->
        @Suppress("ComplexCondition")
        if (
          !overriddenKeys.contains(key) &&
          values.isNotEmpty() &&
          (null == allowedQueryParameters || allowedQueryParameters.contains(key))
        ) {
          builder.appendQueryParameter(sep, key, values)
        }
      }
    }

    return builder.toString()
  }

  /**
   * Returns the value for the [name] query parameter if it exists, null otherwise. Note that only
   * the first value will be examined.
   */
  fun queryParameter(name: String): String? {
    val values = queryParametersMap[name]
    return if (values.isNullOrEmpty()) null else values.first
  }

  /**
   * Returns the value for the [name] query parameter if it exists, [fallback] otherwise. Note
   * that only the first value will be examined.
   */
  fun queryParameterOr(
    name: String,
    fallback: String,
  ): String {
    val values = queryParametersMap[name]
    return if (values.isNullOrEmpty()) fallback else values.first
  }
}
