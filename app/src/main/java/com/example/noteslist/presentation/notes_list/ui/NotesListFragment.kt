package com.example.noteslist.presentation.notes_list.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteslist.NotesListApp
import com.example.noteslist.databinding.FragmentNotesListBinding
import com.example.noteslist.di.noteslist.NotesListComponent
import com.example.noteslist.presentation.note_detail.model.NoteNavArgs
import com.example.noteslist.presentation.note_detail.ui.NoteDetailFragment
import com.example.noteslist.presentation.notes_list.NotesListViewModel
import com.example.noteslist.presentation.notes_list.ui.adapter.CompositeDelegateAdapter
import com.example.noteslist.presentation.notes_list.ui.adapter.mapNotesToListItems
import com.example.noteslist.presentation.notes_list.ui.delegate.DateHeaderDelegate
import com.example.noteslist.presentation.notes_list.ui.delegate.ImportantNoteDelegate
import com.example.noteslist.presentation.notes_list.ui.delegate.NoteStackDelegate
import com.example.noteslist.presentation.settings.SettingsBottomSheetFragment
import javax.inject.Inject
import kotlinx.coroutines.launch

class NotesListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var notesListComponent: NotesListComponent? = null

    private var _binding: FragmentNotesListBinding? = null
    private val binding: FragmentNotesListBinding
        get() = _binding ?: error("FragmentNotesListBinding is null")

    private lateinit var compositeDelegateAdapter: CompositeDelegateAdapter
    private var detailContainer: FrameLayout? = null

    private val notesListViewModel: NotesListViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var noteStackDelegate: NoteStackDelegate

    override fun onAttach(context: android.content.Context) {
        val app = requireActivity().application as NotesListApp
        notesListComponent = app.appComponent.notesListComponentFactory().create()
        notesListComponent?.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailContainer = view.findViewById(com.example.noteslist.R.id.detailContainer)

        setupRecyclerView()
        setupActions()
        observeViewModel()
    }

    override fun onDestroyView() {
        detailContainer = null
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        notesListComponent = null
        super.onDestroy()
    }

    private fun setupRecyclerView() {
        noteStackDelegate = NoteStackDelegate(
            onNoteClick = { noteId -> openDetail(noteId) },
            onNoteLongClick = { noteId, isRead ->
                notesListViewModel.onReadToggle(noteId, isRead)
            }
        )

        compositeDelegateAdapter = CompositeDelegateAdapter(
            listOf(
                DateHeaderDelegate(),
                ImportantNoteDelegate(
                    onNoteClick = { noteId -> openDetail(noteId) },
                    onNoteLongClick = { noteId, isRead ->
                        notesListViewModel.onReadToggle(noteId, isRead)
                    }
                ),
                noteStackDelegate
            )
        )

        binding.notesListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notesListRecyclerView.adapter = compositeDelegateAdapter
    }

    private fun setupActions() {
        binding.fabAddNote.setOnClickListener {
            openDetail(null)
        }
        binding.btnSettings?.setOnClickListener {
            SettingsBottomSheetFragment.show(parentFragmentManager)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                notesListViewModel.state.collect { state ->
                    compositeDelegateAdapter.items = mapNotesToListItems(state.notes)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                notesListViewModel.settings.collect { settings ->
                    noteStackDelegate.stackSettings = settings
                    compositeDelegateAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun openDetail(noteId: Long?) {
        if (isTwoPaneMode()) {
            childFragmentManager.commit {
                replace(
                    requireNotNull(detailContainer).id,
                    NoteDetailFragment.newInstance(NoteNavArgs(noteId))
                )
                setReorderingAllowed(true)
            }
            return
        }

        val directions = NotesListFragmentDirections
            .actionNotesListFragmentToNoteDetailFragment(NoteNavArgs(noteId = noteId))
        findNavController().navigate(directions)
    }

    fun closeDetailPaneIfVisible(): Boolean {
        if (!isTwoPaneMode()) return false

        val container = detailContainer ?: return false
        val detailFragment = childFragmentManager.findFragmentById(container.id)
            ?: return false

        childFragmentManager.commit {
            remove(detailFragment)
            setReorderingAllowed(true)
        }
        return true
    }

    private fun isTwoPaneMode(): Boolean = detailContainer?.isVisible == true
}
