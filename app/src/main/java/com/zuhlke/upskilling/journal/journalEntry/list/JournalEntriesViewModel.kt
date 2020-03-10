package com.zuhlke.upskilling.journal.journalEntry.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zuhlke.upskilling.journal.database.model.JournalEntry
import com.zuhlke.upskilling.journal.database.dao.JournalEntryDAO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class JournalEntriesViewModel(
    val database: JournalEntryDAO,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    val journalEntries = database.getAll()

    private var deletedItem: JournalEntry? = null

    private val _navigateToJournalEntry = MutableLiveData<Int>()
    val navigateToJournalEntry
        get() = _navigateToJournalEntry

    fun doneNavigating() {
        _navigateToJournalEntry.value = null
    }

    fun onJournalEntryClicked(journalEntryId: Int) {
        _navigateToJournalEntry.value = journalEntryId
    }

    fun delete(entry: JournalEntry) {
        Timber.i("Delete $entry")
        withDispatcherContext { deleteEntry(entry) }
    }

    private fun deleteEntry(entry: JournalEntry) {
        deletedItem = entry
        database.delete(entry.id)
    }

    fun undoDelete() {
        Timber.i("Undo delete")
        withDispatcherContext { restoreEntry() }
    }

    private fun restoreEntry() {
        deletedItem?.let {
            database.add(it)
        }
        deletedItem = null
    }

    private fun withDispatcherContext(work: () -> Unit) {
        viewModelScope.launch {
            withContext(dispatcher) { work() }
        }
    }
}
