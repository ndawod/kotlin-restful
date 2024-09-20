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

@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.noordawod.kotlin.restful.model

import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers
import org.noordawod.kotlin.core.extension.trimOrNull
import org.noordawod.kotlin.restful.extension.bufferOutput

/**
 * Defines the possible responses that this API server supports.
 */
sealed class HttpResponse {
  /**
   * The response's content type.
   */
  abstract val contentType: String

  /**
   * Sends the content type of this response to the specified [exchange].
   *
   * @param exchange the HTTP I/O exchange
   */
  fun setContentType(exchange: HttpServerExchange) {
    val contentTypeNormalized = contentType.trimOrNull()
    if (null != contentTypeNormalized) {
      exchange.responseHeaders.put(
        Headers.CONTENT_TYPE,
        contentTypeNormalized,
      )
    }
  }

  /**
   * There's no content to send in this response.
   *
   * @param statusCode the HTTP status code to send with the response
   */
  class NoContent(
    val statusCode: Int = io.undertow.util.StatusCodes.NO_CONTENT,
  ) : HttpResponse() {
    override val contentType: String = ""

    override fun equals(other: Any?): Boolean = other is NoContent &&
      statusCode == other.statusCode

    override fun hashCode(): Int = statusCode

    override fun toString(): String = "NoContent<statusCode=$statusCode>"

    /**
     * Sends the correct headers and body content, if any, to the remote client.
     *
     * @param exchange the HTTP I/O exchange
     */
    fun send(exchange: HttpServerExchange) {
      exchange.statusCode = statusCode
    }
  }

  /**
   * The requested operation isn't implemented yet.
   */
  object NotImplemented : HttpResponse() {
    override val contentType: String = ""

    override fun equals(other: Any?): Boolean = other is NotImplemented

    override fun hashCode(): Int = contentType.hashCode()

    override fun toString(): String = "NotImplemented"

    /**
     * Sends the correct headers and body content, if any, to the remote client.
     *
     * @param exchange the HTTP I/O exchange
     */
    fun send(exchange: HttpServerExchange) {
      exchange.statusCode = io.undertow.util.StatusCodes.NOT_IMPLEMENTED
    }
  }

  /**
   * The response body is JSON.
   *
   * @param body the actual JSON body
   * @param charset the character set of [body], defaults to
   * [UTF-8][java.nio.charset.StandardCharsets.UTF_8]
   */
  class Json<T>(
    val body: T,
    val charset: java.nio.charset.Charset = java.nio.charset.StandardCharsets.UTF_8,
  ) : HttpResponse() {
    @Suppress("StringLiteralDuplication")
    override val contentType: String = if ("$charset".isBlank()) {
      JSON
    } else {
      "$JSON; $CHARSET$charset".lowercase()
    }

    override fun equals(other: Any?): Boolean = other is Json<*> &&
      body == other.body &&
      charset == other.charset

    override fun hashCode(): Int = 179 * body.hashCode() + 109 * charset.hashCode()

    override fun toString(): String = "Json<$CHARSET$charset, $BODY$body>"

    /**
     * Sends the correct headers and body content, if any, to the remote client.
     *
     * @param exchange the HTTP I/O exchange
     * @param encoder converts the T value to a String
     */
    fun send(
      exchange: HttpServerExchange,
      encoder: (T) -> String,
    ) {
      setContentType(exchange)
      exchange.outputStream.writer(charset).use {
        it.write(encoder(body))
      }
    }
  }

  /**
   * The response body is a JSON String.
   *
   * @param body the actual JSON body as a String
   * @param charset the character set of [body], defaults to
   * [UTF-8][java.nio.charset.StandardCharsets.UTF_8]
   */
  class JsonString(
    val body: String,
    val charset: java.nio.charset.Charset = java.nio.charset.StandardCharsets.UTF_8,
  ) : HttpResponse() {
    @Suppress("StringLiteralDuplication")
    override val contentType: String = if ("$charset".isBlank()) {
      JSON
    } else {
      "$JSON; $CHARSET$charset".lowercase()
    }

    override fun equals(other: Any?): Boolean = other is JsonString &&
      body == other.body &&
      charset == other.charset

    override fun hashCode(): Int = 179 * body.hashCode() + 109 * charset.hashCode()

    override fun toString(): String = "Json<$CHARSET$charset, $BODY$body>"

    /**
     * Sends the correct headers and body content, if any, to the remote client.
     *
     * @param exchange the HTTP I/O exchange
     */
    fun send(exchange: HttpServerExchange) {
      setContentType(exchange)
      exchange.outputStream.writer(charset).use {
        it.write(body)
      }
    }
  }

