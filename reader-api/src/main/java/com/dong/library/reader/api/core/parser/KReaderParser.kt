package com.dong.library.reader.api.core.parser

import com.dong.library.reader.api.core.KRequestInfo
import okhttp3.Headers

interface IKReaderParser<T> {

    fun onParse(headers: Headers, result: String, complete: (code: Int, result: T?, any: Any?) -> Unit, error: (code: Int, describe: Int) -> Unit)
    fun onComplete(info: KRequestInfo, result: T?)
}