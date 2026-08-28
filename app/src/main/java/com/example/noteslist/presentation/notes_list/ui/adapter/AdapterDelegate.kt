package com.example.noteslist.presentation.notes_list.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.presentation.notes_list.model.ListItem

interface AdapterDelegate {
    fun viewType(): Int
    fun isForItem(item: ListItem): Boolean
    fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun bindViewHolder(holder: RecyclerView.ViewHolder, item: ListItem)
}