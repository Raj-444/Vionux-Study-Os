package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.FinanceRepository
import com.example.data.Transaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: FinanceRepository) : ViewModel() {

  val transactions: StateFlow<List<Transaction>> = repository.allTransactions
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // Seed default data if database is empty on init
  init {
    // Zero Dummy Data Rule: Initializing with empty state
  }

  // Derived state: total expenses
  val totalExpenses: StateFlow<Double> = transactions.map { list ->
    list.filter { it.isExpense }.sumOf { it.amount }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = 0.0
  )

  // Monthly target = 4000.0
  val monthlyTarget = 4000.0

  val remainingBalance: StateFlow<Double> = totalExpenses.map { expenses ->
    monthlyTarget - expenses
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = 4000.0
  )

  // Analytics: Today's expenses
  val todayExpenses: StateFlow<Double> = transactions.map { list ->
    val now = System.currentTimeMillis()
    val startOfDay = now - (now % (24 * 60 * 60 * 1000))
    list.filter { it.isExpense && it.timestamp >= startOfDay }.sumOf { it.amount }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = 0.0
  )

  // Analytics: Last 7 days spending trend
  val weeklyTrend: StateFlow<List<Double>> = transactions.map { list ->
    val now = System.currentTimeMillis()
    val oneDayMs = 24 * 60 * 60 * 1000L
    (0..6).map { i ->
      val start = now - (now % oneDayMs) - (i * oneDayMs)
      val end = start + oneDayMs
      list.filter { it.isExpense && it.timestamp in start until end }.sumOf { it.amount }
    }.reversed()
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = List(7) { 0.0 }
  )

  // Analytics: Monthly Forecast
  val monthlyForecast: StateFlow<Double> = transactions.map { list ->
    if (list.isEmpty()) return@map 0.0
    val firstTx = list.minByOrNull { it.timestamp }?.timestamp ?: System.currentTimeMillis()
    val daysSinceFirst = ((System.currentTimeMillis() - firstTx) / (24 * 60 * 60 * 1000L)).coerceAtLeast(1)
    val totalSpent = list.filter { it.isExpense }.sumOf { it.amount }
    val dailyAvg = totalSpent / daysSinceFirst
    dailyAvg * 30 // Rough estimate for 30 days
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = 0.0
  )

  fun addTransaction(title: String, amount: Double, category: String, isExpense: Boolean = true) {
    viewModelScope.launch {
      val tx = Transaction(
        title = title.trim(),
        amount = amount,
        category = category,
        isExpense = isExpense
      )
      repository.insertTransaction(tx)
    }
  }

  fun deleteTransaction(transaction: Transaction) {
    viewModelScope.launch {
      repository.deleteTransaction(transaction)
    }
  }

  fun clearAllTransactions() {
    viewModelScope.launch {
      repository.clearAllTransactions()
    }
  }
}

class DashboardViewModelFactory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
      return DashboardViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
