package com.example.fitnesstracker.data.managers

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.managers.PreferencesManager.PreferencesKeys.FIRST_TIME
import com.example.fitnesstracker.data.managers.PreferencesManager.PreferencesKeys.NAME
import com.example.fitnesstracker.data.managers.PreferencesManager.PreferencesKeys.SORT_METHOD
import com.example.fitnesstracker.data.managers.PreferencesManager.PreferencesKeys.WEIGHT
import com.example.fitnesstracker.data.models.Error
import com.example.fitnesstracker.data.models.SortMethod
import com.example.fitnesstracker.data.models.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val PREFERENCES_NAME = "settings"
        private const val SORT_TAG = "sort_method"
        private const val NAME_TAG = "name"
        private const val WEIGHT_TAG = "weight"
        private const val FIRST_TIME_TAG = "first_time"
    }

    private object PreferencesKeys {
        val SORT_METHOD = intPreferencesKey(SORT_TAG)
        val NAME = stringPreferencesKey(NAME_TAG)
        val WEIGHT = doublePreferencesKey(WEIGHT_TAG)
        val FIRST_TIME = booleanPreferencesKey(FIRST_TIME_TAG)
    }

    private val Context.dataStore by preferencesDataStore(PREFERENCES_NAME)

    private val dataStore = context.dataStore

    val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Timber.e(exception, "Error reading preferences")
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            mapUserPreferences(it)
        }

    private val _errorFlow = MutableSharedFlow<Error>()
    val errorFlow = _errorFlow.asSharedFlow()

    suspend fun updateSortMethod(sortMethod: Int) {
        if (sortMethod !in 0..SortMethod.values().size) {
            _errorFlow.emit(Error(context.getString(R.string.error_sort_doesnt_exist)))
            return
        }
        dataStore.edit { preferences ->
            preferences[SORT_METHOD] = sortMethod
        }
    }

    suspend fun updateName(name: String) {
        dataStore.edit { preferences ->
            preferences[NAME] = name
        }
    }

    suspend fun updateWeight(weight: Double) {
        dataStore.edit { preferences ->
            preferences[WEIGHT] = weight
        }
    }
    suspend fun updateIsFirstFirstTime(isFirst: Boolean) {
        dataStore.edit { preferences ->
            preferences[FIRST_TIME] = isFirst
        }
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val sortMethod = SortMethod.fromOrdinal(preferences[SORT_METHOD] ?: 0)
        val name = preferences[NAME] ?: context.getString(R.string.user)
        val weight = preferences[WEIGHT] ?: 0.0
        val isFirstTime = preferences[FIRST_TIME] ?: true
        return UserPreferences(sortMethod, name, weight, isFirstTime)
    }
}