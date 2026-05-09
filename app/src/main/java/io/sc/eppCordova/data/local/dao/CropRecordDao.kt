package io.sc.eppCordova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.sc.eppCordova.data.local.entity.AdminUnit
import io.sc.eppCordova.data.local.entity.CropRecord
import io.sc.eppCordova.data.local.entity.Farmer
import io.sc.eppCordova.data.local.entity.LandRecord

@Dao
interface CropRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCropRecord(cropRecord: CropRecord)

    @Query("SELECT * FROM crop_records WHERE isSubmitted = 0")
    suspend fun getPendingRecords(): List<CropRecord>

    @Query("UPDATE crop_records SET isSubmitted = 1 WHERE cropId = :id")
    suspend fun markAsSubmitted(id: Int)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarmer(farmer: Farmer)

    @Query("SELECT * FROM farmers LIMIT 1")
    suspend fun getFarmer(): Farmer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdminUnits(units: List<AdminUnit>)

    @Query("SELECT * FROM admin_units")
    suspend fun getAllAdminUnits(): List<AdminUnit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLandRecords(records: List<LandRecord>)

    @Query("SELECT * FROM land_records")
    suspend fun getAllLandRecords(): List<LandRecord>
}
