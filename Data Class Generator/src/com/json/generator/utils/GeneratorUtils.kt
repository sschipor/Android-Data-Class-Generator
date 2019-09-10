package com.json.generator.utils

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.lang.Exception

class GeneratorUtils {

    companion object {
        private val instance = GeneratorUtils()
        @JvmStatic
        fun getInstance(): GeneratorUtils {
            return instance
        }
    }

    fun isJsonObject(input: String): Boolean {
        return try {
            Gson().fromJson<JsonObject>(input, JsonObject::class.java)
            true
        } catch (ex: Exception) {
            false
        }
    }

    fun isJsonArray(input: String): Boolean {
        return try {
            Gson().fromJson<JsonArray>(input, JsonArray::class.java)
            true
        } catch (ex: Exception) {
            false
        }
    }
}