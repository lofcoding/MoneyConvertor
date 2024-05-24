package com.example.moneyconvertor

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.mc.data.worker.SyncWorker
import com.mc.data.worker.WorkerUtil
import com.mc.data.repository.CurrencyRepo
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MoneyConvertorApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: MyHiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(WorkerUtil.DefaultConstraints)
            .build()

        WorkManager.getInstance(this).beginUniqueWork(
            SyncWorker.NAME,
            ExistingWorkPolicy.REPLACE,
            request
        ).enqueue()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

class MyHiltWorkerFactory @Inject constructor(
    private val currencyRepo: CurrencyRepo
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncWorker::class.java.name -> SyncWorker(appContext, workerParameters, currencyRepo)
            else -> null
        }
    }

}