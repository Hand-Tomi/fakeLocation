package com.sugaryple.fakelocation

import android.location.Location
import com.google.android.libraries.maps.model.LatLng
import com.sugaryple.fakelocation.data.SimpleLatLng

fun SimpleLatLng.toLatLng() = LatLng(latitude, longitude)

fun Location.toSimpleLatLng() =
    SimpleLatLng(latitude, longitude)

fun LatLng.toSimpleLatLng() =
    SimpleLatLng(latitude, longitude)