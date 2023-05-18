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

package org.noordawod.kotlin.restful.di

import dagger.Module
import dagger.Provides
import org.apache.hc.client5.http.classic.HttpClient
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.noordawod.kotlin.core.util.Environment
import org.noordawod.kotlin.restful.repository.HtmlCompressorRepository
import org.noordawod.kotlin.restful.repository.impl.HtmlCompressorRepositoryImpl

/**
 * Singleton instances related to HTTP/HTML accessible via dependency injection.
 *
 * @param environment the [Environment] this app is running in
 * @param compressCss whether to compress inline styles, default is true
 * @param compressJs whether to compress inline JavaScript, default is true
 */
@Module
class HttpHtmlModule(
  private val environment: Environment,
  private val compressCss: Boolean = true,
  private val compressJs: Boolean = true
) {
  /**
   * Returns the singleton [HtmlCompressorRepository] instance.
   */
  @javax.inject.Singleton
  @Provides
  fun htmlCompressor(): HtmlCompressorRepository {
    val enabled = environment.isBeta || environment.isProduction

    return HtmlCompressorRepositoryImpl(
      enabled = enabled,
      compressCss = compressCss,
      compressJs = compressJs
    )
  }

  /**
   * Returns the singleton [HttpClient] instance.
   */
  @javax.inject.Singleton
  @Provides
  fun httpClient(): HttpClient = HttpClientBuilder.create()
    .disableCookieManagement()
    .disableDefaultUserAgent()
    .build()
}
