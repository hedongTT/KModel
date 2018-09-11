@file:Suppress("unused")

package com.dong.library.reader.api.utils

import android.util.Log

/**
 */
class Logger {

    companion object {
        private const val PREFIX = "[KReader]::"
        private var isDebug = false

        internal fun openDebug() {
            isDebug = true
        }

        private fun String.wrap(): String {
            return ">>>   " + this + "   <<<"
        }

        fun d(msg: String, throwable: Throwable? = null) {
            if (isDebug) {
                Log.d(PREFIX, msg.wrap(), throwable)
            }
        }

        fun i(msg: String, throwable: Throwable? = null) {
            if (isDebug) {
                Log.i(PREFIX, msg.wrap(), throwable)
            }
        }

        fun w(msg: String, throwable: Throwable? = null) {
            Log.w(PREFIX, msg.wrap(), throwable)
        }

        fun wtf(msg: String, throwable: Throwable? = null) {
            Log.wtf(PREFIX, msg.wrap(), throwable)
        }

        fun e(msg: String, throwable: Throwable? = null) {
            Log.e(PREFIX, msg.wrap(), throwable)
        }
    }
}