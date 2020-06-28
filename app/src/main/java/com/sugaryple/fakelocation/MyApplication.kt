package com.sugaryple.fakelocation

import android.app.Application

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        MyKoin().start(this)
    }
}