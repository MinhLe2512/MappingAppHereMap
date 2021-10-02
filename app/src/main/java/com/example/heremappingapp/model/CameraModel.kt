package com.example.heremappingapp.model

import android.util.Log
import com.example.heremappingapp.R
import com.here.sdk.core.Point2D
import com.here.sdk.mapview.MapCamera
import com.here.sdk.mapview.MapCameraObserver

class CameraModel(private val camera: MapCamera) {
    private val mapCameraObserver = MapCameraObserver {
        Log.d("Camera", it.targetCoordinates.longitude.toString() + ", " +
                it.targetCoordinates.latitude.toString())
    }

    fun addObserver() {
        camera.addObserver(mapCameraObserver)
    }

    fun addPrincipalPoint(mapWidthInPixels: Double, mapHeightInPixels: Double): Point2D{
        val newTransformerCenter = Point2D(mapWidthInPixels / 2, mapHeightInPixels * 3 / 4)
        camera.principalPoint = newTransformerCenter
        return newTransformerCenter
    }
}