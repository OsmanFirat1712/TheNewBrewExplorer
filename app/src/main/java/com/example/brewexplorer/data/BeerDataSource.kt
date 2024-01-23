package com.example.brewexplorer.data

import com.example.brewexplorer.data.remote.BeerRemoteDataSource
import com.example.brewexplorer.data.remote.NetworkResponse
import com.example.brewexplorer.data.remote.model.Beer
import com.example.brewexplorer.data.remote.model.RepositoryException
import com.example.brewexplorer.data.remote.model.RepositoryResponse

/**
 * Interface defining the data source for retrieving beer information.
 */
interface BeerDataSource {
    /**
     * Fetches a list of beers from the data source.
     *
     * @return A RepositoryResponse which can be a Success with a list of Beer objects or an Error.
     */
    suspend fun getBeerList(): RepositoryResponse<List<Beer>>
}


/**
 * Implementation of BeerDataSource that retrieves beer data from a remote source.
 *
 * @property remoteDataSource The remote data source to fetch beer data.
 */
class BeerDataSourceImpl(private val remoteDataSource: BeerRemoteDataSource) : BeerDataSource {

    /**
     * Fetches a list of beers from the remote data source.
     * Maps the NetworkResponse from the remote data source to a RepositoryResponse.
     *
     * @return A RepositoryResponse containing either a list of Beer objects (Success)
     *         or an error (Error).
     */
    override suspend fun getBeerList(): RepositoryResponse<List<Beer>> =
        remoteDataSource.getBeerList().let { networkResponse ->
            when (networkResponse) {
                // In case of network error, wrap the throwable in a RepositoryException and return.
                is NetworkResponse.Error -> RepositoryResponse.Error(
                    RepositoryException.RemoteException(
                        networkResponse.throwable
                    )
                )

                // On success, map the data to Beer domain objects and return.
                is NetworkResponse.Success -> {
                    RepositoryResponse.Success(
                        networkResponse.data.map {
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


