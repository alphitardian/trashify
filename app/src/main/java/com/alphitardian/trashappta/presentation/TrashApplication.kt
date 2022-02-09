package com.alphitardian.trashappta.presentation

import android.app.Application
import com.alphitardian.trashappta.BuildConfig
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TrashApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Places.initialize(this, BuildConfig.MAP_KEY)
    }
}