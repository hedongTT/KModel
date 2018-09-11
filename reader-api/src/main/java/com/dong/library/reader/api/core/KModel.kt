@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.dong.library.reader.api.core

import android.content.Context
import com.dong.library.reader.api.core.callback.IKResultCallback
import com.dong.library.reader.api.utils.Logger

object KModel {

    fun openDebug(): Unit = Logger.openDebug()

    fun init(context: Context) {
        _KModel.init(context)
    }

    fun create(key: String): Navigator {
        return Navigator(key)
    }

    @Suppress("unused")
    class Navigator internal constructor(val key: String) {

        internal val extras: HashMap<String, Any> = HashMap()

        internal var onReadStart: IKResultCallback? = null
            private set
        internal var onReadIng: IKResultCallback? = null
            private set
        internal var onReadComplete: IKResultCallback? = null
            private set
        internal var onReadFailed: IKResultCallback? = null
            private set

        private fun createCallbackIpl(callback: (data: KReaderResult) -> Unit): IKResultCallback {

            return object : IKResultCallback {

                override fun onCallback(result: KReaderResult) {
                    callback.invoke(result)
                }
            }
        }

        fun onReadStart(callback: (data: KReaderResult) -> Unit): Navigator {
            return onReadStart(createCallbackIpl(callback))
        }

        fun onReadStart(callback: IKResultCallback): Navigator {
            this.onReadStart = callback
            return this
        }

        fun onReadIng(callback: (data: KReaderResult) -> Unit): Navigator {
            return onReadIng(createCallbackIpl(callback))
        }

        fun onReadIng(callback: IKResultCallback): Navigator {
            this.onReadIng = callback
            return this
        }

        fun onReadComplete(callback: (data: KReaderResult) -> Unit): Navigator {
            onReadComplete(createCallbackIpl(callback))
            return this
        }

        fun onReadComplete(callback: IKResultCallback): Navigator {
            this.onReadComplete = callback
            return this
        }

        fun onReadFailed(callback: (data: KReaderResult) -> Unit): Navigator {
            onReadFailed(createCallbackIpl(callback))
            return this
        }

        fun onReadFailed(callback: IKResultCallback): Navigator {
            this.onReadFailed = callback
            return this
        }

        fun withParam(key: String, any: Any?): Navigator {
            extras[key] = any ?: "null"
            return this
        }

        fun withParams(init: HashMap<String, Any>.() -> Unit): Navigator {
            extras.init()
            return this
        }

        fun withParams(params: HashMap<String, Any>): Navigator {
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

