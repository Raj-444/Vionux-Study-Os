package com.example.data

import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val transactionDao: TransactionDao) {
  val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

  suspend fun insertTransaction(transaction: Transaction) {
    transactionDao.insertTransaction(transaction)
  }

  suspend fun deleteTransaction(transaction: Transaction) {
    transactionDao.deleteTransaction(transaction)
  }

  suspend fun clearAllTransactions() {
    transactionDao.clearAllTransactions()
  }
}
