package com.test.aanotationtest

import com.dong.library.reader.api.core.IKHttpParser
import com.dong.library.reader.api.core.KReader
import okhttp3.*
import retrofit2.Call

interface ICHttpParser<T>: IKHttpParser<T> {

    private fun parseHeaders(headers: Headers, callback: (token: String?, code: Int, msg: String, pageNo: Int, pageCount: Int, dataCount: Int) -> Unit) {
        val token: String? = headers.get("Authorization_User")

        val msg: String = headers.get("result_msg") ?: ""
        val code: Int = headers.get("result_code")?.toInt() ?: Int.MIN_VALUE

        val pageNo: Int = headers.get("page-no")?.toInt() ?: Int.MIN_VALUE
        val pageCount: Int = headers.get("page-count")?.toInt() ?: Int.MIN_VALUE
        val dataCount: Int = headers.get("all-count")?.toInt() ?: Int.MIN_VALUE

        callback.invoke(token, code, msg, pageNo, pageCount, dataCount)
    }

    override fun onParse(headers: Headers, result: String, complete: (result: T?, any: Any?) -> Unit, error: (errorId: Int) -> Unit) {
        // parser the headers
        parseHeaders(headers, { token, code, msg, pageNo, pageCount, dataCount ->
            val pager: Pager? = if (pageNo == Int.MIN_VALUE || pageCount == Int.MIN_VALUE || dataCount == Int.MIN_VALUE) {
                null
            } else {
                Pager(pageNo, pageCount, dataCount)
            }
            onParse(code, msg, result, pager, {
                complete.invoke(it, pager)
            }, {
                error.invoke(it)
            })
        })
    }

    fun onParse(code: Int, msg: String, result: String, pager: Pager?, complete: (result: T?) -> Unit, error: (messageId: Int) -> Unit)
}

data class Pager(val pageNo: Int, val pageCount: Int, val dataCount: Int)


abstract class CReader<in T> : KReader<T>() {

    override val baseUrl: String
        get() = "http://192.168.1.119:8080/thinkdata/"

    override fun onHttpInterceptor(request: Request, params: MutableMap<String, Any>): Request.Builder {
        val builder: Request.Builder = request.newBuilder()

        fun getUrl():String {
            val http: HttpUrl = request.url()
            return "${http.scheme()}://${http.host()}:${http.port()}${http.encodedPath()}"
        }

        when (request.method()) {
            "GET" -> {
                val http: HttpUrl = request.url()
                val keys: MutableSet<String> = http.queryParameterNames()

                val b: FormBody.Builder = FormBody.Builder()
                var has = false

                keys.forEach { key: String ->
                    val value: String? = http.queryParameter(key)
                    if (value != null) {
                        if (key == "_method") {
                            has = true
                        }
                        b.add(key, value)
                    }
                }

                if (!has) b.add("_method", "GET")

                builder.url(getUrl())
                builder.post(b.build())
            }
            "DELETE", "PATCH" -> {
                val method = request.method()
                val original: RequestBody? = request.body()
                val b: RequestBody = if (original == null) {
                    val sample: FormBody.Builder = FormBody.Builder()
                    sample.add("_method", method)
                    sample.build()
                } else {
                    val sample: MultipartBody.Builder = MultipartBody.Builder()
                    sample.addPart(original)
                    sample.addFormDataPart("_method", method)
                    sample.build()
                }

                builder.url(getUrl())
                builder.post(b)
            }
        }

        return builder
    }

    private fun <Type> convertParser(parser: IKHttpParser<Type>): ICHttpParser<Type> {
        if (parser !is ICHttpParser<Type>) {
            throw RuntimeException("Parser be implement ICHttpParser!")
        }
        return parser
    }

    override fun <Type> applyCall(describe: Int, call: Call<String>, parser: IKHttpParser<Type>) {
        super.applyCall(describe, call, convertParser(parser))
    }
}

