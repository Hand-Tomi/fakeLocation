package com.sugaryple.fakelocation.model

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.SystemClock
import androidx.work.*
import com.sugaryple.fakelocation.data.SimpleLatLng
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.TimeUnit

class GpsProviderModel(appContext: Context) {

    private var callback: GpsProviderCallback? = null
    private val workManager = WorkManager.getInstance(appContext)
    private var requestId: UUID? = null
    private var workOperation: Operation? = null

    companion object {
        const val providerName = LocationManager.GPS_PROVIDER
    }

    fun setCallback(callback: GpsProviderCallback) {
        this.callback = callback
    }

    fun pushLocation(simpleLatLng: SimpleLatLng) {
        requestId?.let { workManager.cancelWorkById(it) }
        val request = PeriodicWorkRequestBuilder<UploadWorker>(150, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    UploadWorker.TARGET_LATITUDE to simpleLatLng.latitude,
                    UploadWorker.TARGET_LONGITUDE to simpleLatLng.longitude
                )
            )
            .build()
        workOperation = workManager.enqueue(request)
        workOperation?.state?.observeForever { state ->
            when (state) {
                is Operation.State.FAILURE -> onError(state.throwable)
            }
        }
        requestId = request.id
    }

    private fun onError(throwable: Throwable) {
        when (throwable) {
            is SecurityException -> callback?.requiredDebugSetting()
        }
    }
}

class UploadWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    companion object {
        const val TARGET_LATITUDE = "target_latitude"
        const val TARGET_LONGITUDE = "target_longitude"
    }

    private val locationManager: LocationManager
            = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override fun doWork(): Result {
        return try {
            if (isGPSProviderEnabled()) {
                initMockLocationProvider()
            }
            pushLocation()
            Result.success()
        } catch(e: Exception){
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun isGPSProviderEnabled(): Boolean
            = locationManager.isProviderEnabled(GpsProviderModel.providerName)

    private fun pushLocation() {
        val mockLocation = Location(GpsProviderModel.providerName)
        val currentTime = System.currentTimeMillis()
        val latitude = inputData.getDouble(TARGET_LATITUDE, Double.MIN_VALUE)
        val longitude = inputData.getDouble(TARGET_LONGITUDE, Double.MIN_VALUE)
        if (latitude == Double.MIN_VALUE || longitude == Double.MIN_VALUE) {
            throw IllegalStateException("data is null")
        }
        mockLocation.latitude = latitude
        mockLocation.longitude = longitude
        mockLocation.time = currentTime
        mockLocation.accuracy = 1.0f
        mockLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()

//        locationManager.setTestProviderStatus(providerName, LocationProvider.AVAILABLE, mockLocation.extras, currentTime)
        locationManager.setTestProviderLocation(GpsProviderModel.providerName, mockLocation)
    }

    private fun initMockLocationProvider() {
        locationManager.addTestProvider(
            GpsProviderModel.providerName,
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            Criteria.NO_REQUIREMENT,
            Criteria.ACCURACY_FINE
        )
        locationManager.setTestProviderEnabled(GpsProviderModel.providerName, true)
    }
}

interface GpsProviderCallback {
    fun requiredDebugSetting()
}