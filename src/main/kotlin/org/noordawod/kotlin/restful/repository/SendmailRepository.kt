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

package org.noordawod.kotlin.restful.repository

import com.sanctionco.jmail.JMail
import org.noordawod.kotlin.core.config.SmtpConfiguration
import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.mailer.MailerBuilder

/**
 * The contract that describes few methods to send an [email message][SendmailMessage]
 * from [one person][SendmailPerson] to [another][SendmailPerson].
 */
interface SendmailRepository {
  /**
   * Sends an email [message] synchronously. This means that client waits until the message
   * is sent out, which will obviously block the current thread.
   *
   * @param message details of the message like sender, recipient, subject line, etc.
   */
  fun send(message: SendmailMessage): Throwable?

  /**
   * Sends an email [message] asynchronously. This means that client does not wait until the
   * message is sent out as a new thread is spun to do the work.
   *
   * @param message details of the message like sender, recipient, subject line, etc.
   */
  fun asyncSend(message: SendmailMessage): Throwable?

  companion object {
    /**
     * Creates a new [Mailer] instance based on the provided parameters.
     *
     * @param timeout the timeout, in milliseconds, before giving up send
     * @param config the [SmtpConfiguration] instance to use
     */
    fun createMailer(
      config: SmtpConfiguration,
      timeout: Long
    ): Mailer = MailerBuilder
      .withSMTPServer(config.host, config.port, config.auth?.user, config.auth?.pass)
      .withTransportStrategy(TransportStrategy.SMTP)
      .clearEmailValidator()
      .withEmailValidator(
        JMail.validator()
          .disallowIpDomain()
          .requireTopLevelDomain()
          .disallowObsoleteWhitespace()
          .disallowReservedDomains()
          .disallowExplicitSourceRouting()
      )
      .withDebugLogging(true == config.logging)
      .withSessionTimeout(timeout.toInt())
      .buildMailer()
  }
}

/**
 * Identifies a single person to send or receive email messages.
 *
 * @param email the person's email address
 * @param firstName the first name of a user
 * @param lastName the last name of a user
 * @param fullName the full name of a user
 */
data class SendmailPerson(
  val email: String,
  val firstName: String?,
  val lastName: String?,
  val fullName: String?
)

/**
 * A model representing a message to be sent out from [one person][from] to
 * [another][recipient].
 *
 * There is no guarantee that the [recipient] will actually receive the email message,
 * this depends on many factors such as SMTP delivery, anti-spam measures, etc.
 *
 * @param sender the person to receive bounced/undelivered emails
 * @param from the person appearing in the "From:" message address
 * @param replyTo the person to receive replies
 * @param recipient the person to receive the [html]
 * @param subject the Subject: line in the email
 * @param html the HTML message to send out
 * @param textual the plain text message to send out
 * @param isHtml whether the [html] is HTML (true) or text-only (false)
 */
@Suppress("LongParameterList")
open class SendmailMessage(
  val sender: String,
  val from: SendmailPerson,
  val replyTo: SendmailPerson?,
  val recipient: SendmailPerson,
  val subject: String,
  val html: String,
  val textual: String,
  val isHtml: Boolean = true
)
