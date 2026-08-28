package com.example.noteslist.di

import com.example.noteslist.presentation.settings.SettingsBottomSheetFragment
import dagger.Subcomponent

@Subcomponent
interface SettingsComponent {
    fun inject(fragment: SettingsBottomSheetFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): SettingsComponent
    }
}

