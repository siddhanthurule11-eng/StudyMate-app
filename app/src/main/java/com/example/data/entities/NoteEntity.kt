package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subject: String = "General",
    val content: String,
    val dateCreated: String,
    val colorHex: String = "#3B82F6",
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val folder: String = "General"
)
