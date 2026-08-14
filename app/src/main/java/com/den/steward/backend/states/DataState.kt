// Bless be to LORD of hosts
package com.den.steward.backend.states

sealed class DataState<out T> {
    data object Loading : DataState<Nothing>()
    data object Empty : DataState<Nothing>()
    data class Success<T>(val data: T) : DataState<T>()
    data class Error(val message: String) : DataState<Nothing>()
}
