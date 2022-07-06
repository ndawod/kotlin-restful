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

package org.noordawod.kotlin.restful.extension

import freemarker.template.utility.StringUtil
import org.noordawod.kotlin.restful.freemarker.FreeMarkerDataModel
import org.noordawod.kotlin.restful.util.QuerySeparator

/**
 * A handy method to generate a [String] in the format key=value or key[]=value1,key[]=value2,...
 * based on the specified [values].
 */
fun StringBuilder.appendQueryParameter(sep: QuerySeparator, key: String, values: Iterable<*>) {
  append(sep.value)
  append(StringUtil.URLEnc(key, FreeMarkerDataModel.CHARSET_NAME))

  val list = if (values is Collection<*>) values else values.toCollection(mutableListOf())

  if (1 < list.size) {
    list.forEach {
      append("[]")
      append('=')
      append(StringUtil.URLEnc(it.toString(), FreeMarkerDataModel.CHARSET_NAME))
    }
  } else {
    append('=')
    append(StringUtil.URLEnc(list.first().toString(), FreeMarkerDataModel.CHARSET_NAME))
  }
}
