package com.test.aanotationtest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dong.library.reader.annotations.Reader
import com.dong.library.reader.api.core.*

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
            request("Abc", {
                put("a", 1)
                put("b", 2)
                put("c", 3)
            }, {
                onReadFailed {
                    println("onReadFailed, ${it.code}")
                }
                onReadStart {
                    println("onReadStart, ${it.describe}")
                }
                onReadComplete {
                    println("onReadComplete, $it")
                }
            })
        }
    }
}

@Reader(["Abc"])
class MainReader : KReader<Api>() {

    override val baseUrl: String
        get() = "http://baidu.com"

    override fun onRequest(api: Api, key: String, params: MutableMap<String, Any>, callback: KReaderCallback) {
        applyCall(R.string.app_name, api.getUser("", 1), object: IKReaderParser<String> {

            override fun onParse(headers: Headers, result: String, complete: (result: String?, any: Any?) -> Unit, error: (errorId: Int) -> Unit) {
                complete.invoke(result, null)
            }

            override fun onComplete(result: String?, any: Any?) {
                println("result=$result")
            }
        })
    }
}

interface Api {

    @DELETE("user/{id}")
    fun getUser(@Header("Authorization") authorization: String, @Path("id") id: Int): Call<String>
}