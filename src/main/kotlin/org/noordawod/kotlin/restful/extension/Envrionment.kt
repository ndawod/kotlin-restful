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

import freemarker.template.Version
import org.noordawod.kotlin.core.util.Environment
import org.noordawod.kotlin.restful.freemarker.FreeMarkerConfiguration

/**
 * Returns the base directory for this [Environment] where configuration files are stored.
 */
fun Environment.configDirectory() =
  "config${java.io.File.separatorChar}$this"

/**
 * Returns the path to a configuration [file] based on this [Environment].
 */
fun Environment.configFile(file: String) =
  "${configDirectory()}${java.io.File.separatorChar}$file"

/**
 * Returns the base directory for this [Environment] where HTML files are stored.
 */
fun Environment.htmlDirectory() =
  "html${java.io.File.separatorChar}$this"

/**
 * Returns the path to an HTML [file] based on this [Environment].
 */
fun Environment.htmlFile(file: String) =
  "${htmlDirectory()}${java.io.File.separatorChar}$file"

/**
 * Returns the base directory for this [Environment] where FreeMarker templates are stored.
 */
fun Environment.templatesDirectory(name: String = "templates") =
  "${htmlDirectory()}${java.io.File.separatorChar}$name"

/**
 * Returns the path to an HTML [file] based on this [Environment].
 */
fun Environment.templateFile(file: String) =
  "${templatesDirectory()}${java.io.File.separatorChar}$file"

/**
 * Returns the base directory for this [Environment] where localization files are stored.
 */
fun Environment.l10nDirectory() =
  "l10n${java.io.File.separatorChar}$this"

/**
 * Returns the path to a localization [file] based on this [Environment].
 */
fun Environment.l10nFile(file: String) =
  "${l10nDirectory()}${java.io.File.separatorChar}$file"

/**
 * Generates a compatible [FreeMarkerConfiguration] based on this [Environment].
 *
 * @param version which version of FreeMarker templating to use, defaults to
 * [FreeMarkerConfiguration.DEFAULT_VERSION]
 * @param charset character set of templates files, defaults to
 * [UTF-8][java.nio.charset.StandardCharsets.UTF_8]
 */
fun Environment.freeMarkerConfiguration(
  version: Version = FreeMarkerConfiguration.DEFAULT_VERSION,
  charset: java.nio.charset.Charset = java.nio.charset.StandardCharsets.UTF_8
): FreeMarkerConfiguration {
  val freeMarker = FreeMarkerConfiguration(version, charset)

  // Files are always loaded from a directory inspired by the running environment.
  freeMarker.setDirectoryForTemplateLoading(java.io.File(htmlDirectory()))

  // Use a different handler based on the running environment.
  freeMarker.templateExceptionHandler = when (this) {
    Environment.LOCAL,
    Environment.DEVEL -> freemarker.template.TemplateExceptionHandler.HTML_DEBUG_HANDLER
    Environment.BETA -> freemarker.template.TemplateExceptionHandler.DEBUG_HANDLER
    Environment.PRODUCTION -> freemarker.template.TemplateExceptionHandler.RETHROW_HANDLER
  }

  return freeMarker
}
