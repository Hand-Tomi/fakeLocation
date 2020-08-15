package com.sugaryple.fakelocation

import android.location.Location
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.libraries.maps.model.LatLng
import com.sugaryple.fakelocation.data.SimpleLatLng

fun SimpleLatLng.toLatLng() = LatLng(latitude, longitude)

fun Location.toSimpleLatLng() =
    SimpleLatLng(latitude, longitude)

fun LatLng.toSimpleLatLng() =
    SimpleLatLng(latitude, longitude)

// 오직 한개만 DialogFragment를 표시하도록 한다.
fun DialogFragment.showOnlyOne(manager: FragmentManager, tag: String) {
    if (manager.findFragmentByTag(tag) == null) {
        show(manager, tag)
    }
}