package com.dong.library.reader.annotations.model

/**
 * 类型
 */
enum class KReaderType(val className: String) {
    UNKNOWN(""),
    READER("com.dong.library.reader.api.core._KReader"),
    READER_SERVICE("com.dong.library.reader.api.core.IKReaderApi")
}

/**
 * Description：Reader元数据，用于存储被[com.dong.library.reader.api.core.KReader]注解的类的信息
 */
data class KReaderMetadata(
        /**
         * Type of Reader
         */
        val readerType: KReaderType = KReaderType.UNKNOWN,
        /**
         * Key of Reader
         */
        val key: String = "",
        /**
         * Name of Reader
         */
        val name: String = "undefine",
        /**
         * Class of Reader
         */
        val readerCls: Class<*> = Any::class.java)

/**
 * Description：Interceptor元数据，用于存储被[com.dong.library.reader.annotations.Interceptor]注解的类的信息
 */
data class InterceptorMetaData(
        /**
         * Priority of Interceptor
         */
        val priority: Int = -1,
        /**
         * Name of Interceptor
         */
        val name: String = "undefine",
        /**
         * Class desc of Interceptor
         */
        val clazz: Class<*> = Any::class.java)