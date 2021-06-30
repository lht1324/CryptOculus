package org.techtown.cryptoculus.repository.model

import android.content.SharedPreferences

interface SavedSharedPreferences {
    fun getExchange(): String

    fun putExchange(value: String)

    fun getSortMode(): Int

    fun putSortMode(value: Int)

    fun getIdleCheck(): Boolean

    fun putIdleCheck(value: Boolean)

    fun getFirstRun(): Boolean

    fun putFirstRun(value: Boolean)
}

class SavedSharedPreferencesImpl(private val preferences: SharedPreferences) : SavedSharedPreferences {
    override fun getExchange() = preferences.getString("exchange", "Coinone")!!

    override fun putExchange(value: String) = preferences.edit().putString("exchange", value).apply()

    override fun getSortMode() = preferences.getInt("sortMode", 0)

    override fun putSortMode(value: Int) = preferences.edit().putInt("sortMode", value).apply()

    override fun getIdleCheck() = preferences.getBoolean("idleCheck", true)

    override fun putIdleCheck(value: Boolean) = preferences.edit().putBoolean("idleCheck", value).apply()

    override fun getFirstRun() = preferences.getBoolean("firstRun", true)

    override fun putFirstRun(value: Boolean) = preferences.edit().putBoolean("firstRun", value).apply()
}