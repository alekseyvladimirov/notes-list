package com.example.noteslist.presentation.main

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.noteslist.R
import com.example.noteslist.databinding.ActivityMainBinding
import com.example.noteslist.presentation.notes_list.ui.NotesListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this) {
            handleBackPress()
        }
    }

    private fun handleBackPress() {
        val navHost = supportFragmentManager.findFragmentById(R.id.mainNavHost) as? NavHostFragment
            ?: return

        val listFragment = navHost.childFragmentManager.fragments
            .firstOrNull { it is NotesListFragment } as? NotesListFragment

        if (listFragment?.closeDetailPaneIfVisible() == true) {
            return
        }

        val navController = navHost.navController
        if (navController.currentDestination?.id != R.id.notesListFragment) {
            navController.popBackStack()
            return
        }

        showExitDialog()
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.exit_dialog_title))
            .setMessage(getString(R.string.exit_dialog_message))
            .setPositiveButton(getString(R.string.exit_dialog_confirm)) { _, _ ->
                finish()
            }
            .setNegativeButton(getString(R.string.exit_dialog_cancel), null)
            .show()
    }
}