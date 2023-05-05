package dev.sourav.base.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hushstore")

    suspend fun writeDoubleData(storeKey: String, value: Double) {
        val key = doublePreferencesKey(storeKey)
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun getDoubleValue(storeKey: String, default: Double = 0.00): Double {
        val dataStoreKey = doublePreferencesKey(storeKey)
        val preferences: Preferences? = context.dataStore.data.firstOrNull()
        return if (preferences != null) {
            preferences[dataStoreKey].toString().toDouble()
        } else {
            default
        }
    }

    suspend fun writeBooleanData(storeKey: String, value: Boolean) {
        val key = booleanPreferencesKey(storeKey)
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun getBooleanValue(storeKey: String): Boolean {
        val dataStoreKey = booleanPreferencesKey(storeKey)
        val preferences: Preferences = context.dataStore.data.first()
        val value = preferences[dataStoreKey]
        return value ?: false
    }

    suspend fun writeIntData(storeKey: String, value: Int) {
        val key = intPreferencesKey(storeKey)
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun getIntValue(storeKey: String, default: Int = 0): Int {
        val dataStoreKey = intPreferencesKey(storeKey)
        val preferences: Preferences = context.dataStore.data.first()
        return if (preferences[dataStoreKey] != null) {
            preferences[dataStoreKey] as Int
        } else default
    }

    suspend fun writeStringData(storeKey: String, value: String) {
        val key = stringPreferencesKey(storeKey)
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun getStringValue(storeKey: String): String {
        val dataStoreKey = stringPreferencesKey(storeKey)
        val preferences: Preferences = context.dataStore.data.first()
        return preferences[dataStoreKey].toString()
    }

    private suspend fun removeValue(storeKey: String) {
        val key = stringPreferencesKey(storeKey)
        context.dataStore.edit {
            if (it.contains(key)) {
                it.remove(key)
            }
        }
    }

    suspend fun removeData(storeKey: String) = removeValue(storeKey)

    suspend fun getToken(): String {
        return getStringValue("TOKEN")
    }

    fun getBooleanValueAsFlow(storeKey: String):Flow<Boolean>{
        val dataStoreKey = booleanPreferencesKey(storeKey)
        return context.dataStore.data.map { preferences ->
            preferences[dataStoreKey] ?: false
        }
    }
}
