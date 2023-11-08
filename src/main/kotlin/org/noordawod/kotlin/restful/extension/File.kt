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

package org.noordawod.kotlin.restful.extension

import freemarker.cache.CacheStorage
import freemarker.template.TemplateExceptionHandler
import freemarker.template.Version
import org.noordawod.kotlin.core.util.Environment
import org.noordawod.kotlin.restful.freemarker.FreeMarkerConfiguration

/**
 * Generates a compatible [FreeMarkerConfiguration] based on this [Environment].
 *
 * @param version which version of FreeMarker templating to use, defaults to
 * [FreeMarkerConfiguration.DEFAULT_VERSION]
 * @param charset character set of templates files, defaults to
 * [UTF-8][java.nio.charset.StandardCharsets.UTF_8]
 * @param cache a storage of cached FreeMarker templates, optional
 * @param env an environment to set the default
 * [exceptions handler][TemplateExceptionHandler], optional
 */
fun java.io.File.freeMarkerConfiguration(
  version: Version = FreeMarkerConfiguration.DEFAULT_VERSION,
  charset: java.nio.charset.Charset = java.nio.charset.StandardCharsets.UTF_8,
  cache: CacheStorage? = null,
  env: Environment? = null,
): FreeMarkerConfiguration {
  val freeMarker = FreeMarkerConfiguration(
    version = version,
    charset = charset,
    cache = cache,
  )

  freeMarker.setDirectoryForTemplateLoading(this)

  if (null != env) {
    freeMarker.templateExceptionHandler = when (env) {
      Environment.LOCAL,
      Environment.DEVEL,
      -> TemplateExceptionHandler.HTML_DEBUG_HANDLER

      Environment.BETA -> TemplateExceptionHandler.DEBUG_HANDLER
      Environment.PRODUCTION -> TemplateExceptionHandler.RETHROW_HANDLER
    }
  }

  return freeMarker
}
