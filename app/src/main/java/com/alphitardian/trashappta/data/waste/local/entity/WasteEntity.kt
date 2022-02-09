package com.alphitardian.trashappta.data.waste.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "waste")
data class WasteEntity(
    @PrimaryKey val wasteId: String,
    @ColumnInfo(name = "waste_name") val wasteName: String,
    @ColumnInfo(name = "waste_alias") val wasteAlias: String
)