  /**
   * The response body is plain text (not HTML).
   *
   * @param charset the character set of [body], defaults to
   * [UTF-8][java.nio.charset.StandardCharsets.UTF_8]
   * @param body the actual JSON body
   */
  open class Text(
    val body: String,
    val charset: java.nio.charset.Charset = java.nio.charset.StandardCharsets.UTF_8,
  ) : HttpResponse() {
    override val contentType: String = if ("$charset".isBlank()) {
      TEXT_PLAIN
    } else {
      "$TEXT_PLAIN; $CHARSET$charset".lowercase()
    }

    override fun equals(other: Any?): Boolean = other is Text &&
      body == other.body &&
      charset == other.charset

    override fun hashCode(): Int = 179 * body.hashCode() + 109 * charset.hashCode()

    override fun toString(): String = "Text<$CHARSET$charset, $BODY$body>"

    /**
     * Sends the correct headers and body content, if any, to the remote client.
     *
     * @param exchange the HTTP I/O exchange
     */
    fun send(exchange: HttpServerExchange) {
      setContentType(exchange)
      exchange.outputStream.writer(charset).use {
        it.write(body)
      }
    }

    /**
     * Sends the correct headers and body content, if any, to the remote client.
     *
     * @param exchange the HTTP I/O exchange
     * @param body the body content to send
     */
    fun send(
      exchange: HttpServerExchange,
      body: String,
    ) {
      setContentType(exchange)
      exchange.responseSender.send(body)
    }
  }

  /**
   * The response body is plain text (not HTML).
   *
   * @param charset the character set of [body], defaults to
   * [UTF-8][java.nio.charset.StandardCharsets.UTF_8]
   * @param body the actual JSON body
   */
  class Html(
    body: String,
    charset: java.nio.charset.Charset = java.nio.charset.StandardCharsets.UTF_8,
  ) : Text(body, charset) {
    override val contentType: String = if ("$charset".isBlank()) {
      TEXT_HTML
    } else {
      "$TEXT_HTML; $CHARSET$charset".lowercase()
    }

    override fun toString(): String = "HTML<$CHARSET$charset, $BODY$body>"
  }

  /**
   * The response is a list of binary bytes.
   *
   * @param bytes the actual bytes comprising the binary response payload
   * @param bufferSize the amount of memory to reserve for buffering the response, defaults to
   * [DEFAULT_BUFFER_SIZE]
   * @param contentType the response's content type, defaults to
   * ["application/octet-stream"][BINARY].
   */
  open class BinaryBytes(
    val bytes: ByteArray,
    val bufferSize: Int = DEFAULT_BUFFER_SIZE,
    override val contentType: String = BINARY,
  ) : HttpResponse() {
    protected open val klassName: String = "BinaryBytes"

    override fun equals(other: Any?): Boolean = other is BinaryBytes &&
      bytes.contentEquals(other.bytes) &&
      contentType == other.contentType

    override fun hashCode(): Int = 179 * bytes.contentHashCode() + 109 * contentType.hashCode()

    override fun toString(): String =
      "$klassName<contentType=$contentType, bufferSize=$bufferSize, bytes.size=${bytes.size}>"

    /**
     * Sends the correct headers and body content, if any, to the remote client.
     *
     * @param exchange the HTTP I/O exchange
     */
    fun send(exchange: HttpServerExchange) {
      setContentType(exchange)
      java.io.ByteArrayInputStream(bytes).use { inputStream ->
        exchange.sendBinaryResponse(
          inputStream = if (bufferSize < bytes.size) {
            java.io.BufferedInputStream(inputStream, bufferSize)
          } else {
            inputStream
          },
          bufferSize = bufferSize,
        )
      }
    }
  }

