package com.sugaryple.fakelocation.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sugaryple.fakelocation.R
import com.sugaryple.fakelocation.core.Event
import com.sugaryple.fakelocation.data.SimpleLatLng
import com.sugaryple.fakelocation.feature.fakeGps.FakeGpsWorkSate
import com.sugaryple.fakelocation.map.MapModel
import com.sugaryple.fakelocation.map.MyMarker

class MapsViewModel(
    private val mapModel: MapModel
): ViewModel() {

    private var targetMarker: MyMarker? = null

    private val _resPlayButtonIcon = MutableLiveData<Int>(R.drawable.ic_baseline_my_location_24)
    val resPlayButtonIcon: LiveData<Int>
        get() = _resPlayButtonIcon

    private val _enabledPlayButton = MutableLiveData<Boolean>(false)
    val enabledPlayButton: LiveData<Boolean>
        get() = _enabledPlayButton

    private val _clickEventPlay = MutableLiveData<Event<Unit>>()
    val clickEventPlay: LiveData<Event<Unit>>
        get() = _clickEventPlay

    private val _fakeGpsWorkFailedEvent = MutableLiveData<Event<Unit>>()
    val fakeGpsWorkFailedEvent: LiveData<Event<Unit>>
        get() = _fakeGpsWorkFailedEvent

    fun onClickPlay() {
        _clickEventPlay.value = Event(Unit)
    }

    private fun clearTargetMarker() {
        targetMarker?.remove()
    }

    private fun setTargetMarker(latLng: SimpleLatLng) {
        clearTargetMarker()
        targetMarker = mapModel.addMarker(latLng)
    }

    fun onChangedState(state: FakeGpsWorkSate) {
        when (state) {
            is FakeGpsWorkSate.On -> {
                 state.pinLatLng?.let { setTargetMarker(it) }
                _resPlayButtonIcon.value = R.drawable.ic_baseline_my_location_24
            }
            is FakeGpsWorkSate.Failed -> {
                 clearTargetMarker()
                _fakeGpsWorkFailedEvent.value = Event(Unit)
                _resPlayButtonIcon.value = R.drawable.ic_baseline_location_searching_24
            }
            else -> {
                 clearTargetMarker()
                _resPlayButtonIcon.value = R.drawable.ic_baseline_location_searching_24
            }
        }
    }

}