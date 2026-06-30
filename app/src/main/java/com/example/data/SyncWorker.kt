package com.example.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first

/**
 * Worker for background data synchronization.
 *
 * This Worker syncs local Room/SQLite database records
 * with a remote Firebase Firestore instance.
 */
class SyncWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            Log.d("SyncWorker", "Starting background data synchronization...")
            
            // 1. Get database instance
            val database = AppDatabase.getDatabase(applicationContext)
            val taskDao = database.taskDao()
            val transactionDao = database.transactionDao()
            
            val taskRepository = TaskRepository(taskDao)
            val financeRepository = FinanceRepository(transactionDao)
            
            // 2. Instantiate SyncService
            val syncService = SyncService(taskRepository, financeRepository)
            
            // 3. Fetch all tasks from local database (using flow.first() to get the current list)
            val localTasks = taskRepository.allTasks.first()
            
            // 4. Sync tasks to Firestore
            if (localTasks.isNotEmpty()) {
                syncService.syncLocalTasksToRemote(localTasks)
                Log.d("SyncWorker", "Successfully scheduled remote synchronization for ${localTasks.size} tasks.")
            } else {
                Log.d("SyncWorker", "No local tasks to sync.")
            }
            
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Background synchronization failed: ${e.message}", e)
            Result.retry()
        }
    }
}
