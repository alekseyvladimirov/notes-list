package com.example.noteslist.presentation.note_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.usecase.GetNoteByIdUseCase
import com.example.noteslist.domain.usecase.SaveNoteUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteDetailViewModel @Inject constructor(
    private val getNoteById: GetNoteByIdUseCase,
    private val saveNote: SaveNoteUseCase,
    private val noteId: Long?
) : ViewModel() {

    private companion object {
        private const val MAX_TITLE_LENGTH = 50
    }

    val isEditMode: Boolean = noteId != null

    private val _state = MutableStateFlow(NoteDetailState())
    val state = _state.asStateFlow()

    init {
        if (noteId != null) {
            load(noteId)
        }
    }

    private fun load(id: Long) {
        viewModelScope.launch {
            val note = getNoteById(id).firstOrNull() ?: return@launch

            _state.update {
                it.copy(
                    title = note.title,
                    text = note.text,
                    isImportant = note.isImportant,
                    isRead = note.isRead,
                    createdAt = note.timestamp
                )
            }
        }
    }

    fun onTitleChange(value: String) {
        val tooLong = value.length > MAX_TITLE_LENGTH
        _state.update {
            it.copy(
                title = value,
                isTitleError = false,
                isTitleTooLong = tooLong
            )
        }
    }

    fun onContentChange(value: String) {
        _state.update {
            it.copy(text = value)
        }
    }

    fun onReadChange(value: Boolean) {
        _state.update {
            it.copy(isRead = value)
        }
    }

    fun onImportantChange(value: Boolean) {
        _state.update {
            it.copy(isImportant = value)
        }
    }

    fun onSave(): Boolean {
        val currentNote = state.value

        if (currentNote.title.isBlank()) {
            _state.update {
                it.copy(isTitleError = true)
            }
            return false
        }

        if (currentNote.isTitleTooLong) {
            return false
        }

        saveNote(
            Note(
                id = noteId,
                title = currentNote.title,
                text = currentNote.text,
                timestamp = currentNote.createdAt ?: System.currentTimeMillis(),
                isImportant = currentNote.isImportant,
                isRead = currentNote.isRead
            )
        )
        return true
    }
}
