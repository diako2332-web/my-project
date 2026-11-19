package com.tebmed.inventory.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object BackupUtils {
    fun backupDatabase(context: Context): File {
        val dbFile = context.getDatabasePath("tebmed_inventory.db")
        val backupDir = File(context.getExternalFilesDir(null), "TebMedBackups")
        if (!backupDir.exists()) backupDir.mkdirs()
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        val backupFile = File(backupDir, "backup_${sdf.format(Date())}.db")
        dbFile.copyTo(backupFile, overwrite = true)
        return backupFile
    }

    fun restoreDatabase(context: Context, backupFile: File): Boolean {
        return try {
            val dbFile = context.getDatabasePath("tebmed_inventory.db")
            // Close DB first is handled by caller
            backupFile.copyTo(dbFile, overwrite = true)
            true
        } catch (e: Exception) { false }
    }

    fun listBackups(context: Context): List<File> {
        val dir = File(context.getExternalFilesDir(null), "TebMedBackups")
        if (!dir.exists()) return emptyList()
        return dir.listFiles()?.sortedByDescending { it.lastModified() }?.toList() ?: emptyList()
    }
}
