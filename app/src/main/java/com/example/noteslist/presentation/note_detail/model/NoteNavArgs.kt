package com.example.noteslist.presentation.note_detail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteNavArgs(
    val noteId: Long?
) : Parcelable