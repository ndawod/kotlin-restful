/*
 * The MIT License
 *
 * Copyright 2024 Noor Dawod. All rights reserved.
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

import org.noordawod.kotlin.core.Constants
import org.noordawod.kotlin.core.extension.MILLIS_IN_1_SECOND
import org.noordawod.kotlin.core.extension.trimOrNull
import org.noordawod.kotlin.restful.util.QuerySeparator

/**
 * Returns a new cookie value from this String where the expiration date is baked into
 * the final cookie value.
 *
 * Use [decodeCookieWithExpiration] to decode the cookie value back to its parts.
 *
 * @param useSeconds treat expiration value as seconds since the epoch, otherwise milliseconds
 * @param separator the separator between the cookie value and the numeric expiration value
 */
fun String.encodeCookieWithExpiration(
  expiration: java.util.Date,
  useSeconds: Boolean = true,
  separator: Char = '.',
): String {
  val multiplier = if (useSeconds) MILLIS_IN_1_SECOND else 1
  val expirationValue = expiration.time / multiplier

  return "$this$separator$expirationValue"
}

/**
 * Parses this String value and returns the cookie value and its expiration time
 * on success, null otherwise.
 *
 * Use [encodeCookieWithExpiration] to encode the cookie value and the expiration.
 *
 * @param useSeconds treat expiration value as seconds since the epoch, otherwise milliseconds
 * @param separator the separator between the cookie value and the numeric expiration value
 */
fun String.decodeCookieWithExpiration(
  useSeconds: Boolean = true,
  separator: Char = '.',
): Pair<String, java.util.Date>? {
  val separatorPos = lastIndexOf(separator)
  if (0 > separatorPos) {
    return null
  }

  val cookieValue = substring(0, separatorPos).trimOrNull() ?: return null
  val expirationValue = substring(1 + separatorPos).toLongOrNull() ?: return null
  val multiplier = if (useSeconds) MILLIS_IN_1_SECOND else 1

  return cookieValue to java.util.Date(expirationValue * multiplier)
}

/**
 * Returns an absolute URI based on this base URI.
 *
 * @param path the path to the page, including the leading '/'
 * @param params list of query parameters
 * @param allowedParams the list of allowed query parameters
 */
fun String.uri(
  path: String,
  params: Map<String, Any> = emptyMap(),
  allowedParams: Set<String>? = null,
): String = this + path.buildPath(
  params = params,
  allowedParams = allowedParams,
)

/**
 * Returns an absolute URI from this base path.
 *
 * @param params list of query parameters
 * @param allowedParams the list of allowed query parameters
 */
fun String.buildPath(
  params: Map<String, Any> = emptyMap(),
  allowedParams: Set<String>? = null,
): String {
  val builder = StringBuilder(Constants.DEFAULT_LINE_WIDTH)
  val sep = QuerySeparator()

  builder.append(this)

  for (entry in params.entries) {
    val key = entry.key
    val value = entry.value

    if (allowedParams.isNullOrEmpty() || allowedParams.contains(key)) {
      builder.appendQueryParameter(
        sep = sep,
        key = key,
        values = if (value is Iterable<*>) value else listOf(value),
      )
    }
  }

  return builder.toString()
}

/**
 * Returns these paths by prefixing them with a base path.
 *
 * @param basePath the base (canonical) path
 */
fun Collection<String>.basePaths(basePath: String): Collection<String> = mapNotNull {
  val path = it.trim()

  when {
    path.isEmpty() -> null
    path.startsWith(java.io.File.separatorChar) -> path
    else -> "$basePath/$path"
  }
}
