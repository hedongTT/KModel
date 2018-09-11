package com.test.aanotationtest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.dong.library.reader.annotations.Reader
import com.dong.library.reader.api.core.*
import com.dong.library.reader.api.core.callback.KReaderCallback
import com.dong.library.reader.api.core.enums.KReaderMethod
import com.dong.library.reader.api.core.params.KFileList
import com.dong.library.reader.api.core.parser.IKReaderParser
import okhttp3.FormBody

import okhttp3.Headers
import okhttp3.Request
import org.jetbrains.anko.find
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

        find<Button>(R.id.button).setOnClickListener {
            //            request("Abc", {
//                put("a", 1)
//                put("b", 2)
//                put("c", 3)
//            }, {
//                onReadFailed {
//                    println("onReadFailed, ${it.code}")
//                }
//                onReadStart {
//                    println("onReadStart, ${it.describe}")
//                }
//                onReadComplete {
//                    println("onReadComplete, $it")
//                }
//            })

            request("Abcd", {
                put("a", 1)
            }, {
                onReadStart {
                    println("onReadStart, ${it.describe}")
                }
                onReadFailed {
                    println("onReadFailed, ${it.code}")
                }
                onReadComplete {
                    println("onReadComplete, $it")
                }
            })
        }
    }
}
//
//@Reader(["Abc"])
//class MainReader : KReader<Api>() {
//
//    override val baseUrl: String
//        get() = "http://baidu.com"
//
//    override fun onRequest(api: Api, key: String, params: HashMap<String, Any>, callback: KReaderCallback) {
//
//        applyCall(R.string.app_name, api.getUser("", 1), object : IKReaderParser<String> {
//
//            override fun onParse(headers: Headers, result: String, complete: (code: Int, result: String?, any: Any?) -> Unit, error: (code: Int, describe: Int) -> Unit) {
//                complete.invoke(0, result, null)
//            }
//
//            override fun onComplete(info: KRequestInfo, result: String?) {
//                println("result=$result")
//            }
//        })
//    }
//}
//
//interface Api {
//
//    @DELETE("user/{id}")
//    fun getUser(@Header("Authorization") authorization: String, @Path("id") id: Int): Call<String>
//}

@Reader(["Abcd"])
class MainReaderI : KDelReader() {

    override val baseUrl: String
        get() = "http://baidu.com"

    override fun onHttpInterceptor(request: Request, params: HashMap<String, Any>): Request {
        // 重写拦截器、可添加公共Header，重定义属性
        return request.newBuilder()
                .addHeader("A", "a")
                .addHeader("B", "b")
                .method(request.method(), request.body())
                .build()
    }

    override fun onRequest(helper: Helper, key: String, params: HashMap<String, Any>) {

        val builder = FormBody.Builder()
        builder.add("abc", "abc.value")

        helper.post("abc", {
            put("a", 1)
        }, object: IKReaderParser<String> {

            override fun onParse(headers: Headers, result: String, complete: (code: Int, result: String?, any: Any?) -> Unit, error: (code: Int, describe: Int) -> Unit) {
                complete.invoke(0, result, null)
            }

            override fun onComplete(info: KRequestInfo, result: String?) {
                println("result=$result")
            }
        })
    }

    override fun <T> requestMethod(method: KReaderMethod, api: KDelApi, url: String, params: HashMap<String, String>, files: KFileList?, parser: IKReaderParser<T>) {
        // 根据需求重写方法, 将非POST方法改为POST方法，并添加method标记
        val sample = when(method) {
            KReaderMethod.GET -> {
                params["method"] = "GET"
                KReaderMethod.POST
            }
            KReaderMethod.PUT -> {
                params["method"] = "PUT"
                KReaderMethod.POST
            }
            KReaderMethod.POST -> {
                KReaderMethod.POST
            }
            KReaderMethod.PATCH -> {
                params["method"] = "PATCH"
                KReaderMethod.POST
            }
            KReaderMethod.DELETE -> {
                params["method"] = "DELETE"
                KReaderMethod.POST
            }
        }

        super.requestMethod(sample, api, url, params, files, parser)
    }
}