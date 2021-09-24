package com.example.heremappingapp.activity

import androidx.fragment.app.Fragment
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.heremappingapp.R
import com.example.heremappingapp.`class`.UserLocation
import com.example.heremappingapp.databinding.ActivityMainBinding
import com.example.heremappingapp.fragment.MapFragment
import com.example.heremappingapp.fragment.SearchFragment
import com.here.sdk.mapview.*
import com.here.sdk.search.*


@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var searchFragment: SearchFragment? = null
    private var mapFragment: MapFragment? = null
//    private var favoriteFragment: FavoriteFragment? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        mapFragment?.let { setCurrentFragment(it) }
//        floatingButton()
    }

    private fun initView() {
        //Init view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //init Search Fragment
        searchFragment = SearchFragment()
        mapFragment = MapFragment()
//        favoriteFragment = FavoriteFragment()
        binding.navBotView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mapFragment -> setCurrentFragment(mapFragment!!)
//                R.id.favFragment -> setCurrentFragment(favoriteFragment!!)
            }
            false
        }
        setUpFABButton()
    }

    private fun setUpFABButton() {
        binding.fabSearch.setOnClickListener {
            searchFragment?.let { it1 ->
                val centerGeoCoordinates = mapFragment!!.getCenterViewMap()
                val userLocation = UserLocation(centerGeoCoordinates)

                val bundle = Bundle()
                bundle.putSerializable("user_location", userLocation)
                searchFragment!!.arguments = bundle;
                supportFragmentManager.beginTransaction().add(R.id.search_container, it1)
                    .commit()
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.map_container, fragment).commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

    


