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

package org.noordawod.kotlin.restful.undertow

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import org.noordawod.kotlin.restful.model.HttpResponse

/**
 * Contains metadata about an incoming HTTP request.
 */
interface BaseMetadata

/**
 * The base of loggable interfaces, namely: [NoPayloadHttpHandler] and [PayloadHttpHandler].
 */
interface LoggableHttpHandler : HttpHandler

/**
 * All actions that expect no payload and that wish to log their work must extend
 * this base class.
 */
interface NoPayloadHttpHandler<M : BaseMetadata> : HttpHandler {
  /**
   * Handles the incoming HTTP request and returns a compatible [HttpResponse].
   *
   * @param exchange the HTTP I/O exchange
   * @param metadata the embedded metadata in the incoming HTTP request
   */
  fun handleAction(
    exchange: HttpServerExchange,
    metadata: M
  ): HttpResponse
}

/**
 * All actions that expect a payload and that wish to log their work must extend
 * this base class.
 *
 * @param T the payload's type
 */
interface PayloadHttpHandler<M : BaseMetadata, T> : HttpHandler {
  /**
   * Returns the expected payload's [Class] for this action.
   */
  val payloadClass: Class<T>

  /**
   * Returns a slightly modified payload for logging purposes.
   *
   * The default implementation simply returns the same payload, but if an action contains
   * personal details, like passwords, ID numbers, etc., then override this method and hide
   * them from the returned payload.
   */
  fun cleanPayload(payload: T): T = payload

  /**
   * Handles the incoming HTTP request and returns a compatible [HttpResponse].
   *
   * @param exchange the HTTP I/O exchange
   * @param metadata the embedded metadata in the incoming HTTP request
   * @param payload the expected payload
   */
  fun handleAction(
    exchange: HttpServerExchange,
    metadata: M,
    payload: T
  ): HttpResponse
}

/**
 * All actions that expect a payload of type List and that wish to log their work must
 * extend this base class.
 *
 * @param T the payload List's type
 */
interface ListPayloadHttpHandler<M : BaseMetadata, T> : HttpHandler {
  /**
   * Returns the expected payload's [Class] for this action.
   */
  val payloadClass: Class<T>

  /**
   * Returns a slightly modified payload for logging purposes.
   *
   * The default implementation simply returns the same payload, but if an action contains
   * personal details, like passwords, ID numbers, etc., then override this method and hide
   * them from the returned payload.
   */
  fun cleanPayload(payload: List<T>): List<T> = payload

  /**
   * Handles the incoming HTTP request and returns a compatible [HttpResponse].
   *
   * @param exchange the HTTP I/O exchange
   * @param metadata the embedded metadata in the incoming HTTP request
   * @param payload the expected List payload
   */
  fun handleAction(
    exchange: HttpServerExchange,
    metadata: M,
    payload: List<T>
  ): HttpResponse
}

/**
 * All actions that expect a payload of type Set and that wish to log their work must
 * extend this base class.
 *
 * @param T the payload Set's type
 */
interface SetPayloadHttpHandler<M : BaseMetadata, T> : HttpHandler {
  /**
   * Returns the expected payload's [Class] for this action.
   */
  val payloadClass: Class<T>

  /**
   * Returns a slightly modified payload for logging purposes.
   *
   * The default implementation simply returns the same payload, but if an action contains
   * personal details, like passwords, ID numbers, etc., then override this method and hide
   * them from the returned payload.
   */
  fun cleanPayload(payload: Set<T>): Set<T> = payload

  /**
   * Handles the incoming HTTP request and returns a compatible [HttpResponse].
   *
   * @param exchange the HTTP I/O exchange
   * @param metadata the embedded metadata in the incoming HTTP request
   * @param payload the expected Set payload
   */
  fun handleAction(
    exchange: HttpServerExchange,
    metadata: M,
    payload: Set<T>
  ): HttpResponse
}
