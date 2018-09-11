package com.dong.library.reader.api.utils

import com.google.gson.Gson

class KStringMap: HashMap<String, String>() {

    fun put(key: String, value: Any?) {
        if (value == null) return
        when {
            (value is Int) or (value is String) or (value is Long) or (value is Short) or (value is Byte) or (value is Float) or (value is Double) or (value is Boolean) or (value is Char) -> {
                put(key, value.toString())
            }
            else -> put(key, Gson().toJson(value))
        }
    }
}