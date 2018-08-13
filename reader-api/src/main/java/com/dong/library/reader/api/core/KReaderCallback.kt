package com.dong.library.reader.api.core

import android.content.Context
import android.support.annotation.StringRes
import com.dong.library.reader.api.R

abstract class KReaderCallback(internal val context: Context) {

    private fun getString(@StringRes resId: Int, @StringRes defaultResId: Int): String {
        return try {
            context.getString(resId)
        } catch (e: Exception) {
            context.getString(defaultResId)
        }
    }

    private fun toInvoke(callback: (data: KReaderRequest) -> Unit, init: KReaderRequest.() -> Unit) {
        val data = KReaderRequest()
        data.init()
        callback.invoke(data)
    }

    /**
     * OnReadStart Start
     ****/
    fun onReadStart() {
        onReadStart(R.string.k_model_on_reader_start)
    }

    fun onReadStart(@StringRes describe: Int) {
        toInvoke(this::onReadStart, {
            withDescribe(getString(describe, R.string.k_model_on_reader_start))
        })
    }

    fun onReadStart(describe: String) {
        toInvoke(this::onReadStart, {
            withDescribe(describe)
        })
    }

    fun onReadStart(data: MutableMap<String, Any>) {
        toInvoke(this::onReadStart, {
            withDescribe(context.getString(R.string.k_model_on_reader_start))
            withData(data)
        })
    }

    fun onReadStart(dataInit: MutableMap<String, Any>.() -> Unit) {
        toInvoke(this::onReadStart, {
            withDescribe(context.getString(R.string.k_model_on_reader_start))
            withData(dataInit)
        })
    }

    fun onReadStart(@StringRes describe: Int, dataInit: MutableMap<String, Any>.() -> Unit) {
        toInvoke(this::onReadStart, {
            withDescribe(getString(describe, R.string.k_model_on_reader_start))
            withData(dataInit)
        })
    }

    fun onReadStart(@StringRes describe: Int, data: MutableMap<String, Any>) {
        toInvoke(this::onReadStart, {
            withDescribe(getString(describe, R.string.k_model_on_reader_start))
            withData(data)
        })
    }

    fun onReadStart(describe: String, dataInit: MutableMap<String, Any>.() -> Unit) {
        toInvoke(this::onReadStart, {
            withDescribe(describe)
            withData(dataInit)
        })
    }

    fun onReadStart(describe: String, data: MutableMap<String, Any>) {
        toInvoke(this::onReadStart, {
            withDescribe(describe)
            withData(data)
        })
    }

    abstract fun onReadStart(data: KReaderRequest)
    /**
     * OnReadStart End
     ****/

    /**
     * OnReadIng Start
     ****/
    fun onReadIng() {
        onReadIng(R.string.k_model_on_reader_ing)
    }

    fun onReadIng(@StringRes describe: Int) {
        toInvoke(this::onReadIng, {
            withDescribe(getString(describe, R.string.k_model_on_reader_ing))
        })
    }

    fun onReadIng(describe: String) {
        toInvoke(this::onReadIng, {
            withDescribe(describe)
        })
    }

    fun onReadIng(dataInit: MutableMap<String, Any>.() -> Unit) {
        toInvoke(this::onReadIng, {
            withDescribe(context.getString(R.string.k_model_on_reader_ing))
            withData(dataInit)
        })
    }

    fun onReadIng(data: MutableMap<String, Any>) {
        toInvoke(this::onReadIng, {
            withDescribe(context.getString(R.string.k_model_on_reader_ing))
            withData(data)
        })
    }

    fun onReadIng(@StringRes describe: Int, dataInit: MutableMap<String, Any>.() -> Unit) {
        toInvoke(this::onReadIng, {
            withDescribe(getString(describe, R.string.k_model_on_reader_ing))
            withData(dataInit)
        })
    }

    fun onReadIng(@StringRes describe: Int, data: MutableMap<String, Any>) {
        toInvoke(this::onReadIng, {
            withDescribe(getString(describe, R.string.k_model_on_reader_ing))
            withData(data)
        })
    }

    fun onReadIng(describe: String, data: MutableMap<String, Any>) {
        toInvoke(this::onReadIng, {
            withDescribe(describe)
            withData(data)
        })
    }

    abstract fun onReadIng(data: KReaderRequest)
    /**
     * OnReadIng End
     ****/

    /**
     * OnReadComplete Start
     ****/
    fun onReadComplete() {
        onReadComplete(R.string.k_model_on_reader_complete)
    }

    fun onReadComplete(@StringRes describe: Int) {
        toInvoke(this::onReadComplete, {
            withDescribe(getString(describe, R.string.k_model_on_reader_complete))
        })
    }

    fun onReadComplete(describe: String) {
        toInvoke(this::onReadComplete, {
            withDescribe(describe)
        })
    }

    fun onReadComplete(dataInit: MutableMap<String, Any>.() -> Unit) {
        toInvoke(this::onReadComplete, {
            withDescribe(context.getString(R.string.k_model_on_reader_complete))
            withData(dataInit)
        })
    }

    fun onReadComplete(result: MutableMap<String, Any>) {
        toInvoke(this::onReadComplete, {
            withDescribe(context.getString(R.string.k_model_on_reader_complete))
            withData(result)
        })
    }

    fun onReadComplete(@StringRes describe: Int, dataInit: MutableMap<String, Any>.() -> Unit) {
        toInvoke(this::onReadComplete, {
            withDescribe(getString(describe, R.string.k_model_on_reader_complete))
            withData(dataInit)
        })
    }

    fun onReadComplete(@StringRes describe: Int, result: MutableMap<String, Any>) {
        toInvoke(this::onReadComplete, {
            withDescribe(getString(describe, R.string.k_model_on_reader_complete))
            withData(result)
        })
    }

    fun onReadComplete(describe: String, result: MutableMap<String, Any>) {
        toInvoke(this::onReadComplete, {
            withDescribe(describe)
            withData(result)
        })
    }

    abstract fun onReadComplete(data: KReaderRequest)

    /**
     * OnReadComplete End
     ****/

    /**
     * OnReadFailed Start
     ****/
    fun onReadFailed(code: Int, describe: String) {
        toInvoke(this::onReadFailed, {
            withCode(code)
            withDescribe(describe)
        })
    }

    fun onReadFailed(init: KReaderRequest.() -> Unit) {
        toInvoke(this::onReadFailed, init)
    }

    abstract fun onReadFailed(data: KReaderRequest)
    /**
     * OnReadFailed End
     ****/
}