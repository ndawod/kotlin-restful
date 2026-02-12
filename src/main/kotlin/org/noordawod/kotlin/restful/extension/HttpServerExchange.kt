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

@file:Suppress("unused", "ReplaceIsEmptyWithIfEmpty")

package org.noordawod.kotlin.restful.extension

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.undertow.server.HttpServerExchange
import io.undertow.util.AttachmentKey
import io.undertow.util.Headers
import io.undertow.util.HttpString
import io.undertow.util.StatusCodes
import okio.BufferedSink
import okio.BufferedSource
import org.noordawod.kotlin.core.extension.mutableListWith
import org.noordawod.kotlin.core.extension.simplifyType
import org.noordawod.kotlin.core.extension.toCountryCodeOrNull
import org.noordawod.kotlin.core.extension.trimOrNull
import org.noordawod.kotlin.restful.JwtAuthentication
import org.noordawod.kotlin.restful.undertow.handler.JwtAuthenticationHandler

/**
 * A magic string to signal that the authorization should be deleted at the client side.
 */
const val HTTP_HEADER_DELETE: String = "delete"

/**
 * The default character used to separate encoded values in a query parameter.
 */
const val DEFAULT_QUERY_VALUE_SEPARATOR: Char = ','

/**
 * A signature for a callback function that provides a [JsonAdapter].
 */
typealias JsonAdapterProvider<T> = (T) -> JsonAdapter<T>

/**
 * A signature for a callback function that returns a [Throwable].
 */
typealias ThrowableProvider = (cause: Throwable?) -> Throwable

/**
 * Encodes the data [model] as a JSON and sends it via this [HttpServerExchange]'s
 * send channel.
 *
 * @param moshi the [Moshi] instance used to encode the data
 * @param model the data model to encode as JSON
 */
fun HttpServerExchange.encode(
  moshi: Moshi,
  model: Any,
) {
  jsonOutput(model) {
    moshi.adapter(it.javaClass.simplifyType())
  }
}

/**
 * Encodes the data [model] as a JSON and sends it via this [HttpServerExchange]'s
 * send channel. If encoding fails, an exception will be thrown.
 *
 * @param moshi the [Moshi] instance used to encode the data
 * @param model the data model to encode as JSON
 * @param provider the function that creates a [Throwable]
 */
fun HttpServerExchange.encodeOrThrow(
  moshi: Moshi,
  model: Any?,
  provider: ThrowableProvider,
) {
  if (null == model) {
    throw provider(null)
  }

  try {
    encode(moshi, model)
  } catch (
    @Suppress("TooGenericExceptionCaught")
    cause: Throwable,
  ) {
    throw provider(cause)
  }
}

/**
 * Decodes and returns the JSON residing in this [HttpServerExchange]'s input channel
 * on success, null otherwise.
 *
 * @param moshi the [Moshi] instance used to decode the data
 * @param klass which JVM object type this JSON maps to
 */
fun <T> HttpServerExchange.decode(
  moshi: Moshi,
  klass: Class<T>,
): T? = moshi.adapter(klass).fromJson(bufferedInput())

/**
 * Decodes and returns the JSON residing in this [HttpServerExchange]'s input channel
 * on success, throwing an exception otherwise.
 *
 * @param moshi the [Moshi] instance used to decode the data
 * @param klass which JVM object type this JSON maps to
 * @param provider the function that creates a [Throwable]
 */
fun <T> HttpServerExchange.decodeOrThrow(
  moshi: Moshi,
  klass: Class<T>,
  provider: ThrowableProvider,
): T = decode(
  moshi = moshi,
  klass = klass,
) ?: throw provider(null)

/**
 * Decodes and returns the JSON residing in this [HttpServerExchange]’s input channel
 * as a [List] on success, null otherwise.
 *
 * @param moshi the [Moshi] instance used to decode the data
 * @param klass which JVM object type this JSON array maps to
 */
