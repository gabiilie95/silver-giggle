package com.ilieinc.dontsleep.util

import com.ilieinc.core.util.Logger
import kotlinx.serialization.json.Json

/**
 * Centralized, safe serializer.
 *
 */
object JsonUtils {

    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    inline fun<reified T> T.serializeToJson() = json.encodeToString(this)

    inline fun<reified T> String.deserializeFromJson(): T? {
        val jsonString = this
        if (jsonString.isBlank()) return null
        return runCatching {
            json.decodeFromString<T>(jsonString)
        }.onFailure {
            Logger.error("${T::class.simpleName}: failed to deserialize state", it)
        }.getOrNull()
    }
}