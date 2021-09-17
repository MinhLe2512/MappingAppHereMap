package com.example.heremappingapp.activity

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.heremappingapp.R
import com.example.heremappingapp.databinding.ActivityMainBinding
import com.example.heremappingapp.fragment.SearchFragment
import com.here.sdk.mapview.*
import com.here.sdk.search.*


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
    private var searchFragment: SearchFragment? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        navBetweenFragments()
//        floatingButton()
    }

    private fun initView() {
        //Init view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //init Search Fragment
        searchFragment = SearchFragment()
        setUpEditText()

    }

    private fun setUpEditText() {

        binding.edtTxt.setOnFocusChangeListener { _, _ ->
            supportFragmentManager.beginTransaction().replace(
                R.id.search_container,
                searchFragment!!
            ).commit() }

    }

    private fun navBetweenFragments() {
        val navController = findNavController(R.id.map_container)
        binding.navBotView.setupWithNavController(navController)
    }
}

