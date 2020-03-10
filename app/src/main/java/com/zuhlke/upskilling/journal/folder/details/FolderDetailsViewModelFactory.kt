package com.zuhlke.upskilling.journal.folder.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zuhlke.upskilling.journal.database.JournalDatabaseInstance

class FolderDetailsViewModelFactory(
    private val journalDatabaseInstance: JournalDatabaseInstance,
    private val folderId: Int
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FolderDetailsViewModel::class.java))
            return with(journalDatabaseInstance) {
                FolderDetailsViewModel(
                    folderId = folderId,
                    folderToJournalsDatabase = folderAndJournalsDAO,
                    folderDatabase = folderDAO,
                    journalEntryDatabase = journalEntryDAO
                ) as T
            }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
