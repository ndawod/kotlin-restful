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

@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.noordawod.kotlin.restful.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Defines configuration of a typical multithreaded server.
 *
 * @param ipAddr which IP address to bind to when starting the server
 * @param host the public host name of this server
 * @param port which port to bind to when starting the server
 * @param threads configuration to run a multithreaded server properly
 * @param buffer configuration for the buffer pool tied to listeners
 *
 * @see <a href="https://tinyurl.com/t8yyhxm">Assembling a Server Manually</a>
 * @see <a href="https://tinyurl.com/wdrwhe7">Architecture Overview</a>
 */
@Serializable
open class ServerConfiguration(
  val ipAddr: String,
  val host: String,
  val port: Int,
  val threads: ServerThreadsConfiguration,
  val buffer: ServerBufferConfiguration
) {
  /**
   * Returns the combination of this server's hostname and port, separated by a colon.
   */
  @Transient
  val hostAndPort: String = "$host:$port"

  override fun equals(other: Any?): Boolean = other is ServerConfiguration &&
    other.ipAddr == ipAddr &&
    other.host == host &&
    other.port == port &&
    other.threads == threads &&
    other.buffer == buffer

  @Suppress("MagicNumber")
  override fun hashCode(): Int = port +
    ipAddr.hashCode() * 1609 +
    host.hashCode() * 2269 +
    threads.hashCode() * 947 +
    buffer.hashCode() * 457
}

/**
 * Defines the threading configuration of a multithreaded server.
 *
 * @param io maximum number of XNIO I/O threads
 * @param worker default number of XNIO worker threads
 * @param perCore how many threads in each CPU core
 * @param concurrency maximum number of tasks to allow before rejecting new ones
 * @param aliveThreshold duration, in milliseconds, to keep worker threads alive
 *
 * @see <a href="https://tinyurl.com/wdrwhe7">Architecture Overview</a>
 * @see <a href="http://xnio.jboss.org/">XNIO</a>
 */
@Serializable
open class ServerThreadsConfiguration(
  val io: Int,
  val worker: Int,
  val perCore: Int = 1,
  val concurrency: Int = 0,
  val aliveThreshold: Int = 0
) {
  override fun equals(other: Any?): Boolean = other is ServerThreadsConfiguration &&
    other.io == io &&
    other.worker == worker &&
    other.perCore == perCore &&
    other.concurrency == concurrency &&
    other.aliveThreshold == aliveThreshold

  @Suppress("MagicNumber")
  override fun hashCode(): Int = io * 349 +
    worker * 1609 +
    perCore * 2269 +
    concurrency * 7 +
    aliveThreshold * 13
}

/**
 * Defines the configuration a buffer pool tied to listeners.
 *
 * @param size size of buffer pool
 * @param perRegion size of buffer pool per region
 *
 * @see <a href="https://tinyurl.com/v2c7p3u">Buffer Pool</a>
 * @see <a href="https://tinyurl.com/ww4xyct">Listeners</a>
 */
@Serializable
open class ServerBufferConfiguration(
  val size: Int,
  val perRegion: Int
) {
  override fun equals(other: Any?): Boolean = other is ServerBufferConfiguration &&
    other.size == size &&
    other.perRegion == perRegion

  @Suppress("MagicNumber")
  override fun hashCode(): Int = size * 349 + perRegion * 1609
}
