package com.example.heremappingapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchLocationSharedViewModel(): ViewModel() {
    private val searchLocation = MutableLiveData<SearchResultModel>()

    fun setSearchLocation(searchResultModel: SearchResultModel) {
        searchLocation.value = searchResultModel
    }

    fun getSearchLocation(): LiveData<SearchResultModel>{
        return searchLocation
    }
}