package com.alphitardian.trashappta.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alphitardian.trashappta.data.waste.local.dao.WasteDao
import com.alphitardian.trashappta.data.waste.local.entity.WasteEntity

@Database(entities = [WasteEntity::class], version = 1, exportSchema = false)
abstract class TrashDatabase: RoomDatabase() {
    abstract fun wasteDao(): WasteDao
}