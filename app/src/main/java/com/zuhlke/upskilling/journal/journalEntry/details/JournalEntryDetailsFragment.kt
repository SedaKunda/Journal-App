package com.zuhlke.upskilling.journal.journalEntry.details

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.zuhlke.upskilling.journal.R
import com.zuhlke.upskilling.journal.database.JournalDatabaseInstance
import com.zuhlke.upskilling.journal.journalEntry.ADD_ATTACHMENT_CODE
import com.zuhlke.upskilling.journal.journalEntry.hideKeyboard
import kotlinx.android.synthetic.main.fragment_journal_entry_item.*

class JournalEntryDetailsFragment : Fragment() {
    private lateinit var viewModel: JournalEntryDetailsViewModel
    private val arguments: JournalEntryDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val application = requireActivity().application
        val dataSource = JournalDatabaseInstance.getInstance(application).journalEntryDAO
        val factory = JournalEntryDetailsViewModelFactory(
            dataSource,
            arguments.journalEntryId,
            arguments.folderId
        )
        viewModel = ViewModelProvider(this, factory).get(JournalEntryDetailsViewModel::class.java)
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_journal_entry_item, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.journal_note_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> if (viewModel.journalEntryIsValid()) {
                viewModel.updateEntry()
                navigateBack()
            }
            R.id.delete -> {
                viewModel.deleteEntry()
                navigateBack()
            }
            R.id.share -> {
                startActivity(Intent.createChooser(shareContent(), null))
                navigateBack()
            }
            R.id.attachment -> {
                startActivityForResult(
                    Intent.createChooser(
                        addAttachment(),
                        "Select a File to Upload"
                    ), ADD_ATTACHMENT_CODE
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.entry.observe(viewLifecycleOwner, Observer { journalEntry ->
            if (title.text.toString() != journalEntry.title) {
                title.setText(journalEntry.title)
            }
            if (contents.text.toString() != journalEntry.contents) {
                contents.setText(journalEntry.contents)
            }
            journalEntry.images?.let {
                imageView.setImageURI(Uri.parse(it))
            }
        })

        title.doAfterTextChanged { viewModel.updateTitle(it) }
        contents.doAfterTextChanged { viewModel.updateContents(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ATTACHMENT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.updateImage(data?.data.toString())
            }
        }
    }

    private fun shareContent() = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_SUBJECT, title.text.toString())
        putExtra(Intent.EXTRA_TITLE, title.text.toString())
        putExtra(Intent.EXTRA_TEXT, contents.text.toString())
        type = "text/plain"
    }

    private fun addAttachment() = Intent().apply {
        action = Intent.ACTION_OPEN_DOCUMENT
        type = "image/*";
        addCategory(Intent.CATEGORY_OPENABLE)
        addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    private fun navigateBack() {
        hideKeyboard(requireContext(), view)
        findNavController().popBackStack()
    }
}
