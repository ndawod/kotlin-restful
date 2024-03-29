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

package org.noordawod.kotlin.restful.undertow.handler

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange

/**
 * An [HttpHandler] that always relays HTTP requests to a XNIO worker thread.
 *
 * @param endExchangeOnError whether this [HttpHandler] should close end the exchange
 * when an error occurs executing the [handleWork] method
 */
abstract class WorkerHttpHandler(
  private val endExchangeOnError: Boolean = true,
) : HttpHandler {
  final override fun handleRequest(exchange: HttpServerExchange) {
    if (exchange.isInIoThread) {
      exchange.dispatch(this)
    } else {
      exchange.startBlocking()
      var shouldEndExchange = false
      try {
        shouldEndExchange = handleWork(exchange)
      } catch (
        @Suppress("TooGenericExceptionCaught")
        error: Throwable,
      ) {
        val classSimpleName = javaClass.simpleName
        log("Unhandled error while handling work in '$classSimpleName'", error)
        shouldEndExchange = endExchangeOnError
        try {
          handleError(
            exchange = exchange,
            error = error,
          )
        } catch (
          @Suppress("TooGenericExceptionCaught")
          throwable: Throwable,
        ) {
          log("Unhandled error while handling error for '$classSimpleName'", throwable)
        }
      } finally {
        if (shouldEndExchange) {
          exchange.endExchange()
        }
      }
    }
  }

  /**
   * Logs a message, defaults to writing it to the standard output.
   *
   * @param message message to log
   * @param error optional error to log too
   */
  protected open fun log(
    message: String,
    error: Throwable? = null,
  ) {
    System.err.println(message)
    @Suppress("PrintStackTrace")
    error?.printStackTrace()
  }

  /**
   * Handles the request in blocking mode and returns whether to end the exchange or not.
   *
   * @param exchange the HTTP request/response exchange
   */
  abstract fun handleWork(exchange: HttpServerExchange): Boolean

  /**
   * Handles an exception [error] that was thrown while work was being done using [handleWork].
   *
   * @param exchange the HTTP request/response exchange
   */
  abstract fun handleError(
    exchange: HttpServerExchange,
    error: Throwable,
  )
}
