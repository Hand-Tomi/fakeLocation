package com.sugaryple.fakelocation.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sugaryple.fakelocation.R
import com.sugaryple.fakelocation.core.Event
import com.sugaryple.fakelocation.feature.fakeGps.FakeGpsWorkSate
import com.sugaryple.fakelocation.map.MapModel

class MapsViewModel(
    private val mapModel: MapModel
): ViewModel() {

    private val _resPlayButtonIcon = MutableLiveData<Int>(R.drawable.ic_baseline_my_location_24)
    val resPlayButtonIcon: LiveData<Int>
        get() = _resPlayButtonIcon

    private val _enabledPlayButton = MutableLiveData<Boolean>(false)
    val enabledPlayButton: LiveData<Boolean>
        get() = _enabledPlayButton

    private val _clickEventPlay = MutableLiveData<Event<Unit>>()
    val clickEventPlay: LiveData<Event<Unit>>
        get() = _clickEventPlay

    fun onClickPlay() {
        _clickEventPlay.value = Event(Unit)
    }

    fun onChangedState(state: FakeGpsWorkSate) {
        when (state) {
            is FakeGpsWorkSate.On -> {
                // TODO 기능 구현 해야 됨
                // state.pinLatLng?.let { setTargetMarker(it) }
                _resPlayButtonIcon.value = R.drawable.ic_baseline_my_location_24
            }
            is FakeGpsWorkSate.Failed -> {
                // TODO 기능 구현 햐야됨
                // clearTargetMarker()
                // startRequiredMockLocationDialog()
                _resPlayButtonIcon.value = R.drawable.ic_baseline_location_searching_24
            }
            else -> {
                // TODO 기능 구현햐여됨
                // clearTargetMarker()
                _resPlayButtonIcon.value = R.drawable.ic_baseline_location_searching_24
            }
        }
    }

}