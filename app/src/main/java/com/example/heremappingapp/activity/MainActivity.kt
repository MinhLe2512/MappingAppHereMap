package com.example.heremappingapp.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.heremappingapp.R
import com.example.heremappingapp.activity.`interface`.PlatformPositioningListener
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapview.MapScene
import com.here.sdk.mapview.MapScheme
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LocationListener{

    private lateinit var locationPlatformListener: PlatformPositioningListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        map_view.onCreate(savedInstanceState)
        loadMapScreen()
        getUserLocation()
    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
    }

    override fun onResume() {
        super.onResume()
        map_view.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        map_view.onDestroy()
    }

    private fun getUserLocation() {
        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Positioning permissions denied", Toast.LENGTH_SHORT).show()
            return
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                this.packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS))
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 100f, this)
        else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 100f, this)
        else
            Toast.makeText(this, "Requesting location is not possible", Toast.LENGTH_SHORT).show()
    }

    private fun loadMapScreen() {
        map_view.mapScene.loadScene(MapScheme.NORMAL_DAY, MapScene.LoadSceneCallback() {
            if (it == null) {
                val distanceInMeters = 1000.0 * 10.0
                map_view.camera.lookAt(GeoCoordinates(14.0583, 108.2772), distanceInMeters)
            } else
                Toast.makeText(this, "Loading map failed", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onLocationChanged(location: Location) {
        locationPlatformListener.onLocationUpdated(location)
    }
}
