package com.sugaryple.fakelocation.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sugaryple.fakelocation.core.Event

class MapsViewModel: ViewModel() {

    private val _clickEventPlay = MutableLiveData<Event<Unit>>()
    val clickEventPlay: LiveData<Event<Unit>>
        get() = _clickEventPlay

    fun onClickPlay() {
        _clickEventPlay.value = Event(Unit)
    }

}