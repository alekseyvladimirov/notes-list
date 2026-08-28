package com.example.noteslist.presentation.notes_list.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.presentation.notes_list.model.ListItem

class CompositeDelegateAdapter(
    private val delegates: List<AdapterDelegate>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return when {
                oldItem is ListItem.DateHeader && newItem is ListItem.DateHeader -> {
                    oldItem.date == newItem.date
                }

                oldItem is ListItem.ImportantNote && newItem is ListItem.ImportantNote -> {
                    oldItem.note.id == newItem.note.id
                }

                oldItem is ListItem.NoteStack && newItem is ListItem.NoteStack -> {
                    oldItem.date == newItem.date
                }

                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    })

    var items: List<ListItem> = emptyList()
        set(value) {
            field = value
            differ.submitList(value)
        }

    override fun getItemViewType(position: Int): Int =
        delegates.first { it.isForItem(differ.currentList[position]) }.viewType()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegates.first { it.viewType() == viewType }.createViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = differ.currentList[position]
        delegates.first { it.isForItem(item) }
            .bindViewHolder(holder, item)
    }

    override fun getItemCount(): Int = differ.currentList.size
}