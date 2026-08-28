package com.example.noteslist.data.settings

import android.content.Context
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl @Inject constructor(
    private val context: Context
) : SettingsRepository {

    private val spacingKey = floatPreferencesKey("stack_spacing_dp")
    private val maxVisibleKey = intPreferencesKey("stack_max_visible")

    override val noteStackSettings: Flow<NoteStackSettings> =
        context.settingsDataStore.data.map { prefs ->
            NoteStackSettings(
                stackSpacingDp = prefs[spacingKey] ?: 20f,
                stackMaxVisible = prefs[maxVisibleKey] ?: 3
            )
        }

    override suspend fun setStackSpacingDp(value: Float) {
        context.settingsDataStore.edit { prefs ->
            prefs[spacingKey] = value
        }
    }

    override suspend fun setStackMaxVisible(value: Int) {
        context.settingsDataStore.edit { prefs ->
            prefs[maxVisibleKey] = value
        }
    }
}

