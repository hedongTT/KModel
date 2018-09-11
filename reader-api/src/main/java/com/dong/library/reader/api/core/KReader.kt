@file:Suppress("unused")

package com.dong.library.reader.api.core

import android.content.Context
import android.support.annotation.CallSuper
import com.dong.library.reader.api.core.callback.KReaderCallback
import com.dong.library.reader.api.core.parser.IKReaderParser
import com.dong.library.reader.api.utils.Logger
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.reflect.ParameterizedType

@Suppress("MemberVisibilityCanBePrivate", "ClassName")
abstract class _KReader {

    protected lateinit var mContext: Context

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

    internal fun request(key: String, params: HashMap<String, Any>, callback: KReaderCallback) {
        sCallback = callback
        onRequest(key, params, callback)
    }

    abstract fun onRequest(key: String, params: HashMap<String, Any>, callback: KReaderCallback)

    companion object {

        private var mReaderMap: HashMap<Class<*>, _KReader> = HashMap()

        internal fun getReader(cls: Class<*>): _KReader? {
            return mReaderMap[cls]
        }

        internal fun addReader(cls: Class<*>, reader: _KReader) {
            mReaderMap[cls] = reader
        }
    }
}

abstract class KReader<in T> : _KReader() {

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

    private fun getApi(params: HashMap<String, Any>): T {
        val retrofit: Retrofit = getRetrofit(params)

        mApi = mApi ?: retrofit.create(mApiCls)

        return mApi ?: throw RuntimeException()
    }

    private fun getRetrofit(params: HashMap<String, Any>): Retrofit {

        val client: OkHttpClient = getOkHttpClient(params)

        sRetrofit = sRetrofit ?: Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .client(client)
                .build()

        return sRetrofit ?: throw RuntimeException()
    }

    private fun getOkHttpClient(params: HashMap<String, Any>): OkHttpClient {
        mClient = mClient ?: OkHttpClient.Builder().addInterceptor { chain ->
            return@addInterceptor chain.proceed(onHttpInterceptor(chain.request(), params))
        }.build()

        return mClient ?: throw RuntimeException()
    }

    protected open fun onHttpInterceptor(request: Request, params: HashMap<String, Any>): Request {
        return request
    }

    final override fun onRequest(key: String, params: HashMap<String, Any>, callback: KReaderCallback) {
        sCallback = callback
        val api: T = getApi(params)
        onRequest(api, key, params, callback)
    }

    abstract fun onRequest(api: T, key: String, params: HashMap<String, Any>, callback: KReaderCallback)

    @CallSuper
    protected open fun <Type> applyCall(describe: Int, call: Call<String>, parser: IKReaderParser<Type>) {

        sCallback?.onStart(describe)

        Logger.d("applyCall describe=${mContext.getString(describe)}")

        call.enqueue(object : Callback<String> {
            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected
             * exception occurred creating the request or processing the response.
             */
            override fun onFailure(call: Call<String>, t: Throwable) {
                Logger.d("applyCall onFailure")
                sCallback?.onFailed(t.message ?: "net failed")
            }

            /**
             * Invoked for a received HTTP response.
             *
             *
             * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
             * Call [Response.isSuccessful] to determine if the response indicates success.
             */
            override fun onResponse(call: Call<String>, response: Response<String>) {
                doAsync {
                    val code: Int = response.code()
                    Logger.d("applyCall onResponse code=$code")
                    val headers: Headers = response.headers()
                    when (code) {
                        200 -> {
                            val body: String = response.body()

                            parser.onParse(headers, body, { c: Int, result: Type?, any: Any? ->
                                Logger.d("applyCall onResponse parse complete")
                                uiThread {
                                    parser.onComplete(KRequestInfo(c, any), result)
                                }
                            }, { errorCode: Int, describe: Int ->
                                Logger.d("applyCall onResponse parse error code=$errorCode")
                                uiThread {
                                    sCallback?.onFailed(errorCode, describe)
                                }
                            })
                        }

                        206 -> {
                            // download?
                            Logger.d("applyCall onResponse 206, try to download")
                            onDownload(headers, response.errorBody())
                        }
                        else -> {
                            Logger.d("applyCall onResponse unKnow code: $code")
                            uiThread {
                                sCallback?.onFailed(code)
                            }
                        }
                    }
                }
            }
        })
    }

    protected open fun onDownload(headers: Headers, body: ResponseBody) {}

    companion object {

        private var sRetrofit: Retrofit? = null
    }
}