package com.sugaryple.fakelocation.feature.fakeGps

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.*
import com.sugaryple.fakelocation.data.SimpleLatLng

class FakeGpsWorkManager(private val workManager: WorkManager) {

    private var workOperation: Operation? = null

    val state: LiveData<FakeGpsWorkSate> =
        workManager.getWorkInfosForUniqueWorkLiveData(NAME_FAKE_GPS_WORK).map { worksInfo ->
            if (worksInfo.size >= 2) throw IllegalStateException("work is only one")
            val workInfo = worksInfo.firstOrNull()
            when (workInfo?.state) {
                null -> FakeGpsWorkSate.Uninitialized
                WorkInfo.State.RUNNING -> FakeGpsWorkSate.On(workInfo.toTargetLatLng())
                WorkInfo.State.FAILED -> FakeGpsWorkSate.Failed
                else -> FakeGpsWorkSate.Off
            }
        }

    fun start(simpleLatLng: SimpleLatLng) {
        val request = OneTimeWorkRequestBuilder<FakeGpsWorker>()
            .setInputData(
                workDataOf(
                    FakeGpsWorker.TARGET_LATITUDE to simpleLatLng.latitude,
                    FakeGpsWorker.TARGET_LONGITUDE to simpleLatLng.longitude
                )
            )
            .build()
        workOperation = workManager.enqueueUniqueWork(
            NAME_FAKE_GPS_WORK,
            ExistingWorkPolicy.REPLACE, // REPLACE : 먼저 있던 Work를 취소하고 삭제한후 새로운 Work를 넣는다.
            request
        )
    }

    fun stop() {
        workManager.cancelUniqueWork(NAME_FAKE_GPS_WORK)
    }

    companion object {
        const val NAME_FAKE_GPS_WORK = "name_fake_gps_work"
    }
}

private fun WorkInfo.toTargetLatLng(): SimpleLatLng? {
    val latitude = this.progress.getDouble(FakeGpsWorker.TARGET_LATITUDE, Double.MIN_VALUE)
    val longitude = this.progress.getDouble(FakeGpsWorker.TARGET_LONGITUDE, Double.MIN_VALUE)
    return if (latitude != Double.MIN_VALUE && longitude != Double.MIN_VALUE) {
        SimpleLatLng(latitude, longitude)
    } else {
        null
    }
}