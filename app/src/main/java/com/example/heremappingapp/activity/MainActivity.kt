package com.example.heremappingapp.activity

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
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
        toolBarController()

    }

    private fun toolBarController() {
        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.mainToolbar.setNavigationOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.search_container, searchFragment!!)
                .commit()
        }
        searchFragment = SearchFragment()
    }

    private fun navBetweenFragments() {
        val navController = findNavController(R.id.map_container)
        binding.navBotView.setupWithNavController(navController)
    }
}

