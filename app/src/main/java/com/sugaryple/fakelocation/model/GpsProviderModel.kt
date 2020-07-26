package com.sugaryple.fakelocation.model

import android.app.Activity
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.SystemClock
import com.sugaryple.fakelocation.data.SimpleLatLng

class GpsProviderModel {
    private lateinit var locationManager: LocationManager

    private var callback: GpsProviderCallback? = null

    companion object {
        private const val providerName = LocationManager.GPS_PROVIDER
    }

    fun setCallback(callback: GpsProviderCallback) {
        this.callback = callback
    }

    fun isGPSProviderEnabled(): Boolean
            = locationManager.isProviderEnabled(providerName)

    fun init(activity: Activity) {
        locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    fun initMockLocationProvider() {
        try {
            locationManager.addTestProvider(
                providerName,
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
            locationManager.setTestProviderEnabled(providerName, true)
        } catch(e: Exception){
            e.printStackTrace()
            if (e is SecurityException) {
                callback?.requiredDebugSetting()
            }
        }
    }

    fun pushLocation(simpleLatLng: SimpleLatLng) {
        try {
            val mockLocation = Location(providerName)
            val currentTime = System.currentTimeMillis()
            mockLocation.latitude = simpleLatLng.latitude
            mockLocation.longitude = simpleLatLng.longitude
            mockLocation.time = currentTime
            mockLocation.accuracy = 1.0f
            mockLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()

//            locationManager.setTestProviderStatus(providerName, LocationProvider.AVAILABLE, mockLocation.extras, currentTime)
            locationManager.setTestProviderLocation(providerName, mockLocation)
        } catch(e: Exception){
            e.printStackTrace()
        }
    }
}

interface GpsProviderCallback {
    fun requiredDebugSetting()
}