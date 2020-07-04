package com.sugaryple.fakelocation

import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment

class GoogleMapModel(mapFragment: SupportMapFragment): MapModel, OnMapReadyCallback {

    init {
        mapFragment.getMapAsync(this)
    }

    private var map: GoogleMap? = null

    private var _mapReadyEvent = MutableLiveData<Unit>()
    override val mapReadyEvent: LiveData<Unit>
        get() = _mapReadyEvent

    override fun onMapReady(map: GoogleMap?) {
        this.map = map
        _mapReadyEvent.value = Unit
    }

    override fun moveCamera(location: SimpleLatLng, zoom: Float?) {
        map?.moveCamera(
            if (zoom == null) {
                CameraUpdateFactory.newLatLng(location.toLatLng())
            } else {
                CameraUpdateFactory.newLatLngZoom(location.toLatLng(), zoom)
            }
        )
    }

    @RequiresPermission(anyOf = [
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.ACCESS_FINE_LOCATION"
    ])

    override fun setIsMyLocationEnabled(enable: Boolean) {
        map?.isMyLocationEnabled = enable
    }

}