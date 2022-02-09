package com.alphitardian.trashappta.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

const val PRODUCTION_URL = "https://waste-detection.herokuapp.com/api/"
const val MOCK_URL = "https://stoplight.io/mocks/kelvindev/waste-detector-backend/35211751/api/"
const val IMAGE_UPLOAD_URL = "https://api.imgbb.com/1/"

const val STORE_NAME = "token_datastore"
val USER_TOKEN = stringPreferencesKey(name = "user_token")
val USER_ID = stringPreferencesKey(name = "user_id")
val REFRESH_TOKEN = stringPreferencesKey(name = "refresh_token")
val EXPIRED_TIME = longPreferencesKey(name = "expired_time")
val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = STORE_NAME)