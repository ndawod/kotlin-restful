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
import org.noordawod.kotlin.core.DEFAULT_LIST_CAPACITY
import org.noordawod.kotlin.core.extension.mutableListWith
import org.noordawod.kotlin.core.util.FileSystem
import org.noordawod.kotlin.core.util.ImageDetails
import org.noordawod.kotlin.core.util.ImageDimension

/**
 * Utility functions for image manipulation.
 */
@Suppress("MemberVisibilityCanBePrivate")
object ImageUtils {
  /**
   * Returns the [image details][ImageDetails] for the image [sourceFile].
   *
   * @param sourceFile source image file
   */
  fun getDetails(sourceFile: java.io.File): ImageDetails {
    val dimension = getDimension(sourceFile)

    return ImageDetails(
      file = sourceFile,
      dimension = ImageDimension(dimension.first, dimension.second),
    )
  }

  /**
   * Compresses the [sourceFile] using JPEG compression with the specified [quality],
   * stores the resulting file as [targetFile], and returns the compressed image details.
   *
   * @param convertPaths possible paths to ImageMagick's convert program
   * @param sourceFile source image file to compress
   * @param targetFile target directory to store the file in, creating it if necessary
   * @param quality compression quality, a floating number between 0.0 - 1.0
   * @param maxSize maximum image size for the [output image][targetFile]
   */
  fun compressImage(
    convertPaths: Collection<String>,
    sourceFile: java.io.File,
    targetFile: java.io.File,
    quality: Float,
    maxSize: Int?,
  ): ImageDetails {
    val convertPath = convertPaths.firstOrNull {
      val file = java.io.File(it)
      file.isFile && file.canExecute()
    } ?: throw java.io.IOException("Unable to find convert program in: $convertPaths")

    val args = mutableListWith<String>(DEFAULT_LIST_CAPACITY)

    args.addAll(
      listOf(
        sourceFile.canonicalPath,
        "-density",
        "72",
        "-type",
        "TrueColor",
        "-compress",
        "JPEG",
        "-quality",
        @Suppress("MagicNumber")
        "${quality * 100.0f}",
      ),
    )
    if (null != maxSize && 0 < maxSize) {
      args.add("-resize")
      args.add("${maxSize}x$maxSize")
    }
    args.add(targetFile.canonicalPath)

    FileSystem.execute(
      program = convertPath,
      args = args,
      includeErrors = false,
    )

    return getDetails(targetFile)
  }

  /**
   * Returns the size of the image pointed to by the provided [filePath].
   *
   * @param filePath location of the image to identify
   */
  fun getDimension(filePath: java.io.File): Pair<Int, Int> {
    val input: java.awt.image.BufferedImage = javax.imageio.ImageIO.read(
      java.io.BufferedInputStream(java.io.FileInputStream(filePath.canonicalFile)),
    )

    return input.width to input.height
  }

  /**
   * Returns the size of the image pointed to by the provided [filePath].
   *
   * @param convertPath path to ImageMagick's convert program
   * @param filePath location of the image to identify
   */
  fun getDimension(
    convertPath: String,
    filePath: java.io.File,
  ): Pair<Int, Int> {
    // Construct the program's arguments.
    val args = listOf(
      "-ping",
      "-format",
      "%w %h",
      filePath.absolutePath,
    )

    // Execute the external program.
    val result = FileSystem.execute(
      program = convertPath,
      args = args,
      includeErrors = false,
    )
    var width = 0
    var height = 0

    if (result.isNotEmpty()) {
      val dimension = result.split(" ")
      width = dimension[0].toInt()
      height = dimension[1].toInt()
    }

    return width to height
  }

  /**
   * Compresses the [sourceFile] using JPEG compression with the specified [quality], and
   * stores the resulting file as [targetFile].
   *
   * @param sourceFile source image file to compress
   * @param targetFile target directory to store the file in, creating it if necessary
   * @param quality compression quality, a floating number between 0.0 - 1.0
   * @param maxSize maximum image size for the [output image][targetFile]
   */
  fun compressImage2D(
    sourceFile: java.io.File,
    targetFile: java.io.File,
    quality: Float,
    maxSize: Int,
  ): ImageDetails {
    if (!sourceFile.isFile) {
      error("Source file is invalid: $sourceFile")
    }

    // Attempt to create any subdirectory that doesn't exist.
    val parentFile = targetFile.parentFile
    if (parentFile.isFile) {
      error("Parent directory cannot be a file: $parentFile")
    }
    if (!parentFile.exists() && !parentFile.mkdirs()) {
      error("Parent directory cannot be created: $parentFile")
    }

    val jpegWriter = getImageWriter()

    // Configure for use with the correct compression.
    val writerParam = jpegWriter.defaultWriteParam
    writerParam.compressionMode = javax.imageio.ImageWriteParam.MODE_EXPLICIT
    writerParam.progressiveMode = javax.imageio.ImageWriteParam.MODE_DISABLED
    writerParam.compressionQuality = quality

    // Prepare input and output images.
    val input: java.awt.image.BufferedImage = javax.imageio.ImageIO.read(
      java.io.BufferedInputStream(java.io.FileInputStream(sourceFile)),
    )

    // Meta data about input image.
    val width = input.width
    val height = input.height

    // New size for output image.
    var newWidth = width
    var newHeight = height

    // Check if we need to scale width.
    if (width > maxSize) {
      newWidth = maxSize
      newHeight = (newWidth.toFloat() * height.toFloat() / width.toFloat()).toInt()
    }

    // Check if we need to scale even with the new height.
    if (newHeight > maxSize) {
      newHeight = maxSize
      newWidth = (newHeight.toFloat() * width.toFloat() / height.toFloat()).toInt()
    }

    val resizedImage = java.awt.image.BufferedImage(newWidth, newHeight, input.type)

    resizedImage.graphics.drawImage(
      input.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH),
      0,
      0,
      null,
    )

    // Write resized image.
    javax.imageio.ImageIO.write(resizedImage, FORMAT_NAME, targetFile)

    return ImageDetails(
      file = targetFile,
      dimension = ImageDimension(newWidth, newHeight),
    )
  }

  /**
   * Returns the calculated blur hash for the provided [file].
   *
   * @param file file to calculate the blur hash for
   */
  fun getBlurHash(file: java.io.File): String = BlurHash.encode(
    javax.imageio.ImageIO.read(java.io.BufferedInputStream(java.io.FileInputStream(file))),
  )

  /**
   * Returns the calculated blur hash for the file with the provided [path].
   *
   * @param path path to file to calculate the blur hash for
   */
  fun getBlurHash(path: String): String = BlurHash.encode(
    javax.imageio.ImageIO.read(java.io.BufferedInputStream(java.io.FileInputStream(path))),
  )

  private fun getImageWriter(): javax.imageio.ImageWriter {
    val jpegWriters = javax.imageio.ImageIO.getImageWritersByFormatName(FORMAT_NAME)
    if (!jpegWriters.hasNext()) {
      error("This system has no configured JPEG image writers.")
    }

    return jpegWriters.next()
  }

  private const val FORMAT_NAME = "JPEG"
}
