package com.dong.library.reader.api.core

@Suppress("MemberVisibilityCanBePrivate")
class KReaderResult : HashMap<String, Any>() {

    var describe: String? = null
        private set

    var code: Int = Int.MIN_VALUE
        private set

    var any: Any? = null
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

    fun withAny(any: Any) {
        this.any = any
    }

    fun withData(data: MutableMap<String, Any>) {
        for ((key, value) in data) {
            this[key] = value
        }
    }
}