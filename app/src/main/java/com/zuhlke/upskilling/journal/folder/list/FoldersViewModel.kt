package com.zuhlke.upskilling.journal.folder.list

import androidx.lifecycle.*
import com.zuhlke.upskilling.journal.database.model.Folder
import com.zuhlke.upskilling.journal.database.dao.FolderAndJournalsDAO
import com.zuhlke.upskilling.journal.database.dao.FolderDAO
import com.zuhlke.upskilling.journal.journalEntry.DEFAULT_FOLDER
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FoldersViewModel(
    private val folderDatabase: FolderDAO,
    private val folderToJournalsDatabase: FolderAndJournalsDAO,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _navigateToFolderContents = MutableLiveData<Int>()

    lateinit var folders: LiveData<List<UiFolder>>
    var size = 0
    fun loadFolders() {
        folders = Transformations.switchMap(folderDatabase.test()) { size: Int ->
            Transformations.map(folderToJournalsDatabase.getAll()) { foldersAndJournalsList ->
                foldersAndJournalsList.map { i -> UiFolder(i.folder, i.journals.size) }
                    .plus(UiFolder(DEFAULT_FOLDER, size))
            }
        }
    }

    val navigateToFolderContents
        get() = _navigateToFolderContents

    fun delete(folder: Folder) {
        withDispatcherContext { folderDatabase.delete(folder.id) }
    }

    fun doneNavigating() {
        _navigateToFolderContents.value = null
    }

    fun onFolderClicked(folderId: Int) {
        _navigateToFolderContents.value = folderId
    }

    fun navigateToNewFolder() {
        viewModelScope.launch {
            withContext(dispatcher) {
                folderDatabase.add(Folder())
                _navigateToFolderContents.postValue(folderDatabase.getLatest().id)
            }
        }
    }

    private fun withDispatcherContext(work: () -> Unit) {
        viewModelScope.launch {
            withContext(dispatcher) { work() }
        }
    }
}

data class UiFolder(val folder: Folder, val journalsCount: Int)
