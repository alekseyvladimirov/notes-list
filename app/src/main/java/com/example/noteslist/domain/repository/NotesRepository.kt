package com.example.noteslist.domain.repository

import com.example.noteslist.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun observeNotes(): Flow<List<Note>>
    fun observeNoteById(id: Long): Flow<Note?>
    fun saveNote(note: Note)
    fun removeNote(id: Long)
}
