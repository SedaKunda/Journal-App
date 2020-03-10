package com.zuhlke.upskilling.journal.folder.list

import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.leinardi.android.speeddial.SpeedDialView
import com.zuhlke.upskilling.journal.R
import com.zuhlke.upskilling.journal.database.model.Folder
import com.zuhlke.upskilling.journal.database.JournalDatabaseInstance
import com.zuhlke.upskilling.journal.folder.list.FoldersAdapter.FolderListener
import com.zuhlke.upskilling.journal.journalEntry.addAction
import kotlinx.android.synthetic.main.fragment_folders_list.*
import com.zuhlke.upskilling.journal.folder.list.FoldersFragmentDirections.actionFoldersListFragmentToFolderDetailsFragment as toFolderDetails
import com.zuhlke.upskilling.journal.folder.list.FoldersFragmentDirections.actionFoldersListFragmentToJournalEntryItemFragment as toJournalEntry

class FoldersFragment : Fragment() {
    private lateinit var viewModel: FoldersViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FoldersAdapter
    private var actionMode: ActionMode? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val application = requireActivity().application
        val factory = FoldersViewModelFactory(JournalDatabaseInstance.getInstance(application))
        viewModel = ViewModelProvider(this, factory).get(FoldersViewModel::class.java)
        adapter = FoldersAdapter(FolderListener(viewModel::onFolderClicked), ::onLongClick, resources)
        return inflater.inflate(R.layout.fragment_folders_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadFolders()
        setupRecyclerView()
        registerForContextMenu(recyclerView)
        observeLiveData()

        val speedDialView = speedDial
        speedDialView.addAction(R.id.addFolder, R.drawable.add_folder, R.string.add_folder)
        speedDialView.addAction(R.id.addEntry, R.drawable.add_note, R.string.add_journal_entry)
        speedDialView.addAction(R.id.other, R.drawable.other_random_image, R.string.other)
        speedDialListener(speedDialView)
    }

    private fun speedDialListener(view: SpeedDialView) {
        view.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.addFolder -> {
                    viewModel.navigateToNewFolder()
                    view.close()
                }
                R.id.addEntry -> {
                    navigateToJournalEntry(0)
                    view.close()
                }
                R.id.other -> {
                    Snackbar.make(requireView(), "This is a fake option", Snackbar.LENGTH_LONG).show()
                    view.close()
                }
            }
            false
        }
    }

    private fun onLongClick(itemView: View, folder: Folder) {
        itemView.setOnLongClickListener{
            view ->
            when (actionMode) {
                null -> {
                    var callback = ActionModeCallback {delete(folder)}
                    activity?.startActionMode(callback)
                    callback.liveDataActionMode.observe(this, Observer{actionMode = it})
                    view.isSelected = true
                    true
                }
                else -> false
            }
        }
    }

    private fun observeLiveData() {
        with(viewModel) {
            navigateToFolderContents.observe(viewLifecycleOwner, Observer {
                it?.let { navigateToFolderDetails(it) }
            })

            folders.observe(viewLifecycleOwner, Observer {
                adapter.addHeaderAndSubmitList(it)
            })
        }
    }

    private fun navigateToFolderDetails(id: Int) {
        findNavController().navigate(toFolderDetails(id))
        viewModel.doneNavigating()
    }

    private fun navigateToJournalEntry(id: Int) {
        findNavController().navigate(toJournalEntry(id, 0))
        viewModel.doneNavigating()
    }

    private fun delete(folder: Folder) {
        viewModel.delete(folder)
    }

    private fun setupRecyclerView() {
        recyclerView = folders
        recyclerView.adapter = adapter
        recyclerView.layoutManager = gridLayoutManager()
    }

    private fun gridLayoutManager(): GridLayoutManager {
        val manager = GridLayoutManager(context, 3)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = when (position) {
                0 -> 3
                else -> 1
            }
        }
        return manager
    }
}
