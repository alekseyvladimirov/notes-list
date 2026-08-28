package com.example.noteslist.data.storage

import com.example.noteslist.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesStorage {
    fun observeNotes(): Flow<List<Note>>
    fun observeNoteById(id: Long): Flow<Note?>
    suspend fun saveNote(note: Note)
    suspend fun removeNote(id: Long)
    suspend fun countNotes(): Long
}