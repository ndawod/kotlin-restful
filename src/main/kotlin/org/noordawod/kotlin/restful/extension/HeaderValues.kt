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
@file:OptIn(ExperimentalContracts::class)

package org.noordawod.kotlin.restful.extension

import io.undertow.util.HeaderValues
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import org.noordawod.kotlin.core.extension.mutableListWith

/**
 * Returns the list of preferred languages (as a list of [Locale][java.util.Locale]s)
 * from this [HeaderValues], null if no preferred languages were provided by the
 * remote client.
 */
fun HeaderValues?.acceptLocales(): Iterable<java.util.Locale>? {
  contract {
    returnsNotNull() implies (this@acceptLocales != null)
  }

  val headerValue = this?.firstOrNull()
    ?.replace('_', '-')
    ?.ifEmpty { null } ?: return null

  val languageRanges = java.util.Locale.LanguageRange.parse(headerValue)
  val result = mutableListWith<java.util.Locale>(languageRanges.size)

  for (language in languageRanges) {
    result.add(java.util.Locale.forLanguageTag(language.range))
  }

  return if (result.isEmpty()) null else result
}
