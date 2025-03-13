package com.example.todolist.common.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TDApplication : Application() {
    /* **********************************************************************
     * Function - Lifecycle
     ********************************************************************** */
    override fun onCreate() {
        super.onCreate()
    }
}