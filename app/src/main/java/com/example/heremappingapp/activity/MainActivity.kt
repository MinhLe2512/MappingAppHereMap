package com.example.heremappingapp.activity

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.heremappingapp.R
import com.example.heremappingapp.databinding.ActivityMainBinding
import com.example.heremappingapp.fragment.MapFragment
import com.example.heremappingapp.fragment.SearchFragment
import com.example.heremappingapp.model.SearchLocationSharedViewModel
import com.example.heremappingapp.model.UserLocationSharedViewModel


@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var searchFragment = SearchFragment()
    private var mapFragment = MapFragment()
//    private var favoriteFragment: FavoriteFragment? = null

    private lateinit var searchResultModel: SearchLocationSharedViewModel
    private lateinit var userLocationModel: UserLocationSharedViewModel

    var FINE_LOCATION_REQUEST: Int = 888
    var TAG: String = "MainActivity"


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        floatingButton()
        initView()
        initNavBot()
        initTxtView()
    }

    private fun initView() {
        //Init view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtSearchRes.setOnClickListener{
            Toast.makeText(this, "Touch TextView", Toast.LENGTH_SHORT).show()
            supportFragmentManager.beginTransaction().add(R.id.search_container, searchFragment).commit()
        }
        setCurrentFragment(mapFragment)
    }

    private fun initTxtView() {
        searchResultModel = ViewModelProvider(this)[SearchLocationSharedViewModel::class.java]
        searchResultModel.getSearchLocation().observe(this,
            { searchResult -> binding.txtSearchRes.text = searchResult?.address })
    }

    private fun initNavBot() {
        binding.navBotView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mapFragment -> setCurrentFragment(mapFragment)
//                R.id.favFragment -> setCurrentFragment(favoriteFragment!!)
            }
            false
        }
    }

    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.map_container, fragment).commit()
    }

}

    


