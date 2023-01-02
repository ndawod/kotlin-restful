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

package org.noordawod.kotlin.restful.freemarker

import freemarker.cache.CacheStorage
import freemarker.template.Version

/**
 * Encapsulates a common configuration for the FreeMarker processor.
 */
open class FreeMarkerConfiguration constructor(
  version: Version = DEFAULT_VERSION,
  charset: java.nio.charset.Charset = java.nio.charset.StandardCharsets.UTF_8,
  cache: CacheStorage? = null
) : freemarker.template.Configuration(version) {
  init {
    defaultEncoding = charset.name()
    urlEscapingCharset = charset.name()
    fallbackOnNullLoopVariable = false
    recognizeStandardFileExtensions = true
    logTemplateExceptions = false
    wrapUncheckedExceptions = true
    localizedLookup = false
    if (null != cache) {
      cacheStorage = cache
    }
  }

  companion object {
    /**
     * The [Version] of FreeMarker we're targeting.
     */
    val DEFAULT_VERSION: Version = VERSION_2_3_31
  }
}
