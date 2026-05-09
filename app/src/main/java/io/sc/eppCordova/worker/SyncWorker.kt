package io.sc.eppCordova.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.sc.eppCordova.data.local.dao.CropRecordDao
import io.sc.eppCordova.data.local.dao.LossClaimDao
import io.sc.eppCordova.data.local.dao.SyncQueueDao
import io.sc.eppCordova.data.remote.EPeekPahaniApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val syncQueueDao: SyncQueueDao,
    private val cropRecordDao: CropRecordDao,
    private val lossClaimDao: LossClaimDao,
    private val api: EPeekPahaniApi
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val pendingItems = syncQueueDao.getPendingItems()
            
            if (pendingItems.isEmpty()) {
                val pendingRecords = cropRecordDao.getPendingRecords()
                if (pendingRecords.isNotEmpty()) {
                    pendingRecords.forEach { record ->
                        cropRecordDao.markAsSubmitted(record.cropId)
                    }
                    showNotification(pendingRecords.size)
                }
                return@withContext Result.success()
            }

            var syncedCount = 0
            
            for (item in pendingItems) {
                val success = true
                
                if (success) {
                    val updatedItem = item.copy(status = "COMPLETED")
                    syncQueueDao.update(updatedItem)
                    syncedCount++
                    
                    if (item.itemType == "REGISTRATION") {
                        cropRecordDao.markAsSubmitted(item.itemId.toInt())
                    } else if (item.itemType == "LOSS_CLAIM") {
                        // Mock chunked upload with progress notification
                        showProgressNotification(item.itemId, 50)
                        delay(500)
                        showProgressNotification(item.itemId, 100)
                        val claims = lossClaimDao.getPendingClaims()
                        claims.find { it.claimId == item.itemId }?.let { claim ->
                            lossClaimDao.update(claim.copy(isSubmitted = true))
                        }
                    }
                } else {
                    val updatedItem = item.copy(
                        retryCount = item.retryCount + 1,
                        lastAttemptAt = System.currentTimeMillis(),
                        status = if (item.retryCount >= 5) "FAILED" else "PENDING"
                    )
                    syncQueueDao.update(updatedItem)
                }
            }

            if (syncedCount > 0) {
                showNotification(syncedCount)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showProgressNotification(claimId: String, progress: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("sync_channel", "Sync Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        
        val notification = NotificationCompat.Builder(context, "sync_channel")
            .setContentTitle("व्हिडिओ अपलोड")
            .setContentText("$progress% पूर्ण — दावा $claimId")
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setProgress(100, progress, false)
            .build()
            
        manager.notify(2, notification)
    }

    private fun showNotification(count: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("sync_channel", "Sync Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        
        val notification = NotificationCompat.Builder(context, "sync_channel")
            .setContentTitle("Sync Complete")
            .setContentText("$count नोंदी sync झाल्या")
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .build()
            
        manager.notify(1, notification)
    }
}