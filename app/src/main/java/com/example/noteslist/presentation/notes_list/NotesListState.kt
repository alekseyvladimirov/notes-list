package com.example.noteslist.presentation.notes_list

import com.example.noteslist.domain.model.Note

data class NotesListState(
    val notes: List<Note> = emptyList()
)