package com.zuhlke.upskilling.journal.journalEntry.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zuhlke.upskilling.journal.database.dao.JournalEntryDAO

class JournalEntriesViewModelFactory(private val dataSource: JournalEntryDAO) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalEntriesViewModel::class.java))
            return JournalEntriesViewModel(dataSource) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
