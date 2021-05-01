package org.techtown.cryptoculus.repository.model

import android.content.SharedPreferences

interface SavedPreferences {
    fun getBoolean(key: String, default: Boolean): Boolean

    fun getString(key: String, default: String): String
}

class AndroidPreferences(private val preferences: SharedPreferences) : SavedPreferences {
    override fun getBoolean(key: String, default: Boolean) = preferences.getBoolean(key, default)

    override fun getString(key: String, default: String) = preferences.getString(key, default)!!
}