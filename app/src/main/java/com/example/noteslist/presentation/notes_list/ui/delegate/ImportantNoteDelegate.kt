package com.example.noteslist.presentation.notes_list.ui.delegate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.presentation.notes_list.view.NoteView
import com.example.noteslist.R
import com.example.noteslist.presentation.notes_list.ui.adapter.AdapterDelegate
import com.example.noteslist.presentation.notes_list.ui.adapter.ViewTypes
import com.example.noteslist.presentation.notes_list.model.ListItem

class ImportantNoteDelegate(
    private val onNoteClick: (noteId: Long) -> Unit = { _ -> },
    private val onNoteLongClick: (noteId: Long, isRead: Boolean) -> Unit = { _, _ -> }
) : AdapterDelegate {
    override fun viewType(): Int = ViewTypes.IMPORTANT_NOTE

    override fun isForItem(item: ListItem): Boolean =
        item is ListItem.ImportantNote


    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_important_note, parent, false)
        return ImportantNoteViewHolder(view)
    }

    override fun bindViewHolder(holder: RecyclerView.ViewHolder, item: ListItem) {
        val vh = holder as? ImportantNoteViewHolder ?: return
        val importantNoteItem = item as? ListItem.ImportantNote ?: return
        val noteId = importantNoteItem.note.id ?: return

        vh.nvImportantNote.apply {
            onNoteClick = {
                this@ImportantNoteDelegate.onNoteClick(noteId)
            }
            onNoteLongClick = { noteView ->
                this@ImportantNoteDelegate.onNoteLongClick(noteId, !noteView.isRead)
            }

            title = importantNoteItem.note.title
            noteText = importantNoteItem.note.text
            timestamp = importantNoteItem.note.timestamp
            isImportant = importantNoteItem.note.isImportant
            isRead = importantNoteItem.note.isRead
        }
    }

    private class ImportantNoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nvImportantNote: NoteView = itemView.findViewById(R.id.noteView)
    }
}