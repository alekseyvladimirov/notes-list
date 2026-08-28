package com.example.noteslist

import android.app.Application

class NotesListApp : Application() {
    lateinit var appComponent: com.example.noteslist.di.AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = com.example.noteslist.di.DaggerAppComponent.factory().create(this)
    }
}
