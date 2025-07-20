package com.example.myapplication.di

import com.example.myapplication.deepseek.data.v2.LotteryRepository
import com.example.myapplication.deepseek.data.LotteryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideLotteryRepository(dao: LotteryDao) = LotteryRepository(dao)
}