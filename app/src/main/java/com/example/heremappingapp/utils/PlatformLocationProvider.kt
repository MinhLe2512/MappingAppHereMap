package com.example.heremappingapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import com.here.sdk.core.GeoCoordinates
import java.util.*
const val LOCATION_UPDATE_INTERVAL_IN_MS: Long = 100

class PlatformLocationProvider(private val context: Context): LocationListener {
    private lateinit var locationManager: LocationManager

    private var locationPlatformListener: LocationPlatformListener? = null

    interface LocationPlatformListener {
        fun onLocationUpdated(location: Location?) {}
    }

    override fun onLocationChanged(location: Location) {
        if (locationPlatformListener != null)
            locationPlatformListener?.onLocationUpdated(location)
    }

    @SuppressLint("MissingPermission")
    fun startLocating(locationCallback: LocationPlatformListener) {
        if (locationPlatformListener != null) {
            throw RuntimeException("Please stop locating before trying again.")
        }
        locationPlatformListener = locationCallback
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS))
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL_IN_MS,
            1.0f, this
        )
        else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVAL_IN_MS, 1.0f, this
            )
        else {
            Log.d(PlatformLocationProvider::class.simpleName, "Positioning not possible")
            stopLocating()
        }
    }

    fun stopLocating() {
        locationManager.removeUpdates(this)
        locationPlatformListener = null
    }

    fun convertLocation(nativeLocation: Location): com.here.sdk.core.Location {
        val geoCoordinates = GeoCoordinates(nativeLocation.latitude, nativeLocation.longitude, nativeLocation.altitude)
        val location = com.here.sdk.core.Location(geoCoordinates, Date())
        if (nativeLocation.hasBearing()) {
            location.bearingInDegrees = nativeLocation.bearing.toDouble()
        }
        if (nativeLocation.hasSpeed()) {
            location.speedInMetersPerSecond = nativeLocation.speed.toDouble()
        }
        if (nativeLocation.hasAccuracy()) {
            location.horizontalAccuracyInMeters = nativeLocation.accuracy.toDouble()
        }
        return location
    }
}