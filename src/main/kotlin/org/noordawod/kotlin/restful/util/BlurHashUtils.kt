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

package org.noordawod.kotlin.restful.util

import io.trbl.blurhash.BlurHash

/**
 * Utility functions for image manipulation.
 */
@Suppress("MemberVisibilityCanBePrivate")
object BlurHashUtils {
  /**
   * Returns the calculated blur hash for the provided [file].
   *
   * @param file file to calculate the blur hash for
   */
  fun get(file: java.io.File): String = BlurHash.encode(
    javax.imageio.ImageIO.read(java.io.BufferedInputStream(java.io.FileInputStream(file))),
  )

  /**
   * Returns the calculated blur hash for the file with the provided [path].
   *
   * @param path path to file to calculate the blur hash for
   */
  fun get(path: String): String = BlurHash.encode(
    javax.imageio.ImageIO.read(java.io.BufferedInputStream(java.io.FileInputStream(path))),
  )
}
