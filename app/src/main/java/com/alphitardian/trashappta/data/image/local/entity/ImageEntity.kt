package com.alphitardian.trashappta.data.image.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image")
data class ImageEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String,
)
