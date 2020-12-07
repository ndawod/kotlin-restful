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

package org.noordawod.kotlin.auth

/**
 * Defines a contract to maintain a list of [resources][Resource] and make it easy
 * to access and query.
 */
interface ResourceRepository {
  /**
   * Define all available [resources][Resource] in the app. This is usually called once
   * when the app is starting up.
   *
   * It’s not possible to clear the list of resources, but only to re-define them again. So
   * calling this method a second time would clear the previous list and define a totally new
   * one, as specified.
   *
   * @param resources list of [resources][Resource] to define
   */
  fun define(resources: Collection<Resource>)

  /**
   * Define all available [resources][Resource] in the app. This is usually called once
   * when the app is starting up.
   *
   * It’s not possible to clear the list of resources, but only to re-define them again. So
   * calling this method a second time would clear the previous list and define a totally new
   * one, as specified.
   *
   * @param resources list of [resources][Resource] to define
   */
  fun define(resources: Array<out Resource>)

  /**
   * Returns true if the specified [resource] is defined, false otherwise.
   *
   * @param resource the resource to check
   */
  fun isDefined(resource: Resource): Boolean

  /**
   * Returns true if a resource identified by its [resourceId] is defined, false otherwise.
   *
   * @param resourceId the unique resource identifier to check
   */
  fun isDefined(resourceId: ResourceId): Boolean

  /**
   * Returns a copy of all defined [resources][Resource].
   */
  fun getAll(): Resources

  /**
   * Retrieves a resource by its unique [identifier][resourceId]. If the resource isn’t found,
   * an Exception is thrown.
   *
   * @param resourceId the unique resource identifier to retrieve
   */
  fun get(resourceId: ResourceId): Resource

  /**
   * Retrieves a resource by its unique [identifier][resourceId], null if not found.
   *
   * @param resourceId the unique resource identifier to retrieve
   */
  fun getOrNull(resourceId: ResourceId): Resource?

  /**
   * Checks if [part1] and [part2] are both a [Resource] and their
   * [unique identifiers][Resource.identifier] are equal.
   *
   * @param part1 first element to check for equality
   * @param part2 second element to check for equality
   */
  fun equals(part1: Any, part2: Any): Boolean
}

/**
 * A [repository][ResourceRepository] implementation to deal with [resources][Resource].
 *
 * This uses an in-memory [Map] to store and access all resources.
 */
class ResourceRepositoryImpl : ResourceRepository {
  private val cache = java.util.concurrent.ConcurrentHashMap<ResourceId, Resource>(
    CachingAuthorizationRepository.DEFAULT_ENTRIES
  )

  override fun define(resources: Collection<Resource>) {
    cache.clear()
    resources.forEach {
      cache[it.identifier] = it
    }
  }

  override fun define(resources: Array<out Resource>) {
    cache.clear()
    resources.forEach {
      cache[it.identifier] = it
    }
  }

  override fun isDefined(resource: Resource): Boolean = isDefined(resource.identifier)

  override fun isDefined(resourceId: ResourceId): Boolean = cache.containsKey(resourceId)

  override fun getAll(): Resources =
    LinkedHashSet<Resource>(cache.size).apply {
      cache.forEach { (_, resource) ->
        add(resource)
      }
    }

  override fun get(resourceId: ResourceId): Resource =
    checkNotNull(getOrNull(resourceId)) { "Resource $resourceId is invalid." }

  override fun getOrNull(resourceId: ResourceId): Resource? = cache[resourceId]

  override fun equals(part1: Any, part2: Any): Boolean =
    part1 is Resource && part2 is Resource && part1.identifier == part2.identifier
}
