package com.example.heremappingapp.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.heremappingapp.*
import com.example.heremappingapp.databinding.FragmentMapBinding
import com.example.heremappingapp.model.SearchFragmentViewModel
import com.example.heremappingapp.utils.PermissionRequester
import com.example.heremappingapp.utils.PlatformLocationProvider
import com.here.sdk.core.*
import com.here.sdk.core.errors.InstantiationErrorException

import com.here.sdk.mapview.MapImageFactory
import com.here.sdk.mapview.MapMarker
import com.here.sdk.mapview.MapPolyline
import com.here.sdk.mapview.MapScene.LoadSceneCallback
import com.here.sdk.mapview.MapScheme
import com.here.sdk.routing.*
import kotlin.collections.ArrayList


class MapFragment : Fragment(R.layout.fragment_map) {
    private var binding: FragmentMapBinding? = null

    private lateinit var platformLocationProvider: PlatformLocationProvider
    private lateinit var permissionRequester: PermissionRequester

    private val sharedSearchViewModel: SearchFragmentViewModel by activityViewModels()

    private lateinit var userLocation: GeoCoordinates
    private var routingEngine: RoutingEngine? = null

    private var mapMarkerList: MutableList<MapMarker> = ArrayList()
    private var mapPolyLines: MutableList<MapPolyline> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        binding?.mapView?.onCreate(savedInstanceState)
//        arguments?.getString("user_location")?.let { Log.d("TAG", it) }

        loadMapScene()
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


    private fun loadMapScene() {
        binding?.mapView?.mapScene?.loadScene(
            MapScheme.NORMAL_DAY,
            LoadSceneCallback { mapError ->
                if (mapError == null) {
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

                            val startWayPoint = Waypoint(userLocation)
                            sharedSearchViewModel.getSearchLocation().observe(viewLifecycleOwner,
                                { searchRes ->
                                    run {
                                        if (searchRes != null) {
                                            clearMap()
                                            val destinationWayPoint =
                                                searchRes.geoCoordinates?.let { Waypoint(it) }
                                            if (destinationWayPoint != null) {
                                                startRouting(startWayPoint, destinationWayPoint)
                                            }
                                        }
                                    }
                                })

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

    //Routing from user Location to Search Location
    private fun startRouting(start: Waypoint, end: Waypoint) {
        clearMap()
        try {
            routingEngine = RoutingEngine()
        }catch (e: InstantiationErrorException) {
            throw RuntimeException("Initialize Routing engined failed" + e.error.name)
        }

        val wayPoints = ArrayList<Waypoint>(listOf(start, end))

        routingEngine!!.calculateRoute(wayPoints,
        CarOptions()
        ) { routingError: RoutingError?, routes: MutableList<Route>? ->
            if (routingError == null) {
                val route = routes?.get(0)
                if (route != null) {
                    showRouteOnMap(route)
                    addMapCircleMarker(end.coordinates, R.drawable.red_dot)
                    //Zoom to route
                    val listSection = route.sections
                    //Instruction for section
                    for (section in listSection) {
                        maneuverInstruction(section)
                    }

                    binding!!.mapView.camera.lookAt(route.boundingBox, GeoOrientationUpdate(null, null))
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun maneuverInstruction(section: Section) {
        val listManeuvers = section.maneuvers
        for (maneuver in listManeuvers) {
            val maneuverAction = maneuver.action
            val maneuverLocation = maneuver.coordinates
            maneuver.lengthInMeters
            Log.d("Directions", maneuver.text +
                    ", Action: " + maneuverAction +
                    ", GeoCoordinates: " + maneuverLocation)
//            binding!!.txtManeuver.text = maneuver.text +
//                    ", Action: " + maneuverAction +
//                    ", GeoCoordinates: " + maneuverLocation.latitude +
//                    ", " + maneuverLocation.longitude
        }
    }

    private fun showRouteOnMap(route: Route) {
        val routeGeoPolyline = GeoPolyline(route.polyline)
        val routeMapPolyline = MapPolyline(routeGeoPolyline, 20.0, Color.valueOf(0.0f, 0.56f, 0.54f, 0.63f))

        binding!!.mapView.mapScene.addMapPolyline(routeMapPolyline)
        mapPolyLines.add(routeMapPolyline)
    }

    private fun addMapCircleMarker(geoCoordinates: GeoCoordinates, resourceId: Int) {
        val mapImage = MapImageFactory.fromResource(context?.resources, resourceId)
        val mapMarker = MapMarker(geoCoordinates, mapImage)
        binding!!.mapView.mapScene.addMapMarker(mapMarker)
        mapMarkerList.add(mapMarker)
    }

    private fun clearMap() {
        for (mapMarker in mapMarkerList)
            binding!!.mapView.mapScene.removeMapMarker(mapMarker)
        mapMarkerList.clear()
        for (mapPolyline in mapPolyLines)
            binding!!.mapView.mapScene.removeMapPolyline(mapPolyline)
        mapPolyLines.clear()
    }
}