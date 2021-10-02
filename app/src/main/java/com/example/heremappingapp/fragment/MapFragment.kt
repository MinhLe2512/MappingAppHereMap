package com.example.heremappingapp.fragment

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.heremappingapp.*
import com.example.heremappingapp.model.UserLocationSharedViewModel
import com.example.heremappingapp.databinding.FragmentMapBinding
import com.example.heremappingapp.model.CameraModel
import com.example.heremappingapp.utils.PermissionRequester
import com.example.heremappingapp.utils.PlatformLocationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.here.sdk.core.*
import com.here.sdk.mapview.MapCamera
import com.here.sdk.mapview.MapImageFactory
import com.here.sdk.mapview.MapMarker
import com.here.sdk.mapview.MapScene.LoadSceneCallback
import com.here.sdk.mapview.MapScheme


class MapFragment : Fragment(R.layout.fragment_map) {
    private var binding: FragmentMapBinding? = null

    private lateinit var platformLocationProvider: PlatformLocationProvider
    private lateinit var permissionRequester: PermissionRequester

    private lateinit var userLocation: GeoCoordinates
    private lateinit var camera: CameraModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        binding?.mapView?.onCreate(savedInstanceState)
//        arguments?.getString("user_location")?.let { Log.d("TAG", it) }

        loadMapScene()
        initFabButton()
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionRequester.onRequestPermissionsRequest(requestCode, grantResults)
    }

    private fun initFabButton() {
        binding!!.fabLocation.setOnClickListener{
//            locateUserLocation()
            onMapReady()
        }
    }

    private fun onMapReady() {
        camera = CameraModel(binding!!.mapView.camera)
        camera.addObserver()
        val transformCenter = camera.addPrincipalPoint(binding!!.mapView.width.toDouble(),
            binding!!.mapView.height.toDouble())
        val cameraTargetView = binding!!.catPolite
        cameraTargetView.x = transformCenter.x.toFloat() - cameraTargetView.x / 2
        cameraTargetView.y = transformCenter.y.toFloat() - cameraTargetView.y / 2

    }

    private fun loadMapScene() {
        binding?.mapView?.mapScene?.loadScene(
            MapScheme.NORMAL_DAY,
            LoadSceneCallback { mapError ->
                if (mapError == null) {
                    onMapReady()
                    locateUserLocation()
                }
            })
    }

    private fun locateUserLocation() {
        permissionRequester = PermissionRequester(requireActivity())
        permissionRequester.request(object : PermissionRequester.ResultListener {
            override fun permissionsGranted() {
                platformLocationProvider = PlatformLocationProvider(requireContext())
                platformLocationProvider.startLocating(object : PlatformLocationProvider.LocationPlatformListener {
                    override fun onLocationUpdated(location: android.location.Location?) {
                        if (location != null) {
                            Toast.makeText(requireContext(), location.latitude.toString(), Toast.LENGTH_SHORT).show()
                            userLocation = GeoCoordinates(location.latitude, location.longitude)

                            handleCamera(userLocation)
                            addPoiMapMarker(userLocation)

                            platformLocationProvider.stopLocating()
                        }
                        super.onLocationUpdated(location)
                    }
                })
            }
            override fun permissionsDenied() {
                permissionRequester.checkForPermissions(Manifest.permission.ACCESS_FINE_LOCATION, "fine_location", 888)
            }

        })
    }

    private fun createPoiMapMarker(geoCoordinates: GeoCoordinates): MapMarker {
        val mapImage = MapImageFactory.fromResource(resources, R.drawable.poi)
        return MapMarker(geoCoordinates, mapImage, Anchor2D(0.5, 1.0))
    }

    private fun addPoiMapMarker(geoCoordinates: GeoCoordinates) {
        val mapMarker: MapMarker = createPoiMapMarker(geoCoordinates)
        binding!!.mapView.mapScene.addMapMarker(mapMarker)
//        mapMarkerList.add(mapMarker)
    }

    private val bearingInDegrees: Double = 90.0
    private val tiltInDegree: Double = 0.0
    private val distanceInMeters: Double = 1000.0 * 7.0

    private fun handleCamera(geoCoordinates: GeoCoordinates) {
        val geoOrientationUpdate = GeoOrientationUpdate(bearingInDegrees, tiltInDegree)
        binding!!.mapView.camera.lookAt(geoCoordinates, geoOrientationUpdate, distanceInMeters)
    }

    fun getCenterViewMap(): GeoCoordinates {
        return binding!!.mapView.viewToGeoCoordinates(
            Point2D(binding!!.mapView.width / 2.0, binding!!.mapView.height / 2.0)
        ) ?: throw RuntimeException("CenterGeoCoordinates are null")
    }

}