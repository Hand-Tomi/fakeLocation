package com.sugaryple.fakelocation

import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData

interface MapModel {
    val mapReadyEvent: LiveData<Unit>
    fun moveCamera(location: SimpleLatLng, zoom: Float? = null)

    @RequiresPermission(anyOf = [
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.ACCESS_FINE_LOCATION"
    ])
    fun setIsMyLocationEnabled(enable: Boolean)
}