package com.example.heremappingapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heremappingapp.R
import com.example.heremappingapp.`interface`.SearchResultClick
import com.example.heremappingapp.databinding.FragmentSearchBinding
import com.example.heremappingapp.model.SearchLocationSharedViewModel
import com.example.heremappingapp.model.SearchResultModel
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.LanguageCode
import com.here.sdk.core.errors.InstantiationErrorException
import com.here.sdk.search.*
import kotlinx.android.synthetic.main.activity_main.*


class SearchFragment : Fragment(R.layout.fragment_search) {
    private var binding: FragmentSearchBinding? = null
    private var searchEngine: SearchEngine? = null

    private val maxItems = 30
    private val maxSuggestedItems = 10
    private var centerGeoCoordinates = GeoCoordinates(10.762307, 106.640608)
    private var searchOptions: SearchOptions? = null
    private val searchLocationSharedViewModel: SearchLocationSharedViewModel by activityViewModels()


    private var adapter: ResultSearchAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        initSearch()
        handleBackButton()
        handleSearch()

        return binding?.root
    }

    private fun handleBackButton() {
        binding!!.backBtn.setOnClickListener {
            closeSearchFragment()
        }
    }

    private fun closeSearchFragment() {
        (activity as AppCompatActivity).supportFragmentManager.beginTransaction().remove(this).commit()
    }
    private fun initSearch() {
        try {
            searchEngine = SearchEngine()
        } catch (e: InstantiationErrorException) {
            throw RuntimeException("Initialization of SearchEngine failed: " + e.error.name)
        }
        searchOptions = SearchOptions(LanguageCode.EN_US, maxSuggestedItems)
//        val userLocation = arguments?.getSerializable("user_location") as UserLocation
//        centerGeoCoordinates = userLocation.geoCoordinates.coordinates
    }

    private fun initRecyclerView(list: MutableList<Place>) {
        binding!!.searchRes.layoutManager = LinearLayoutManager(activity)
        adapter = ResultSearchAdapter(list, object: SearchResultClick{
            override fun onSearchResultClick(searchResultModel: SearchResultModel?) {
                Toast.makeText(context, searchResultModel?.address, Toast.LENGTH_SHORT).show()
                closeSearchFragment()
                if (searchResultModel != null) {
                    searchLocationSharedViewModel.setSearchLocation(searchResultModel)
                }
            }

        })
        binding!!.searchRes.adapter = adapter


        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding!!.searchRes.addItemDecoration(itemDecoration)
    }

    private fun handleSearch() {
        binding!!.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchEngine!!.suggest(
                    TextQuery(newText!!, centerGeoCoordinates), searchOptions!!, autoSuggestCallback)
                return true
            }
        })
    }

    private var autoSuggestCallback =
        SuggestCallback { searchError: SearchError?, mutableList: MutableList<Suggestion>? ->
            if (searchError != null) {
                Log.d("Search_error", searchError.name)
                return@SuggestCallback
            }

            if (mutableList != null) {
                val list =  mutableListOf<Place>()
                for (suggestion in mutableList) {
                    val place = suggestion.place
                    if (place != null)
                        list.add(place)
                }
                initRecyclerView(list)
                return@SuggestCallback
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private inner class ResultSearchAdapter(private val list: MutableList<Place>, private var searchResultClick: SearchResultClick)
        : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_search_result, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val itemsViewSearchRes = list[position]
            holder.titleView.text = itemsViewSearchRes.title
            holder.addressView.text = itemsViewSearchRes.address.addressText

            holder.layoutSearchRes.setOnClickListener{
                searchResultClick.onSearchResultClick(SearchResultModel(itemsViewSearchRes.address.addressText,
                    itemsViewSearchRes.geoCoordinates, itemsViewSearchRes.id, itemsViewSearchRes.placeType, itemsViewSearchRes.title))
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }
    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.txt_title)
        val addressView: TextView = view.findViewById(R.id.txt_address)
        val layoutSearchRes: LinearLayout = view.findViewById(R.id.layout_search_res)
    }
}


