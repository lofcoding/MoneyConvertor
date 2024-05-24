package com.mc.data.worker

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkManagerSyncManager @Inject constructor(
    @ApplicationContext private val context: Context
): SyncManager {
    override val isSyncing: Flow<Boolean>
        get() = WorkManager
            .getInstance(context)
            .getWorkInfosForUniqueWorkFlow(SyncWorker.NAME)
            .map(List<WorkInfo>::anyRunning)

}
private fun List<WorkInfo>.anyRunning() = any { it.state == WorkInfo.State.RUNNING }
interface SyncManager {
    val isSyncing: Flow<Boolean>
}