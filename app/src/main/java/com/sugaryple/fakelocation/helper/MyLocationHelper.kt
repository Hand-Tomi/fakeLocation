package com.sugaryple.fakelocation.helper

import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.sugaryple.fakelocation.data.SimpleLatLng
import com.sugaryple.fakelocation.toSimpleLatLng

class MyLocationHelper(
    private val fusedLocationClient: FusedLocationProviderClient
) {
    private val _myLocation = MutableLiveData<SimpleLatLng>()
    val myLocation: LiveData<SimpleLatLng>
        get() = _myLocation

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    fun init() {
        fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
            lastLocation?.let { _myLocation.value = it.toSimpleLatLng() }
        }
    }
}