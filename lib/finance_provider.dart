// lib/finance_provider.dart

import 'package:flutter/material.dart';
import 'transaction_model.dart';
import 'database_helper.dart';
import 'sync_service.dart';

/// Provider handling state management and SQLite CRUD transactions for personal finance.
/// Integrates with SyncService to support offline-first Cloud Synchronization.
class FinanceProvider extends ChangeNotifier {
  final DatabaseHelper _dbHelper = DatabaseHelper();
  
  // Backing list for transactions
  List<TransactionModel> _transactions = [];

  /// Retrieves the current list of transactions.
  List<TransactionModel> get transactions => _transactions;

  /// Loads all transactions from the SQLite database.
  /// Allocates a new list reference so that listeners detect changes.
  Future<void> loadTransactions() async {
    try {
      final list = await _dbHelper.getAllTransactions();
      _transactions = List<TransactionModel>.from(list);
      notifyListeners();
    } catch (e) {
      print("Error loading transactions in provider: $e");
    }
  }

  /// Adds a transaction using SyncService (writes locally first, then syncs online).
  Future<void> addTransaction(TransactionModel transaction) async {
    try {
      await SyncService().saveTransaction(transaction);
      await loadTransactions();
    } catch (e) {
      print("Error adding transaction in provider: $e");
    }
  }

  /// Deletes a transaction from the SQLite database by String UUID, then refreshes the state list.
  Future<void> deleteTransaction(String id) async {
    try {
      await _dbHelper.deleteTransaction(id);
      await loadTransactions();
    } catch (e) {
      print("Error deleting transaction in provider: $e");
    }
  }
}
