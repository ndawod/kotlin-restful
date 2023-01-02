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

package org.noordawod.kotlin.restful.di

import dagger.Component
import kotlinx.serialization.ExperimentalSerializationApi
import org.noordawod.kotlin.core.di.BaseComponent

/**
 * The app-wide Dagger II [Component].
 */
@ExperimentalSerializationApi
@javax.inject.Singleton
@Component(
  modules = [
    HttpHtmlModule::class,
    SecurityModule::class,
    SendmailModule::class,
    SerializableModule::class
  ]
)
abstract class CoreComponent : BaseComponent {
  /**
   * Attaches the singleton instance of this component and make it accessible through the
   * static [instance] variable.
   */
  final override fun attach() {
    if (null == privateInstance) {
      privateInstance = this
    } else {
      error("The component singleton instance has already been set.")
    }
  }

  companion object {
    private var privateInstance: CoreComponent? = null

    /**
     * The app-wide [CoreComponent] singleton instance.
     */
    val instance: CoreComponent
      get() {
        return privateInstance
          ?: error("The component singleton instance has not been attached yet.")
      }
  }
}
