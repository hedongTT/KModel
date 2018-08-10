package com.dong.library.reader.api.core

import android.content.Context

object KModel {

    fun init(context: Context) {
        _KModel.init(context)
    }

    fun create(key: String): Navigator {
        return Navigator(key)
    }

    @Suppress("unused")
    class Navigator internal constructor(val key: String) {

        val extras: MutableMap<String, Any> = mutableMapOf()

        var onReadStart: ((data: KReader.RequestData) -> Unit)? = null
            private set
        var onReadIng: ((data: KReader.RequestData) -> Unit)? = null
            private set
        var onReadComplete: ((data: KReader.RequestData) -> Unit)? = null
            private set
        var onReadFailed: ((data: KReader.RequestData) -> Unit)? = null
            private set

        var onProcessBefore: ((navigator: Navigator, className: String) -> Unit)? = null
            private set
        var onProcessNotFound: ((navigator: Navigator, className: String) -> Unit)? = null
            private set
        var onProcessArrived: ((navigator: Navigator, className: String) -> Unit)? = null
            private set
        var onProcessIntercept: ((navigator: Navigator, className: String) -> Unit)? = null
            private set
        var onProcessError: ((navigator: Navigator, className: String) -> Unit)? = null
            private set

        fun onReadStart(callback: (data: KReader.RequestData) -> Unit): Navigator {
            this.onReadStart = callback
            return this
        }

        fun onReadIng(callback: (data: KReader.RequestData) -> Unit): Navigator {
            this.onReadIng = callback
            return this
        }

        fun onReadComplete(callback: (data: KReader.RequestData) -> Unit): Navigator {
            this.onReadComplete = callback
            return this
        }

        fun onReadFailed(callback: (data: KReader.RequestData) -> Unit): Navigator {
            this.onReadFailed = callback
            return this
        }

        fun onProcessBefore(callback: (navigator: Navigator, className: String) -> Unit): Navigator {
            this.onProcessBefore = callback
            return this
        }

        fun onProcessNotFound(callback: (navigator: Navigator, className: String) -> Unit): Navigator {
            this.onProcessNotFound = callback
            return this
        }

        fun onProcessArrived(callback: (navigator: Navigator, className: String) -> Unit): Navigator {
            this.onProcessArrived = callback
            return this
        }

        fun onProcessIntercept(callback: (navigator: Navigator, className: String) -> Unit): Navigator {
            this.onProcessIntercept = callback
            return this
        }

        fun onProcessError(callback: (navigator: Navigator, className: String) -> Unit): Navigator {
            this.onProcessError = callback
            return this
        }

        fun withParam(key: String, any: Any?): Navigator {
            extras[key] = any ?: "null"
            return this
        }

        fun withParams(init: MutableMap<String, Any>.() -> Unit): Navigator {
            extras.init()
            return this
        }

        fun withParams(params: MutableMap<String, Any>): Navigator {
            for ((key, value) in params) {
                extras[key] = value
            }
            return this
        }

        fun request() {
            _KModel.getInstance().request(this)
        }
    }
}

