package com.example.brewexplorer.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Executes a given suspendable network call and encapsulates the result within a [NetworkResponse].
 *
 * This function handles the network call safely by catching any exceptions that occur during execution.
 * The result of the network call is wrapped in a [NetworkResponse] object, which can either be a
 * [NetworkResponse.Success] containing the data, or a [NetworkResponse.Error] containing the exception.
 *
 * @param T The type of data expected from the network call.
 * @param apiCall The suspendable function representing the network call.
 * @return [NetworkResponse] The response object containing either the successful data (T) or an error.
 * @throws Exception if an error occurs during the network call.
 */

suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResponse<T> {
    return try {
        val result = withContext(Dispatchers.IO) {
            apiCall.invoke()
        }
        NetworkResponse.Success(result)
    } catch (e: Exception) {
        NetworkResponse.Error(e)
    }
}

/**
 * Represents the response from a network call.
 *
 * This sealed class encapsulates the result of a network operation, which can be successful ([Success])
 * containing the data, or a failure ([Error]) containing the throwable that caused the failure.
 */
sealed class NetworkResponse<out T> {
    data class Success<T>(val data: T) : NetworkResponse<T>()
    data class Error(val throwable: Throwable) : NetworkResponse<Nothing>()
}