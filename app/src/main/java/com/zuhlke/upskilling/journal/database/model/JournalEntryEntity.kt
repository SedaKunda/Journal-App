package com.zuhlke.upskilling.journal.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "journal_entries_table")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var folderId: Int = 0,
    var date: LocalDateTime = LocalDateTime.now(),
    var title: String = "",
    var contents: String = "",
    var images: String ?= null
)