fun <T> HttpServerExchange.decodeList(
  moshi: Moshi,
  klass: Class<T>,
): List<T>? = try {
  val listType = Types.newParameterizedType(List::class.java, klass)
  moshi.adapter<List<T>>(listType).fromJson(bufferedInput())
} catch (ignored: java.io.EOFException) {
  null
}

/**
 * Decodes and returns the JSON residing in this [HttpServerExchange]’s input channel
 * as a [List] on success, throwing an exception otherwise.
 *
 * @param moshi the [Moshi] instance used to decode the data
 * @param klass which JVM object type this JSON array maps to
 * @param provider the function that creates a [Throwable]
 */
fun <T> HttpServerExchange.decodeListOrThrow(
  moshi: Moshi,
  klass: Class<T>,
  provider: ThrowableProvider,
): List<T> = decodeList(
  moshi = moshi,
  klass = klass,
) ?: throw provider(null)

/**
 * Decodes and returns the JSON residing in this [HttpServerExchange]’s input channel
 * as a [Set] on success, null otherwise.
 *
 * @param moshi the [Moshi] instance used to decode the data
 * @param klass which JVM object type this JSON array maps to
 */
fun <T> HttpServerExchange.decodeSet(
  moshi: Moshi,
  klass: Class<T>,
): Set<T>? = decodeList(
  moshi = moshi,
  klass = klass,
)?.toSet()

/**
 * Decodes and returns the JSON residing in this [HttpServerExchange]’s input channel
 * as a [Set] on success, throwing an exception otherwise.
 *
 * @param moshi the [Moshi] instance used to decode the data
 * @param klass which JVM object type this JSON array maps to
 * @param provider the function that creates a [Throwable]
 */
fun <T> HttpServerExchange.decodeSetOrThrow(
  moshi: Moshi,
  klass: Class<T>,
  provider: ThrowableProvider,
): Set<T> = decodeSet(
  moshi = moshi,
  klass = klass,
) ?: throw provider(null)

/**
 * Decodes and returns the JSON residing in this [HttpServerExchange]’s input channel
 * as a [Map] on success, null otherwise.
 *
 * Note: Keys in the Map must be strings.
 *
 * @param moshi the [Moshi] instance used to decode the data
 * @param klass which JVM object type this JSON array maps to
 */
fun <T> HttpServerExchange.decodeMap(
  moshi: Moshi,
  klass: Class<T>,
): Map<String, T>? = try {
  val mapType = Types.newParameterizedType(Map::class.java, String::class.java, klass)
  moshi.adapter<Map<String, T>>(mapType).fromJson(bufferedInput())
} catch (ignored: java.io.EOFException) {
  null
}

/**
 * Decodes and returns the JSON residing in this [HttpServerExchange]’s input channel
 * as a [Map] on success, throwing an exception otherwise.
 *
 * Note: Keys in the Map must be strings.
 *
 * @param moshi the [Moshi] instance used to decode the data
 * @param klass which JVM object type this JSON array maps to
 * @param provider the function that creates a [Throwable]
 */
fun <T> HttpServerExchange.decodeMapOrThrow(
  moshi: Moshi,
  klass: Class<T>,
  provider: ThrowableProvider,
): Map<String, T> = decodeMap(
  moshi = moshi,
  klass = klass,
) ?: throw provider(null)

/**
 * Notifies the client that there will be no content in the response, finally this will also
 * close this [HttpServerExchange]’s send channel.
 */
fun HttpServerExchange.setNoContent() {
  statusCode = StatusCodes.NO_CONTENT
  endExchange()
}

/**
 * Notifies the client that the requested resource cannot be found on the server, finally
 * this will also close this [HttpServerExchange]’s send channel.
 */
fun HttpServerExchange.setNotFound() {
  statusCode = StatusCodes.NOT_FOUND
  endExchange()
}

/**
 * Returns the value of a parameter embedded in this [HttpServerExchange] request
 * channel, if any, or null otherwise.
 *
 * @param paramName parameter name to retrieve
 */
