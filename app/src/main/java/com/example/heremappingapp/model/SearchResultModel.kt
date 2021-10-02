package com.example.heremappingapp.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.search.PlaceType

class SearchResultModel(
    val address: String,
    val geoCoordinates: GeoCoordinates?,
    val id: String,
    val placeType: PlaceType,
    val title: String
)
