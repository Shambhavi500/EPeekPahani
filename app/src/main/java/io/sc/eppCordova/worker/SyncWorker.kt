package io.sc.eppCordova.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.sc.eppCordova.data.repository.CropRepository

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val cropRepository: CropRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            cropRepository.syncPending()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
