package com.sugaryple.fakelocation.feature.fakeGps

import android.content.Context
import androidx.work.*
import com.sugaryple.fakelocation.data.SimpleLatLng

class FakeGps(appContext: Context) {

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
        workManager.getWorkInfoByIdLiveData(request.id).observeForever {
            when (it.state) {
                WorkInfo.State.FAILED -> {
                    it.outputData.getString(FakeGpsWorker.DATA_KEY_REASON_OF_FAILURE)?.let { reason ->
                        errorHandling(FakeGpsWorker.ReasonOfFailure.valueOf(reason))
                    }
                }
                else -> { }
            }
        }
        workOperation = workManager.enqueue(request)
    }

    private fun errorHandling(reason: FakeGpsWorker.ReasonOfFailure) {
        when (reason) {
            FakeGpsWorker.ReasonOfFailure.MOCK_LOCATION_REQUIRED -> callback?.requiredDebugSetting()
            else -> { }
        }
    }

    companion object {
        const val TAG_FAKE_GPS_WORK = "tag_fake_gps_work"
    }
}

interface FakeGpsCallBack {
    fun requiredDebugSetting()
}