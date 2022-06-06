package com.put.ubi

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.put.ubi.data.FundsProvider
import com.put.ubi.model.Fund
import com.put.ubi.util.sha512
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fundsProvider: FundsProvider
) {
    private val Context.preferences: DataStore<Preferences> by preferencesDataStore(name = "user")

    suspend fun setPassword(password: String) {
        context.preferences.edit {
            it[PASSWORD] = sha512(password)
        }
    }

    suspend fun getPassword() = context.preferences.data.map { it[PASSWORD] }.first()

    suspend fun setFund(fund: Fund) {
        context.preferences.edit {
            it[FUND] = fund.id
        }
    }

    suspend fun getFund(): Fund? {
        return context.preferences.data.map { it[FUND] }
            .map { id -> fundsProvider.getFunds().firstOrNull { it.id == id } }
            .first()
    }

    suspend fun setBiometrics(enable: Boolean) {
        context.preferences.edit {
            it[BIOMETRICS] = enable
        }
    }

    suspend fun getBiometrics(): Boolean {
        return context.preferences.data.map { it[BIOMETRICS] }.first() ?: false
    }
    companion object {
        val PASSWORD = stringPreferencesKey("PASSWORD")
        val FUND = stringPreferencesKey("FUND")
        val BIOMETRICS = booleanPreferencesKey("BIOMETRICS")
    }
}