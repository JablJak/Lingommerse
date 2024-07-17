package dev.jjablonski.lingommerse.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "language_lists")
data class LanguageList(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val originalLanguage: String,
    val translationLanguage: String
)
