package com.zuhlke.upskilling.journal.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.zuhlke.upskilling.journal.database.model.Folder

@Dao
interface FolderDAO {
    @Insert
    fun add(folder: Folder)

    @Update
    fun update(folder: Folder)

    @Query("SELECT count(*) from journal_entries_table")
    fun test(): LiveData<Int>

    @Query("SELECT * from folders_table WHERE id = :id")
    fun get(id: Int): Folder?

    @Query("SELECT * from folders_table ORDER BY id DESC LIMIT 1")
    fun getLatest(): Folder

    @Query("SELECT * FROM folders_table ORDER BY date DESC")
    fun getAll(): LiveData<List<Folder>>

    @Query("DELETE FROM folders_table")
    fun clear()

    @Query("DELETE FROM folders_table WHERE id = :id")
    fun delete(id: Int)
}