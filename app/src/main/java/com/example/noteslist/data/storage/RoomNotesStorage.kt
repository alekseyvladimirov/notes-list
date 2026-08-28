package com.example.noteslist.data.storage

import com.example.noteslist.data.db.NoteDao
import com.example.noteslist.data.db.toDomain
import com.example.noteslist.data.db.toEntity
import com.example.noteslist.domain.model.Note
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomNotesStorage @Inject constructor(
    private val noteDao: NoteDao
) : NotesStorage {
    override fun observeNotes(): Flow<List<Note>> {
        return noteDao.observeNotes().map { list -> list.map { it.toDomain() } }
    }

    override fun observeNoteById(id: Long): Flow<Note?> {
        return noteDao.observeNoteById(id).map { it?.toDomain() }
    }

    override suspend fun saveNote(note: Note) {
        noteDao.insert(note.toEntity())
    }

    override suspend fun removeNote(id: Long) {
        noteDao.deleteById(id)
    }

    override suspend fun countNotes(): Long {
        return noteDao.countNotes()
    }
}

