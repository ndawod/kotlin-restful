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

package org.noordawod.kotlin.restful

/**
 * Common constants and values that can be used throughout the module and beyond.
 */
object Constants {
  /**
   * Top level directory name holding the configuration files.
   */
  const val CONFIG_DIR_NAME: String = "config"

  /**
   * Crude, reusable defaults for this module.
   */
  const val LANGUAGE_QUERY_PARAM: String = "lang"

  /**
   * Location of FreeMarker page templates.
   */
  const val FTL_PAGE_PATH: String = "templates"

  /**
   * Location of FreeMarker email templates.
   */
  const val FTL_EMAIL_PATH: String = "email"

  /**
   * Extension used for all template files.
   */
  const val FTL_EXTENSION: String = "ftlh"

  /**
   * A regular expression pattern that matches a cache-buster URI base path.
   */
  val CACHE_BUSTER_PATTERN: java.util.regex.Pattern =
    java.util.regex.Pattern.compile("/(\\+| |v\\d+)/")
}
