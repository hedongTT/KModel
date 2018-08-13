package com.test.aanotationtest

import android.app.Application
import com.dong.library.reader.api.core.KModel

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        KModel.init(this)

        // 测试一下 // 改一下？
    }
}