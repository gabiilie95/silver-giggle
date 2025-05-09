package com.ilieinc.dontsleep

import com.ilieinc.dontsleep.viewmodel.MainActivityViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMainActivityViewModel() = MainActivityViewModel()
}