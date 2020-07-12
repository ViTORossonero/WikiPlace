package com.qulix.wikiplace.di

import android.app.Application
import android.content.Context
import com.qulix.wikiplace.WikiPlacesApp
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    NetworkModule::class
])
interface AppComponent {

    fun plus(module: ActivityModule): ActivityComponent

    companion object {
        fun create(application: Application): AppComponent =
                DaggerAppComponent.builder()
                        .appModule(AppModule(application))
                        .networkModule(NetworkModule())
                        .build()

        fun from(context: Context): AppComponent = (context.applicationContext as WikiPlacesApp).appComponent
    }
}