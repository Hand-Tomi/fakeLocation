package com.sugaryple.fakelocation.feature.fakeGps

import com.sugaryple.fakelocation.data.SimpleLatLng

sealed class FakeGpsWorkSate {
    object Uninitialized: FakeGpsWorkSate()
    class On(val pinLatLng: SimpleLatLng?): FakeGpsWorkSate()
    object Off: FakeGpsWorkSate()
    class Failed(val reason: FakeGpsWorker.ReasonOfFailure?): FakeGpsWorkSate()
}