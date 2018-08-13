package com.dong.library.reader.api.core

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    private var mApi: T? = null

    private val mRetrofit: Retrofit
        get() {
            var retrofit: Retrofit? = sRetrofit

            return if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .baseUrl(baseUrl)
                        .client(generateOkHttpClient())
                        .build()
                sRetrofit = retrofit
                retrofit
            } else {
                retrofit
            }
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

    final override fun onRequest(key: String, params: MutableMap<String, Any>, callback: KReaderCallback) {
        mApi = mApi ?: mRetrofit.create(mApiCls)
        val api = mApi ?: throw RuntimeException()
        onRequest(api, key, params, callback)
    }

    abstract fun onRequest(api: T, key: String, params: MutableMap<String, Any>, callback: KReaderCallback)

    protected fun <T> applyCall(call: Call<String>, parser: IKHttpParser<T>) {

        call.enqueue(object: Callback<String> {
            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected
             * exception occurred creating the request or processing the response.
             */
            override fun onFailure(call: Call<String>, t: Throwable) {

            }

            /**
             * Invoked for a received HTTP response.
             *
             *
             * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
             * Call [Response.isSuccessful] to determine if the response indicates success.
             */
            override fun onResponse(call: Call<String>, response: Response<String>) {

                parser.onParse(response.headers(), response.body(), {

                }, {

                })
            }
        })
    }

    companion object {

        private var sRetrofit: Retrofit? = null
    }
}