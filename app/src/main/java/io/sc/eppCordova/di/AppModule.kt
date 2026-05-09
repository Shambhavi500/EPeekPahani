package io.sc.eppCordova.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// UserPreferences uses @Inject constructor — no manual @Provides needed.
// DatabaseModule and NetworkModule handle all other bindings.
@Module
@InstallIn(SingletonComponent::class)
object AppModule
