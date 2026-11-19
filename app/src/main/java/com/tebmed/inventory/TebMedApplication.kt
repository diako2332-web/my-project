package com.tebmed.inventory

import android.app.Application
import androidx.room.Room
import com.tebmed.inventory.data.database.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TebMedApplication : Application() {
    // Lazy DB instance for non-Hilt quick access if needed
    val database by lazy { AppDatabase.getInstance(this) }
}
