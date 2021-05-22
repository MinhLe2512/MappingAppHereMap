package com.example.heremappingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapview.MapScene
import com.here.sdk.mapview.MapScheme
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        map_view.onCreate(savedInstanceState)
        loadMapScreen()
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

    private fun loadMapScreen() {
        map_view.mapScene.loadScene(MapScheme.NORMAL_DAY, MapScene.LoadSceneCallback() {
            if (it == null) {
                val distanceInMeters = 1000.0 * 10.0
                map_view.camera.lookAt(GeoCoordinates(14.0583, 108.2772), distanceInMeters)
            }
            else
                Toast.makeText(this, "Loading map failed", Toast.LENGTH_SHORT).show()
        })
    }
}