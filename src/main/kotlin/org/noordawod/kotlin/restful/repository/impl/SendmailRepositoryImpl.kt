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

package org.noordawod.kotlin.restful.repository.impl

import org.noordawod.kotlin.core.config.SmtpConfiguration
import org.noordawod.kotlin.restful.repository.SendmailMessage
import org.noordawod.kotlin.restful.repository.SendmailRepository
import org.simplejavamail.api.email.Recipient
import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.email.EmailBuilder

/**
 * This repository provides sendmail services to consumers through the excellent 3rd party
 * [org.simplejavamail.api.mailer] library to perform its duties.
 *
 * @param timeout the timeout, in milliseconds, before giving up send
 * @param config the [SmtpConfiguration] instance to use
 */
internal class SendmailRepositoryImpl(
  private val config: SmtpConfiguration,
  timeout: Long,
) : SendmailRepository {
  private val mailer: Mailer = SendmailRepository.createMailer(config, timeout)

  override fun send(message: SendmailMessage): Throwable? = sendImpl(message, false)

  override fun asyncSend(message: SendmailMessage): Throwable? = sendImpl(message, true)

  /**
   * Wraps the sending operation in a try-catch clause, returns the [Throwable] it it was
   * thrown while sending.
   *
   * @param message the message to send
   * @param async whether to launch another thread to send the [message] or not
   */
  private fun sendImpl(message: SendmailMessage, async: Boolean): Throwable? {
    val replyTo = if (null == message.replyTo) {
      null
    } else {
      Recipient(message.replyTo.fullName, message.replyTo.email, null)
    }

    return try {
      EmailBuilder.startingBlank().apply {
        withBounceTo(message.sender)
        if (null != replyTo) {
          withReplyTo(replyTo)
        }
        withSubject(message.subject)
        from(message.from.fullName, message.from.email)
        to(message.recipient.fullName, message.recipient.email)
        withPlainText(message.textual)

        if (message.isHtml) {
          withHTMLText(message.html)
        }

        buildEmail().apply {
          mailer.sendMail(this, async)
        }
      }

      null
    } catch (@Suppress("TooGenericExceptionCaught") error: Throwable) {
      error
    }
  }
}
