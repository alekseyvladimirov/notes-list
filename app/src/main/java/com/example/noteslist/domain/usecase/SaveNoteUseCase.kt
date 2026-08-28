package com.example.noteslist.domain.usecase

import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.repository.NotesRepository
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {

    operator fun invoke(note: Note) {
        repository.saveNote(note)
    }
}