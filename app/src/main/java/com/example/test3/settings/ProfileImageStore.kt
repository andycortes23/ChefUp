package com.example.test3.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

private fun userKey(userId: String) = stringPreferencesKey("profile_image_uri_$userId")

object ProfileImageStore {

    fun getProfileImageUri(context: Context, userId: String): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[userKey(userId)]
        }
    }

    suspend fun saveProfileImageUri(context: Context, userId: String, uri: String) {
        context.dataStore.edit { prefs ->
            prefs[userKey(userId)] = uri
        }
    }
}
