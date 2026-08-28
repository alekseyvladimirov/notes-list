package com.example.noteslist.presentation.note_detail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.noteslist.NotesListApp
import com.example.noteslist.databinding.FragmentNoteDetailBinding
import com.example.noteslist.di.notedetail.NoteDetailComponent
import com.example.noteslist.presentation.note_detail.NoteDetailViewModel
import com.example.noteslist.presentation.note_detail.model.NoteNavArgs
import com.example.noteslist.presentation.notes_list.ui.NotesListFragment
import androidx.navigation.fragment.findNavController
import javax.inject.Inject
import androidx.lifecycle.ViewModelProvider

class NoteDetailFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var noteDetailComponent: NoteDetailComponent? = null

    private var _binding: FragmentNoteDetailBinding? = null
    private val binding: FragmentNoteDetailBinding
        get() = _binding ?: error("FragmentNoteDetailBinding is null")

    private val noteIdArg: Long?
        get() = BundleCompat.getParcelable(arguments ?: Bundle(), ARG_NOTE_ARGS, NoteNavArgs::class.java)?.noteId

    private val viewModel: NoteDetailViewModel by viewModels {
        viewModelFactory
    }

    override fun onAttach(context: android.content.Context) {
        val app = requireActivity().application as NotesListApp
        noteDetailComponent = app.appComponent
            .noteDetailComponentFactory()
            .create(noteIdArg)
        noteDetailComponent?.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.noteDetailComposeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
        binding.noteDetailComposeView.setContent {
            NoteDetailScreen(
                viewModel = viewModel,
                onBackClick = { closeSelf() },
                onSaved = { closeSelf() }
            )
        }
    }

    private fun closeSelf() {
        val parent = parentFragment
        if (parent is NotesListFragment && parent.closeDetailPaneIfVisible()) {
            return
        }

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        noteDetailComponent = null
        super.onDestroy()
    }

    companion object {
        private const val ARG_NOTE_ARGS = "noteArgs"

        fun newInstance(noteArgs: NoteNavArgs): NoteDetailFragment {
            return NoteDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_NOTE_ARGS, noteArgs)
                }
            }
        }
    }
}
