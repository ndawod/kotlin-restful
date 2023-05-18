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

import dagger.Module
import dagger.Provides
import org.noordawod.kotlin.core.config.SmtpConfiguration
import org.noordawod.kotlin.core.extension.timeoutInMilliseconds
import org.noordawod.kotlin.core.util.Environment
import org.noordawod.kotlin.restful.repository.SendmailRepository
import org.noordawod.kotlin.restful.repository.impl.SendmailRepositoryImpl
import org.simplejavamail.api.mailer.Mailer

/**
 * Mail-focused singleton instances accessible via dependency injection.
 *
 * @param smtp the current [SmtpConfiguration] to use
 */
@Module
class SendmailModule(private val smtp: SmtpConfiguration) {
  /**
   * The [Mailer] singleton instance.
   *
   * @param environment the [Environment] instance to use
   */
  @javax.inject.Singleton
  @Provides
  fun mailer(environment: Environment): Mailer =
    SendmailRepository.createMailer(smtp, environment.timeoutInMilliseconds())

  /**
   * The [SendmailRepository] singleton instance.
   */
  @javax.inject.Singleton
  @Provides
  fun sendmailRepository(environment: Environment): SendmailRepository =
    SendmailRepositoryImpl(smtp, environment.timeoutInMilliseconds())
}
