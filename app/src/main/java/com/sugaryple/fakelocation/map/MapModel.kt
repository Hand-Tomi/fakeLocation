package com.sugaryple.fakelocation.map

import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import com.google.android.libraries.maps.model.Marker
import com.sugaryple.fakelocation.data.SimpleLatLng

interface MapModel {
    val mapReadyEvent: LiveData<Unit>
    fun moveCamera(location: SimpleLatLng, zoom: Float? = null)

    @RequiresPermission(anyOf = [
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.ACCESS_FINE_LOCATION"
    ])
    fun setIsMyLocationEnabled(enable: Boolean)
    fun setZoomControlsEnabled(enable: Boolean)
    fun setCompassEnabled(enable: Boolean)
    fun getCenterLocation(): SimpleLatLng?
    fun addMarker(location: SimpleLatLng): Marker?
}