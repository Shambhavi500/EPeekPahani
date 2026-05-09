package io.sc.eppCordova.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.sc.eppCordova.data.local.AppDatabase
import io.sc.eppCordova.data.local.dao.AdminUnitDao
import io.sc.eppCordova.data.local.dao.CropRecordDao
import io.sc.eppCordova.data.local.dao.LandRecordDao
import io.sc.eppCordova.data.local.dao.LossClaimDao
import io.sc.eppCordova.data.local.dao.SyncQueueDao
import io.sc.eppCordova.utils.Constants
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DB_NAME
        ).addMigrations(AppDatabase.MIGRATION_2_3, AppDatabase.MIGRATION_3_4).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideCropRecordDao(database: AppDatabase): CropRecordDao =
        database.cropRecordDao()

    @Provides
    @Singleton
    fun provideAdminUnitDao(database: AppDatabase): AdminUnitDao =
        database.adminUnitDao()

    @Provides
    @Singleton
    fun provideLandRecordDao(database: AppDatabase): LandRecordDao =
        database.landRecordDao()

    @Provides
    @Singleton
    fun provideSyncQueueDao(database: AppDatabase): SyncQueueDao =
        database.syncQueueDao()

    @Provides
    @Singleton
    fun provideLossClaimDao(database: AppDatabase): LossClaimDao =
        database.lossClaimDao()
}
