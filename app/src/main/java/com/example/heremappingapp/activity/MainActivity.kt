package com.example.heremappingapp.activity

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toolbar
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
        //searchCities()
        setSupportActionBar(binding.mainToolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val menuItem = menu?.findItem(R.id.action_search)
        val searchView = menuItem?.actionView as SearchView
        menuItem.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener,
            MenuItem.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                Log.d("Checking", " Pressed toolbar")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, searchFragment!!)
                    .commit()
                return true
            }

        })
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun navBetweenFragments() {
        val navController = findNavController(R.id.fragment_container)
        binding.navBotView.setupWithNavController(navController)
    }
}

