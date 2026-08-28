package com.example.noteslist.di.notedetail

import com.example.noteslist.di.ViewModelFactoryModule
import com.example.noteslist.presentation.note_detail.ui.NoteDetailFragment
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(
    modules = [
        NoteDetailModule::class,
        ViewModelFactoryModule::class
    ]
)
interface NoteDetailComponent {
    fun inject(fragment: NoteDetailFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance noteId: Long?): NoteDetailComponent
    }
}
