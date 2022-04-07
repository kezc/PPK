package com.put.ubi

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.put.ubi.util.sha512
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {
    private val Context.preferences: DataStore<Preferences> by preferencesDataStore(name = "user")

    suspend fun setPassword(password: String) {
        context.preferences.edit {
            it[PASSWORD] = sha512(password)
        }
    }

    suspend fun getPassword() = context.preferences.data.map { it[PASSWORD] }.first()

    companion object {
        val PASSWORD = stringPreferencesKey("PASSWORD")
    }
}