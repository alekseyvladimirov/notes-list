package com.example.noteslist.data.repository

import com.example.noteslist.data.storage.NotesStorage
import com.example.noteslist.data.storage.testDataNotes
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.repository.NotesRepository
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NotesRepositoryImpl @Inject constructor(
    private val storage: NotesStorage,
    private val appScope: CoroutineScope
) : NotesRepository {

    private val seeded = AtomicBoolean(false)

    init {
        seedDemoNotesIfNeeded()
    }

    override fun observeNotes(): Flow<List<Note>> {
        return storage.observeNotes()
    }

    override fun observeNoteById(id: Long): Flow<Note?> {
        return storage.observeNoteById(id)
    }

    override fun saveNote(note: Note) {
        appScope.launch {
            storage.saveNote(note)
        }
    }

    override fun removeNote(id: Long) {
        appScope.launch {
            storage.removeNote(id)
        }
    }

    private fun seedDemoNotesIfNeeded() {
        if (!seeded.compareAndSet(false, true)) return
        appScope.launch {
            if (storage.countNotes() > 0) return@launch
            testDataNotes.forEach { demoNote ->
                storage.saveNote(demoNote)
            }
        }
    }
}