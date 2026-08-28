package com.example.noteslist.data.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val noteStackSettings: Flow<NoteStackSettings>
    suspend fun setStackSpacingDp(value: Float)
    suspend fun setStackMaxVisible(value: Int)
}

