package com.example.brewexplorer.data.remote

import com.example.brewexplorer.data.remote.api.BeerListApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Defines a Koin module for setting up the network layer of the application.
 * This module configures various network components using Koin, a popular Kotlin dependency injection framework.
 */

internal val remoteModule = module {
    // Provides a HttpLoggingInterceptor with the log level set to BODY, enabling detailed logging of requests and responses.
    single { HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY } }

    // Provides an OkHttpClient instance, incorporating the HttpLoggingInterceptor for logging network requests.
    single { provideOkHttpClient(httpLoggingInterceptor = get()) }

    // Provides a Retrofit service for the API, configured with the OkHttpClient and Gson converter factory.
    single { provideGitHubApi(okHttpClient = get()) }

    // Provides an implementation of the BeerRemoteDataSourceImpl, injecting the GitHub API service.
    single<BeerRemoteDataSource> { BeerRemoteDataSourceImpl(api = get()) }
}

private fun provideOkHttpClient(
    httpLoggingInterceptor: HttpLoggingInterceptor
) = OkHttpClient.Builder()
    .apply {
        addInterceptor(httpLoggingInterceptor)
    }
    .build()

private fun provideGitHubApi(
    okHttpClient: OkHttpClient
) = Retrofit.Builder()
    .baseUrl("https://api.punkapi.com/v2/")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(BeerListApi::class.java)