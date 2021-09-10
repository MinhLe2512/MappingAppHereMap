package com.example.heremappingapp.activity

import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.heremappingapp.R
import com.example.heremappingapp.databinding.ActivityMainBinding
import com.example.heremappingapp.fragment.SearchFragment
import com.here.sdk.mapview.*
import com.here.sdk.search.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlin.collections.ArrayList


@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {

    private lateinit var searchEngine: SearchEngine
    private var listMapMarker = ArrayList<MapMarker>()
    private var listPolyline = ArrayList<MapPolyline>()
    private lateinit var userLocation: Location
    private val PERMISSION_REQUEST_LOCATION = 0
    private val REQUEST_LOCATION_SETTINGS = 1
    private val REQUEST_WIFI_CONNECTION = 2

    private lateinit var binding: ActivityMainBinding
    private var searchFragment: SearchFragment?= null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        floatingButton()
    }

    private fun initView() {
        //Init view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //init Search Fragment
        searchFragment = SearchFragment()
    }

    private fun floatingButton() {
        binding.floatingActionButton.setOnClickListener() {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, searchFragment!!).commit()
        }
    }
}

