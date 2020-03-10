package com.zuhlke.upskilling.journal.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "folders_table")
data class Folder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var date: LocalDateTime = LocalDateTime.now(),
    var name: String = ""
)