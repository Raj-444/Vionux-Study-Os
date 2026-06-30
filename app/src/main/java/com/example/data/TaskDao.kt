package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, createdAt DESC")
  fun getAllTasks(): Flow<List<Task>>

  @Query("SELECT * FROM tasks WHERE category = :category ORDER BY isCompleted ASC, createdAt DESC")
  fun getTasksByCategory(category: String): Flow<List<Task>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: Task)

  @Update
  suspend fun updateTask(task: Task)

  @Delete
  suspend fun deleteTask(task: Task)

  @Query("DELETE FROM tasks WHERE id = :id")
  suspend fun deleteTaskById(id: Int)

  @Query("DELETE FROM tasks WHERE isCompleted = 1")
  suspend fun clearCompletedTasks()
}
