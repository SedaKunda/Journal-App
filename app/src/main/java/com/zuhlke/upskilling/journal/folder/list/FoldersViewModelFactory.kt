package com.zuhlke.upskilling.journal.folder.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zuhlke.upskilling.journal.database.JournalDatabaseInstance

class FoldersViewModelFactory(private val journalDatabaseInstance: JournalDatabaseInstance) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoldersViewModel::class.java))
            return with(journalDatabaseInstance) {
                FoldersViewModel(folderDAO, folderAndJournalsDAO) as T
            }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
