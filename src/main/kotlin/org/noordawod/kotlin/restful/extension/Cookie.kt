/*
 * The MIT License
 *
 * Copyright 2024 Noor Dawod. All rights reserved.
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

package org.noordawod.kotlin.restful.extension

import io.undertow.server.handlers.Cookie
import io.undertow.server.handlers.CookieImpl
import org.noordawod.kotlin.core.extension.minusDays
import org.noordawod.kotlin.core.extension.plusSeconds
import org.noordawod.kotlin.restful.Constants
import org.noordawod.kotlin.restful.config.CookieConfiguration

/**
 * Returns a Strict cookie with the specified name, value and [this Duration][java.time.Duration].
 *
 * @param name the name of the cookie
 * @param value the value of the cookie
 * @param secure whether to send the cookie for a secure (https) website
 * @param sameSite whether to send the cookie only for the same site it was created in
 * @param separator the separator character between the cookie value and its expiration,
 * defaults to [COOKIE_EXPIRATION_SEPARATOR][Constants.COOKIE_EXPIRATION_SEPARATOR]
 * @param now the base time for calculating expiration, defaults to current time
 */
@Suppress("LongParameterList")
fun java.time.Duration.createCookie(
  name: String,
  value: String,
  secure: Boolean = true,
  sameSite: Boolean? = true,
  separator: Char = Constants.COOKIE_EXPIRATION_SEPARATOR,
  now: java.util.Date = java.util.Date(),
): Cookie {
  val maxAge = seconds.toInt()
  val expiration = now.plusSeconds(maxAge)
  val valueEncoded = value.encodeCookieWithExpiration(
    expiration = expiration,
    separator = separator,
  )

  return CookieImpl(name, valueEncoded)
    .setHttpOnly(false)
    .setSecure(secure)
    .setSameSiteMode(when (sameSite) {
      true -> SAME_SITE_STRICT
      false -> SAME_SITE_NONE
      null -> null
    })
    .setPath("/")
    .setMaxAge(maxAge)
    .setExpires(expiration)
}

/**
 * Returns a Strict cookie with the specified name, value and
 * [expiration][CookieConfiguration.seconds].
 *
 * @param name the name of the cookie
 * @param value the value of the cookie
 * @param secure whether to send the cookie for a secure (https) website
 * @param sameSite whether to send the cookie only for the same site it was created in
 * @param separator the separator character between the cookie value and its expiration
 * @param now the current date and time, defaults to current time
 */
@Suppress("LongParameterList")
fun CookieConfiguration.createCookie(
  name: String,
  value: String,
  secure: Boolean = true,
  sameSite: Boolean? = true,
  separator: Char = Constants.COOKIE_EXPIRATION_SEPARATOR,
  now: java.util.Date = java.util.Date(),
): Cookie = java.time.Duration.ofSeconds(seconds.toLong()).createCookie(
  name = name,
  value = value,
  secure = secure,
  sameSite = sameSite,
  separator = separator,
  now = now,
)

/**
 * Returns a Strict cookie with an expired date named like this String.
 *
 * This is the way to force deletion of that cookie on the client side.
 *
 * @param secure whether to send the cookie for a secure (https) website
 * @param sameSite whether to send the cookie only for the same site it was created in
 */
@Suppress("MagicNumber")
fun String.deleteCookie(
  secure: Boolean = true,
  sameSite: Boolean? = true,
): Cookie = CookieImpl(this, HTTP_HEADER_DELETE)
  .setHttpOnly(false)
  .setSecure(secure)
  .setSameSiteMode(when (sameSite) {
    true -> SAME_SITE_STRICT
    false -> SAME_SITE_NONE
    null -> null
  })
  .setPath("/")
  .setExpires(java.util.Date().minusDays(365L))

private const val SAME_SITE_STRICT: String = "Strict"
private const val SAME_SITE_NONE: String = "None"
