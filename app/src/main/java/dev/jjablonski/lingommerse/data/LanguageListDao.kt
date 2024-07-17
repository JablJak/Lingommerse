package dev.jjablonski.lingommerse.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.jjablonski.lingommerse.model.LanguageList

@Dao
interface LanguageListDao {
    @Query("SELECT * FROM language_lists")
    suspend fun getAll(): List<LanguageList>

    @Query("SELECT * FROM language_lists WHERE id = :id")
    suspend fun getById(id: Int): LanguageList?

    @Insert
    suspend fun insert(list: LanguageList)

    @Delete
    suspend fun delete(list: LanguageList)
}
