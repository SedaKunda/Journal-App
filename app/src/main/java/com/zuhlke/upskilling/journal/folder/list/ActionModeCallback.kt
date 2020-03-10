package com.zuhlke.upskilling.journal.folder.list

import android.view.ActionMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.MutableLiveData
import com.zuhlke.upskilling.journal.R

class ActionModeCallback (private val folderAction: () -> Unit) : ActionMode.Callback {
    var liveDataActionMode = MutableLiveData<ActionMode?>()

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        liveDataActionMode.value = mode
        val inflater: MenuInflater = mode.menuInflater
        inflater.inflate(R.menu.folder_context_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                folderAction()
                mode.finish()
                true
            }
            else -> false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        liveDataActionMode.value = null
    }
}