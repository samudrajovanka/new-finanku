package com.mobprog.finanku.preference

import android.content.Context
import com.mobprog.finanku.data.User

internal class AuthPreference(context: Context) {
    companion object {
        private const val PREFS_NAME = "auth_pref"
        private const val IS_LOGIN = "is_login"
        private const val TOKEN = "token"
        private const val EMAIL = "email"
        private const val ID = "id"
        private const val NAME = "name"
        private const val USERNAME = "username"
    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun login(token: String, user: User) {
        val editor = preferences.edit()
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(TOKEN, String.format("Bearer %s", token))

        editor.putInt(ID, user.id)
        editor.putString(NAME, user.name)
        editor.putString(EMAIL, user.email)
        editor.putString(USERNAME, user.username)

        editor.apply()
    }

    fun isLogin(): Boolean {
        return preferences.getBoolean(IS_LOGIN, false)
    }

    fun logout() {
        val editor = preferences.edit()
        editor.putBoolean(IS_LOGIN, false)

        editor.putInt(ID, 0)
        editor.putString(NAME, "")
        editor.putString(EMAIL, "")
        editor.putString(USERNAME, "")

        editor.apply()
    }

    fun getToken(): String? {
        return preferences.getString(TOKEN, "")
    }

    fun getUser(): User {
        return User(
            preferences.getInt(ID, 0),
            preferences.getString(NAME, ""),
            preferences.getString(EMAIL, ""),
            preferences.getString(USERNAME, "")
        )
    }

    fun updateEmail(email: String) {
        val editor = preferences.edit()
        editor.putString(EMAIL, email)
        editor.putString(USERNAME, email)

        editor.apply()
    }
}