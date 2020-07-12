package com.qulix.wikiplace.di

import android.app.Application
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.qulix.wikiplace.DispatchersProvider
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
class AppModule(
        private val application: Application
) {
    @Provides
    @Singleton
    internal fun provideApplication(): Application = application

    @Provides
    @Singleton
    internal fun provideDispatchersProvider(): DispatchersProvider = DispatchersProvider(
        Dispatchers.Main,
        Dispatchers.Default,
        Dispatchers.IO
    )

    @Provides
    @Singleton
    internal fun provideFusedLocationProviderClient(): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)
}