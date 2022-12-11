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

package org.noordawod.kotlin.restful.repository.impl

import com.googlecode.htmlcompressor.compressor.HtmlCompressor
import org.noordawod.kotlin.restful.repository.HtmlCompressorRepository

/**
 * Implementation class for [HtmlCompressorRepository].
 *
 * @param enabled whether to automatically enable the compressor
 */
internal class HtmlCompressorRepositoryImpl constructor(
  enabled: Boolean
) : HtmlCompressorRepository {
  private val compressor = buildDefaultHtmlCompressor(enabled)

  override val isEnabled: Boolean get() = compressor.isEnabled

  override fun setEnabled(enabled: Boolean) {
    compressor.isEnabled = enabled
  }

  override fun compress(html: String): String =
    // We'll check to see if there are special IE tags that need special attention.
    if (
      1 > html.indexOf("<![endif]-->", ignoreCase = true) &&
      1 > html.indexOf("<!--<![endif]-->", ignoreCase = true)
    ) {
      compressor.compress(html)
    } else {
      // We shall retain all code before the starting <head> tag because that code may
      // container IE-specific code using <!--[if IE]> and <![endif]-->, which will fail
      // to work if the new lines are removed.
      val htmlTagStart = html.indexOf("<head", ignoreCase = true)

      // Compress from the correct position in the HTML.
      val compressedHtml = compressor.compress(
        if (0 < htmlTagStart) {
          html.substring(htmlTagStart)
        } else {
          html
        }
      )

      // Prepend content appearing before the <head> tag, if any.
      if (0 < htmlTagStart) {
        "${html.substring(0, htmlTagStart)}$compressedHtml"
      } else {
        compressedHtml
      }
    }

  override fun compressCssStyles(styles: String): String =
    compressor.cssCompressor.compress(styles)

  override fun compressJavaScriptStyles(js: String): String =
    compressor.javaScriptCompressor.compress(js)
}

/**
 * Builds an [HtmlCompressor] instance configured with default configuration.
 *
 * @param enabled whether to automatically enable the compressor (default is true)
 */
internal fun buildDefaultHtmlCompressor(enabled: Boolean): HtmlCompressor =
  HtmlCompressor().apply {
    isEnabled = enabled
    isCompressCss = false
    isCompressJavaScript = false
    isGenerateStatistics = false
    isRemoveComments = true
    isPreserveLineBreaks = false
    isRemoveFormAttributes = false
    isRemoveIntertagSpaces = true
    isRemoveMultiSpaces = true
    isRemoveMultiSpaces = true
    isRemoveQuotes = true
    isRemoveScriptAttributes = true
    isRemoveStyleAttributes = true
  }
