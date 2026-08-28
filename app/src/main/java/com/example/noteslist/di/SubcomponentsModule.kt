package com.example.noteslist.di

import com.example.noteslist.di.notedetail.NoteDetailComponent
import com.example.noteslist.di.noteslist.NotesListComponent
import com.example.noteslist.di.SettingsComponent
import dagger.Module

@Module(
    subcomponents = [
        NotesListComponent::class,
        NoteDetailComponent::class,
        SettingsComponent::class
    ]
)
object SubcomponentsModule
