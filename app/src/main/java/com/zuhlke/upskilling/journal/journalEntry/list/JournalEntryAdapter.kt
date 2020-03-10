package com.zuhlke.upskilling.journal.journalEntry.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zuhlke.upskilling.journal.R
import com.zuhlke.upskilling.journal.database.model.JournalEntry
import com.zuhlke.upskilling.journal.journalEntry.list.JournalEntryAdapter.JourneyEntryViewHolder
import com.zuhlke.upskilling.journal.journalEntry.timeFromNow

class JournalEntryAdapter(
    private val clickListener: JournalEntryListener,
    private val delete: (entry: JournalEntry) -> Unit
) :
    ListAdapter<JournalEntry, JourneyEntryViewHolder>(JournalEntryDiff) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneyEntryViewHolder {
        return JourneyEntryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: JourneyEntryViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    fun deleteItem(position: Int) {
        delete(getItem(position))
    }

    class JourneyEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contents = itemView.findViewById<TextView>(R.id.journal_entry_contents)
        private val title = itemView.findViewById<TextView>(R.id.journey_entry_title)
        private val date = itemView.findViewById<TextView>(R.id.edited_date)

        fun bind(entry: JournalEntry, clickListener: JournalEntryListener) {
            contents.text = entry.contents
            title.text = entry.title
            date.text = timeFromNow(entry.date)

            itemView.setOnClickListener { clickListener.onClick(entry) }
        }

        companion object {
            fun from(parent: ViewGroup): JourneyEntryViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.recycler_view_journal_entry_item, parent, false)
                return JourneyEntryViewHolder(view)
            }
        }
    }

    companion object JournalEntryDiff : DiffUtil.ItemCallback<JournalEntry>() {
        override fun areItemsTheSame(oldItem: JournalEntry, newItem: JournalEntry): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: JournalEntry, newItem: JournalEntry): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class JournalEntryListener(val clickListener: (id: Int) -> Unit) {
        fun onClick(journalEntry: JournalEntry) {
            clickListener(journalEntry.id)
        }
    }
}
