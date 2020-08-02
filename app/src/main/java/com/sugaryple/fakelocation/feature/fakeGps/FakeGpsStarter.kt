package com.sugaryple.fakelocation.feature.fakeGps

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.sugaryple.fakelocation.data.SimpleLatLng
import java.util.*

class FakeGpsStarter(appContext: Context) {

    private var callback: FakeGpsCallBack? = null
    private val workManager = WorkManager.getInstance(appContext)
    private var workOperation: Operation? = null

    fun setCallback(callback: FakeGpsCallBack) {
        this.callback = callback
    }

    fun start(simpleLatLng: SimpleLatLng) {
        workManager.cancelAllWorkByTag(TAG_FAKE_GPS_WORK)
        val request = OneTimeWorkRequestBuilder<FakeGpsWorker>()
            .setInputData(
                workDataOf(
                    FakeGpsWorker.TARGET_LATITUDE to simpleLatLng.latitude,
                    FakeGpsWorker.TARGET_LONGITUDE to simpleLatLng.longitude
                )
            )
            .addTag(TAG_FAKE_GPS_WORK)
            .build()
        workOperation = workManager.enqueue(request)
        workOperation?.state?.observeForever { state ->
            when (state) {
                is Operation.State.FAILURE -> onError(state.throwable)
            }
        }
    }

    private fun onError(throwable: Throwable) {
        when (throwable) {
            is SecurityException -> callback?.requiredDebugSetting()
        }
    }

    companion object {
        const val TAG_FAKE_GPS_WORK = "tag_fake_gps_work"
    }
}

interface FakeGpsCallBack {
    fun requiredDebugSetting()
}