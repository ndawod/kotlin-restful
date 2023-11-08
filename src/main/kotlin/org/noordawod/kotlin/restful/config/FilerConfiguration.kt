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

package org.noordawod.kotlin.restful.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Configuration for how and where uploaded files will be stored, which will always be renamed
 * to a very long file name.
 *
 * @param logicalPath directory path to where to store files
 * @param alphabet which characters to use when creating file names
 * @param length maximum length of file names
 * @param depth depth of subdirectories structure for storing files in [path]
 */
@Serializable
data class FilerConfiguration(
  private val logicalPath: String,
  val alphabet: String,
  val length: Int,
  val depth: Int,
) {
  /**
   * Absolute path to where to store files. This is the canonical path which does not include
   * `.` or `..` in its path.
   */
  @Transient
  val path: String = java.io.File(logicalPath).canonicalPath
}
