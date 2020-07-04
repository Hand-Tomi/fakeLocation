package com.sugaryple.fakelocation

import android.location.Location
import com.google.android.libraries.maps.model.LatLng

fun SimpleLatLng.toLatLng() = LatLng(latitude, longitude)

fun Location.toSimpleLatLng() = SimpleLatLng(latitude, longitude)