fun HttpServerExchange.queryParameter(paramName: String): String? {
  var paramValue = queryParameters[paramName]?.firstOrNull()?.trim()
  if (null != paramValue) {
    paramValue = paramValue.ifEmpty { null }
  }
  return paramValue
}

/**
 * Returns the list of values of a parameter embedded in this [HttpServerExchange]
 * request channel separated by [the default separator][DEFAULT_QUERY_VALUE_SEPARATOR],
 * if any, or null otherwise.
 *
 * @param paramName parameter name to retrieve
 */
fun HttpServerExchange.listQueryParameter(paramName: String): List<String>? =
  listQueryParameter(paramName, DEFAULT_QUERY_VALUE_SEPARATOR)

/**
 * Returns the list of values of a parameter embedded in this [HttpServerExchange]
 * request channel separated by [separator], if any, or null otherwise.
 *
 * @param paramName parameter name to retrieve
 * @param separator the separator character between values
 */
fun HttpServerExchange.listQueryParameter(
  paramName: String,
  separator: Char,
): List<String>? {
  val values = queryParameter(paramName)?.split(separator)
  if (values.isNullOrEmpty()) {
    return null
  }

  val result = mutableListWith<String>(values.size)

  for (value in values) {
    val normalizedValue = value.trim()
    if (normalizedValue.isNotEmpty()) {
      result.add(normalizedValue)
    }
  }

  return if (result.isEmpty()) null else result
}

/**
 * Returns the list of values of a parameter embedded in this [HttpServerExchange]
 * request channel separated by [the default separator][DEFAULT_QUERY_VALUE_SEPARATOR],
 * if any, or null otherwise.
 *
 * @param paramName parameter name to retrieve
 * @param transform a block to transform String to a [T]
 */
fun <T> HttpServerExchange.listQueryParameter(
  paramName: String,
  transform: (String) -> T?,
): List<T>? = listQueryParameter(
  paramName = paramName,
  separator = DEFAULT_QUERY_VALUE_SEPARATOR,
  transform = transform,
)

/**
 * Returns a list of unique values, transformed via the provided [transform] block, of a
 * parameter embedded in this [HttpServerExchange] request channel separated by
 * [separator], if any, or null otherwise.
 *
 * @param paramName parameter name to retrieve
 * @param separator the separator character between values
 * @param transform a block to transform String to a [T]
 */
fun <T> HttpServerExchange.listQueryParameter(
  paramName: String,
  separator: Char,
  transform: (String) -> T?,
): List<T>? {
  val values = queryParameter(paramName)?.split(separator)
  if (values.isNullOrEmpty()) {
    return null
  }

  val result = mutableListWith<T>(values.size)

  for (value in values) {
    val normalizedValue = value.trim()
    if (normalizedValue.isNotEmpty()) {
      val transformedValue = transform(normalizedValue)
      if (null != transformedValue) {
        result.add(transformedValue)
      }
    }
  }

  return if (result.isEmpty()) null else result
}

/**
 * Returns the unique values of a parameter embedded in this [HttpServerExchange]
 * request channel separated by [the default separator][DEFAULT_QUERY_VALUE_SEPARATOR], if any,
 * or null otherwise.
 *
 * @param paramName parameter name to retrieve
 */
fun HttpServerExchange.setQueryParameter(paramName: String): Set<String>? =
  setQueryParameter(paramName, DEFAULT_QUERY_VALUE_SEPARATOR)

/**
 * Returns the unique values of a parameter embedded in this [HttpServerExchange]
 * request channel separated by [separator], if any, or null otherwise.
 *
 * @param paramName parameter name to retrieve
 * @param separator the separator character between values
 */
fun HttpServerExchange.setQueryParameter(
  paramName: String,
  separator: Char,
): Set<String>? = listQueryParameter(
  paramName = paramName,
  separator = separator,
)?.toSet()

