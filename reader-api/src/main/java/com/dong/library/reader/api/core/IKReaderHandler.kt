package com.dong.library.reader.api.core

import android.content.Context
import com.dong.library.reader.annotations.model.KReaderMetadata
import com.dong.library.reader.api.exceptions.HandleException

/**
 * 路由处理器抽象类
 *
 * @author Wuruiqiang <a href="mailto:263454190@qq.com">Contact me.</a>
 * @version v1.0
 * @since 18/1/23 上午9:29
 */
internal abstract class IKReaderHandler(val metadata: KReaderMetadata) {
    @Throws(HandleException::class)
    abstract fun handle(context: Context, navigator: KModel.Navigator): Any?
}