package com.test.aanotationtest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dong.library.reader.annotations.Reader

import com.dong.library.reader.api.core.KModel
import com.dong.library.reader.api.core.KReaderCallback
import com.dong.library.reader.api.core._KReader
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private fun request(key: String, paramInit: MutableMap<String, Any>.() -> Unit, callbackInit: KModel.Navigator.() -> Unit) {
        val navigator = KModel.create(key)
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
            request("L", {
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

@Reader(["L"])
class _MainReader: _KReader() {

    override fun onRequest(key: String, params: MutableMap<String, Any>, callback: KReaderCallback) {

    }
}