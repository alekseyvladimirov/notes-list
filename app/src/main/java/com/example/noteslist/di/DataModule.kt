package com.example.noteslist.di

import com.example.noteslist.data.repository.NotesRepositoryImpl
import com.example.noteslist.data.settings.SettingsRepository
import com.example.noteslist.data.settings.SettingsRepositoryImpl
import com.example.noteslist.data.storage.NotesStorage
import com.example.noteslist.data.storage.RoomNotesStorage
import com.example.noteslist.domain.repository.NotesRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindNotesRepository(impl: NotesRepositoryImpl): NotesRepository

    @Binds
    @Singleton
    abstract fun bindNotesStorage(impl: RoomNotesStorage): NotesStorage

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
