package com.example.brewexplorer.data.remote.model

sealed class RepositoryException(msg: String, throwable: Throwable) : Exception(msg, throwable) {
    data class RemoteException(val throwable: Throwable) :
        RepositoryException("Error occurred in remote data repo", throwable)
}