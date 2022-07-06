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

package org.noordawod.kotlin.restful.undertow.handler

import freemarker.template.Template
import freemarker.template.TemplateException
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import org.noordawod.kotlin.core.extension.withExtension
import org.noordawod.kotlin.restful.freemarker.BaseFreeMarkerRunnable
import org.noordawod.kotlin.restful.freemarker.FreeMarkerConfiguration

/**
 * An [HttpHandler] that orchestrates the task to prepare a data model,
 * an [output buffer][java.io.BufferedWriter], and then using a FreeMarker template to
 * generate the content and write it to the output buffer.
 *
 * @param T type of the data model
 * @param config configuration for FreeMarker
 * @param basePath where template files reside, excluding the trailing slash
 */
abstract class BaseFreeMarkerHttpHandler<T : Any> protected constructor(
  config: FreeMarkerConfiguration,
  basePath: String
) : BaseFreeMarkerRunnable<T>(config, basePath), HttpHandler {
  final override fun run() {
    error("Use handleRequest(exchange) instead.")
  }

  final override fun modelProvider(): T {
    error("Use modelProvider(exchange) instead.")
  }

  final override fun prepareWriter(): java.io.BufferedWriter {
    error("Use prepareWriter(exchange) instead.")
  }

  final override fun getTemplateFile(): String {
    error("Use getTemplateFile(exchange) instead.")
  }

  /**
   * Provider function for model of type [T].
   */
  protected abstract fun modelProvider(exchange: HttpServerExchange): T

  /**
   * Prepares a [buffer][java.io.BufferedWriter] to write the FreeMarker output to.
   *
   * @param exchange the HTTP request/response exchange
   */
  protected abstract fun prepareWriter(exchange: HttpServerExchange): java.io.BufferedWriter

  /**
   * The FreeMarker template file that resides under [basePath], excluding a leading slash.
   *
   * @param exchange the HTTP request/response exchange
   */
  protected abstract fun getTemplateFile(exchange: HttpServerExchange): String

  /**
   * Allow extended classes to modify the FreeMarker template before it's processed.
   *
   * @param exchange the HTTP request/response exchange
   * @param template the FreeMarker template to modify
   */
  protected open fun modifyTemplate(exchange: HttpServerExchange, template: Template) {
    // NO-OP
  }

  @Throws(TemplateException::class, java.io.IOException::class)
  override fun handleRequest(exchange: HttpServerExchange) {
    // Prepare the model so all other abstract or overloaded methods have access to it.
    model = modelProvider(exchange)

    // Logical file path + configured template extension.
    val filePath = getTemplateFile(exchange).withExtension(fileExtension)

    // The location of the FreeMarker template is always under the configured base path.
    val template = config.getTemplate("$basePath/$filePath")

    // Allow extended classes to modify the FreeMarker template, if needed.
    modifyTemplate(exchange, template)

    // Prepare the writer buffer and generate the content into it.
    prepareWriter(exchange).apply {
      template.process(model, this)
      flush()
    }
  }
}
