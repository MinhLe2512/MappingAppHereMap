package com.example.heremappingapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.heremappingapp.R
import com.example.heremappingapp.databinding.FragmentMapBinding
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapview.MapScene.LoadSceneCallback
import com.here.sdk.mapview.MapScheme


class MapFragment : Fragment(R.layout.fragment_map) {
    private var binding: FragmentMapBinding?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        binding?.mapView?.onCreate(savedInstanceState)
        loadMapScene()
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
                    binding?.mapView?.camera?.lookAt(GeoCoordinates(52.530932, 13.384915), distanceInMeters)
                } else {
//                    Log.d(TAG, "Loading map failed: mapError: " + mapError.name)
                }
            })
    }
}