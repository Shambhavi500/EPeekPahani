package io.sc.eppCordova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.sc.eppCordova.data.local.entity.LandRecord

@Dao
interface LandRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<LandRecord>)

    @Query("SELECT * FROM land_records WHERE villageId = :villageId")
    suspend fun getLandRecordsByVillage(villageId: Int): List<LandRecord>
}
