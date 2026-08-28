package com.example.noteslist.presentation.note_detail.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.noteslist.R
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Immutable
data class NoteDetailUiState(
    val title: String,
    val text: String,
    val isImportant: Boolean,
    val isRead: Boolean,
    val createdAt: Long?,
    val isTitleError: Boolean,
    val isTitleTooLong: Boolean,
    val isEditMode: Boolean
)

@Composable
fun NoteDetailContent(
    padding: PaddingValues,
    state: NoteDetailUiState,
    onTitleChange: (String) -> Unit = { _ -> },
    onTextChange: (String) -> Unit = { _ -> },
    onImportantChange: (Boolean) -> Unit = { _ -> },
    onReadChange: (Boolean) -> Unit = { _ -> },
    onSaveClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(padding)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        TitleField(
            value = state.title,
            isError = state.isTitleError || state.isTitleTooLong,
            onValueChange = onTitleChange
        )

        if (state.isTitleError) {
            Text(
                text = stringResource(R.string.need_to_fill),
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        } else if (state.isTitleTooLong) {
            Text(
                text = stringResource(R.string.title_too_long),
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        ContentField(
            value = state.text,
            onValueChange = onTextChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        BooleanField(
            text = stringResource(R.string.note_is_important),
            checked = state.isImportant,
            onCheckedChange = onImportantChange
        )

        if (state.isEditMode) {
            BooleanField(
                text = stringResource(R.string.note_is_read),
                checked = state.isRead,
                onCheckedChange = onReadChange
            )

            val createdAt = state.createdAt
            if (createdAt != null) {
                Text(
                    text = stringResource(
                        R.string.note_created_at_format,
                        formatDateTime(createdAt)
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (state.isEditMode) {
                    stringResource(R.string.note_action_save)
                } else {
                    stringResource(R.string.note_action_add)
                }
            )
        }
    }
}

@Composable
private fun TitleField(
    value: String,
    isError: Boolean,
    onValueChange: (String) -> Unit = { _ -> }
) {
    BaseTextField(
        value = value,
        onValueChange = onValueChange,
        label = stringResource(R.string.note_title_label),
        singleLine = true,
        maxLines = 1,
        isError = isError
    )
}

@Composable
private fun ContentField(
    value: String,
    onValueChange: (String) -> Unit = { _ -> },
) {
    BaseTextField(
        value = value,
        onValueChange = onValueChange,
        label = stringResource(R.string.note_text_label),
        singleLine = false,
        maxLines = Int.MAX_VALUE
    )
}

@Composable
private fun BooleanField(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit = { _ -> }
) {
    TextButton(
        onClick = { onCheckedChange(!checked) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors()
        )
        Text(text)
    }
}

@Composable
private fun BaseTextField(
    value: String,
    onValueChange: (String) -> Unit = { _ -> },
    label: String,
    singleLine: Boolean,
    maxLines: Int,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        maxLines = maxLines,
        isError = isError,
        modifier = Modifier.fillMaxWidth()
    )
}

private fun formatDateTime(timestamp: Long): String {
    return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
}
