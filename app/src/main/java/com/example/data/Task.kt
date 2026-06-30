package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val title: String,
  val notes: String = "",
  val category: String = "Work", // "Work", "Personal", "Study", "Creative"
  val priority: String = "Medium", // "High", "Medium", "Low"
  val isCompleted: Boolean = false,
  val createdAt: Long = System.currentTimeMillis(),
  val completedAt: Long? = null
)
