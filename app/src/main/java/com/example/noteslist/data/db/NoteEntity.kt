package com.example.noteslist.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val text: String,
    val timestamp: Long,
    val isImportant: Boolean,
    val isRead: Boolean
)

