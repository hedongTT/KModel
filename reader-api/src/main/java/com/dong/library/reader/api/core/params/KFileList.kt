package com.dong.library.reader.api.core.params

import java.io.File

class KFileList constructor(val name: String, list: List<File>): ArrayList<File>() {

    constructor(name: String): this(name, listOf())

    init {
        addAll(list)
    }
}