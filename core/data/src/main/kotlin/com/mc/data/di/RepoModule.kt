package com.mc.data.di

import com.mc.data.repository.CurrencyRepo
import com.mc.data.repository.OfflineFirstCurrencyRepo
import com.mc.data.worker.SyncManager
import com.mc.data.worker.WorkManagerSyncManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepoModule {

    @Binds
    abstract fun bindCurrencyRepo(impl: OfflineFirstCurrencyRepo): CurrencyRepo

    @Binds
    abstract fun bindSyncManager(impl: WorkManagerSyncManager): SyncManager
}