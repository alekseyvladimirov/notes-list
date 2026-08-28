package com.example.noteslist.presentation.notes_list.model

import com.example.noteslist.domain.model.Note
import java.time.LocalDate

sealed class ListItem {
    data class DateHeader(val date: LocalDate) : ListItem()
    data class ImportantNote(val note: Note) : ListItem()
    data class NoteStack(val date: LocalDate, val notes: List<Note>) : ListItem()
}