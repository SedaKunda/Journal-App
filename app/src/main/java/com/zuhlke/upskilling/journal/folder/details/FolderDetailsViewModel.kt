package com.zuhlke.upskilling.journal.folder.details

import androidx.lifecycle.*
import com.zuhlke.upskilling.journal.database.dao.FolderAndJournalsDAO
import com.zuhlke.upskilling.journal.database.dao.FolderDAO
import com.zuhlke.upskilling.journal.database.dao.JournalEntryDAO
import com.zuhlke.upskilling.journal.database.model.Folder
import com.zuhlke.upskilling.journal.database.model.JournalEntry
import com.zuhlke.upskilling.journal.journalEntry.DEFAULT_FOLDER
import com.zuhlke.upskilling.journal.journalEntry.DEFAULT_FOLDER_ID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FolderDetailsViewModel(
    val folderId: Int,
    val folderToJournalsDatabase: FolderAndJournalsDAO,
    val folderDatabase: FolderDAO,
    val journalEntryDatabase: JournalEntryDAO,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    var folder: LiveData<Folder>
    var journalEntries: LiveData<List<JournalEntry>>
    private var deletedItem: JournalEntry? = null
    private val _navigateToJournalEntry = MutableLiveData<Int>()
    val navigateToJournalEntry get() = _navigateToJournalEntry

    init {
        when (folderId) {
            DEFAULT_FOLDER_ID -> {
                folder = MutableLiveData<Folder>(DEFAULT_FOLDER)
                journalEntries = journalEntryDatabase.getAll()
        }
            else -> {
                val folderAndJournals = folderToJournalsDatabase.get(folderId)
                folder = Transformations.map(folderAndJournals) { it.folder }
                journalEntries = Transformations.map(folderAndJournals) { it.journals }
            }
        }
    }

    fun doneNavigating() {
        _navigateToJournalEntry.value = null
    }

    fun onJournalEntryClicked(journalEntryId: Int) {
        _navigateToJournalEntry.value = journalEntryId
    }

    fun updateFolderName(name: String) {
        withDispatcherContext {
            folderDatabase.update(
                Folder(
                    id = folderId,
                    name = name
                )
            )
        }
    }

    fun deleteEntry(entry: JournalEntry) {
        withDispatcherContext {
            deletedItem = entry
            journalEntryDatabase.delete(entry.id)
        }
    }

    fun undoDeleteEntry() {
        withDispatcherContext { restoreEntry() }
    }

    private fun restoreEntry() {
        deletedItem?.let {
            journalEntryDatabase.add(it)
        }
        deletedItem = null
    }

    private fun withDispatcherContext(work: () -> Unit) {
        viewModelScope.launch {
            withContext(dispatcher) { work() }
        }
    }
}
