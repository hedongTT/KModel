package com.test.aanotationtest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dong.library.reader.annotations.Reader
import com.dong.library.reader.api.core.IKHttpParser

import com.dong.library.reader.api.core.KModel
import com.dong.library.reader.api.core.KReader
import com.dong.library.reader.api.core.KReaderCallback
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Headers
import retrofit2.Call
import retrofit2.http.*

class MainActivity : AppCompatActivity() {

    private fun request(key: String, paramInit: MutableMap<String, Any>.() -> Unit, callbackInit: KModel.Navigator.() -> Unit) {
        val navigator: KModel.Navigator = KModel.create(key)
        navigator.callbackInit()
        navigator.withParams(paramInit)
        navigator.request()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            request("A", {
                put("a", 1)
                put("b", 2)
                put("c", 3)
            }, {
                onReadStart {
                    println("onReadStart, ${it.describe}")
                }
                onReadComplete {
                    println("onReadComplete, $it")
                }
            })
        }

        button_i.setOnClickListener {
            request("B", {
                put("a", 1)
                put("b", 2)
                put("c", 3)
            }, {
                onReadStart {

                }
                onReadComplete {
                    println("onReadComplete, $it")
                }
            })
        }
    }
}


@Suppress("unused")
@Reader(["A", "B"])
class MainReader : KReader<Api>() {

    override val baseUrl: String
        get() = "http://baidu.com"

    override fun onRequest(api: Api, key: String, params: MutableMap<String, Any>, callback: KReaderCallback) {

        when (key) {
            "A" -> {
                val call: Call<String> = api.getVersion("android")

                applyCall(R.string.app_name, call, object : IKHttpParser<String> {

                    override fun onParse(headers: Headers, result: String, complete: (result: String?, any: Any?) -> Unit, error: (errorId: Int) -> Unit) {
                    }

                    override fun onComplete(result: String?, any: Any?) {
                    }
                })
            }
            "B" -> {
                val call: Call<String> = api.getUser("", 1)
                applyCall(R.string.app_name, call, object : IKHttpParser<String> {

                    override fun onParse(headers: Headers, result: String, complete: (result: String?, any: Any?) -> Unit, error: (errorId: Int) -> Unit) {
                    }

                    override fun onComplete(result: String?, any: Any?) {
                    }
                })
            }
        }
    }
}

interface Api {

    @GET("admin/version")
    fun getVersion(@Query("clientType") type: String): Call<String>

    @DELETE("user/{id}")
    fun getUser(@Header("Authorization") authorization: String, @Path("id") id: Int): Call<String>
}