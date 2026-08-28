package com.example.noteslist.presentation.notes_list.ui.delegate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.data.settings.NoteStackSettings
import com.example.noteslist.presentation.notes_list.view.NoteStackView
import com.example.noteslist.presentation.notes_list.view.NoteView
import com.example.noteslist.R
import com.example.noteslist.presentation.notes_list.ui.adapter.AdapterDelegate
import com.example.noteslist.presentation.notes_list.ui.adapter.ViewTypes
import com.example.noteslist.presentation.notes_list.model.ListItem
import java.time.LocalDate

class NoteStackDelegate(
    private val onNoteClick: (noteId: Long) -> Unit = { _ -> },
    private val onNoteLongClick: (noteId: Long, isRead: Boolean) -> Unit = { _, _ -> }
) : AdapterDelegate {
    var stackSettings: NoteStackSettings? = null
    private val expandedStateByDate = mutableMapOf<LocalDate, Boolean>()

    override fun viewType(): Int = ViewTypes.NOTE_STACK

    override fun isForItem(item: ListItem): Boolean =
        item is ListItem.NoteStack

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_note_stack, parent, false)
        return NoteStackViewHolder(view)
    }

    override fun bindViewHolder(holder: RecyclerView.ViewHolder, item: ListItem) {
        val vh = holder as? NoteStackViewHolder ?: return
        val stackItem = item as? ListItem.NoteStack ?: return

        stackSettings?.let { settings ->
            vh.vgNoteStack.setStackSpacingDp(settings.stackSpacingDp)
            vh.vgNoteStack.setStackMaxVisible(settings.stackMaxVisible)
        }

        vh.boundDate?.let { previousDate ->
            expandedStateByDate[previousDate] = vh.vgNoteStack.isExpandedState()
        }
        vh.boundDate = stackItem.date

        vh.vgNoteStack.setOnExpandedStateChangedListener { isExpanded ->
            expandedStateByDate[stackItem.date] = isExpanded
        }

        val toRemove = mutableListOf<View>()
        for (i in 0 until vh.vgNoteStack.childCount) {
            val child = vh.vgNoteStack.getChildAt(i)
            if (child is NoteView) {
                toRemove.add(child)
            }
        }
        toRemove.forEach { vh.vgNoteStack.removeView(it) }

        stackItem.notes.forEach { note ->
            val noteId = note.id ?: return@forEach
            val noteView = NoteView(vh.itemView.context).apply {
                onNoteClick = {
                    this@NoteStackDelegate.onNoteClick(noteId)
                }
                onNoteLongClick = { clickedView ->
                    this@NoteStackDelegate.onNoteLongClick(noteId, !clickedView.isRead)
                }

                title = note.title
                noteText = note.text
                timestamp = note.timestamp
                isImportant = note.isImportant
                isRead = note.isRead
            }
            vh.vgNoteStack.addNote(noteView)
        }

        vh.vgNoteStack.setExpandedState(expandedStateByDate[stackItem.date] == true)
    }

    private class NoteStackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vgNoteStack: NoteStackView = itemView.findViewById(R.id.noteStackView)
        var boundDate: LocalDate? = null
    }
}