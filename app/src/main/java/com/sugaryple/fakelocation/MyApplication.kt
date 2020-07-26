package com.sugaryple.fakelocation

import android.app.Application
import android.util.Log
import timber.log.Timber
import timber.log.Timber.DebugTree

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        MyKoin().start(this)

        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }
}

private open class CrashReportingTree : Timber.Tree() {
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }
        // TODO Fake Crash Library를 적용할 경우 넣는다.
        // FakeCrashLibrary.log(priority, tag, message)
        if (t != null) {
            if (priority == Log.ERROR) {
                // FakeCrashLibrary.logError(t)
            } else if (priority == Log.WARN) {
                // FakeCrashLibrary.logWarning(t)
            }
        }
    }
}