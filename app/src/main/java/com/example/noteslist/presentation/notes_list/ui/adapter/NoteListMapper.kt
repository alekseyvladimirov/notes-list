package com.example.noteslist.presentation.notes_list.ui.adapter

import com.example.noteslist.presentation.notes_list.model.ListItem
import com.example.noteslist.domain.model.Note
import java.time.Instant
import java.time.ZoneId

fun mapNotesToListItems(notes: List<Note>): List<ListItem> {
    return notes
        .groupBy {
            Instant.ofEpochMilli(it.timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }
        .toSortedMap(compareByDescending { it })
        .flatMap { (date, notesForDate) ->
            mutableListOf<ListItem>().apply {
                add(ListItem.DateHeader(date))
                notesForDate.filter { it.isImportant }
                    .sortedByDescending { it.timestamp }
                    .forEach { add(ListItem.ImportantNote(it)) }
                val ordinary = notesForDate.filter { !it.isImportant }
                    .sortedByDescending { it.timestamp }
                if (ordinary.isNotEmpty())
                    add(ListItem.NoteStack(date, ordinary))
            }
        }
}