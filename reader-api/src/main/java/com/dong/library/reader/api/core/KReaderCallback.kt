@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.dong.library.reader.api.core

import android.content.Context
import android.support.annotation.StringRes
import com.dong.library.reader.api.R
import okhttp3.Headers

abstract class KReaderCallback(internal val context: Context) {

    private fun getString(@StringRes resId: Int, @StringRes defaultResId: Int): String {
        return try {
            context.getString(resId)
        } catch (e: Exception) {
            context.getString(defaultResId)
        }
    }

    private fun toInvoke(callback: (data: KReaderResult) -> Unit, init: KReaderResult.() -> Unit) {
        val data = KReaderResult()
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

    fun onReadStart(data: HashMap<String, Any>) {
        toInvoke(this::onReadStart, {
            withDescribe(context.getString(R.string.k_model_on_reader_start))
            withData(data)
        })
    }

    fun onReadStart(dataInit: HashMap<String, Any>.() -> Unit) {
        toInvoke(this::onReadStart, {
            withDescribe(context.getString(R.string.k_model_on_reader_start))
            withData(dataInit)
        })
    }

    fun onReadStart(@StringRes describe: Int, dataInit: HashMap<String, Any>.() -> Unit) {
        toInvoke(this::onReadStart, {
            withDescribe(getString(describe, R.string.k_model_on_reader_start))
            withData(dataInit)
        })
    }

    fun onReadStart(@StringRes describe: Int, data: HashMap<String, Any>) {
        toInvoke(this::onReadStart, {
            withDescribe(getString(describe, R.string.k_model_on_reader_start))
            withData(data)
        })
    }

    fun onReadStart(describe: String, dataInit: HashMap<String, Any>.() -> Unit) {
        toInvoke(this::onReadStart, {
            withDescribe(describe)
            withData(dataInit)
        })
    }

    fun onReadStart(describe: String, data: HashMap<String, Any>) {
        toInvoke(this::onReadStart, {
            withDescribe(describe)
            withData(data)
        })
    }

    abstract fun onReadStart(data: KReaderResult)
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

    fun onReadIng(dataInit: HashMap<String, Any>.() -> Unit) {
        toInvoke(this::onReadIng, {
            withDescribe(context.getString(R.string.k_model_on_reader_ing))
            withData(dataInit)
        })
    }

    fun onReadIng(data: HashMap<String, Any>) {
        toInvoke(this::onReadIng, {
            withDescribe(context.getString(R.string.k_model_on_reader_ing))
            withData(data)
        })
    }

    fun onReadIng(@StringRes describe: Int, dataInit: HashMap<String, Any>.() -> Unit) {
        toInvoke(this::onReadIng, {
            withDescribe(getString(describe, R.string.k_model_on_reader_ing))
            withData(dataInit)
        })
    }

    fun onReadIng(@StringRes describe: Int, data: HashMap<String, Any>) {
        toInvoke(this::onReadIng, {
            withDescribe(getString(describe, R.string.k_model_on_reader_ing))
            withData(data)
        })
    }

    fun onReadIng(describe: String, data: HashMap<String, Any>) {
        toInvoke(this::onReadIng, {
            withDescribe(describe)
            withData(data)
        })
    }

    abstract fun onReadIng(data: KReaderResult)
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

    fun onReadComplete(result: HashMap<String, Any>) {
        toInvoke(this::onReadComplete, {
            withDescribe(context.getString(R.string.k_model_on_reader_complete))
            withData(result)
        })
    }

    fun onReadComplete(@StringRes describe: Int, dataInit: HashMap<String, Any>.() -> Unit) {
        toInvoke(this::onReadComplete, {
            withDescribe(getString(describe, R.string.k_model_on_reader_complete))
            withData(dataInit)
        })
    }

    fun onReadComplete(@StringRes describe: Int, result: HashMap<String, Any>) {
        toInvoke(this::onReadComplete, {
            withDescribe(getString(describe, R.string.k_model_on_reader_complete))
            withData(result)
        })
    }

    fun onReadComplete(describe: String, result: HashMap<String, Any>) {
        toInvoke(this::onReadComplete, {
            withDescribe(describe)
            withData(result)
        })
    }

    abstract fun onReadComplete(data: KReaderResult)

    /**
     * OnReadComplete End
     ****/

    /**
     * OnReadFailed Start
     ****/
    fun onReadFailed(describe: String) {
        toInvoke(this::onReadFailed, {
            withDescribe(describe)
        })
    }

    fun onReadFailed(init: KReaderResult.() -> Unit) {
        toInvoke(this::onReadFailed, init)
    }

    abstract fun onReadFailed(data: KReaderResult)
    /**
     * OnReadFailed End
     ****/
    /**
     * OnReadError Start
     ****/
    abstract fun onReadError(code: Int, headers: Headers)
    /**
     * OnReadError End
     ****/
}