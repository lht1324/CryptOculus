package org.techtown.cryptoculus.repository.model

import android.content.SharedPreferences

interface SavedPreferences {
    fun getRestartApp(): Boolean

    fun putRestartApp(value: Boolean)

    fun getExchange(): String

    fun putExchange(value: String)
}

class Preferences(private val preferences: SharedPreferences) : SavedPreferences {
    override fun getRestartApp() = preferences.getBoolean("restartApp", false)

    override fun putRestartApp(value: Boolean) = preferences.edit().putBoolean("restartApp", value).apply()

    override fun getExchange() = preferences.getString("exchange", "Coinone")!!

    override fun putExchange(value: String)= preferences.edit().putString("exchange", value).apply()
}