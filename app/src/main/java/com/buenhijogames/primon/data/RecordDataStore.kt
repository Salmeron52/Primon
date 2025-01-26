package com.buenhijogames.primon.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecordDataStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Record")
        val RECORD = intPreferencesKey("record")
    }

    val getRecord: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[RECORD] ?: 0
    }

    suspend fun saveRecord(record: Int) {
        context.dataStore.edit { preferences ->
            preferences[RECORD] = record
        }
    }
}