/**
 * Returns a set of unique values, transformed via the provided [transform] block, of a
 * parameter embedded in this [HttpServerExchange] request channel separated by
 * [the default separator][DEFAULT_QUERY_VALUE_SEPARATOR], if any, or null otherwise.
 *
 * @param paramName parameter name to retrieve
 * @param transform a block to transform String to a [T]
 */
fun <T> HttpServerExchange.setQueryParameter(
  paramName: String,
  transform: (String) -> T?,
): Set<T>? = setQueryParameter(
  paramName = paramName,
  separator = DEFAULT_QUERY_VALUE_SEPARATOR,
  transform = transform,
)

/**
 * Returns a set of unique values, transformed via the provided [transform] block, of a
 * parameter embedded in this [HttpServerExchange] request channel separated by
 * [separator], if any, or null otherwise.
 *
 * @param paramName parameter name to retrieve
 * @param separator the separator character between values
 * @param transform a block to transform String to a [T]
 */
fun <T> HttpServerExchange.setQueryParameter(
  paramName: String,
  separator: Char,
  transform: (String) -> T?,
): Set<T>? = listQueryParameter(
  paramName = paramName,
  separator = separator,
  transform = transform,
)?.toSet()

/**
 * Returns the [Boolean] value of a parameter embedded in this [HttpServerExchange]
 * request channel, if any, or null otherwise.
 *
 * @param paramName parameter name to retrieve
 */
fun HttpServerExchange.booleanQueryParameter(paramName: String): Boolean? {
  val result = queryParameter(paramName)

  return if (null == result) {
    null
  } else {
    "1" == result || "true" == result.lowercase(java.util.Locale.ENGLISH)
  }
}

/**
 * Returns the [Int] value of a parameter embedded in this [HttpServerExchange]
 * request channel, if any, or null otherwise.
 *
 * @param paramName parameter name to retrieve
 */
fun HttpServerExchange.intQueryParameter(paramName: String): Int? = queryParameter(paramName)
  ?.toIntOrNull()

/**
 * Returns the [Int] value of a parameter embedded in this [HttpServerExchange]
 * request channel, if any, or null otherwise.
 *
 * @param paramName parameter name to retrieve
 */
fun HttpServerExchange.shortQueryParameter(paramName: String): Short? = queryParameter(paramName)
  ?.toShortOrNull()

/**
 * Returns the [Long] value of a parameter embedded in this [HttpServerExchange]
 * request channel, if any, or null otherwise.
 *
 * @param paramName parameter name to retrieve
 */
fun HttpServerExchange.longQueryParameter(paramName: String): Long? = queryParameter(paramName)
  ?.toLongOrNull()

/**
 * Returns the [Float] value of a parameter embedded in this [HttpServerExchange]
 * request channel, if any, or null otherwise.
 *
 * @param paramName parameter name to retrieve
 */
fun HttpServerExchange.floatQueryParameter(paramName: String): Float? = queryParameter(paramName)
  ?.toFloatOrNull()

/**
 * Returns the [Double] value of a parameter embedded in this [HttpServerExchange]
 * request channel, if any, or null otherwise.
 *
 * @param paramName parameter name to retrieve
 */
fun HttpServerExchange.doubleQueryParameter(paramName: String): Double? = queryParameter(paramName)
  ?.toDoubleOrNull()

/**
 * Returns a list of [Locales][java.util.Locale] matching the
 * [Accept-Language][Headers.ACCEPT_LANGUAGE] HTTP header value on success, null otherwise.
 */
fun HttpServerExchange.acceptLanguages(): List<java.util.Locale>? = requestHeaders
  .acceptLocales()
  ?.toList()

/**
 * Returns the client’s preferred locale embedded in
 * [Accept-Language][Headers.ACCEPT_LANGUAGE] HTTP header value on success, null otherwise.
 */
fun HttpServerExchange.clientLocale(): java.util.Locale? = acceptLanguages()
  ?.firstOrNull()

/**
 * Returns the client’s preferred locale embedded in this [HttpServerExchange] request
 * channel, if any, or [fallback] otherwise.
 *
 * @param fallback fallback locale
 */
