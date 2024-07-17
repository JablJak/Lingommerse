package dev.jjablonski.lingommerse.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.jjablonski.lingommerse.model.LanguageList
import dev.jjablonski.lingommerse.model.LanguagePair

@Database(entities = [LanguagePair::class, LanguageList::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun languagePairDao(): LanguagePairDao
    abstract fun languageListDao(): LanguageListDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
