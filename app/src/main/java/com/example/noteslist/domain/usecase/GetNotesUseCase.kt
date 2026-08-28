package com.example.noteslist.domain.usecase

import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(private val repository: NotesRepository) {

    operator fun invoke(): Flow<List<Note>> {
        return repository.observeNotes()
    }
}