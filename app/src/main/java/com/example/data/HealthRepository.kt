package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HealthRepository(private val healthDao: HealthDao) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getTodayLog(): Flow<HealthLog?> {
        val today = dateFormat.format(Date())
        return healthDao.getLogForDate(today)
    }

    fun getRecentLogs(): Flow<List<HealthLog>> = healthDao.getRecentLogs()

    suspend fun updateWater(currentLog: HealthLog?, glasses: Int) {
        val today = dateFormat.format(Date())
        val log = currentLog?.copy(waterGlasses = glasses) ?: HealthLog(date = today, waterGlasses = glasses)
        healthDao.insertLog(log)
    }

    suspend fun updateWorkout(currentLog: HealthLog?, completed: Boolean) {
        val today = dateFormat.format(Date())
        val log = currentLog?.copy(workoutCompleted = completed) ?: HealthLog(date = today, workoutCompleted = completed)
        healthDao.insertLog(log)
    }
}
