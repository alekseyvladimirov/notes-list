package com.example.noteslist.presentation.note_detail

data class NoteDetailState(
    val title: String = "",
    val text: String = "",
    val isImportant: Boolean = false,
    val isRead : Boolean = false,
    val createdAt: Long? = null,
    val isTitleError: Boolean = false,
    val isTitleTooLong: Boolean = false
)