package com.example.noteslist.di.notedetail

import androidx.lifecycle.ViewModel
import com.example.noteslist.di.ViewModelKey
import com.example.noteslist.presentation.note_detail.NoteDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class NoteDetailModule {
    @Binds
    @IntoMap
    @ViewModelKey(NoteDetailViewModel::class)
    abstract fun bindNoteDetailViewModel(viewModel: NoteDetailViewModel): ViewModel
}
