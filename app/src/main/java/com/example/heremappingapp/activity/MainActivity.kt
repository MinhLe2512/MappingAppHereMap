package com.example.heremappingapp.activity

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.heremappingapp.R
import com.example.heremappingapp.databinding.ActivityMainBinding

import com.example.heremappingapp.fragment.MapFragment
import com.example.heremappingapp.fragment.SearchFragment
import com.example.heremappingapp.model.SearchFragmentViewModel
import com.example.heremappingapp.model.MainActivityViewModel


@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var searchFragment = SearchFragment()
    private var mapFragment = MapFragment()
//    private var favoriteFragment: FavoriteFragment? = null

    private lateinit var searchResultModel: SearchFragmentViewModel
    private lateinit var userLocationModel: MainActivityViewModel

    var FINE_LOCATION_REQUEST: Int = 888
    var TAG: String = "MainActivity"


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        floatingButton()
        initView()
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
    }

    private fun initTxtView() {
        searchResultModel = ViewModelProvider(this)[SearchFragmentViewModel::class.java]
        searchResultModel.getSearchLocation().observe(this,
            { searchResult -> binding.txtSearchRes.text = searchResult?.address })

    }

}

    


