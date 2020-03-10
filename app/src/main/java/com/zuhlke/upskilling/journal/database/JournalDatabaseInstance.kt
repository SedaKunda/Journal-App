package com.zuhlke.upskilling.journal.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zuhlke.upskilling.journal.database.dao.FolderAndJournalsDAO
import com.zuhlke.upskilling.journal.database.dao.FolderDAO
import com.zuhlke.upskilling.journal.database.dao.JournalEntryDAO
import com.zuhlke.upskilling.journal.database.model.Folder
import com.zuhlke.upskilling.journal.database.model.JournalEntry
import com.zuhlke.upskilling.journal.database.util.Converters


@Database(
    entities = [
        JournalEntry::class,
        Folder::class
    ],
    version = 2,
    exportSchema = false)
@TypeConverters(Converters::class)
abstract class JournalDatabaseInstance : RoomDatabase() {
    abstract val journalEntryDAO: JournalEntryDAO
    abstract val folderDAO: FolderDAO
    abstract val folderAndJournalsDAO: FolderAndJournalsDAO

    companion object {
        @Volatile
        private var INSTANCE: JournalDatabaseInstance? = null
        fun getInstance(context: Context): JournalDatabaseInstance {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        JournalDatabaseInstance::class.java,
                        "journal_database")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}