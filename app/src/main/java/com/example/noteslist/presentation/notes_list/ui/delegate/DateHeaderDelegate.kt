package com.example.noteslist.presentation.notes_list.ui.delegate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.presentation.notes_list.ui.adapter.AdapterDelegate
import com.example.noteslist.presentation.notes_list.ui.adapter.ViewTypes
import com.example.noteslist.presentation.notes_list.model.ListItem
import java.time.format.DateTimeFormatter

class DateHeaderDelegate : AdapterDelegate {

    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    }

    override fun viewType(): Int = ViewTypes.DATE_HEADER

    override fun isForItem(item: ListItem): Boolean {
        return item is ListItem.DateHeader
    }

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_date_header, parent, false)
        return DateHeaderViewHolder(view)
    }

    override fun bindViewHolder(holder: RecyclerView.ViewHolder, item: ListItem) {
        val vh = holder as? DateHeaderViewHolder ?: return
        val headerItem = item as? ListItem.DateHeader ?: return

        vh.tvDateHeader.text = headerItem.date.format(DATE_FORMATTER)
    }

    private class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDateHeader: TextView = itemView.findViewById(R.id.tvDateHeader)
    }
}