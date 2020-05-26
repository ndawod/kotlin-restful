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

@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.noordawod.kotlin.restful.undertow.handler

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers
import io.undertow.util.HttpString
import io.undertow.util.Methods
import io.undertow.util.StatusCodes

/**
 * A signature for passing a list of hosts to [BaseCorsHttpHandler].
 */
typealias HostsCollection = Collection<String>

/**
 * A base [HttpHandler] that implements a simple CORS contract.
 *
 * @param next next [HTTP handler][HttpHandler] to execute if CORS passes
 * @param hosts list of allowed CORS hosts (must not be empty to be used)
 * @param headers list of headers to expose to remote clients; this is needed for browsers
 * to allow JavaScript code to access such headers in the xhr.responseHeaders
 * @param maxAge how long, in seconds, to allow browsers to cache CORS headers
 */
@Suppress("UnnecessaryAbstractClass")
abstract class BaseCorsHttpHandler constructor(
  val next: HttpHandler,
  val hosts: HostsCollection,
  val headers: Collection<String>,
  val maxAge: Long
) : HttpHandler {
  protected fun setCorsResponseHeaders(exchange: HttpServerExchange, originHost: String) {
    // Allowed headers are usually supplied by the client.
    val allowedMethod =
      exchange.requestHeaders[ACCESS_CONTROL_REQUEST_METHOD]?.firstOrNull() ?: "*"

    // For now we'll allow all such requests.
    exchange.responseHeaders.put(Headers.CONNECTION, "keep-alive")
    exchange.responseHeaders.put(ACCESS_CONTROL_ALLOW_ORIGIN, originHost)
    exchange.responseHeaders.put(ACCESS_CONTROL_ALLOW_METHODS, allowedMethod)
    exchange.responseHeaders.put(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
    exchange.responseHeaders.put(ACCESS_CONTROL_MAX_AGE, maxAge)
    exchange.responseHeaders.put(Headers.VARY, Headers.ORIGIN_STRING)

    // Without this, the browser may not allow JavaScript to access it. It must be always
    // sent and not only in preflight requests.
    exchange.responseHeaders.put(
      ACCESS_CONTROL_EXPOSE_HEADERS,
      headers.joinToString(separator = ", ")
    )

    // If this is a preflight method, we're done.
    if (exchange.requestMethod == Methods.OPTIONS) {
      // Allowed headers are usually supplied by the client.
      val allowedHeaders =
        exchange.requestHeaders[ACCESS_CONTROL_REQUEST_HEADERS]?.firstOrNull() ?: mutableListOf(
          Headers.AUTHORIZATION_STRING,
          Headers.ORIGIN_STRING,
          Headers.CONTENT_TYPE_STRING,
          Headers.COOKIE_STRING,
          Headers.CONNECTION_STRING
        ).joinToString(separator = ", ")

      exchange.responseHeaders.put(ACCESS_CONTROL_ALLOW_HEADERS, allowedHeaders)
      exchange.statusCode = StatusCodes.OK
      exchange.endExchange()
      return
    }
  }

  companion object {
    /**
     * Value for "Access-Control-Request-Method" HTTP header.
     */
    val ACCESS_CONTROL_REQUEST_METHOD = HttpString("Access-Control-Request-Method")

    /**
     * Value for "Access-Control-Request-Headers" HTTP header.
     */
    val ACCESS_CONTROL_REQUEST_HEADERS = HttpString("Access-Control-Request-Headers")

    /**
     * Value for "Access-Control-Allow-Origin" HTTP header.
     */
    val ACCESS_CONTROL_ALLOW_ORIGIN = HttpString("Access-Control-Allow-Origin")

    /**
     * Value for "Access-Control-Allow-Methods" HTTP header.
     */
    val ACCESS_CONTROL_ALLOW_METHODS = HttpString("Access-Control-Allow-Methods")

    /**
     * Value for "Access-Control-Allow-Headers" HTTP header.
     */
    val ACCESS_CONTROL_ALLOW_HEADERS = HttpString("Access-Control-Allow-Headers")

    /**
     * Value for "Access-Control-Allow-Credentials" HTTP header.
     */
    val ACCESS_CONTROL_ALLOW_CREDENTIALS = HttpString("Access-Control-Allow-Credentials")

    /**
     * Value for "Access-Control-Expose-Headers" HTTP header.
     */
    val ACCESS_CONTROL_EXPOSE_HEADERS = HttpString("Access-Control-Expose-Headers")

    /**
     * Value for "Access-Control-Max-Age" HTTP header.
     */
    val ACCESS_CONTROL_MAX_AGE = HttpString("Access-Control-Max-Age")
  }
}