fun HttpServerExchange.clientLocaleOr(fallback: java.util.Locale): java.util.Locale =
  clientLocale() ?: fallback

/**
 * Returns the source [IP address][java.net.InetAddress], be it an IPv6 or IPv6, provided
 * by this [HttpServerExchange] request, if any, or null otherwise.
 *
 * This method will look at any headers implanted by favourite proxy/CDN services, and use
 * their value instead if found.
 */
fun HttpServerExchange.sourceAddress(): java.net.InetAddress? {
  var address: java.net.InetAddress? = getAttachment(REMOTE_IP_ADDR_ID)
  if (null == address) {
    var forwardedSourceAddress: String? = requestHeaders["CF-Connecting-IP"]?.peekFirst()
      ?: requestHeaders["X-Forwarded-For"]?.peekFirst()
      ?: requestHeaders["X-Real-IP"]?.peekFirst()

    forwardedSourceAddress = forwardedSourceAddress?.trimOrNull()

    if (null != forwardedSourceAddress) {
      val commaPos = forwardedSourceAddress.indexOf(',')
      if (0 < commaPos) {
        forwardedSourceAddress = forwardedSourceAddress.substring(0, commaPos)
      }

      address = try {
        java.net.InetAddress.getByName(forwardedSourceAddress)
      } catch (_: java.net.UnknownHostException) {
        null
      }
    }

    if (null == address) {
      address = sourceAddress?.address
    }

    if (null != address) {
      putAttachment(REMOTE_IP_ADDR_ID, address)
    }
  }

  return address
}

/**
 * Returns the source [IP address][java.net.InetAddress], be it an IPv6 or IPv6, provided
 * by this [HttpServerExchange] request, if any, throws otherwise.
 */
@Suppress("NestedBlockDepth")
fun HttpServerExchange.sourceAddressOrThrow(provider: ThrowableProvider): java.net.InetAddress =
  sourceAddress() ?: throw provider(null)

/**
 * Returns the source [IP address][java.net.InetAddress], be it an IPv6 or IPv6, provided
 * by this [HttpServerExchange] request, if any, throws otherwise.
 *
 * Note: This function caches the resolved IP address in the same [HttpServerExchange], so
 * calling it repeatedly will cause it reuse the cached value.
 */
@Throws(IllegalStateException::class)
fun HttpServerExchange.sourceAddressOrThrow(): java.net.InetAddress = sourceAddressOrThrow {
  RuntimeException("Unable to retrieve the source IP address.")
}

/**
 * Returns the detected country code of the remote user as set by CloudFlare  on success,
 * null otherwise.
 *
 * For more details: https://rdr.to/CtMivGGvoi6
 */
fun HttpServerExchange.cloudFlareClientCountryCode(): String? = requestHeaders["CF-IPCountry"]
  ?.peekFirst()
  ?.trimOrNull()
  ?.toCountryCodeOrNull()

/**
 * A helper extension function to set the value of [name] to [HTTP_HEADER_DELETE].
 *
 * @param name the header to delete
 */
fun HttpServerExchange.setDeleteHeader(name: String) {
  setDeleteHeader(HttpString(name))
}

/**
 * A helper extension function to set the value of [name] to [HTTP_HEADER_DELETE].
 *
 * @param name the header to delete
 */
fun HttpServerExchange.setDeleteHeader(name: HttpString) {
  setHeader(
    name = name,
    value = HTTP_HEADER_DELETE,
  )
}

/**
 * A helper extension function to set the value of [name] to the specified value.
 *
 * @param name the header to set
 * @param value the header value to set
 */
fun HttpServerExchange.setHeader(
  name: String,
  value: String,
) {
  responseHeaders.put(HttpString(name), value)
}

/**
 * A helper extension function to set the value of [name] to the specified value.
 *
 * @param name the header to set
 * @param value the header value to set
 */
fun HttpServerExchange.setHeader(
  name: HttpString,
  value: String,
) {
  responseHeaders.put(name, value)
}

