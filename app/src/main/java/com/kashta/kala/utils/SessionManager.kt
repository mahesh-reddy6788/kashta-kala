package com.kashta.kala.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("kashta_session", Context.MODE_PRIVATE)

    companion object {
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_USER_ID      = "user_id"
        const val KEY_NAME         = "name"
        const val KEY_EMAIL        = "email"
        const val KEY_IS_ADMIN     = "is_admin"
        const val KEY_ONBOARDING   = "pref_onboarding_shown"
    }

    fun saveSession(userId: Int, name: String, email: String, isAdmin: Boolean) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .putBoolean(KEY_IS_ADMIN, isAdmin)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    fun isAdmin(): Boolean    = prefs.getBoolean(KEY_IS_ADMIN, false)
    fun getUserId(): Int      = prefs.getInt(KEY_USER_ID, -1)
    fun getUserName(): String = prefs.getString(KEY_NAME, "Guest") ?: "Guest"
    fun getUserEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""

    fun updateUserName(name: String) = prefs.edit().putString(KEY_NAME, name).apply()

    fun isOnboardingShown(): Boolean = prefs.getBoolean(KEY_ONBOARDING, false)
    fun setOnboardingShown() = prefs.edit().putBoolean(KEY_ONBOARDING, true).apply()

    fun logout() {
        val onboarding = isOnboardingShown()
        prefs.edit().clear().apply()
        if (onboarding) setOnboardingShown()
    }
}
