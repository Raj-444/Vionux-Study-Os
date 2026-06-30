// lib/transaction_model.dart

import 'package:uuid/uuid.dart';

/// Data model representing a transaction in the premium finance application.
/// Integrated with UUID identifiers, sync tracking, and last updated timestamps for Offline-First sync.
class TransactionModel {
  final String id;
  final String title;
  final double amount;
  final DateTime date;
  final String category; // e.g., Food, Other, Transportation, Study Materials
  final String type;     // e.g., 'expense', 'income'
  bool isSynced;
  DateTime lastUpdated;

  TransactionModel({
    String? id,
    required this.title,
    required this.amount,
    required this.date,
    required this.category,
    required this.type,
    this.isSynced = false,
    DateTime? lastUpdated,
  })  : id = id ?? const Uuid().v4(),
        lastUpdated = lastUpdated ?? DateTime.now();

  /// Converts a [TransactionModel] instance to a Map for SQLite and Firestore storage.
  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'title': title,
      'amount': amount,
      'date': date.toIso8601String(),
      'category': category,
      'type': type,
      'isSynced': isSynced ? 1 : 0, // SQLite doesn't support booleans directly, store as integer
      'lastUpdated': lastUpdated.toIso8601String(),
    };
  }

  /// Factory constructor to reconstruct a [TransactionModel] from a database or Firestore Map.
  factory TransactionModel.fromMap(Map<String, dynamic> map) {
    return TransactionModel(
      id: map['id'] as String,
      title: map['title'] as String,
      amount: (map['amount'] as num).toDouble(),
      date: DateTime.parse(map['date'] as String),
      category: map['category'] as String,
      type: map['type'] as String,
      isSynced: (map['isSynced'] == 1 || map['isSynced'] == true),
      lastUpdated: map['lastUpdated'] != null
          ? DateTime.parse(map['lastUpdated'] as String)
          : DateTime.now(),
    );
  }
}
