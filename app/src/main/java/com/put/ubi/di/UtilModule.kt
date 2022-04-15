package com.put.ubi.di

import android.app.Application
import android.content.res.Resources
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class UtilModule {
    @Provides
    fun provideGSON(): Gson = Gson()

    @Provides
    fun provideResources(application: Application): Resources = application.resources
}