package com.test.aanotationtest

import com.dong.library.reader.annotations.Reader
import com.dong.library.reader.api.core.KReader
import com.dong.library.reader.api.core.KReaderCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import java.lang.reflect.ParameterizedType

abstract class Parser<T> {
    abstract fun onParse(code: Int, data: String?, complete: (t: T?) -> Unit)
    abstract fun onComplete(t: T?)
    fun onError() {}
    fun onInfo() {}
}

abstract class CReader<T> : KReader<T>() {

    private val mRetrofit: Retrofit
        get() {
            sRetrofit = sRetrofit ?: generateRetrofit()
            return sRetrofit ?: throw RuntimeException()
        }

    private val mApiCls: Class<T>
        get() {
            val type: ParameterizedType = javaClass.genericSuperclass as ParameterizedType
            @Suppress("UNCHECKED_CAST")
            return type.actualTypeArguments[0] as? Class<T> ?: throw RuntimeException()
        }

    private var mApi: T? = null
        get() {
            if (field == null) {
                field = mRetrofit.create(mApiCls)
            }
            return field
        }
//
//    protected open fun generateRetrofit(): Retrofit {
//
//        return Retrofit.Builder()
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .baseUrl("http://192.168.1.119:8080")
//                .build()
//    }

    protected fun <C> onRequest(key: String, params: MutableMap<String, Any>, parser: Parser<C>) {

        getCall(mApi ?: throw RuntimeException("no api"), key, params)
                .enqueue(object : Callback<String> {

                    override fun onFailure(call: Call<String>?, t: Throwable?) {
                        parser.onError()
                    }

                    override fun onResponse(call: Call<String>?, response: Response<String>) {
                        parser.onParse(response.code(), response.body(), {
                            parser.onComplete(it)
                        })
                    }
                })
    }

    abstract fun getCall(api: T, key: String, params: MutableMap<String, Any>): Call<String>

    companion object {

        private var sRetrofit: Retrofit? = null
    }
}

@Reader(["A", "B"])
class MainReader : CReader<Api>() {

    override fun getCall(api: Api, key: String, params: MutableMap<String, Any>): Call<String> {
        return api.getUser("")
    }

    override fun onRequest(key: String, params: MutableMap<String, Any>, callback: KReaderCallback) {

        callback.onReadStart()

        super.onRequest(key, params, object : Parser<User>() {

            override fun onParse(code: Int, data: String?, complete: (t: User?) -> Unit) {
                println("code=$code, data=$data")
                complete.invoke(null)
            }

            override fun onComplete(t: User?) {
                println("t=${t == null}")
            }
        })
    }

    data class User(val id: Long, val name: String)

}

interface Api {

    @GET("user")
    fun getUser(@Header("Authorization") authorization: String): Call<String>
}

