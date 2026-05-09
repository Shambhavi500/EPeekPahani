package io.sc.eppCordova.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.sc.eppCordova.data.local.entity.SyncQueueEntity

@Dao
interface SyncQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(syncQueueEntity: SyncQueueEntity)

    @Update
    suspend fun update(syncQueueEntity: SyncQueueEntity)

    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY priority DESC, id ASC")
    suspend fun getPendingItems(): List<SyncQueueEntity>

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status = 'PENDING'")
    fun getPendingSyncCount(): LiveData<Int>

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteById(id: Int)
}
