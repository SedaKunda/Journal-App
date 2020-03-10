package com.zuhlke.upskilling.journal.journalEntry.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zuhlke.upskilling.journal.database.model.JournalEntry
import com.zuhlke.upskilling.journal.database.dao.JournalEntryDAO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JournalEntryDetailsViewModel(
    val database: JournalEntryDAO,
    var journalEntryId: Int,
    val folderId: Int,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    var entry = MutableLiveData(JournalEntry())
    private fun isNewEntry(entry: JournalEntry) = entry.id == 0

    init {
        viewModelScope.launch {
            var result =
                JournalEntry(
                    folderId = folderId
                )
            withContext(dispatcher) {
                result = database.get(journalEntryId) ?: JournalEntry(
                    folderId = folderId
                )
            }
            entry.value = result
        }
    }

    fun updateEntry() {
        viewModelScope.launch { updateEntry(entry) }
    }

    private suspend fun updateEntry(entry: MutableLiveData<JournalEntry>) {
        withContext(dispatcher) {
            entry.value?.let {
                when {
                    isNewEntry(it) -> database.add(it)
                    else -> database.update(it)
                }
            }
        }
    }

    fun updateTitle(title: CharSequence?) {
        entry.value?.apply {
            entry.postValue(
                copy(
                    title = title.toString()
                )
            )
        }
    }

    fun updateContents(contents: CharSequence?) {
        entry.value?.apply {
            entry.postValue(
                copy(
                    contents = contents.toString()
                )
            )
        }
    }

    fun updateImage(path: String) {
        entry.value?.apply {
            entry.postValue(
                copy(
                    images = path
                )
            )
        }
    }

    fun journalEntryIsValid(): Boolean {
        return entry.value?.run {
            title.isNotEmpty() || contents.isNotEmpty()
        } ?: false
    }

    fun deleteEntry() {
        viewModelScope.launch {
            deleteEntry(entry)
        }
    }

    private suspend fun deleteEntry(entry: MutableLiveData<JournalEntry>) {
        withContext(dispatcher) {
            entry.value?.let {
                when {
                    isNewEntry(it) -> Unit
                    else -> database.delete(it.id)
                }
            }
        }
    }
}
