package com.oqba26.hozorghiabapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.oqba26.hozorghiabapp.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension to create DataStore on Context
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object SettingsKeys {
    val MONTHLY_FEE = intPreferencesKey("monthly_fee")
    val FONT_KEY = stringPreferencesKey("font_key")
}

// Helper functions to read settings
fun Context.settingsFlow(): Flow<AppSettings> {
    return settingsDataStore.data.map { prefs ->
        AppSettings(
            monthlyFee = prefs[SettingsKeys.MONTHLY_FEE] ?: 200_000,
            fontKey = prefs[SettingsKeys.FONT_KEY] ?: "vazirmatn"
        )
    }
}

// Helper functions to write settings
suspend fun Context.setMonthlyFee(value: Int) {
    settingsDataStore.edit { prefs ->
        prefs[SettingsKeys.MONTHLY_FEE] = value
    }
}

suspend fun Context.setFontKey(key: String) {
    settingsDataStore.edit { prefs ->
        prefs[SettingsKeys.FONT_KEY] = key
    }
}
