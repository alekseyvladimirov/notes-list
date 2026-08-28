package com.example.noteslist.data.db

import com.example.noteslist.domain.model.Note

fun NoteEntity.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        text = text,
        timestamp = timestamp,
        isImportant = isImportant,
        isRead = isRead
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id ?: 0L,
        title = title,
        text = text,
        timestamp = timestamp,
        isImportant = isImportant,
        isRead = isRead
    )
}

