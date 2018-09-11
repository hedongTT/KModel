@file:Suppress("unused")

package com.dong.library.reader.api.core

import android.support.annotation.CallSuper
import com.dong.library.reader.api.R
import com.dong.library.reader.api.core.callback.KReaderCallback
import com.dong.library.reader.api.core.enums.KReaderMethod
import com.dong.library.reader.api.core.params.KFileList
import com.dong.library.reader.api.core.parser.IKReaderParser
import com.dong.library.reader.api.utils.KStringMap
import com.dong.library.reader.api.utils.Logger
import okhttp3.*
import okhttp3.Headers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.lang.reflect.ParameterizedType

abstract class KReader<in Api> : _KReader() {

    abstract val baseUrl: String

    private var mClient: OkHttpClient? = null

    private val mApiCls: Class<Api>
        get() {
            val type: ParameterizedType = javaClass.genericSuperclass as ParameterizedType
            @Suppress("UNCHECKED_CAST")
            val cls: Class<Api> = type.actualTypeArguments[0] as? Class<Api>
                    ?: throw RuntimeException()
            if (!cls.isInterface) {
                throw IllegalArgumentException("API declarations must be interfaces.")
            }
            return cls
        }

    private var mApi: Api? = null

    private fun getApi(params: HashMap<String, Any>): Api {
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
        val api: Api = getApi(params)
        onRequest(api, key, params, callback)
    }

    abstract fun onRequest(api: Api, key: String, params: HashMap<String, Any>, callback: KReaderCallback)

