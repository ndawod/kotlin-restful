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

package org.noordawod.kotlin.restful.handler

import com.squareup.moshi.JsonAdapter
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import org.noordawod.kotlin.restful.extension.JsonAdapterProvider
import org.noordawod.kotlin.restful.extension.jsonOutput
import org.noordawod.kotlin.restful.undertow.handler.WorkerHttpHandler

private const val DASHES =
  "--------------------------------------------------------------------------------"

/**
 * A signature to describe a [Throwable] and an associated HTTP status code.
 */
typealias ThrowableHttpStatus<T> = Pair<T, Int>

/**
 * A generic [WorkerHttpHandler] that handles errors and ends the exchange when one is
 * thrown. The [mapThrowable] method must be overridden by implementations and must return
 * a proper HTTP status code for a [Throwable].
 *
 * @param next the next handler to execute in the chain
 */
abstract class ErrorWorkerHttpHandler constructor(
  private val next: HttpHandler
) : WorkerHttpHandler() {
  /**
   * Maps the Throwable [t] to an HTTP status code which will guide [handleError] method
   * on how to end the exchange and return the status code to the client.
   */
  abstract fun mapThrowable(t: Throwable): Int

  override fun handleWork(exchange: HttpServerExchange): Boolean {
    next.handleRequest(exchange)
    return true
  }

  override fun handleError(exchange: HttpServerExchange, e: Throwable) {
    if (!exchange.isResponseStarted) {
      exchange.statusCode = mapThrowable(e)
    }
  }

  override fun log(message: String, e: Throwable?) {
    System.err.println(DASHES)
    super.log(message, e)
    System.err.println(DASHES)
  }
}

/**
 * A generic [WorkerHttpHandler] that processes a [next] and, if it throws, will handle
 * it. Implementations need to override [mapThrowable] to convert a [Throwable] to a
 * [ThrowableHttpStatus] that includes both a body element to be sent out via [adapterProvider],
 * and also an HTTP status code to send to the client.
 *
 * @param next the next handler to execute in the chain
 * @param adapterProvider a provider function that returns a [JsonAdapter] with type [T]
 */
abstract class OutputWorkerHttpHandler<T> constructor(
  private val next: HttpHandler,
  private val adapterProvider: JsonAdapterProvider<T>
) : WorkerHttpHandler() {
  /**
   * Maps the Throwable [error] to a [ThrowableHttpStatus] which will guide [handleError] method
   * on how to end the exchange and return the proper HTTP status code to the client.
   */
  abstract fun mapThrowable(exchange: HttpServerExchange, e: Throwable): ThrowableHttpStatus<T>

  override fun handleWork(exchange: HttpServerExchange): Boolean {
    next.handleRequest(exchange)
    return true
  }

  override fun handleError(exchange: HttpServerExchange, e: Throwable) {
    if (!exchange.isResponseStarted) {
      val error = mapThrowable(exchange, e)
      exchange.statusCode = error.second
      exchange.jsonOutput(error.first, adapterProvider)
    }
  }

  override fun log(message: String, e: Throwable?) {
    // NO-OP.
  }
}
