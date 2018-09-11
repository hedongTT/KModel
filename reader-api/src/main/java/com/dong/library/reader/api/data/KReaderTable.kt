@file:Suppress("unused")

package com.dong.library.reader.api.data

import com.dong.library.reader.annotations.model.InterceptorMetaData
import com.dong.library.reader.annotations.model.KReaderMetadata
import com.dong.library.reader.api.interfaces.KPathMatcher
import java.util.*

internal object KReaderTable {

    internal val readers = HashMap<String, KReaderMetadata>()
    internal val matchers: MutableList<KPathMatcher> = mutableListOf(KDefaultMatcher)
    internal val interceptors: TreeMap<Int, InterceptorMetaData> = TreeMap()

    fun clear() {
        readers.clear()
        matchers.clear()
    }
}

object KDefaultMatcher : KPathMatcher {
    override fun match(path: String, path2: String): Boolean {
        return path == path2
    }
}