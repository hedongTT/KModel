package com.dong.library.reader.api.interfaces

import android.content.Context
import com.dong.library.reader.annotations.model.KReaderMetadata

interface IKReaderLoader {
    fun loadInto(map: MutableMap<String, KReaderMetadata>)
}

/**
 * 拦截器
 *
 * @author: Wuruiqiang <a href="mailto:263454190@qq.com">Contact me.</a>
 * @version: v1.0
 * @since: 18/1/5 上午9:12
 */
interface IKReaderInterceptor {
    fun intercept(context: Context, path: String, extras: MutableMap<String, Any>): Boolean
}

/**
 * 路由规则匹配器
 *
 * @author: Wuruiqiang <a href="mailto:263454190@qq.com">Contact me.</a>
 * @version: v1.0
 * @since: 18/1/17 下午7:45
 */
interface KPathMatcher {
    /**
     *
     * @param path route path
     * @param path2 navigator path
     * @return if true,means this path was match
     */
    fun match(path: String, path2: String): Boolean
}