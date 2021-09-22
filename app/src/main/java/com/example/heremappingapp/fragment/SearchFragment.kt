package com.example.heremappingapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.heremappingapp.R
import com.example.heremappingapp.`class`.UserLocation
import com.example.heremappingapp.databinding.FragmentSearchBinding
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.LanguageCode
import com.here.sdk.core.errors.InstantiationErrorException
import com.here.sdk.search.*


class SearchFragment : Fragment(R.layout.fragment_search) {
    private var binding: FragmentSearchBinding? = null
    private var searchEngine: SearchEngine? = null

    private val maxItems = 30
    private val maxSuggestedItems = 5
    private var centerGeoCoordinates: GeoCoordinates? = null
    private var searchOptions: SearchOptions? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        initSearch()
        handleBackButton()
        handleSearch()
        return binding?.root
    }

    private fun handleBackButton() {
        binding!!.imgBtn.setOnClickListener{
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction().remove(this).commit()
        }
    }

    private fun initSearch() {
        try{
            searchEngine = SearchEngine()
        } catch (e: InstantiationErrorException) {
            throw RuntimeException("Initialization of SearchEngine failed: " + e.error.name)
        }
        searchOptions = SearchOptions(LanguageCode.EN_US, maxSuggestedItems)
        val userLocation = arguments?.getSerializable("user_location") as UserLocation
        centerGeoCoordinates = userLocation.centerGeoCoordinates
    }

    private fun handleSearch() {

        binding!!.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchEngine!!.suggest(
                    TextQuery(newText!!, centerGeoCoordinates!!), searchOptions!!, autoSuggestCallback
                )
                return true
            }

        })
    }

    private final var autoSuggestCallback = SuggestCallback(){ searchError: SearchError?, mutableList: MutableList<Suggestion>? ->
        if (searchError != null) {
            Toast.makeText(activity, searchError.name, Toast.LENGTH_LONG).show()
            return@SuggestCallback
        }
        if (mutableList != null) {
            Log.d("LOG_TAG", "Autosuggest results: " + mutableList.size)
        }

        if (mutableList != null) {
            for (autoSuggestResult in mutableList) {
                var addressText = "Not a place."
                val place = autoSuggestResult.place
                if (place != null) {
                    addressText = place.address.addressText
                }
                Log.d(
                    "LOG_TAG", "Autosuggest result: " + autoSuggestResult.title +
                            " addressText: " + addressText
                )
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}