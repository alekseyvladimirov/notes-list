package com.example.noteslist.data.storage

import com.example.noteslist.domain.model.Note
import java.time.LocalDateTime
import java.time.ZoneId

private fun toMillis(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long =
    LocalDateTime.of(year, month, day, hour, minute)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

val testDataNotes = mutableListOf(
    Note(1L, "Важная встреча", "Подготовить презентацию к 15:00", toMillis(2026, 3, 10, 9, 0), isImportant = true, isRead = false),
    Note(2L, "Позвонить врачу", "Записаться на приём", toMillis(2026, 3, 10, 10, 30), isImportant = true, isRead = true),
    Note(3L, "Купить продукты", "Молоко, хлеб, яйца", toMillis(2026, 3, 10, 11, 0), isImportant = false, isRead = false),
    Note(4L, "Полить цветы", "Фиалки и кактус", toMillis(2026, 3, 10, 12, 0), isImportant = false, isRead = true),
    Note(5L, "Почитать книгу", "Глава 5–7", toMillis(2026, 3, 10, 14, 0), isImportant = false, isRead = false),

    Note(6L, "Срочный дедлайн", "Сдать отчёт до конца дня", toMillis(2026, 3, 9, 8, 0), isImportant = true, isRead = false),
    Note(7L, "Прогулка", "Парк в 18:00", toMillis(2026, 3, 9, 18, 0), isImportant = false, isRead = false),
    Note(8L, "Стирка", "Запустить машинку", toMillis(2026, 3, 9, 20, 0), isImportant = false, isRead = true),
    Note(9L, "Ужин", "Приготовить пасту", toMillis(2026, 3, 9, 19, 0), isImportant = false, isRead = false),
    Note(10L, "Зарядка", "Утренняя разминка 15 мин", toMillis(2026, 3, 9, 7, 0), isImportant = false, isRead = true),
    Note(11L, "Помыть посуду", "После ужина", toMillis(2026, 3, 9, 21, 0), isImportant = false, isRead = false),

    Note(12L, "Поздравить маму", "8 Марта!", toMillis(2026, 3, 8, 9, 0), isImportant = true, isRead = true),
    Note(13L, "Купить цветы", "Тюльпаны", toMillis(2026, 3, 8, 10, 0), isImportant = false, isRead = false),
    Note(14L, "Забронировать ресторан", "Столик на 19:00", toMillis(2026, 3, 8, 11, 0), isImportant = false, isRead = false)
)

//fun getTestDataWithAddedNotes(): List<Note> {
//    val base = getTestData()
//    return base + listOf(
//        Note(15L, "Новая срочная", "Добавлена после первого рендера", toMillis(2026, 3, 11, 9, 15), isImportant = true, isRead = false),
//        Note(16L, "Новая обычная", "Должна попасть в стек 11.03.2026", toMillis(2026, 3, 11, 9, 45), isImportant = false, isRead = false)
//    )
//}
//
//fun getTestDataWithChangedDate(): List<Note> {
//    val base = getTestDataWithAddedNotes()
//    return base.map { note ->
//        if (note.title == "Прогулка") {
//            note.copy(timestamp = toMillis(2026, 3, 11, 20, 30))
//        } else {
//            note
//        }
//    }
//}
//
//fun getTestDataWithChangedContent(): List<Note> {
//    val base = getTestDataWithChangedDate()
//    return base.map { note ->
//        when (note.title) {
//            "Купить продукты" -> note.copy(
//                text = "Молоко, хлеб, яйца, сыр",
//                isRead = true
//            )
//
//            "Новая обычная" -> note.copy(
//                title = "Новая обычная (обновлена)",
//                text = "Текст был изменен после добавления",
//                isRead = true
//            )
//
//            else -> note
//        }
//    }
//}

