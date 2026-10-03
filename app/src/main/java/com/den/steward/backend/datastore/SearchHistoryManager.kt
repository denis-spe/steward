// Glory be to LORD our GOD
package com.den.steward.backend.datastore

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "search_history")

class SearchHistoryManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val RECENT_SEARCHES_KEY = stringPreferencesKey("recent_searches")
        private const val MAX_RECENT_SEARCHES = 5
        private const val DELIMITER = "|"
    }

    // List of recent searches
    val recentSearches: Flow<ImmutableList<String>> = context.dataStore.data.catch {
        emit(emptyPreferences())
    }.map { preferences ->
        (preferences[RECENT_SEARCHES_KEY]?.split(DELIMITER) ?: emptyList())
            .toImmutableList()
    }

    // Save new search query
    suspend fun saveSearchQuery(query: String) {
        if (query.isBlank()) return

        context.dataStore.edit { preferences ->
            val currentSearches = preferences[RECENT_SEARCHES_KEY]?.split(DELIMITER) ?: emptyList()
            val updatedSearches = currentSearches.toMutableList().apply {
                remove(query)
                add(0, query)
            }.take(MAX_RECENT_SEARCHES).joinToString(DELIMITER)

            preferences[RECENT_SEARCHES_KEY] = updatedSearches
        }
    }

    // Clear all recent searches
    suspend fun clearRecentSearches() {
        context.dataStore.edit { preferences ->
            preferences.remove(RECENT_SEARCHES_KEY)
        }
    }
}