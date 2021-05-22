package com.example.heremappingapp.activity.`interface`

import android.location.Location

interface PlatformPositioningListener {
    fun onLocationUpdated(location: Location)
}