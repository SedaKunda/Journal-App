package com.zuhlke.upskilling.journal.folder.details

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.zuhlke.upskilling.journal.R
import com.zuhlke.upskilling.journal.database.JournalDatabaseInstance
import com.zuhlke.upskilling.journal.database.model.JournalEntry
import com.zuhlke.upskilling.journal.journalEntry.displayTextIfEmpty
import com.zuhlke.upskilling.journal.journalEntry.hideKeyboard
import com.zuhlke.upskilling.journal.journalEntry.list.JournalEntryAdapter
import com.zuhlke.upskilling.journal.journalEntry.list.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_folder_journal_entries_list.*
import kotlinx.android.synthetic.main.fragment_all_journal_entries_list.journal_entries_list
import kotlinx.android.synthetic.main.fragment_all_journal_entries_list.new_journal_entry_button

class FolderDetailsFragment : Fragment() {

    private lateinit var viewModel: FolderDetailsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JournalEntryAdapter
    private val arguments: FolderDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val application = requireActivity().application
        viewModel = ViewModelProvider(this, factory(application)).get(FolderDetailsViewModel::class.java)
        adapter = JournalEntryAdapter(JournalEntryAdapter.JournalEntryListener(viewModel::onJournalEntryClicked), ::deleteAndShowUndo)
        return inflater.inflate(R.layout.fragment_folder_journal_entries_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        new_journal_entry_button.setOnClickListener { navigateToJournalEntry(0) }
        folder_name?.doAfterTextChanged { viewModel.updateFolderName(it.toString()) }
        observeLiveData()
    }

    private fun factory(application: Application): FolderDetailsViewModelFactory {
        return JournalDatabaseInstance.getInstance(application).run {
            FolderDetailsViewModelFactory(this, arguments.folderId)
        }
    }

    private fun observeLiveData() {
        with(viewModel) {
            navigateToJournalEntry.observe(viewLifecycleOwner, Observer {
                it?.let { navigateToJournalEntry(it) }
            })

            folder.observe(viewLifecycleOwner, Observer {
                if (it.name != folder_name.text.toString()) {
                    folder_name.setText(it.name)
                }
            })

            journalEntries.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        }
    }

    private fun navigateToJournalEntry(id: Int) {
        hideKeyboard(requireContext(), view)
        findNavController().navigate(
            FolderDetailsFragmentDirections.actionFolderDetailsFragmentToJournalEntryItemFragment(
                id, arguments.folderId))
        viewModel.doneNavigating()
    }

    private fun deleteAndShowUndo(entry: JournalEntry) {
        viewModel.deleteEntry(entry)
        showUndoSnackbar()
    }

    private fun showUndoSnackbar() {
        Snackbar.make(requireView(), "Deleted entry", Snackbar.LENGTH_LONG)
            .setAction("Undo") { viewModel.undoDeleteEntry() }
            .show()
    }

    private fun setupRecyclerView() {
        recyclerView = journal_entries_list
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        addSwipeToDelete(adapter)
        displayTextIfEmpty(adapter, emptyView)
    }

    private fun addSwipeToDelete(adapter: JournalEntryAdapter) {
        val color = ContextCompat.getColor(requireContext(), R.color.secondaryColor)
        ItemTouchHelper(SwipeToDeleteCallback(adapter, color))
            .attachToRecyclerView(recyclerView)
    }
}