package dev.jjablonski.lingommerse.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.jjablonski.lingommerse.model.LanguagePair

@Dao
interface LanguagePairDao {
    @Query("SELECT * FROM language_pairs WHERE listId = :listId")
    suspend fun getAllFromList(listId: Int): List<LanguagePair>

    @Insert
    suspend fun insert(pair: LanguagePair)

    @Delete
    suspend fun delete(pair: LanguagePair)
}
