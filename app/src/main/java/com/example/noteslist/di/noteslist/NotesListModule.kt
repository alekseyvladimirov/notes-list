package com.example.noteslist.di.noteslist

import androidx.lifecycle.ViewModel
import com.example.noteslist.di.ViewModelKey
import com.example.noteslist.presentation.notes_list.NotesListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class NotesListModule {
    @Binds
    @IntoMap
    @ViewModelKey(NotesListViewModel::class)
    abstract fun bindNotesListViewModel(viewModel: NotesListViewModel): ViewModel
}
