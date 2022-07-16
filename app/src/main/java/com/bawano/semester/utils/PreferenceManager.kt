package com.bawano.semester.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bawano.semester.models.LastPage
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


enum class SortOrder { BY_NAME, BY_DATE }

class PreferenceManager constructor(val context: Context) {

    val filterPreferences = context.dataStore.data.map {
        val sortOrder = SortOrder.valueOf(
            it[PreferenceKeys.SORT_KEY] ?: SortOrder.BY_DATE.name
        )
        val completed = it[PreferenceKeys.COMPLETED_KEY] ?: true
        FilterPreferences(sortOrder, completed)
    }

    val lastPage: Flow<LastPage> = context.dataStore.data.map {
        val stringData = it[PreferenceKeys.LAST_PAGE_KEY]
        if (stringData.isNullOrBlank() || stringData.isEmpty())return@map LastPage()
        val page: LastPage = Gson().fromJson(stringData, LastPage::class.java)
        page
    }

    suspend fun putLastPage(page: LastPage) = context.dataStore.edit {
        it[PreferenceKeys.LAST_PAGE_KEY] = Gson().toJson(page)
    }

    private object PreferenceKeys {
        val LAST_PAGE_KEY = stringPreferencesKey("LastPage")
        val SORT_KEY = stringPreferencesKey("SortOrder")
        val COMPLETED_KEY = booleanPreferencesKey("Completed")
    }

    suspend fun setSortFilter(sort: SortOrder) = context.dataStore.edit {
        it[PreferenceKeys.SORT_KEY] = sort.name
    }

    suspend fun setCompletedFilter(completed: Boolean) = context.dataStore.edit {
        it[PreferenceKeys.COMPLETED_KEY] = completed
    }

}

data class FilterPreferences(val sortOrder: SortOrder, val showCompleted: Boolean)
