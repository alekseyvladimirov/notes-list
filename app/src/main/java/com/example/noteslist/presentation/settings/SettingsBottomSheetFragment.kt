package com.example.noteslist.presentation.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.noteslist.NotesListApp
import com.example.noteslist.databinding.BottomSheetSettingsBinding
import com.example.noteslist.di.SettingsComponent
import com.example.noteslist.data.settings.SettingsRepository
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject
import kotlinx.coroutines.launch

class SettingsBottomSheetFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private var settingsComponent: SettingsComponent? = null

    private var _binding: BottomSheetSettingsBinding? = null
    private val binding: BottomSheetSettingsBinding
        get() = _binding ?: error("BottomSheetSettingsBinding is null")

    override fun onAttach(context: Context) {
        val app = requireActivity().application as NotesListApp
        settingsComponent = app.appComponent.settingsComponentFactory().create()
        settingsComponent?.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sliderSpacing.stepSize = 1f
        binding.sliderMaxVisible.stepSize = 1f

        binding.sliderSpacing.addOnChangeListener { _, value, fromUser ->
            if (!fromUser) return@addOnChangeListener
            viewLifecycleOwner.lifecycleScope.launch {
                settingsRepository.setStackSpacingDp(value)
            }
        }

        binding.sliderMaxVisible.addOnChangeListener { _, value, fromUser ->
            if (!fromUser) return@addOnChangeListener
            viewLifecycleOwner.lifecycleScope.launch {
                settingsRepository.setStackMaxVisible(value.toInt())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsRepository.noteStackSettings.collect { settings ->
                    binding.sliderSpacing.value = settings.stackSpacingDp
                    binding.sliderMaxVisible.value = settings.stackMaxVisible.toFloat()
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        settingsComponent = null
        super.onDestroy()
    }

    companion object {
        private const val TAG = "SettingsBottomSheet"

        fun show(manager: FragmentManager) {
            SettingsBottomSheetFragment().show(manager, TAG)
        }
    }
}
