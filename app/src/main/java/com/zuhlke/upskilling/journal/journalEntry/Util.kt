package com.zuhlke.upskilling.journal.journalEntry

import android.content.Context
import android.text.format.DateUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.zuhlke.upskilling.journal.R
import com.zuhlke.upskilling.journal.database.model.Folder
import com.zuhlke.upskilling.journal.journalEntry.list.JournalEntryAdapter
import java.time.LocalDateTime
import java.time.ZoneId

const val DEFAULT_FOLDER_ID = 0
const val ITEM_VIEW_TYPE_HEADER = 0
const val ITEM_VIEW_TYPE_ITEM = 1
const val ADD_ATTACHMENT_CODE = 1
val DEFAULT_FOLDER = Folder(id = DEFAULT_FOLDER_ID, name = "ALL ENTRIES")

fun timeFromNow(date: LocalDateTime): CharSequence? {
    return DateUtils.getRelativeTimeSpanString(
        date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    )
}

fun displayTextIfEmpty(adapter: JournalEntryAdapter, view: View) {
    adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            checkEmpty()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            checkEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            checkEmpty()
        }

        fun checkEmpty() {
            view.visibility = (if (adapter.itemCount == 0) View.VISIBLE else View.GONE)
        }
    })
}

fun SpeedDialView.addAction(id: Int, icon: Int, label: Int) {
    this.addActionItem(SpeedDialActionItem.Builder(id, icon)
        .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.primaryLightColor, context?.applicationContext?.theme))
        .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.white, context?.applicationContext?.theme))
        .setLabel(label)
        .setLabelColor(ResourcesCompat.getColor(resources, R.color.primaryColor, context?.applicationContext?.theme))
        .setLabelBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, context?.applicationContext?.theme))
        .setLabelClickable(false)
        .create())
}

fun hideKeyboard(context: Context, view: View?) {
    ContextCompat.getSystemService(context, InputMethodManager::class.java)
        ?.hideSoftInputFromWindow(view?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}


