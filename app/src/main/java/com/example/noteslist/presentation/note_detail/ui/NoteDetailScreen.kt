package com.example.noteslist.presentation.note_detail.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.collectAsState
import com.example.noteslist.R
import com.example.noteslist.presentation.note_detail.NoteDetailViewModel

@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel,
    onBackClick: () -> Unit = {},
    onSaved: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val isEditMode = viewModel.isEditMode

    NoteDetailScreen(
        state = NoteDetailUiState(
            title = state.title,
            text = state.text,
            isImportant = state.isImportant,
            isRead = state.isRead,
            createdAt = state.createdAt,
            isTitleError = state.isTitleError,
            isTitleTooLong = state.isTitleTooLong,
            isEditMode = isEditMode
        ),
        onBackClick = onBackClick,
        onTitleChange = viewModel::onTitleChange,
        onTextChange = viewModel::onContentChange,
        onImportantChange = viewModel::onImportantChange,
        onReadChange = viewModel::onReadChange,
        onSaveClick = {
            if (viewModel.onSave()) {
                onSaved()
            }
        }
    )
}

@Composable
private fun NoteDetailScreen(
    state: NoteDetailUiState,
    onBackClick: () -> Unit = {},
    onTitleChange: (String) -> Unit = { _ -> },
    onTextChange: (String) -> Unit = { _ -> },
    onImportantChange: (Boolean) -> Unit = { _ -> },
    onReadChange: (Boolean) -> Unit = { _ -> },
    onSaveClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            NoteDetailTopBar(
                isEditMode = state.isEditMode,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        NoteDetailContent(
            padding = paddingValues,
            state = state,
            onTitleChange = onTitleChange,
            onTextChange = onTextChange,
            onImportantChange = onImportantChange,
            onReadChange = onReadChange,
            onSaveClick = onSaveClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteDetailTopBar(
    isEditMode: Boolean,
    onBackClick: () -> Unit = {}
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.nav_back)
                )
            }
        },
        title = {
            Text(
                if (isEditMode) {
                    stringResource(R.string.note_detail_title_edit)
                } else {
                    stringResource(R.string.note_detail_title_new)
                }
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.LightGray,
            titleContentColor = Color.Black
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun NoteDetailScreenPreview() {
    NoteDetailScreen(
        state = NoteDetailUiState(
            title = "Sample title",
            text = "Sample text",
            isImportant = true,
            isRead = false,
            createdAt = 0L,
            isTitleError = false,
            isTitleTooLong = false,
            isEditMode = true
        )
    )
}
