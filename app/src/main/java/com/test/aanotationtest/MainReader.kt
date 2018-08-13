package com.test.aanotationtest

import com.dong.library.reader.annotations.Reader
import com.dong.library.reader.api.core.IKHttpParser
import com.dong.library.reader.api.core.KReader
import com.dong.library.reader.api.core.KReaderCallback
import okhttp3.Headers
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

abstract class Parser<T> {
    abstract fun onParse(code: Int, data: String?, complete: (t: T?) -> Unit)
    abstract fun onComplete(t: T?)
    fun onError() {}
    fun onInfo() {}
}

@Reader(["A", "B"])
class MainReader : KReader<Api>() {

    override val baseUrl: String
        get() = "http://192.168.1.119:8080"

    private fun <C> request(call: Call<String>, describe: Int, callback: KReaderCallback, parser: Parser<C>) {
        callback.onReadStart(describe)
    }

    override fun onRequest(api: Api, key: String, params: MutableMap<String, Any>, callback: KReaderCallback) {

        when (key) {
            "A" -> {
                val call: Call<String> = api.getUser("")
                applyCall(call, object: IKHttpParser<String> {

                    override fun onParse(headers: Headers, result: String, complete: (result: String?) -> Unit, error: (errorId: Int) -> Unit) {

                    }

                    override fun onComplete(result: String?) {
                    }
                })
            }
        }
    }

    data class User(val id: Long, val name: String)

}

interface Api {

    @GET("user")
    fun getUser(@Header("Authorization") authorization: String): Call<String>
}

