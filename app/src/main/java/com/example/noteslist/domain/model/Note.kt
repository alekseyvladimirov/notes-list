package com.example.noteslist.domain.model

data class Note(
    val id: Long?,
    val title: String,
    val text: String,
    val timestamp: Long,
    val isImportant: Boolean,
    val isRead: Boolean
)