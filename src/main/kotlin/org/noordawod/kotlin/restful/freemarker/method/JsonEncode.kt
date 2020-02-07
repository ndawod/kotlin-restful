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

@file:Suppress("unused")

package org.noordawod.kotlin.restful.freemarker.method

import com.fasterxml.jackson.databind.ObjectMapper
import freemarker.template.SimpleScalar
import freemarker.template.TemplateMethodModelEx
import freemarker.template.TemplateModel
import freemarker.template.TemplateModelException

/**
 * A handy FreeMarker method to encode any object to a JSON string. Method must be called with at
 * least one parameter.
 */
class JsonEncode : TemplateMethodModelEx {
  private val engine: ObjectMapper = ObjectMapper()

  /** {@inheritDoc}. */
  @Throws(TemplateModelException::class)
  override fun exec(args: List<*>): TemplateModel {
    if (args.isEmpty()) {
      throw TemplateModelException("Method called with a null or no value.")
    }
    return SimpleScalar(engine.writeValueAsString(args[0]))
  }
}
