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

package org.noordawod.kotlin.restful.undertow.handler

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers
import org.noordawod.kotlin.core.extension.SECONDS_IN_1_HOUR
import org.noordawod.kotlin.core.util.Environment

/**
 * An [HttpHandler] that implements a simple CORS contract.
 */
class CorsHttpHandler(
  next: HttpHandler,
  hosts: HostsCollection = listOf(),
  headers: Collection<String> = listOf(Headers.AUTHORIZATION_STRING),
  maxAge: Long = MAX_AGE,
) : BaseCorsHttpHandler(next, hosts, headers, maxAge) {
  override fun handleRequest(exchange: HttpServerExchange) {
    // Returns the value of "Origin:" header, if provided.
    val originHost = exchange.requestHeaders[Headers.ORIGIN]?.firstOrNull()

    // Is there a header?
    if (!originHost.isNullOrBlank()) {
      setCorsResponseHeaders(exchange, originHost)
    }

    if (!exchange.isComplete) {
      next.handleRequest(exchange)
    }
  }

  companion object {
    /**
     * Default maximum age for CORS headers.
     */
    const val MAX_AGE: Long = SECONDS_IN_1_HOUR

    /**
     * Default maximum age for CORS headers in [development environment][Environment.DEVEL].
     */
    const val DEVEL_MAX_AGE: Long = SECONDS_IN_1_HOUR
  }
}
