package com.example.brewexplorer.data

import com.example.brewexplorer.data.remote.BeerRemoteDataSource
import com.example.brewexplorer.data.remote.NetworkResponse
import com.example.brewexplorer.data.remote.model.Beer
import com.example.brewexplorer.data.remote.model.RepositoryException
import com.example.brewexplorer.data.remote.model.RepositoryResponse
import com.example.brewexplorer.ui.theme.translater.Transle
import com.example.brewexplorer.ui.theme.translater.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface BeerDataSource {
    suspend fun getBeerList(): RepositoryResponse<List<Beer>>

}


class BeerDataSourceImpl (private val remoteDataSource: BeerRemoteDataSource) : BeerDataSource {


    override suspend fun getBeerList(): RepositoryResponse<List<Beer>> =
        remoteDataSource.getBeerList().let { networkResponse ->
            when (networkResponse) {

                is NetworkResponse.Error -> RepositoryResponse.Error(
                    RepositoryException.RemoteException(
                        networkResponse.throwable
                    )
                )

                is NetworkResponse.Success -> {
                    RepositoryResponse.Success(
                        networkResponse.data.map {
                         /*   test.text = it.tagline
                            var testus = ""
                            GlobalScope.launch(Dispatchers.IO) {
                                test.test.onEach {
                                    testus = it
                                }.launchIn(this)


                            }*/

                            Beer(
                                id = it.id,
                                name = it.name,
                                imageUrl = it.imageUrl,
                                abv = it.abv,
                                description = it.description,
                                tagline = it.tagline,
                                brewersTips = it.brewersTips,
                                boilVolume = it.boilVolume,
                                ingredients = it.ingredients,

                            )
                        }
                    )
                }

            }
        }
}