    @CallSuper
    protected open fun <Type> applyCall(describe: Int, call: Call<String>, parser: IKReaderParser<Type>) {

        sCallback?.onStart(describe)

        Logger.d("applyCall describe=${mContext.getString(describe)}")

        call.enqueue(object : Callback<String> {

            override fun onFailure(call: Call<String>, t: Throwable) {
                Logger.d("applyCall onFailure")
                sCallback?.onFailed(t.message ?: "net failed")
            }

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

interface KDelApi {

    @GET
    fun get(@Url url: String, @Body body: RequestBody): Call<String>

    @POST
    fun post(@Url url: String, @Body body: RequestBody): Call<String>

    @PUT
    fun put(@Url url: String, @Body body: RequestBody): Call<String>

    @PATCH
    fun patch(@Url url: String, @Body body: RequestBody): Call<String>

    @DELETE
    fun delete(@Url url: String, @Body body: RequestBody): Call<String>
}

abstract class KDelReader : _KReader() {

    abstract val baseUrl: String

    private var mClient: OkHttpClient? = null

    private val mApiCls: Class<KDelApi> = KDelApi::class.java

    private var mApi: KDelApi? = null

    private fun getApi(params: HashMap<String, Any>): KDelApi {
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
        val api: KDelApi = getApi(params)

        //onRequest(key, params, callback)

        onRequest(object : Helper {

            override fun <T> get(url: String, params: HashMap<String, String>, files: KFileList?, parser: IKReaderParser<T>) {
                requestMethod(KReaderMethod.GET, api, url, params, files, parser)
            }

            override fun <T> put(url: String, params: HashMap<String, String>, files: KFileList?, parser: IKReaderParser<T>) {
                requestMethod(KReaderMethod.PUT, api, url, params, files, parser)
            }

            override fun <T> post(url: String, params: HashMap<String, String>, files: KFileList?, parser: IKReaderParser<T>) {
                requestMethod(KReaderMethod.POST, api, url, params, files, parser)
            }

            override fun <T> patch(url: String, params: HashMap<String, String>, files: KFileList?, parser: IKReaderParser<T>) {
                requestMethod(KReaderMethod.PATCH, api, url, params, files, parser)
            }

            override fun <T> delete(url: String, params: HashMap<String, String>, files: KFileList?, parser: IKReaderParser<T>) {
                requestMethod(KReaderMethod.DELETE, api, url, params, files, parser)
            }

        }, key, params)
    }

    protected open fun <T> requestMethod(method: KReaderMethod, api: KDelApi, url: String, params: HashMap<String, String>, files: KFileList?, parser: IKReaderParser<T>) {

        val callFunc = when (method) {
            KReaderMethod.GET -> api::get
            KReaderMethod.PUT -> api::put
            KReaderMethod.POST -> api::post
            KReaderMethod.PATCH -> api::patch
            KReaderMethod.DELETE -> api::delete
        }

        applyCall(R.string.k_model_on_reader_start, callFunc.invoke(url, createBody(params, files)), parser)
    }

    protected open fun createBody(params: HashMap<String, String>, files: KFileList? = null): RequestBody {

        if (files == null || files.isEmpty()) {
            val builder = FormBody.Builder()
            for ((name, value) in params) {
                builder.add(name, value)
            }
            return builder.build()
        }

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for ((name, value) in params) {
            builder.addFormDataPart(name, value)
        }

        val paramName: String = files.name
        files.forEach { file ->
            val body = RequestBody.create(TYPE_MULTI_FORM, file)
            builder.addPart(MultipartBody.Part.createFormData(paramName, file.name, body))
        }
        return builder.build()
    }

    protected open fun createBody(param: String?): RequestBody {
        if (param == null) return createBody(HashMap())
        return RequestBody.create(TYPE_JSON, param)
    }

    abstract fun onRequest(helper: Helper, key: String, params: HashMap<String, Any>)

    @CallSuper
    protected open fun <Type> applyCall(describe: Int, call: Call<String>, parser: IKReaderParser<Type>) {

        sCallback?.onStart(describe)

        Logger.d("applyCall describe=${mContext.getString(describe)}")

        call.enqueue(object : Callback<String> {

            override fun onFailure(call: Call<String>, t: Throwable) {
                Logger.d("applyCall onFailure")
                sCallback?.onFailed(t.message ?: "net failed")
            }

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

        private val TYPE_JSON = MediaType.parse("application/json; charset=utf-8")
        private val TYPE_MULTI_FORM = MediaType.parse("multipart/form-data")
    }

    interface Helper {

        private fun createHashMap(init: KStringMap.() -> Unit): KStringMap {
            val map = KStringMap()
            map.init()
            return map
        }

        fun <T> get(url: String, init: KStringMap.() -> Unit, parser: IKReaderParser<T>) {
            get(url, createHashMap(init), null, parser)
        }

        fun <T> get(url: String, init: KStringMap.() -> Unit, files: KFileList?, parser: IKReaderParser<T>) {
            get(url, createHashMap(init), files, parser)
        }

        fun <T> get(url: String, params: HashMap<String, String>, parser: IKReaderParser<T>) {
            get(url, params, null, parser)
        }

        fun <T> get(url: String, params: HashMap<String, String>, files: KFileList?, parser: IKReaderParser<T>)

        fun <T> put(url: String, init: KStringMap.() -> Unit, parser: IKReaderParser<T>) {
            put(url, createHashMap(init), null, parser)
        }

        fun <T> put(url: String, init: KStringMap.() -> Unit, files: KFileList?, parser: IKReaderParser<T>) {
            put(url, createHashMap(init), files, parser)
        }

        fun <T> put(url: String, params: HashMap<String, String>, parser: IKReaderParser<T>) {
            put(url, params, null, parser)
        }

        fun <T> put(url: String, params: HashMap<String, String>, files: KFileList?, parser: IKReaderParser<T>)

        fun <T> post(url: String, init: KStringMap.() -> Unit, parser: IKReaderParser<T>) {
            post(url, createHashMap(init), parser)
        }

        fun <T> post(url: String, init: KStringMap.() -> Unit, files: KFileList?, parser: IKReaderParser<T>) {
            post(url, createHashMap(init), null, parser)
        }

        fun <T> post(url: String, params: HashMap<String, String>, parser: IKReaderParser<T>) {
            post(url, params, null, parser)
        }

        fun <T> post(url: String, params: HashMap<String, String>, files: KFileList?, parser: IKReaderParser<T>)

        fun <T> patch(url: String, init: KStringMap.() -> Unit, parser: IKReaderParser<T>) {
            patch(url, createHashMap(init), null, parser)
        }

        fun <T> patch(url: String, init: KStringMap.() -> Unit, files: KFileList?, parser: IKReaderParser<T>) {
            patch(url, createHashMap(init), files, parser)
        }

        fun <T> patch(url: String, params: HashMap<String, String>, parser: IKReaderParser<T>) {
            patch(url, params, null, parser)
        }

        fun <T> patch(url: String, params: HashMap<String, String>, files: KFileList?, parser: IKReaderParser<T>)

        fun <T> delete(url: String, init: KStringMap.() -> Unit, parser: IKReaderParser<T>) {
            delete(url, createHashMap(init), null, parser)
        }

        fun <T> delete(url: String, init: KStringMap.() -> Unit, files: KFileList?, parser: IKReaderParser<T>) {
            delete(url, createHashMap(init), files, parser)
        }

        fun <T> delete(url: String, params: HashMap<String, String>, parser: IKReaderParser<T>) {
            delete(url, params, null, parser)
        }

        fun <T> delete(url: String, params: HashMap<String, String>, files: KFileList?, parser: IKReaderParser<T>)
    }
}