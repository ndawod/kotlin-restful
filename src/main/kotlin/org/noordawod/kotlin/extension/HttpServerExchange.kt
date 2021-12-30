/*
 * The MIT License
 *
 * Copyright 2021 Noor Dawod. All rights reserved.
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

package org.noordawod.kotlin.extension

import io.undertow.server.HttpServerExchange
import io.undertow.util.HttpString

/**
 * A magic string to signal that the authorization should be deleted at the client side.
 */
const val HTTP_HEADER_DELETE: String = "delete"

/**
 * A helper extension function to set the value of [headerName] to [HTTP_HEADER_DELETE].
 *
 * @param headerName the header to delete
 */
fun HttpServerExchange.setDeleteHeader(headerName: String) {
  setDeleteHeader(HttpString(headerName))
}

/**
 * A helper extension function to set the value of [headerName] to [HTTP_HEADER_DELETE].
 *
 * @param headerName the header to delete
 */
fun HttpServerExchange.setDeleteHeader(headerName: HttpString) {
  setHeader(headerName, HTTP_HEADER_DELETE)
}

/**
 * A helper extension function to set the value of [headerName] to the specified value.
 *
 * @param headerName the header to set
 * @param headerValue the header value to set
 */
fun HttpServerExchange.setHeader(headerName: String, headerValue: String) {
  responseHeaders.put(HttpString(headerName), headerValue)
}

/**
 * A helper extension function to set the value of [headerName] to the specified value.
 *
 * @param headerName the header to set
 * @param headerValue the header value to set
 */
fun HttpServerExchange.setHeader(headerName: HttpString, headerValue: String) {
  responseHeaders.put(headerName, headerValue)
}
