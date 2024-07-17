package dev.jjablonski.lingommerse.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.jjablonski.lingommerse.model.LanguagePair

@Database(entities = [LanguagePair::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun languagePairDao(): LanguagePairDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
