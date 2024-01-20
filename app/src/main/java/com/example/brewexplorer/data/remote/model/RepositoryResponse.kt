package com.example.brewexplorer.data.remote.model

sealed class RepositoryResponse<out T> {
    data class Success<T>(val data: T) : RepositoryResponse<T>()
    data class Error(val repositoryException: RepositoryException) : RepositoryResponse<Nothing>()
}