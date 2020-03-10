package com.zuhlke.upskilling.journal.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.zuhlke.upskilling.journal.database.model.FolderAndJournals

@Dao
interface FolderAndJournalsDAO {
    @Transaction
    @Query("SELECT * FROM folders_table")
    fun getAll(): LiveData<List<FolderAndJournals>>

    @Transaction
    @Query("SELECT * FROM folders_table WHERE id = :folderId")
    fun get(folderId: Int): LiveData<FolderAndJournals>
}
