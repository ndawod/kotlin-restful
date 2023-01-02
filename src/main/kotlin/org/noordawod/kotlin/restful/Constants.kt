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
   * Default query parameter name used to select the default [Locale][java.util.Locale].
   */
  const val LOCALE_QUERY_PARAM: String = "loc"

  /**
   * Default logical path to the FreeMarker templates.
   */
  const val FTL_PAGE_PATH: String = "src/main/freemarker"

  /**
   * Default name of folder holding the FreeMarker email templates.
   */
  const val FTL_EMAIL_FOLDER: String = "email"

  /**
   * Extension used for FreeMarker textual template files.
   */
  const val FTL_EXTENSION: String = "ftl"

  /**
   * Extension used for FreeMarker HTML template files.
   */
  const val FTLH_EXTENSION: String = "ftlh"

  /**
   * An agreed-upon string value to signal that a resource is marked as deleted.
   */
  const val RESOURCE_DELETED: String = "deleted"

  /**
   * A regular expression pattern that matches a cache-buster URI base path.
   */
  val CACHE_BUSTER_PATTERN: java.util.regex.Pattern =
    java.util.regex.Pattern.compile("/(\\+| |v\\d+)/")
}
