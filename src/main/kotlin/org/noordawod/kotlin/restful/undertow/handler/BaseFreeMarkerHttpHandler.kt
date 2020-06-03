/*
 * The MIT License
 *
 * Copyright 2020 Noor Dawod. All rights reserved.
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

package org.noordawod.kotlin.restful.undertow.handler

import freemarker.template.Template
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import org.noordawod.kotlin.core.extension.withExtension
import org.noordawod.kotlin.restful.freemarker.FreeMarkerConfiguration

/**
 * A signature for a function that accepts an [HttpServerExchange] and returns a model.
 */
typealias DataModelProvider<T> = (HttpServerExchange) -> T

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
  protected val config: FreeMarkerConfiguration,
  protected val basePath: String
) : HttpHandler {
  /**
   * Extension for template files.
   */
  protected abstract val fileExtension: String

  /**
   * Provider function for model of type [T].
   */
  protected abstract val modelProvider: DataModelProvider<T>

  /**
   * The data model itself is evaluated in [handleRequest] and stored here momentarily.
   */
  protected lateinit var model: T

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
   * @param template the FreeMarker template to modify
   */
  protected open fun modifyTemplate(template: Template) {
    // NO-OP
  }

  override fun handleRequest(exchange: HttpServerExchange) {
    // Prepare the model so all other abstract or overloaded methods have access to it.
    model = modelProvider(exchange)

    // Logical file path + configured template extension.
    val filePath = getTemplateFile(exchange).withExtension(fileExtension)

    // Allow extended classes to modify the FreeMarker template, if needed.
    val template = config.getTemplate("$basePath/$filePath")
    modifyTemplate(template)

    // Prepare the writer buffer, generate the content and finally spit it out to the writer.
    prepareWriter(exchange).use { buffer ->
      template.process(model, buffer)
      buffer.flush()
    }
  }
}
