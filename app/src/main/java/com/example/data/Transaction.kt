package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val title: String,
  val amount: Double,
  val category: String, // "Food", "Other", "Transportation", "Study Materials"
  val timestamp: Long = System.currentTimeMillis(),
  val isExpense: Boolean = true
)
