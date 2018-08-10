package com.dong.library.reader.api.core

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Context
import com.dong.library.reader.annotations.*
import com.dong.library.reader.annotations.model.KReaderMetadata
import com.dong.library.reader.api.data.KReaderTable
import com.dong.library.reader.api.exceptions.HandleException
import com.dong.library.reader.api.interfaces.IKReaderLoader
import com.dong.library.reader.api.interfaces.IKReaderInterceptor
import com.dong.library.reader.api.utils.Logger

@Suppress("ClassName")
internal class _KModel private constructor() {

    internal lateinit var context: Context

    internal object Inner {
        @SuppressLint("StaticFieldLeak")
        val instance = _KModel()
    }

    internal fun loadReaderTable() {
        context.assets.list("").filter { it.startsWith("$PROJECT_NAME$SEPARATOR") }.forEach {
            val moduleName = transferModuleName(it)
            if (moduleName.isBlank()) {
                return@forEach
            }
            (loadClassForName("$PACKAGE.$ROUTE_LOADER_NAME$SEPARATOR$moduleName")?.newInstance() as? IKReaderLoader)?.loadInto(KReaderTable.readers)
            println("Center ReaderTable.readers=${KReaderTable.readers}")
        }
    }

    private fun loadClassForName(className: String): Class<*>? {
        return try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    companion object {

        internal fun getInstance(): _KModel = Inner.instance

        fun init(context: Context) {
            getInstance().context = context.applicationContext
            getInstance().loadReaderTable()
        }
    }

    fun request(navigator: KModel.Navigator): Any? {
        val map = addressingComponent(navigator)
        if (map.isEmpty()) {
            navigator.onProcessNotFound?.invoke(navigator, "")
            return null
        }

        val handlers = createReaderHandler(map)
        val isIntercept = isIntercept(navigator)
        handlers.forEach {
            navigator.onProcessBefore?.invoke(navigator, it.metadata.readerCls.name)
            try {
                if (isIntercept) {
                    navigator.onProcessIntercept?.invoke(navigator, it.metadata.readerCls.name)
                } else {
                    val result = it.handle(context, navigator)
                    navigator.onProcessArrived?.invoke(navigator, it.metadata.readerCls.name)
                    if (result is Fragment || result is android.support.v4.app.Fragment) {
                        return result
                    }
                }
            } catch (e: HandleException) {
                e.printStackTrace()
                navigator.onProcessError?.invoke(navigator, it.metadata.readerCls.name)
            }
        }
        return null
    }

    /**
     * 开始寻址，从路由表中获取对应路径的路由
     * @return Map<String, KReaderMetadata> key: 路由路径  value: 路由元数据
     */
    private fun addressingComponent(navigator: KModel.Navigator): Map<String, KReaderMetadata> {
        Logger.d("Addressing >> ${navigator.key}")
        return KReaderTable.readers.filterKeys {
            KReaderTable.matchers.find { matcher ->
                matcher.match(it, navigator.key)
            } != null
        }
    }

    /**
     * 将Map<String, KReaderMetadata>转化为List<AbsRouteHandler>并且按照优先级进行排序
     * @return 返回路由处理者列表
     */
    private fun createReaderHandler(map: Map<String, KReaderMetadata>): List<IKReaderHandler> {
        Logger.w("request 3")
        return map.map {
            createHandler(it.value)
        }//.sortedWith(KReaderPriorityComparator)
    }

    /**
     * 执行拦截器
     * @return true:路由请求被拦截 false:该请求未被拦截
     */
    private fun isIntercept(navigator: KModel.Navigator): Boolean {
        return KReaderTable.interceptors.asSequence().find {
            try {
                val cls = it.value.clazz
                val interceptor = cls.newInstance() as IKReaderInterceptor
                return@find interceptor.intercept(context, navigator.key, navigator.extras)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: ClassCastException) {
                Logger.e("${it.value.clazz.name} is not impl IRouteInterceptor")
            }
            return@find false
        } != null
    }
//
//    object KReaderPriorityComparator : Comparator<IKReaderHandler> {
//        override fun compare(o1: IKReaderHandler, o2: IKReaderHandler): Int = o1.metadata.priority - o2.metadata.priority
//    }
}