package com.dong.library.reader.api.core

import android.content.Context
import com.dong.library.reader.api.core.callback.KReaderCallback

@Suppress("MemberVisibilityCanBePrivate", "ClassName")
abstract class _KReader {

    protected lateinit var mContext: Context

    open fun init(context: Context): _KReader {
        mContext = context
        return this
    }

    protected var sCallback: KReaderCallback? = null

    internal fun request(key: String, params: HashMap<String, Any>, callback: KReaderCallback) {
        sCallback = callback
        onRequest(key, params, callback)
    }

    abstract fun onRequest(key: String, params: HashMap<String, Any>, callback: KReaderCallback)

    companion object {

        private var mReaderMap: HashMap<Class<*>, _KReader> = HashMap()

        internal fun getReader(cls: Class<*>): _KReader? {
            return mReaderMap[cls]
        }

        internal fun addReader(cls: Class<*>, reader: _KReader) {
            mReaderMap[cls] = reader
        }
    }
}