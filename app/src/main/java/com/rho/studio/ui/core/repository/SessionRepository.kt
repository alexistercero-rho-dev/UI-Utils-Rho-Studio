/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         SessionRepository.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-07-14
 * ==============================================================================================
 * Description: Repository for persisting session data.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.rho.studio.ui.core.model.User

/**
 * Interface defining the persistence operations for user sessions.
 */
interface SessionRepository {
    suspend fun saveUser(user: User)
    suspend fun getUser(): User?
    suspend fun clearSession()
}

/**
 * Implementation of [SessionRepository] using SharedPreferences.
 */
class SessionRepositoryImpl(context: Context) : SessionRepository {

    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "session_prefs"
        private const val PREF_KEY_USER = "pref_current_user"
    }

    override suspend fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        preferences.edit {
            putString(PREF_KEY_USER, userJson)
        }
    }

    override suspend fun getUser(): User? {
        val userJson = preferences.getString(PREF_KEY_USER, null) ?: return null
        return try {
            gson.fromJson(userJson, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun clearSession() {
        preferences.edit {
            remove(PREF_KEY_USER)
        }
    }
}
