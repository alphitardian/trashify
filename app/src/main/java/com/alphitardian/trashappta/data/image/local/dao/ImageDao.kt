package com.alphitardian.trashappta.data.image.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alphitardian.trashappta.data.image.local.entity.ImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(imageEntity: ImageEntity)

    @Query("SELECT * FROM image WHERE user_id = :id")
    fun getImage(id: String): Flow<ImageEntity>

    @Query("DELETE FROM image WHERE user_id = :id")
    suspend fun deleteImage(id: String)
}