package com.zuhlke.upskilling.journal.folder.list

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zuhlke.upskilling.journal.R
import com.zuhlke.upskilling.journal.database.model.Folder
import com.zuhlke.upskilling.journal.journalEntry.ITEM_VIEW_TYPE_HEADER
import com.zuhlke.upskilling.journal.journalEntry.ITEM_VIEW_TYPE_ITEM
import com.zuhlke.upskilling.journal.journalEntry.timeFromNow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FoldersAdapter(
    private val clickListener: FolderListener,
    private val longClickListener: (itemView: View, folder: Folder) -> Unit,
    private val resources: Resources
) :
    ListAdapter<FoldersAdapter.DataItem, RecyclerView.ViewHolder>(FolderDiff) {
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> FoldersViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FoldersViewHolder -> {
                val folderItem = getItem(position) as DataItem.FolderItem
                holder.bind(folderItem.folder, clickListener, longClickListener, resources)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.FolderItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    fun addHeaderAndSubmitList(list: List<UiFolder>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + list.map { DataItem.FolderItem(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    class HeaderHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): HeaderHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.recycler_view_folder_header, parent, false)
                return HeaderHolder(view)
            }
        }
    }

    class FoldersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name = itemView.findViewById<TextView>(R.id.name)
        private val date = itemView.findViewById<TextView>(R.id.date)
        private val count = itemView.findViewById<TextView>(R.id.itemCount)

        fun bind(folder: UiFolder, clickListener: FolderListener, longClickListener: (View, Folder) -> Unit, resources: Resources) {
            name.text = folder.folder.name
            date.text = timeFromNow(folder.folder.date)
            count.text = resources.getQuantityString(
                R.plurals.journals_count,
                folder.journalsCount,
                folder.journalsCount
            )
            itemView.setOnClickListener { clickListener.onClick(folder.folder) }
            longClickListener(itemView, folder.folder)
        }

        companion object {
            fun from(parent: ViewGroup): FoldersViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.recycler_view_folder_item, parent, false)
                return FoldersViewHolder(view)
            }
        }
    }

    companion object FolderDiff : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }
    }

    class FolderListener(val clickListener: (id: Int) -> Unit) {
        fun onClick(folder: Folder) {
            clickListener(folder.id)
        }
    }

    sealed class DataItem {
        abstract val id: Int

        data class FolderItem(val folder: UiFolder) : DataItem() {
            override val id = folder.folder.id
        }

        object Header : DataItem() {
            override val id = Int.MIN_VALUE
        }

    }
}
