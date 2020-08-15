package com.sugaryple.fakelocation.model

import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sugaryple.fakelocation.core.Event
import java.lang.Exception

class GpsProviderModel(private val locationManager: LocationManager) {

    private val _eventMockLocationRequest = MutableLiveData<Event<Unit>>()
    val eventMockLocationRequest: LiveData<Event<Unit>>
        get() = _eventMockLocationRequest

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

    /**
     * Mock LocationセットアップをしないとSecurityExceptionが発生する可能性がある
      */
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
        } catch (e: Exception) {
            when (e) {
                is SecurityException -> _eventMockLocationRequest.value = Event(Unit)
            }
        }
    }

    companion object {
        const val providerName = LocationManager.GPS_PROVIDER
    }
}