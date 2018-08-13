package com.dong.library.reader.api.core

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.reflect.ParameterizedType

@Suppress("MemberVisibilityCanBePrivate", "ClassName")
abstract class _KReader {

    private lateinit var mContext: Context

    init {
        @Suppress("LeakingThis")
        if (this !is KReader<*>) {
            throw RuntimeException("Can't directly inherit from [_KReader], You need to extends [KReader<T>]")
        }
    }

    open fun init(context: Context): _KReader {
        mContext = context
        return this
    }

    protected var sCallback: KReaderCallback? = null

    internal fun request(key: String, params: MutableMap<String, Any>, callback: KReaderCallback) {
        sCallback = callback
        onRequest(key, params, callback)
    }

    abstract fun onRequest(key: String, params: MutableMap<String, Any>, callback: KReaderCallback)

    companion object {

        private var mReaderMap: MutableMap<Class<*>, _KReader> = mutableMapOf()

        internal fun getReader(cls: Class<*>): _KReader? {
            return mReaderMap[cls]
        }

        internal fun addReader(cls: Class<*>, reader: _KReader) {
            mReaderMap[cls] = reader
        }
    }
}

abstract class KReader<T> : _KReader() {

    abstract val baseUrl: String

    private var mClient: OkHttpClient? = null

    protected open fun generateRetrofit(): Retrofit {

        return Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(baseUrl)
                .client(generateOkHttpClient())
                .build()
    }

    private fun generateOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original: Request = chain.request()
                val builder: Request.Builder = original.newBuilder()
                onHttpInterceptor(builder)
                return@addInterceptor chain.proceed(builder.build())
            }
            .build()

    protected open fun onHttpInterceptor(builder: Request.Builder) {

    }

    private val mApiCls: Class<T>
        get() {
            val type: ParameterizedType = javaClass.genericSuperclass as ParameterizedType
            @Suppress("UNCHECKED_CAST")
            val cls: Class<T> = type.actualTypeArguments[0] as? Class<T> ?: throw RuntimeException()
            if (!cls.isInterface) {
                throw IllegalArgumentException("API declarations must be interfaces.")
            }
            return cls
        }

    companion object {

        private var sRetrofit: Retrofit? = null
    }
}