/**
 * Sets a new JWT access token in the response headers in this [HttpServerExchange].
 */
fun HttpServerExchange.setAccessToken(
  accessToken: JwtAuthentication,
  prefix: String? = null,
) {
  val accessTokenNormalized = prefix ?: JwtAuthenticationHandler.BEARER_PREFIX

  setHeader(
    name = Headers.AUTHORIZATION,
    value = "$accessTokenNormalized$accessToken",
  )
}

/**
 * Instructs the client via this [HttpServerExchange]'s headers to delete the
 * JWT access token.
 */
fun HttpServerExchange.deleteAccessToken() {
  setDeleteHeader(Headers.AUTHORIZATION)
}

/**
 * Returns a new [BufferedSource] that buffers reads from this [HttpServerExchange]'s
 * [InputStream][java.io.InputStream].
 */
fun HttpServerExchange.bufferedInput(): BufferedSource = inputStream.bufferedInput()

/**
 * Returns the body of the request in this [HttpServerExchange]'s
 * [InputStream][java.io.InputStream].
 */
fun HttpServerExchange.body(): String = inputStream.readBytes().toString(Charsets.UTF_8)

/**
 * Returns the body of the request in this [HttpServerExchange]'s
 * [InputStream][java.io.InputStream] if it's not empty, null otherwise.
 */
fun HttpServerExchange.bodyOrNull(): String? {
  val body = body()

  return if (body.isEmpty()) null else body
}

/**
 * Returns a new [BufferedSink] that buffers writes from this [HttpServerExchange]'s
 * [OutputStream][java.io.OutputStream].
 */
fun HttpServerExchange.bufferedOutput(): BufferedSink = outputStream.bufferedOutput()

/**
 * Returns a new [BufferedSink] that buffers writes from this [HttpServerExchange]'s
 * [OutputStream][java.io.OutputStream].
 */
fun <T> HttpServerExchange.jsonOutput(
  model: T?,
  adapterProvider: JsonAdapterProvider<T>,
) {
  if (null == model) {
    statusCode = StatusCodes.NO_CONTENT
  } else {
    setHeader(
      Headers.CONTENT_TYPE,
      "application/json; charset=utf-8",
    )

    bufferedOutput().use {
      adapterProvider(model).toJson(it, model)
      it.flush()
    }
  }
}

/**
 * Stores the request body of this [HttpServerExchange]'s [InputStream][java.io.InputStream]
 * in a [file], and returns the number of bytes written. If the request contains no body,
 * then 0 is returned.
 *
 * Note that if the [file] already exists in the file system, it will be overwritten.
 */
fun HttpServerExchange.binaryOutput(file: java.io.File): Long = binaryOutput(
  file = file,
  bufferSize = DEFAULT_BUFFER_SIZE,
)

/**
 * Stores the request body of this [HttpServerExchange]'s [InputStream][java.io.InputStream]
 * in a [file], and returns the number of bytes written. If the request contains no body,
 * then 0 is returned.
 *
 * Note that if the [file] already exists in the file system, it will be overwritten.
 */
fun HttpServerExchange.binaryOutput(
  file: java.io.File,
  bufferSize: Int,
): Long = java.io.BufferedOutputStream(
  java.io.FileOutputStream(file),
  bufferSize,
).use { fileStream ->
  inputStream.bufferOutput(
    output = fileStream,
    bufferSize = bufferSize,
  )
}

internal fun java.io.InputStream.bufferOutput(
  output: java.io.OutputStream,
  bufferSize: Int,
): Long {
  var hasBytes: Boolean
  var totalBytes = 0L

  do {
    val buffer = ByteArray(bufferSize)
    val readBytes = read(buffer, 0, buffer.size)

    hasBytes = 0 < readBytes

    if (hasBytes) {
      output.write(buffer, 0, readBytes)
      totalBytes += readBytes
    }
  } while (hasBytes)

  return totalBytes
}

private val REMOTE_IP_ADDR_ID = AttachmentKey.create(java.net.InetAddress::class.java)
