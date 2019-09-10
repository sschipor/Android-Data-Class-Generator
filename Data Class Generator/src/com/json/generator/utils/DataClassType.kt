package com.json.generator.utils

enum class DataClassType(private val type: String) {
    MOSHI("Moshi"),
    GSON("GSON");

    override fun toString(): String {
        return type
    }

    companion object {
        fun find(item: String): DataClassType {
            for (it in values()) {
                if (it.toString() == item) {
                    return it
                }
            }
            return MOSHI
        }
    }
}