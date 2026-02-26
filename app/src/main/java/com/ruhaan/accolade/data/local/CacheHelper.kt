package com.ruhaan.accolade.data.local

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Shared Gson instance for all cache serialisation / deserialisation.
 * Kept here so the repository doesn't need to know about Gson directly.
 */
val cacheGson = Gson()

// ── Expiry check ─────────────────────────────────────────────────────────────

fun isExpired(cachedAt: Long, ttlMs: Long): Boolean =
    System.currentTimeMillis() - cachedAt > ttlMs

// ── Inline reified helpers ────────────────────────────────────────────────────

inline fun <reified T> String.fromJson(): T =
    cacheGson.fromJson(this, object : TypeToken<T>() {}.type)

inline fun <reified T> T.toJson(): String =
    cacheGson.toJson(this)

// ── Generic cache-first executor ─────────────────────────────────────────────

/**
 * Executes a network call with a stale-while-revalidate cache strategy:
 *
 *  1. Fresh cache  → return immediately, skip network.
 *  2. Stale cache  → try network; on success return fresh; on failure return stale.
 *  3. No cache     → try network; on failure rethrow.
 *
 * @param getCached   Suspend lambda to load from DB.
 * @param isFresh     Returns true if the cached entry is still within TTL.
 * @param deserialize Converts the cached JSON string to [T].
 * @param fetchRemote Suspend lambda to fetch from the network.
 * @param saveToCache Suspend lambda to persist the fresh value to DB.
 */

suspend fun <T> cachedOr(
    getCached: suspend () -> String?,
    isFresh: suspend (cachedAt: Long) -> Boolean,
    getCachedAt: suspend () -> Long?,
    deserialize: (String) -> T,
    fetchRemote: suspend () -> T,
    saveToCache: suspend (T) -> Unit,
): T {
    val cachedJson = getCached()
    val cachedAt = getCachedAt()

    // 1. Fresh cache hit → return immediately
    if (cachedJson != null && cachedAt != null && isFresh(cachedAt)) {
        return deserialize(cachedJson)
    }

    // 2 & 3. Attempt network
    return try {
        val fresh = fetchRemote()
        saveToCache(fresh)
        fresh
    } catch (e: Exception) {
        // Network failed — return stale cache if we have any, otherwise rethrow
        if (cachedJson != null) deserialize(cachedJson)
        else throw e
    }
}