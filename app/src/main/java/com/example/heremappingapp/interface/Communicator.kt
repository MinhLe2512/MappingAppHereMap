package com.example.heremappingapp.`interface`

import com.here.sdk.core.Location

interface Communicator {
    fun passUserLocation(userLocation: Location)
}