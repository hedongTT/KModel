package com.dong.library.reader.api.core.callback

import com.dong.library.reader.api.core.KReaderResult

interface IKResultCallback {

    fun onCallback(result: KReaderResult)
}