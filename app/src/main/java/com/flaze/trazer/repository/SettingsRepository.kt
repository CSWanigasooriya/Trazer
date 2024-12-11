package com.flaze.trazer.repository

import android.content.Context
import androidx.core.content.edit

class SettingsRepository(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    fun getSender(): String = sharedPreferences.getString("sender", "") ?: ""
    fun getRegexPattern(): String = sharedPreferences.getString("regexPattern", "") ?: ""

    fun saveSender(sender: String) {
        sharedPreferences.edit { putString("sender", sender) }
    }

    fun saveRegexPattern(regexPattern: String) {
        sharedPreferences.edit { putString("regexPattern", regexPattern) }
    }
}