  /**
   * The response is a binary file.
   *
   * @param file the binary file contents to send as the response
   * @param bufferSize the amount of memory to reserve for buffering the response, defaults to
   * [DEFAULT_BUFFER_SIZE]
   * @param contentType the response's content type, defaults to
   * ["application/octet-stream"][BINARY].
   */
  open class BinaryFile(
    val file: java.io.File,
    val bufferSize: Int = DEFAULT_BUFFER_SIZE,
    override val contentType: String = BINARY,
  ) : HttpResponse() {
    protected open val klassName: String = "BinaryFile"

    override fun equals(other: Any?): Boolean = other is BinaryFile &&
      file.canonicalPath == other.file.canonicalPath &&
      contentType == other.contentType

    override fun hashCode(): Int = 179 * file.hashCode() + 109 * contentType.hashCode()

    override fun toString(): String =
      "$klassName<contentType=$contentType, bufferSize=$bufferSize, file=${file.canonicalPath}>"

    /**
     * Sends the correct headers and body content, if any, to the remote client.
     *
     * @param exchange the HTTP I/O exchange
     */
    fun send(exchange: HttpServerExchange) {
      setContentType(exchange)
      java.io.BufferedInputStream(
        java.io.FileInputStream(file),
        bufferSize,
      ).use { inputStream ->
        exchange.sendBinaryResponse(
          inputStream = inputStream,
          bufferSize = bufferSize,
        )
      }
    }
  }

  /**
   * The response is a JPEG image encoded as a [ByteArray].
   *
   * @param image the actual bytes comprising the JPEG image
   * @param bufferSize the amount of memory to reserve for buffering the response, defaults to
   * [DEFAULT_BUFFER_SIZE]
   */
  class JpegBytes(
    val image: ByteArray,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
  ) : BinaryBytes(bytes = image, bufferSize = bufferSize, contentType = JPEG_IMAGE) {
    override val klassName: String = "JpegBytes"
  }

  /**
   * The response is a JPEG image file.
   *
   * @param image the JPEG image file location
   * @param bufferSize the amount of memory to reserve for buffering the response, defaults to
   * [DEFAULT_BUFFER_SIZE]
   */
  class JpegFile(
    val image: java.io.File,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
  ) : BinaryFile(file = image, bufferSize = bufferSize, contentType = JPEG_IMAGE) {
    override val klassName: String = "JpegFile"
  }

  /**
   * The response is a PNG image.
   *
   * @param image the actual bytes comprising the PNG image
   * @param bufferSize the amount of memory to reserve for buffering the response, defaults to
   * [DEFAULT_BUFFER_SIZE]
   */
  class PngBytes(
    val image: ByteArray,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
  ) : BinaryBytes(bytes = image, bufferSize = bufferSize, contentType = PNG_IMAGE) {
    override val klassName: String = "PngBytes"
  }

  /**
   * The response is a PNG image file.
   *
   * @param image the PNG image file location
   * @param bufferSize the amount of memory to reserve for buffering the response, defaults to
   * [DEFAULT_BUFFER_SIZE]
   */
  class PngFile(
    val image: java.io.File,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
  ) : BinaryFile(file = image, bufferSize = bufferSize, contentType = PNG_IMAGE) {
    override val klassName: String = "PngFile"
  }

  /**
   * Static functions, constants and other values.
   */
  companion object {
    private const val CHARSET: String = "charset="
    private const val BODY: String = "body="

    /**
     * Returns the default buffer size when working with buffered streams.
     */
    @Suppress("MagicNumber")
    const val DEFAULT_BUFFER_SIZE: Int = 8 * 1024

    /**
     * The official content type for a JSON payload.
     */
    const val JSON: String = "application/json"

    /**
     * The official content type for HTML.
     */
    const val TEXT_HTML: String = "text/html"

    /**
     * The official content type for plain text.
     */
    const val TEXT_PLAIN: String = "text/plain"

    /**
     * The official content type for a binary payload.
     */
    const val BINARY: String = "application/octet-stream"

    /**
     * The official content type for a JPEG image.
     */
    const val JPEG_IMAGE: String = "image/jpeg"

    /**
     * The official content type for a PNG image.
     */
    const val PNG_IMAGE: String = "image/png"
  }
}

private fun HttpServerExchange.sendBinaryResponse(
  inputStream: java.io.InputStream,
  bufferSize: Int,
): Long = java.io.BufferedOutputStream(
  outputStream,
  bufferSize,
).use { outputStream ->
  inputStream.bufferOutput(outputStream, bufferSize)
}
