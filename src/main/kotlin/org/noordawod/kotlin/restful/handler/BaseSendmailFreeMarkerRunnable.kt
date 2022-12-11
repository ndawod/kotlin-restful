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

import org.noordawod.kotlin.core.util.Environment
import org.noordawod.kotlin.restful.Constants
import org.noordawod.kotlin.restful.extension.freeMarkerConfiguration
import org.noordawod.kotlin.restful.freemarker.BaseSendmailFreeMarkerRunnable
import org.noordawod.kotlin.restful.repository.HtmlCompressorRepository

/**
 * Top-level class for all [Runnable]s in this app that sends emails.
 *
 * @param file he FreeMarker template file that resides under [basePath], excluding a leading slash
 * @param environment the [Environment] instance to use
 * @param basePath where template files reside, excluding the trailing slash
 * @param htmlCompressor the [HtmlCompressorRepository] instance to use
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseSendmailFreeMarkerRunnable<M : Any> protected constructor(
  protected val file: String,
  protected var environment: Environment,
  basePath: String?,
  protected val htmlCompressor: HtmlCompressorRepository
) : BaseSendmailFreeMarkerRunnable<M>(
  environment.freeMarkerConfiguration(),
  basePath ?: Constants.FTL_EMAIL_PATH
) {
  protected lateinit var html: String

  override val fileExtension: String = Constants.FTL_EXTENSION

  override fun sendEmail(contents: String) {
    html = htmlCompressor.compress(contents)
  }

  override fun getTemplateFile(): String = file
}
