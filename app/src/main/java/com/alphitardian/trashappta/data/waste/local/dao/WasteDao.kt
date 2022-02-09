package com.alphitardian.trashappta.data.waste.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alphitardian.trashappta.data.waste.local.entity.WasteEntity

@Dao
interface WasteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWasteCategory(waste: WasteEntity)

    @Query("SELECT * FROM waste WHERE waste_alias = :alias")
    suspend fun getWasteCategoryByAlias(alias: String): WasteEntity
}