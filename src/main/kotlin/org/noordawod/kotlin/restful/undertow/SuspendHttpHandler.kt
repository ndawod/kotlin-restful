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
    scope.launch {
      val scope = this

      Thread.currentThread().uncaughtExceptionHandler =
        Thread.UncaughtExceptionHandler { _, e: Throwable ->
          if (printStackTraceOnError) {
            e.printStackTrace()
          }
          handleThrowable(exchange, scope, e)
          if (endExchangeOnError) {
            exchange.endExchange()
          }
        }

      exchange.startBlocking()
      if(handleRequest(exchange, this)) {
        exchange.endExchange()
      }
    }
  }

  /**
   * Handles the request in blocking mode and returns whether to end the exchange or not.
   */
  abstract suspend fun handleRequest(exchange: HttpServerExchange, scope: CoroutineScope): Boolean

  /**
   * Handles an unhandled error that was thrown while executing the suspended method.
   */
  abstract fun handleThrowable(
    exchange: HttpServerExchange,
    scope: CoroutineScope,
    e: Throwable
  )
}
