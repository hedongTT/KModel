package com.dong.library.reader.api.core

@Suppress("MemberVisibilityCanBePrivate")
class KReaderRequest : HashMap<String, Any>() {

    var describe: String? = null
        private set

    var code: Int = Int.MIN_VALUE
        private set

    fun withDescribe(describe: String) {
        this.describe = describe
    }

    fun withCode(code: Int) {
        this.code = code
    }

    fun withData(init: MutableMap<String, Any>.() -> Unit) {
        this.init()
    }

    fun withData(data: MutableMap<String, Any>) {
        for ((key, value) in data) {
            this[key] = value
        }
    }
}