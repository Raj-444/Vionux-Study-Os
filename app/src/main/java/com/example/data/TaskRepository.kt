package com.example.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
  val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

  fun getTasksByCategory(category: String): Flow<List<Task>> {
    return taskDao.getTasksByCategory(category)
  }

  suspend fun insertTask(task: Task) {
    taskDao.insertTask(task)
  }

  suspend fun updateTask(task: Task) {
    taskDao.updateTask(task)
  }

  suspend fun deleteTask(task: Task) {
    taskDao.deleteTask(task)
  }

  suspend fun deleteTaskById(id: Int) {
    taskDao.deleteTaskById(id)
  }

  suspend fun clearCompletedTasks() {
    taskDao.clearCompletedTasks()
  }
}
