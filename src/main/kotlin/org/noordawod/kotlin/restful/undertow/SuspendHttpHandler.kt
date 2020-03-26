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

package org.noordawod.kotlin.restful.undertow

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.SameThreadExecutor
import io.undertow.util.StatusCodes
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * An [HttpHandler] that relays HTTP requests to a Kotlin suspend method, even if the exchange
 * is already running on the IO thread.
 */
abstract class SuspendHttpHandler constructor(
  private val executor: java.util.concurrent.Executor = SameThreadExecutor.INSTANCE
) : HttpHandler {
  private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

  /**
   * Whether this [HttpHandler] should close end the exchange when an error occurs executing
   * the suspended method.
   */
  open val endExchangeOnError: Boolean = true

  /**
   * Whether to print an exception's stack trace whenever the suspended method throws an error.
   */
  open val printStackTraceOnError: Boolean = true

  final override fun handleRequest(exchange: HttpServerExchange) {
    scope.launch(exceptionHandler(exchange, scope)) {
      exchange.startBlocking()
      if(handleRequest(exchange, scope)) {
        exchange.endExchange()
      }
    }
  }

  /**
   * Handles the request in blocking mode and returns whether to end the exchange or not.
   */
  abstract suspend fun handleRequest(
    exchange: HttpServerExchange,
    scope: CoroutineScope
  ): Boolean

  /**
   * Returns a generic [CoroutineExceptionHandler] that prints the exception to standard error,
   * and sets the HTTP status code to 500 using [HttpServerExchange.setStatusCode].
   */
  open fun exceptionHandler(
    exchange: HttpServerExchange,
    scope: CoroutineScope
  ): CoroutineExceptionHandler = CoroutineExceptionHandler { _, e: Throwable ->
    if (printStackTraceOnError) {
      e.printStackTrace()
    }
    exchange.statusCode = StatusCodes.INTERNAL_SERVER_ERROR
    if (endExchangeOnError) {
      exchange.endExchange()
    }
  }
}
