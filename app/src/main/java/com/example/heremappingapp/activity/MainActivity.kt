package com.example.heremappingapp.activity

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.heremappingapp.R
import com.example.heremappingapp.databinding.ActivityMainBinding

import com.example.heremappingapp.fragment.SearchFragment
import com.example.heremappingapp.model.SearchFragmentViewModel


@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var searchFragment = SearchFragment()

    private lateinit var searchResultModel: SearchFragmentViewModel

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

    


