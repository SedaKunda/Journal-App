package com.zuhlke.upskilling.journal.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.zuhlke.upskilling.journal.database.model.JournalEntry

@Dao
interface JournalEntryDAO {
    @Insert
    fun add(entry: JournalEntry)

    @Update
    fun update(entry: JournalEntry)

    @Query("SELECT * from journal_entries_table WHERE id = :id")
    fun get(id: Int): JournalEntry?

    @Query("SELECT * from journal_entries_table WHERE title = :title")
    fun getByTitle(title: String): LiveData<List<JournalEntry>>

    @Query("DELETE FROM journal_entries_table")
    fun clear()

    @Query("SELECT * from journal_entries_table ORDER BY date DESC")
    fun getAll(): LiveData<List<JournalEntry>>

    @Query("DELETE FROM journal_entries_table WHERE id = :id")
    fun delete(id: Int)
}