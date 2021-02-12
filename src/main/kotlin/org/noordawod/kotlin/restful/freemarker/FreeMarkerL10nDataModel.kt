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

package org.noordawod.kotlin.restful.freemarker

import com.ibm.icu.text.PluralRules
import org.noordawod.kotlin.core.extension.direction
import org.noordawod.kotlin.core.extension.endAlignment
import org.noordawod.kotlin.core.extension.getNewLanguage
import org.noordawod.kotlin.core.extension.startAlignment
import org.noordawod.kotlin.core.util.Localization

/**
 * A FreeMarker data model that supports localization.
 */
abstract class FreeMarkerL10nDataModel : BaseFreeMarkerDataModel() {
  /**
   * Returns the localized translations for the base [java.util.Locale].
   */
  protected abstract val baseL10n: Localization

  /**
   * Returns the localized translations based on the client's preferred [java.util.Locale].
   */
  protected abstract val clientL10n: Localization

  /**
   * Returns the base [java.util.Locale].
   */
  open val baseLocale: java.util.Locale get() = baseL10n.locale

  /**
   * Returns the client's preferred [java.util.Locale].
   */
  open val locale: java.util.Locale get() = clientL10n.locale

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
   * Localizes the specified text [key] based on the client's preferred [java.util.Locale].
   *
   * @param key the localization key to localize
   */
  open fun l10n(key: String): String = l10n(key, false)

  /**
   * Localizes the specified text [key] based on the client's preferred [java.util.Locale]
   * with the specified arguments to replace any placeholders in the translation
   * (%1$d, %2$s, ...).
   *
   * @param key the localization key to localize
   * @param args list of arguments to substitute in the localized text
   */
  open fun l10n(key: String, args: Iterable<Any>): String {
    val text: String = l10n(key, true)
    return if (text.isEmpty()) {
      ""
    } else {
      @Suppress("SpreadOperator")
      java.lang.String.format(locale, text, *args.map { it.toString() }.toTypedArray())
    }
  }

  /**
   * Localizes the specified text [key] based on the client's preferred [java.util.Locale].
   * If [fallback] is true, and the translations for the [clientL10n] don't have this key,
   * then the original text for this key is retrieved from the [baseL10n].
   *
   * @param key the localization key to localize
   * @param fallback whether to use text from [baseL10n] if current translation is missing
   * the translation for [key]
   */
  open fun l10n(key: String, fallback: Boolean): String {
    var text: String? = clientL10n.translation[key]
    if (fallback && null == text) {
      text = baseL10n.translation[key]
    }
    return text?.trim() ?: key
  }

  /**
   * Localizes a plural text identified by its [key].
   *
   * @param key the localization key to localize
   * @param count the count of items to pluralize
   */
  @Suppress("StringLiteralDuplication")
  fun pluralize(key: String, count: Int): String {
    val pluralRules = PluralRules.forLocale(clientL10n.locale)
    val rule = pluralRules.select(count.toDouble()).toLowerCase(java.util.Locale.ENGLISH)
    return l10n("$key.$rule")
  }

  /**
   * Localizes a plural text identified by its [key] with the specified arguments to replace
   * any placeholders in the translation (%1$d, %2$s, …).
   *
   * @param key the localization key to localize
   * @param args list of arguments to substitute in the localized text
   * @param count the count of items to pluralize
   */
  @Suppress("StringLiteralDuplication")
  fun pluralize(key: String, count: Int, args: Iterable<Any>): String {
    val pluralRules = PluralRules.forLocale(clientL10n.locale)
    val rule = pluralRules.select(count.toDouble()).toLowerCase(java.util.Locale.ENGLISH)
    return l10n("$key.$rule", args)
  }

  /**
   * Localizes a quantity text identified by its [key] using only rules for zero, one, two
   * and other.
   *
   * @param key the localization key to localize
   * @param quantity the quantity value
   */
  @Suppress("StringLiteralDuplication")
  fun quantify(key: String, quantity: Int): String {
    val rule = quantifyRule(quantity)
    return l10n("$key.$rule")
  }

  /**
   * Localizes a quantity text identified by its [key] using only rules for zero, one, two
   * and other and with the specified arguments to replace any placeholders in the
   * translation (%1$d, %2$s, …).
   *
   * @param key the localization key to localize
   * @param args list of arguments to substitute in the localized text
   * @param quantity the quantity value
   */
  @Suppress("StringLiteralDuplication")
  fun quantify(key: String, quantity: Int, args: Iterable<Any>): String {
    val rule = quantifyRule(quantity)
    return l10n("$key.$rule", args)
  }

  private fun quantifyRule(quantity: Int): String = when (quantity) {
    0 -> PluralRules.KEYWORD_ZERO
    1 -> PluralRules.KEYWORD_ONE
    2 -> PluralRules.KEYWORD_TWO
    else -> PluralRules.KEYWORD_OTHER
  }
}
