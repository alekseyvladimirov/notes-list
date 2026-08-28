package com.example.noteslist.data.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val SETTINGS_DATASTORE = "settings"

val Context.settingsDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(
    name = SETTINGS_DATASTORE
)

