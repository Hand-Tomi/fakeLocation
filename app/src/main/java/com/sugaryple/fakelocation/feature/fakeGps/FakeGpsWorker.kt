package com.sugaryple.fakelocation.feature.fakeGps

import android.app.Application
import android.content.Context
import androidx.work.*
import com.sugaryple.fakelocation.model.GpsProviderModel
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.lang.IllegalStateException


class FakeGpsWorker(
    appContext: Context,
    parameters: WorkerParameters
) : CoroutineWorker(appContext, parameters) {

    private val gpsProviderModel: GpsProviderModel by (appContext as Application).inject()
    private val fakeGpsNotificationController: FakeGpsNotificationController by (appContext as Application).inject()

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
            // 현재 어떤 좌표를 Fake하고있는지 알리기위해 setProgress를 사용한다.
            setProgress(inputData)
            if (gpsProviderModel.isGPSProviderEnabled()) {
                gpsProviderModel.initMockLocationProvider()
            }
            repeatPushLocation(latitude, longitude)
            Result.success()
        } catch(e: Exception){
            e.printStackTrace()
            Result.failure()
        } finally {
            finish()
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