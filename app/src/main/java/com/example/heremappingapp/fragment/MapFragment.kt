package com.example.heremappingapp.fragment

import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.heremappingapp.*
import com.example.heremappingapp.`class`.PermissionRequestor
import com.example.heremappingapp.`class`.PlatformPositioningProvider
import com.example.heremappingapp.databinding.FragmentMapBinding
import com.here.sdk.core.Anchor2D
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.Point2D
import com.here.sdk.mapview.MapImage
import com.here.sdk.mapview.MapImageFactory
import com.here.sdk.mapview.MapMarker
import com.here.sdk.mapview.MapScene.LoadSceneCallback
import com.here.sdk.mapview.MapScheme
import java.util.*


class   MapFragment : Fragment(R.layout.fragment_map) {
    private var binding: FragmentMapBinding?= null
    private var permissionRequestor: PermissionRequestor? = null
    private var platformPositioningProvider: PlatformPositioningProvider? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        binding?.mapView?.onCreate(savedInstanceState)
        handleAndroidPermissions()
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun loadMapScene() {
        binding?.mapView?.mapScene?.loadScene(MapScheme.NORMAL_DAY,
            LoadSceneCallback { mapError ->
                if (mapError == null) {
                    val distanceInMeters = (1000 * 10).toDouble()
                    platformPositioningProvider = PlatformPositioningProvider(requireActivity())
                    platformPositioningProvider!!.startLocating(object :
                        PlatformPositioningProvider.PlatformLocationListener {
                        override fun onLocationUpdated(location: Location?) {
                            val userLocation = location?.let { convertLocation(it) }
                            userLocation?.let {
                                addPoiMapMarker(it.coordinates)
                                binding!!.mapView.camera.lookAt((userLocation.coordinates))}
                        }

                    })
                } else {
                    Log.d("MapFragment", "Loading map failed: mapError: " + mapError.name)
                }
            })
    }

    private fun handleAndroidPermissions() {
        permissionRequestor = activity?.let { PermissionRequestor(it) }
        permissionRequestor?.request(object : PermissionRequestor.ResultListener {
            override fun permissionsGranted() {
                loadMapScene()
            }

            override fun permissionsDenied() {
                Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
       permissionRequestor?.onRequestPermissionsRequest(requestCode, grantResults)
    }

    private fun createPoiMapMarker(geoCoordinates: GeoCoordinates): MapMarker {
        val mapImage = MapImageFactory.fromResource(resources, R.drawable.poi)
        return MapMarker(geoCoordinates, mapImage, Anchor2D(0.5, 1.0))
    }

    private fun addPoiMapMarker(geoCoordinates: GeoCoordinates) {
        val mapMarker: MapMarker = createPoiMapMarker(geoCoordinates)
        binding!!.mapView.mapScene.addMapMarker(mapMarker)
//        mapMarkerList.add(mapMarker)
        platformPositioningProvider!!.stopLocating()
    }

    fun getCenterViewMap(): GeoCoordinates {
        return binding!!.mapView.viewToGeoCoordinates(
            Point2D(
                binding!!.mapView.width / 2.0,
                binding!!.mapView.height / 2.0
            )
        ) ?: throw RuntimeException("CenterGeoCoordinates are null")
    }

    private fun convertLocation(nativeLocation: Location): com.here.sdk.core.Location? {
        val geoCoordinates = GeoCoordinates(
            nativeLocation.latitude,
            nativeLocation.longitude,
            nativeLocation.altitude
        )
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