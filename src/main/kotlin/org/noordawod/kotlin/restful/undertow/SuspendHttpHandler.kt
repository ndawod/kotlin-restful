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
 * A handler for an HTTP request that relays the call to a suspend method.
 *
 * The request handler must eventually either call another handler or end the exchange.
 */
abstract class SuspendHttpHandler constructor(
  private val executor: java.util.concurrent.Executor = SameThreadExecutor.INSTANCE
) : HttpHandler {
  private val scope = CoroutineScope(Dispatchers.IO)

  final override fun handleRequest(exchange: HttpServerExchange?) {
    exchange?.let {
      val runnable = Runnable {
        scope.launch {
          var endExchange = false
          it.startBlocking()
          try {
            endExchange = handleRequest(it, this)
          } finally {
            if (endExchange) {
              it.endExchange()
            }
          }
        }
      }
      if (it.isInIoThread) {
        it.dispatch(executor, runnable)
      } else {
        runnable.run()
      }
    }
  }

  /**
   * Handles the request in blocking mode and returns whether to end the exchange or not.
   */
  abstract suspend fun handleRequest(exchange: HttpServerExchange, scope: CoroutineScope): Boolean
}
