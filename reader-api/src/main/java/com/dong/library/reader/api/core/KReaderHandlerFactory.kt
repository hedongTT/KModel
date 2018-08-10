package com.dong.library.reader.api.core

import android.content.*
import com.dong.library.reader.annotations.model.KReaderMetadata
import com.dong.library.reader.annotations.model.KReaderType
import com.dong.library.reader.api.exceptions.HandleException
import com.dong.library.reader.api.exceptions.RouteNotFoundException
import com.dong.library.reader.api.utils.Logger

/**
 * 工厂类，根据不同ReaderType返回不同处理者
 */
internal fun createHandler(metadata: KReaderMetadata): IKReaderHandler {
    return when (metadata.readerType) {
        KReaderType.READER -> {
            KReaderHandler(metadata)
        }
        else -> {
            UnknownReaderHandler(metadata)
        }
    }
}

/**
 * 未知类型
 */
internal class UnknownReaderHandler(metadata: KReaderMetadata) : IKReaderHandler(metadata) {

    override fun handle(context: Context, navigator: KModel.Navigator) {
        Logger.w("Unknown route : ${metadata.readerCls.name}")
    }
}

internal class KReaderHandler(metadata: KReaderMetadata): IKReaderHandler(metadata) {

    override fun handle(context: Context, navigator: KModel.Navigator): Any? {
        try {
            val cls = metadata.readerCls
            var reader = KReader.getReader(cls)
            if (reader == null) {
                reader = cls.newInstance() as KReader
                reader.init(context)
                KReader.addReader(cls, reader)
            }

            reader.request(navigator.key, navigator.extras, object: KReader.ICallback(context) {

                override fun onReadStart(data: KReader.RequestData) {
                    navigator.onReadStart?.invoke(data)
                }

                override fun onReadIng(data: KReader.RequestData) {
                    navigator.onReadIng?.invoke(data)
                }

                override fun onReadComplete(data: KReader.RequestData) {
                    navigator.onReadComplete?.invoke(data)
                }

                override fun onReadFailed(data: KReader.RequestData) {
                    navigator.onReadFailed?.invoke(data)
                }
            })

            return null
        } catch (e: ClassNotFoundException) {
            throw RouteNotFoundException(e)
        } catch (e: ClassCastException) {
            throw HandleException(e)
        }
    }
}