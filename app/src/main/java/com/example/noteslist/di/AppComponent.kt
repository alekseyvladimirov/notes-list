package com.example.noteslist.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        DataModule::class,
        SubcomponentsModule::class
    ]
)
interface AppComponent {
    fun notesListComponentFactory(): com.example.noteslist.di.noteslist.NotesListComponent.Factory
    fun noteDetailComponentFactory(): com.example.noteslist.di.notedetail.NoteDetailComponent.Factory
    fun settingsComponentFactory(): com.example.noteslist.di.SettingsComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}
