package com.example.myview.ui

import android.app.Application
import android.content.Context
import me.weishu.reflection.Reflection

class MyApp : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Reflection.unseal(base)
        SystemLogUtil.updateDebugLog()
    }
}