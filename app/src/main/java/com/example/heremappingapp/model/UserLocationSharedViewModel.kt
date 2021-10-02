package com.example.heremappingapp.model

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class UserLocationSharedViewModel : ViewModel() {
    private val userCoordinates = MutableLiveData<Location>()

    fun setUserLocation(location: Location) {
        userCoordinates.value = location
    }

    fun getUserLocation(): LiveData<Location> {
        return userCoordinates
    }
}