package com.zuhlke.upskilling.journal.journalEntry.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zuhlke.upskilling.journal.database.dao.JournalEntryDAO

class JournalEntryDetailsViewModelFactory(
    private val dataSource: JournalEntryDAO,
    private val journalEntryId: Int,
    val folderId: Int
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalEntryDetailsViewModel::class.java))
            return JournalEntryDetailsViewModel(dataSource, journalEntryId, folderId) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
