package io.sc.eppCordova.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.sc.eppCordova.data.local.entity.LossClaimEntity

@Dao
interface LossClaimDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lossClaim: LossClaimEntity)

    @Update
    suspend fun update(lossClaim: LossClaimEntity)

    @Query("SELECT * FROM loss_claims WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getClaimsByFarmer(farmerId: String): LiveData<List<LossClaimEntity>>

    @Query("SELECT * FROM loss_claims WHERE isSubmitted = 0")
    suspend fun getPendingClaims(): List<LossClaimEntity>

    @Query("SELECT * FROM loss_claims WHERE claimId = :claimId")
    fun getClaimById(claimId: String): LiveData<LossClaimEntity?>
}
