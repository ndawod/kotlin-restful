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

package org.noordawod.kotlin.restful.undertow

import io.undertow.UndertowOptions
import io.undertow.server.HttpHandler
import io.undertow.server.XnioByteBufferPool
import io.undertow.server.protocol.http.HttpOpenListener
import org.xnio.BufferAllocator
import org.xnio.ChannelListeners
import org.xnio.OptionMap
import org.xnio.Options
import org.xnio.StreamConnection
import org.xnio.Xnio
import org.xnio.XnioWorker
import org.xnio.channels.AcceptingChannel

/**
 * A base class that wraps an [AcceptingChannel] and abstracts some settings and configuration
 * suitable to operate an HTTP/1.1 REST server.
 *
 * @param config configuration required to start this Undertow server
 */
open class UndertowServer(
  @Suppress("MemberVisibilityCanBePrivate") val config: Configuration
) {
  private val mainThread = Thread.currentThread()
  private var hook: Thread? = null
  private lateinit var channel: HttpOpenListener
  private lateinit var server: AcceptingChannel<out StreamConnection>

  /**
   * Returns true if the servers has been started, false otherwise.
   */
  val isStarted: Boolean
    get() = null != hook

  /**
   * Allows subclasses to set the initial handler for this [server]'s [channel].
   */
  @Suppress("DEPRECATION")
  protected fun setHandler(
    handler: HttpHandler,
    options: OptionMap? = null
  ) {
    val buffers = org.xnio.ByteBufferSlicePool(
      BufferAllocator.DIRECT_BYTE_BUFFER_ALLOCATOR,
      config.bufferSize,
      config.bufferSize * config.buffersPerRegion
    )

    val finalOptions = OptionMap.builder()
      .set(
        UndertowOptions.BUFFER_PIPELINED_DATA,
        true
      )
      .set(
        UndertowOptions.ALWAYS_SET_KEEP_ALIVE,
        true
      )
      .set(
        UndertowOptions.REQUIRE_HOST_HTTP11,
        true
      )
      .set(
        UndertowOptions.ENABLE_SPDY,
        false
      )
      .set(
        UndertowOptions.ENABLE_HTTP2,
        false
      )

    if (null != options) {
      finalOptions.addAll(options)
    }

    channel = HttpOpenListener(XnioByteBufferPool(buffers), finalOptions.map)

    channel.rootHandler = handler
  }

  /**
   * Called whenever the server is successfully started.
   */
  open fun onStart() {
    // NO-OP
  }

  /**
   * Called whenever the server shuts down.
   */
  open fun onShutdown() {
    // NO-OP
  }

  /**
   * Binds the server to the configured host:port and starts it.
   */
  open fun start(
    dieDuration: Long = 5000L,
    options: OptionMap? = null
  ) {
    startImpl(
      dieDuration = dieDuration,
      options = options
    )
  }

  /**
   * Shuts down the server.
   */
  open fun stop() {
    stopImpl()
  }

  /**
   * Allows children classes to use the same logic to start the server.
   */
  @Suppress("MemberVisibilityCanBePrivate", "LongMethod")
  protected fun startImpl(
    dieDuration: Long,
    options: OptionMap?
  ) {
    if (null == hook) {
      val builder = OptionMap.builder()

      builder
        .set(
          Options.WORKER_IO_THREADS,
          config.ioThreads
        )
        .set(
          Options.WORKER_TASK_CORE_THREADS,
          config.workerThreads
        )
        .set(
          Options.TCP_NODELAY,
          true
        )
        .set(
          Options.BACKLOG,
          config.ioThreads * config.workerThreads
        )
        .set(
          Options.RECEIVE_BUFFER,
          config.bufferSize
        )

      val workerThreadsPerCore = config.workerThreadsPerCore
      if (null != workerThreadsPerCore && 0 < workerThreadsPerCore) {
        builder.set(
          Options.WORKER_TASK_MAX_THREADS,
          config.workerThreads * workerThreadsPerCore
        )
      } else {
        builder.set(
          Options.WORKER_TASK_MAX_THREADS,
          config.workerThreads
        )
      }

      val workerTasksThreshold = config.workerTasksThreshold
      if (null != workerTasksThreshold && 0 < workerTasksThreshold) {
        builder.set(
          Options.WORKER_TASK_KEEPALIVE,
          workerTasksThreshold
        )
      }

      val workerTasks = config.workerTasks
      if (null != workerTasks && 0 < workerTasks) {
        builder.set(
          Options.WORKER_TASK_LIMIT,
          workerTasks
        )
      }

      if (null != options) {
        builder.addAll(options)
      }

      val worker: XnioWorker = Xnio.getInstance().createWorker(builder.map)

      server = worker.createStreamConnectionServer(
        java.net.InetSocketAddress(
          java.net.Inet4Address.getByName(config.ipAddr),
          config.port
        ),
        ChannelListeners.openListenerAdapter(channel),
        OptionMap.builder()
          .set(
            Options.WORKER_IO_THREADS,
            config.ioThreads
          )
          .set(
            Options.TCP_NODELAY,
            true
          )
          .set(
            Options.REUSE_ADDRESSES,
            true
          )
          .map
      )

      object : Thread() {
        override fun run() {
          try {
            mainThread.join(dieDuration)
          } catch (ignored: InterruptedException) {
          }
          this@UndertowServer.stop()
        }
      }.let {
        Runtime.getRuntime().addShutdownHook(it)
        hook = it
      }

      server.resumeAccepts()

      onStart()
    }
  }

  /**
   * Allows children classes to use the same logic to shut down the server.
   */
  @Suppress("MemberVisibilityCanBePrivate")
  protected fun stopImpl() {
    if (null != hook) {
      hook = null
      server.close()

      onShutdown()
    }
  }
}
