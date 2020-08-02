package com.sugaryple.fakelocation.feature.fakeGps

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.sugaryple.fakelocation.model.GpsProviderModel
import kotlinx.coroutines.delay
import timber.log.Timber
import java.lang.IllegalStateException


class FakeGpsWorker(
    appContext: Context,
    parameters: WorkerParameters
) : CoroutineWorker(appContext, parameters) {

    private val gpsProviderModel = GpsProviderModel(appContext)

    private val fakeGpsNotificationController = FakeGpsNotificationController(appContext)

    override suspend fun doWork(): Result {
        Timber.v("##FakeGps## doWork")
        val notification = fakeGpsNotificationController.create(
            WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
        )
        val notificationId = FakeGpsNotificationController.FAKE_GPS_NOTIFICATION_ID
        setForeground(
            foregroundInfo = ForegroundInfo(notificationId, notification)
        )

        return try {
            val latitude = inputData.getDouble(TARGET_LATITUDE, Double.MIN_VALUE)
            val longitude = inputData.getDouble(TARGET_LONGITUDE, Double.MIN_VALUE)
            if (latitude == Double.MIN_VALUE || longitude == Double.MIN_VALUE) {
                throw IllegalStateException("data is null")
            }
            if (gpsProviderModel.isGPSProviderEnabled()) {
                gpsProviderModel.initMockLocationProvider()
            }
            repeatPushLocation(latitude, longitude)
            finish()
            Result.success()
        } catch(e: Exception){
            e.printStackTrace()
            finish()
            Result.failure()
        }
    }

    private fun finish() {
        fakeGpsNotificationController.cancel()
    }

    private suspend fun repeatPushLocation(latitude: Double, longitude: Double) {
        while (true) {
            delay(DELAY_REPEAT_PUSH)
            gpsProviderModel.pushLocation(latitude, longitude)
            Timber.v("##FakeGps## pushLocation")
        }
    }

    companion object {
        const val TARGET_LATITUDE = "target_latitude"
        const val TARGET_LONGITUDE = "target_longitude"
        private const val DELAY_REPEAT_PUSH = 128L
    }
}