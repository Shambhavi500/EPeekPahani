package io.sc.eppCordova

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import io.sc.eppCordova.worker.SyncWorker
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class EPeekPahaniApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Schedule WorkManager sync
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "crop_sync", ExistingPeriodicWorkPolicy.KEEP, syncRequest)
    }
}
