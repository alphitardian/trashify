package com.alphitardian.trashappta.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.alphitardian.trashappta.utils.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppDatastore @Inject constructor(
    @ApplicationContext context: Context,
    private val settingDataStore: DataStore<Preferences> = context.datastore,
) {
    val userToken = settingDataStore.data.map { preference ->
        val token = preference[USER_TOKEN] ?: ""
        token
    }

    suspend fun saveToken(token: String) {
        settingDataStore.edit { preference ->
            preference[USER_TOKEN] = token
        }
    }

    val userId = settingDataStore.data.map { preference ->
        val id = preference[USER_ID] ?: ""
        id
    }

    suspend fun saveUserId(id: String) {
        settingDataStore.edit { preference ->
            preference[USER_ID] = id
        }
    }

    val refreshToken = settingDataStore.data.map { preference ->
        val token = preference[REFRESH_TOKEN] ?: ""
        token
    }

    suspend fun saveRefreshToken(token: String) {
        settingDataStore.edit { preference ->
            preference[REFRESH_TOKEN] = token
        }
    }

    val expiredTime = settingDataStore.data.map { preference ->
        val time = preference[EXPIRED_TIME] ?: 0
        time
    }

    suspend fun saveExpiredTime(time: Long) {
        settingDataStore.edit { preference ->
            preference[EXPIRED_TIME] = time
        }
    }

    suspend fun clear() {
        settingDataStore.edit {
            it.clear()
        }
    }
}