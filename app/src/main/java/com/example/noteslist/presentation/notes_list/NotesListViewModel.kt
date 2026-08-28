package com.example.noteslist.presentation.notes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.data.settings.NoteStackSettings
import com.example.noteslist.data.settings.SettingsRepository
import com.example.noteslist.domain.usecase.GetNotesUseCase
import com.example.noteslist.domain.usecase.SaveNoteUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class NotesListViewModel @Inject constructor(
    private val getNotes: GetNotesUseCase,
    private val saveNote: SaveNoteUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val state: StateFlow<NotesListState> = getNotes()
        .map { NotesListState(notes = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotesListState()
        )

    val settings: StateFlow<NoteStackSettings> = settingsRepository.noteStackSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NoteStackSettings(stackSpacingDp = 20f, stackMaxVisible = 3)
        )

    fun onReadToggle(noteId: Long, isRead: Boolean) {
        val current = state.value.notes
        val changedNote = current.firstOrNull { it.id == noteId }?.copy(isRead = isRead) ?: return
        saveNote(changedNote)
    }
}
