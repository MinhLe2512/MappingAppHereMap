package com.example.heremappingapp.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.heremappingapp.R
import com.example.heremappingapp.activity.*
import com.example.heremappingapp.activity.`interface`.PlatformPositioningListener
import com.example.heremappingapp.activity.`interface`.ResultListener
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapview.MapScene
import com.here.sdk.mapview.MapScheme
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), LocationListener{
    private lateinit var permissionManager: PermissionsManager
    private lateinit var locationPlatformListener: PlatformPositioningListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        map_view.onCreate(savedInstanceState)
        permissionHandler()
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

    override fun onLocationChanged(location: Location) {
        locationPlatformListener.onLocationUpdated(location)
    }


    private fun getUserLocation(locationCallBack: PlatformPositioningListener) {
        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        this.locationPlatformListener = locationCallBack

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
        else {
            Toast.makeText(this, "Requesting location is not possible", Toast.LENGTH_SHORT).show()
            locationManager.removeUpdates(this)
        }
    }

    private fun loadMapScreen(location: Location) {
        map_view.mapScene.loadScene(MapScheme.NORMAL_DAY, MapScene.LoadSceneCallback() {
            if (it == null) {
                val distanceInMeters = 1000.0 * 10.0
                map_view.camera.lookAt(GeoCoordinates(location.latitude, location.longitude, location.altitude), distanceInMeters)
            } else {
                Toast.makeText(this, "Loading map failed", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun permissionHandler() {
        permissionManager = PermissionsManager()
        permissionManager.request(object : ResultListener {
            override fun onPermisisonsGranted() {
               getUserLocation(object : PlatformPositioningListener{
                   override fun onLocationUpdated(location: Location) {
                       loadMapScreen(location)
                   }
               })
            }

            override fun onPermissionsDenied() {
                Toast.makeText(this@MainActivity, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionManager.onRequestPermissionsResult(requestCode, grantResults)
    }
    private inner class PermissionsManager {
        private val PERMISSIONS_REQUEST_CODE = 42
        private lateinit var resultListener: ResultListener

        fun request(resultListener: ResultListener) {
            this.resultListener = resultListener

            val missingPermission = getPermissionsToRequest()
            if (missingPermission.size == 0)
                resultListener.onPermisisonsGranted()
            else
                ActivityCompat.requestPermissions(this@MainActivity, missingPermission.toTypedArray(), PERMISSIONS_REQUEST_CODE)
        }

        fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
            if (PERMISSIONS_REQUEST_CODE == requestCode) {
                var allGranted = true
                for (result: Int in grantResults) {
                    allGranted = allGranted and (result == PackageManager.PERMISSION_GRANTED)
                }
                if (allGranted)
                    resultListener.onPermisisonsGranted()
                else
                    resultListener.onPermissionsDenied()
            }
        }


        private fun getPermissionsToRequest(): ArrayList<String> {
            val listPermissions =  ArrayList<String>()

            val packageInfo = this@MainActivity.packageManager.getPackageInfo(this@MainActivity.packageName, PackageManager.GET_PERMISSIONS)

            for (permission: String in packageInfo.requestedPermissions) {
                if (ActivityCompat.checkSelfPermission(this@MainActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && permission == Manifest.permission.CHANGE_NETWORK_STATE)
                        continue
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && permission == Manifest.permission.ACTIVITY_RECOGNITION
                            && permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                continue
                    listPermissions.add(permission)
                }
            }
            return listPermissions
        }
    }
}
