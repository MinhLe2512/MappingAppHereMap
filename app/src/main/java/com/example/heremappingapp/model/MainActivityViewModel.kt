package com.example.heremappingapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.here.sdk.core.Location
import com.here.sdk.search.Place


class MainActivityViewModel : ViewModel() {
    private val userCoordinates = MutableLiveData<Location>()
    private val userPlace = MutableLiveData<Place>()

    fun setUserPlace(place: Place) {
        userPlace.value = place
    }

    fun setUserLocation(location: Location) {
        userCoordinates.value = location
    }

    fun getUserLocation(): LiveData<Location> {
        return userCoordinates
    }

    fun getUserPlace(): LiveData<Place>{
        return userPlace
    }
}