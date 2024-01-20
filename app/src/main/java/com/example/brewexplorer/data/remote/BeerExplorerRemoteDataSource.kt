package com.example.brewexplorer.data.remote

import com.example.brewexplorer.data.remote.api.BeerListApi
import com.example.brewexplorer.data.remote.model.Beer

interface BeerRemoteDataSource {
    suspend fun getBeerList(): NetworkResponse<List<Beer>>

    suspend fun getBeerById(id:Int): NetworkResponse<List<Beer>>

}

class BeerRemoteDataSourceImpl(private val api: BeerListApi) : BeerRemoteDataSource {

    override suspend fun getBeerList() =
        safeApiCall { api.getBeers() }

    override suspend fun getBeerById(id:Int) =
        safeApiCall { api.getBeerById(id) }
    }
