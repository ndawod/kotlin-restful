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

package org.noordawod.kotlin.restful.freemarker

import org.noordawod.kotlin.core.extension.direction
import org.noordawod.kotlin.core.extension.endAlignment
import org.noordawod.kotlin.core.extension.getNewLanguage
import org.noordawod.kotlin.core.extension.startAlignment
import org.noordawod.kotlin.core.util.Properties

/**
 * A FreeMarker data model that supports localization through the use of a query parameter.
 */
abstract class FreeMarkerL10nDataModel : FreeMarkerDataModel {
  /**
   * Returns the localized translations based on the client's preferred [java.util.Locale].
   */
  abstract val translation: Properties

  /**
   * Returns the [java.util.Locale] for a request, if provided, or reverts back to a configured
   * locale in the app.
   */
  abstract val locale: java.util.Locale

  /**
   * Returns the requested language in this request, if provided, or reverts back to the base,
   * configured language in this module.
   */
  open val language: String get() = locale.getNewLanguage()

  /**
   * Returns the writing direction in the requested language in this request ("rtl", "ltr").
   */
  open val direction: String get() = locale.direction()

  /**
   * Returns the writing direction's starting alignment for requested language in this request
   * ("right", "left").
   */
  open val startAlignment: String get() = locale.startAlignment()

  /**
   * Returns the writing direction's ending alignment for requested language in this request
   * ("right", "left").
   */
  open val endAlignment: String get() = locale.endAlignment()

  /**
   * Localizes the specified text [key] based on the loaded configuration. Localization files are
   * loaded on startup from *.properties found in "l10n/&lt;env&gt;/" directory.
   */
  open fun l10n(key: String): String? = translation[key]

  /**
   * Localizes the specified text [key] based on the loaded configuration. Localization files are
   * loaded on startup from *.properties found in "l10n/&lt;env&gt;/" directory.
   */
  open fun l10n(key: String, defaultValue: String): String = l10n(key) ?: defaultValue
}
