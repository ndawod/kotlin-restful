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

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.noordawod.kotlin.restful.moshi.addBase62JsonAdapter
import org.noordawod.kotlin.restful.moshi.addEpochDateAdapter
import org.noordawod.kotlin.restful.moshi.addOffsetDateTimeAdapter

/**
 * Builds a [Moshi] instance that we'll use for all services in the backend.
 *
 * @param useSeconds whether to use seconds (true) or milliseconds (false) for epoch
 *        calculations (default is false)
 */
fun buildDefaultMoshi(useSeconds: Boolean = false): Moshi = Moshi.Builder()
  .add(KotlinJsonAdapterFactory())
  .addEpochDateAdapter(usingSeconds = useSeconds, zeroAsNull = false)
  .addOffsetDateTimeAdapter(usingSeconds = useSeconds, zeroAsNull = false)
  .addBase62JsonAdapter(rethrowOnError = false)
  .build()
