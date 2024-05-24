package com.mc.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.WorkManager
import androidx.work.WorkRequest

object WorkerUtil {

    val DefaultConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun enqueueWork(
        context: Context,
        workRequest: WorkRequest
    ) {
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}