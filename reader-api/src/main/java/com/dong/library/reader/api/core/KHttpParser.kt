package com.dong.library.reader.api.core

import okhttp3.Headers

interface IKHttpParser<T> {

    fun onParse(headers: Headers, result: String, complete: (result: T?) -> Unit, error: (errorId: Int) -> Unit)
    fun onComplete(result: T?)
}