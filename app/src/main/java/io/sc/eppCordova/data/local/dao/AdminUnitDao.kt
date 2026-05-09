package io.sc.eppCordova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.sc.eppCordova.data.local.entity.AdminUnit

@Dao
interface AdminUnitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(units: List<AdminUnit>)

    @Query("SELECT * FROM admin_units")
    suspend fun getAll(): List<AdminUnit>

    @Query("DELETE FROM admin_units")
    suspend fun clearAll()
}
