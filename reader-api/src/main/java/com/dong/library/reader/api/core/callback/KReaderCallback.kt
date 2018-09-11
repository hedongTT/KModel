@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.dong.library.reader.api.core.callback

import android.content.Context
import android.support.annotation.StringRes
import com.dong.library.reader.api.R
import com.dong.library.reader.api.core.KReaderResult

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
    fun onStart() {
        onStart(R.string.k_model_on_reader_start)
    }

    fun onStart(@StringRes describe: Int) {
        toInvoke(this::onStart) {
            withDescribe(getString(describe, R.string.k_model_on_reader_start))
        }
    }

    fun onStart(describe: String) {
        toInvoke(this::onStart) {
            withDescribe(describe)
        }
    }

    fun onStart(data: HashMap<String, Any>) {
        toInvoke(this::onStart) {
            withDescribe(context.getString(R.string.k_model_on_reader_start))
            withData(data)
        }
    }

    fun onStart(dataInit: HashMap<String, Any>.() -> Unit) {
        toInvoke(this::onStart) {
            withDescribe(context.getString(R.string.k_model_on_reader_start))
            withData(dataInit)
        }
    }

    fun onStart(@StringRes describe: Int, dataInit: HashMap<String, Any>.() -> Unit) {
        toInvoke(this::onStart) {
            withDescribe(getString(describe, R.string.k_model_on_reader_start))
            withData(dataInit)
        }
    }

    fun onStart(@StringRes describe: Int, data: HashMap<String, Any>) {
        toInvoke(this::onStart) {
            withDescribe(getString(describe, R.string.k_model_on_reader_start))
            withData(data)
        }
    }

    fun onStart(describe: String, dataInit: HashMap<String, Any>.() -> Unit) {
        toInvoke(this::onStart) {
            withDescribe(describe)
            withData(dataInit)
        }
    }

    fun onStart(describe: String, data: HashMap<String, Any>) {
        toInvoke(this::onStart) {
            withDescribe(describe)
            withData(data)
        }
    }

    abstract fun onStart(data: KReaderResult)
    /**
     * OnReadStart End
     ****/

    /**
     * OnReadIng Start
     ****/
    fun onProgress() {
        onProgress(R.string.k_model_on_reader_ing)
    }

    fun onProgress(@StringRes describe: Int) {
        toInvoke(this::onProgress) {
            withDescribe(getString(describe, R.string.k_model_on_reader_ing))
        }
    }

    fun onProgress(describe: String) {
        toInvoke(this::onProgress) {
            withDescribe(describe)
        }
    }

    fun onProgress(dataInit: HashMap<String, Any>.() -> Unit) {
        toInvoke(this::onProgress) {
            withDescribe(context.getString(R.string.k_model_on_reader_ing))
            withData(dataInit)
        }
    }

    fun onProgress(data: HashMap<String, Any>) {
        toInvoke(this::onProgress) {
            withDescribe(context.getString(R.string.k_model_on_reader_ing))
            withData(data)
        }
    }

    fun onProgress(@StringRes describe: Int, dataInit: HashMap<String, Any>.() -> Unit) {
        toInvoke(this::onProgress) {
            withDescribe(getString(describe, R.string.k_model_on_reader_ing))
            withData(dataInit)
        }
    }

    fun onProgress(@StringRes describe: Int, data: HashMap<String, Any>) {
        toInvoke(this::onProgress) {
            withDescribe(getString(describe, R.string.k_model_on_reader_ing))
            withData(data)
        }
    }

    fun onProgress(describe: String, data: HashMap<String, Any>) {
        toInvoke(this::onProgress) {
            withDescribe(describe)
            withData(data)
        }
    }

    abstract fun onProgress(data: KReaderResult)
    /**
     * OnReadIng End
     ****/

    /**
     * OnReadComplete Start
     ****/
    fun onComplete() {
        onComplete(R.string.k_model_on_reader_complete)
    }

    fun onComplete(@StringRes describe: Int) {
        toInvoke(this::onComplete) {
            withDescribe(getString(describe, R.string.k_model_on_reader_complete))
        }
    }

    fun onComplete(describe: String) {
        toInvoke(this::onComplete) {
            withDescribe(describe)
        }
    }

    fun onComplete(dataInit: HashMap<String, Any>.() -> Unit) {
        toInvoke(this::onComplete) {
            withDescribe(context.getString(R.string.k_model_on_reader_complete))
            withData(dataInit)
        }
    }

    fun onComplete(result: HashMap<String, Any>) {
        toInvoke(this::onComplete) {
            withDescribe(context.getString(R.string.k_model_on_reader_complete))
            withData(result)
        }
    }

    fun onComplete(@StringRes describe: Int, dataInit: HashMap<String, Any>.() -> Unit) {
        toInvoke(this::onComplete) {
            withDescribe(getString(describe, R.string.k_model_on_reader_complete))
            withData(dataInit)
        }
    }

    fun onComplete(@StringRes describe: Int, result: HashMap<String, Any>) {
        toInvoke(this::onComplete) {
            withDescribe(getString(describe, R.string.k_model_on_reader_complete))
            withData(result)
        }
    }

    fun onComplete(describe: String, result: HashMap<String, Any>) {
        toInvoke(this::onComplete) {
            withDescribe(describe)
            withData(result)
        }
    }

    abstract fun onComplete(data: KReaderResult)

    /**
     * OnReadComplete End
     ****/

    /**
     * OnReadFailed Start
     ****/
    fun onFailed(code: Int) {
        toInvoke(this::onFailed) {
            withCode(code)
        }
    }

    fun onFailed(code: Int, describe: Int) {
        toInvoke(this::onFailed) {
            withCode(code)
            withDescribe(getString(describe, R.string.k_model_on_reader_failed))
        }
    }

    fun onFailed(describe: String) {
        toInvoke(this::onFailed) {
            withDescribe(describe)
        }
    }

    fun onFailed(code: Int, describe: String) {
        toInvoke(this::onFailed) {
            withCode(code)
            withDescribe(describe)
        }
    }

    fun onFailed(init: KReaderResult.() -> Unit) {
        toInvoke(this::onFailed, init)
    }

    abstract fun onFailed(data: KReaderResult)
    /**
     * OnReadFailed End
     ****/
}