package com.zuhlke.upskilling.journal.database.model

import androidx.room.Embedded
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.zuhlke.upskilling.journal.database.model.Folder
import com.zuhlke.upskilling.journal.database.model.JournalEntry

data class FolderAndJournals(
    @PrimaryKey @Embedded
    val folder: Folder,
    @Relation(
        parentColumn = "id",
        entityColumn = "folderId"
    )
    var journals: List<JournalEntry>
)
