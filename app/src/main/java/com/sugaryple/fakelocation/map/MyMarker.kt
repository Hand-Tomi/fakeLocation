package com.sugaryple.fakelocation.map

import com.google.android.libraries.maps.model.Marker

/**
 * Mapライブラリに依存しないようにラッピングするクラス
 */
class MyMarker (
    private val marker: Marker
) {
    fun remove() { marker.remove() }
}

fun Marker.toMy() = MyMarker(this)