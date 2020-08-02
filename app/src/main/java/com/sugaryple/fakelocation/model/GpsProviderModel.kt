package com.sugaryple.fakelocation.model

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.SystemClock

class GpsProviderModel(appContext: Context) {

    private val locationManager: LocationManager
            = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun isGPSProviderEnabled(): Boolean
            = locationManager.isProviderEnabled(providerName)

    fun pushLocation(latitude: Double, longitude: Double) {
        val mockLocation = Location(providerName)
        val currentTime = System.currentTimeMillis()
        mockLocation.latitude = latitude
        mockLocation.longitude = longitude
        mockLocation.time = currentTime
        mockLocation.accuracy = 1.0f
        mockLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()

        locationManager.setTestProviderLocation(providerName, mockLocation)
    }

    fun initMockLocationProvider() {
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
    }

    companion object {
        const val providerName = LocationManager.GPS_PROVIDER
    }
}