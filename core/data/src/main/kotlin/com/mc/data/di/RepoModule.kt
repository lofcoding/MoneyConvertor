package com.mc.data.di

import com.mc.data.repository.CurrencyRepo
import com.mc.data.repository.OfflineFirstCurrencyRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepoModule {

    @Binds
    abstract fun bindCurrencyRepo(impl: OfflineFirstCurrencyRepo): CurrencyRepo
}