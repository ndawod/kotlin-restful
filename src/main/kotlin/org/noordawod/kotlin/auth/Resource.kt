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

package org.noordawod.kotlin.auth

/**
 * An alias for the unique identifier of an [Resource].
 */
typealias ResourceId = Int

/**
 * The grammatical English article that may precede a noun (either "a" or "an").
 *
 * @param word the actual English word
 */
enum class Article constructor(private val word: String) {
  /**
   * The "a" article.
   */
  A("a"),

  /**
   * The "an" article.
   */
  AN("an");

  override fun toString(): String = word
}

/**
 * A [Resource] is data that a [Client] requests to access or operate on. The required
 * [permissions][Permission] necessary to operate on a specific resource are defined in the
 * database, and an operator (via the control panel) may change those permissions at will.
 *
 * The list of available resources are app-bound and a developer must edit the source code in
 * order to add, remove or update them.
 */
interface Resource {
  /**
   * A unique identifier for this resource.
   */
  val identifier: ResourceId

  /**
   * A human-readable label (in English) for this resource.
   */
  val label: String

  /**
   * A short description for this resource, handy when presented in the control panel.
   */
  val description: String

  /**
   * The grammatical article (either "a" or "an") that precedes this [label].
   */
  val article: Article
}
