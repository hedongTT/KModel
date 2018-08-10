package com.dong.library.reader.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Reader(
        /**
         * Keys of Reader
         */
        val keys: Array<String>
)