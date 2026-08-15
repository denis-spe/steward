// Bless be to LORD of hosts
package com.den.steward.backend.states

sealed interface DataState<out T> {
    data object Loading : DataState<Nothing>
    data class Success<T>(val data: T) : DataState<T> {
        val isEmpty: Boolean
            get() = data is List<*> && data.isEmpty()
    }
    data class Error(val message: String) : DataState<Nothing>
}
