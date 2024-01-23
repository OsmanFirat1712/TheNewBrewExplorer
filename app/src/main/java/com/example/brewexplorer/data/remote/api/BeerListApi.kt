package com.example.brewexplorer.data.remote.api

import com.example.brewexplorer.data.remote.model.Beer
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BeerListApi {
    @GET("beers")
    suspend fun getBeers(): List<Beer>
}