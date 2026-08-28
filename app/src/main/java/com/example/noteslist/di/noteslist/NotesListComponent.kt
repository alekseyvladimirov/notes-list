package com.example.noteslist.di.noteslist

import com.example.noteslist.di.ViewModelFactoryModule
import com.example.noteslist.presentation.notes_list.ui.NotesListFragment
import dagger.Subcomponent

@Subcomponent(
    modules = [
        NotesListModule::class,
        ViewModelFactoryModule::class
    ]
)
interface NotesListComponent {
    fun inject(fragment: NotesListFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): NotesListComponent
    }
}
