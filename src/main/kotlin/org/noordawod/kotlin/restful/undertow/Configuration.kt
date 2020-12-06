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

package org.noordawod.kotlin.restful.undertow

import org.xnio.ByteBufferSlicePool

/**
 * Configuration necessary to start up an [UndertowServer].
 */
interface Configuration {
  /**
   * IP address to bind the server to.
   */
  val ipAddr: String

  /**
   * Port to bind the [ipAddr] to.
   */
  val port: Int

  /**
   * IO threads perform non blocking tasks, and should never perform blocking operations because
   * they are responsible for multiple connections, so while the operation is blocking other
   * connections will essentially hang.
   *
   * Two IO threads per CPU core is a reasonable default.
   */
  val ioThreads: Int

  /**
   * When performing blocking operations such as Servlet requests threads from this pool will be
   * used. In general it is hard to give a reasonable default for this, as it depends on the server
   * workload.
   *
   * Generally this should be reasonably high, around 10 per CPU core.
   */
  val workerThreads: Int

  /**
   * These buffers are used for IO operations, and the buffer size has a big impact on application
   * performance. For servers the ideal size is generally 16k, as this is usually the maximum
   * amount of data that can be written out via a write() operation (depending on the network
   * setting of the operating system). Smaller systems may want to use smaller buffers to save
   * memory.
   */
  val bufferSize: Int

  /**
   * How many buffers to allocate per region, this is used when instantiating a new
   * [ByteBufferSlicePool] instance.
   */
  val buffersPerRegion: Int
}
