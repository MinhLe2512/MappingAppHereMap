package com.example.heremappingapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.here.sdk.search.Place

class SearchFragmentViewModel(): ViewModel() {
    private val searchLocation = MutableLiveData<SearchResultModel>()

    fun setSearchLocation(searchResultModel: SearchResultModel) {
        searchLocation.value = searchResultModel
    }

    fun getSearchLocation(): LiveData<SearchResultModel>{
        return searchLocation
    }
}