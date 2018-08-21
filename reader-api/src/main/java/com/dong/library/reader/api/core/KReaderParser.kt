package com.dong.library.reader.api.core

import okhttp3.Headers

interface IKReaderParser<T> {

    fun onParse(headers: Headers, result: String, complete: (code: Int, result: T?, any: Any?) -> Unit, error: (errorId: Int) -> Unit)
    fun onComplete(code: Int, result: T?, any: Any?)
}