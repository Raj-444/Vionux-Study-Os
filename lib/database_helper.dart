// lib/database_helper.dart

import 'dart:async';
import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';
import 'transaction_model.dart';
import 'task_model.dart';
import 'roadmap_model.dart';

/// A singleton class to manage SQLite database operations.
/// Functions as the local Single Source of Truth for the UI.
class DatabaseHelper {
  static final DatabaseHelper _instance = DatabaseHelper._internal();
  factory DatabaseHelper() => _instance;
  DatabaseHelper._internal();

  static Database? _database;

  /// Retrieves the initialized database instance.
  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDatabase();
    return _database!;
  }

  /// Initializes the SQLite database and increments version to 2.
  Future<Database> _initDatabase() async {
    try {
      final dbPath = await getDatabasesPath();
      final path = join(dbPath, 'finance_app.db');

      return await openDatabase(
        path,
        version: 2,
        onCreate: _onCreate,
        onUpgrade: _onUpgrade,
      );
    } catch (e) {
      print('Database Initialization Error: $e');
      rethrow;
    }
  }

  /// Creates tables for transactions, tasks, and roadmaps with sync attributes.
  FutureOr<void> _onCreate(Database db, int version) async {
    // 1. Transactions table (UUID id, metadata for sync)
    await db.execute('''
      CREATE TABLE transactions (
        id TEXT PRIMARY KEY,
        title TEXT NOT NULL,
        amount REAL NOT NULL,
        date TEXT NOT NULL,
        category TEXT NOT NULL,
        type TEXT NOT NULL,
        isSynced INTEGER NOT NULL,
        lastUpdated TEXT NOT NULL
      )
    ''');

    // 2. Tasks table
    await db.execute('''
      CREATE TABLE tasks (
        id TEXT PRIMARY KEY,
        title TEXT NOT NULL,
        subject TEXT NOT NULL,
        priority TEXT NOT NULL,
        dueTime TEXT NOT NULL,
        isCompleted INTEGER NOT NULL,
        isSynced INTEGER NOT NULL,
        lastUpdated TEXT NOT NULL
      )
    ''');

    // 3. Roadmaps table
    await db.execute('''
      CREATE TABLE roadmaps (
        id TEXT PRIMARY KEY,
        title TEXT NOT NULL,
        description TEXT NOT NULL,
        subTasksJson TEXT NOT NULL,
        progress REAL NOT NULL,
        isSynced INTEGER NOT NULL,
        lastUpdated TEXT NOT NULL
      )
    ''');
  }

  /// Handles clean upgrades from SQLite schema version 1.
  FutureOr<void> _onUpgrade(Database db, int oldVersion, int newVersion) async {
    if (oldVersion < 2) {
      await db.execute('DROP TABLE IF EXISTS transactions');
      await db.execute('DROP TABLE IF EXISTS tasks');
      await db.execute('DROP TABLE IF EXISTS roadmaps');
      await _onCreate(db, newVersion);
    }
  }

  // ==========================================
  // TRANSACTION CRUD METHODS
  // ==========================================

  /// Inserts or replaces a transaction.
  Future<int> insertTransaction(TransactionModel transaction) async {
    try {
      final db = await database;
      return await db.insert(
        'transactions',
        transaction.toMap(),
        conflictAlgorithm: ConflictAlgorithm.replace,
      );
    } catch (e) {
      print('SQLite Insert Transaction Error: $e');
      rethrow;
    }
  }

  /// Retrieves all transactions sorted by date descending.
  Future<List<TransactionModel>> getAllTransactions() async {
    try {
      final db = await database;
      final List<Map<String, dynamic>> maps = await db.query(
        'transactions',
        orderBy: 'date DESC',
      );

      return List.generate(maps.length, (i) {
        return TransactionModel.fromMap(maps[i]);
      });
    } catch (e) {
      print('SQLite GetAllTransactions Error: $e');
      return [];
    }
  }

  /// Deletes a specific transaction by its UUID string.
  Future<int> deleteTransaction(String id) async {
    try {
      final db = await database;
      return await db.delete(
        'transactions',
        where: 'id = ?',
        whereArgs: [id],
      );
    } catch (e) {
      print('SQLite DeleteTransaction Error: $e');
      rethrow;
    }
  }

  /// Retrieves all transactions that have not yet been synchronized.
  Future<List<TransactionModel>> getUnsyncedTransactions() async {
    try {
      final db = await database;
      final List<Map<String, dynamic>> maps = await db.query(
        'transactions',
        where: 'isSynced = ?',
        whereArgs: [0],
      );
      return List.generate(maps.length, (i) => TransactionModel.fromMap(maps[i]));
    } catch (e) {
      print('SQLite getUnsyncedTransactions Error: $e');
      return [];
    }
  }

  /// Get transactions within a custom date range.
  Future<List<TransactionModel>> getTransactionsByDateRange(
      DateTime start, DateTime end) async {
    try {
      final db = await database;
      final startStr = start.toIso8601String();
      final endStr = end.toIso8601String();

      final List<Map<String, dynamic>> maps = await db.query(
        'transactions',
        where: 'date >= ? AND date <= ?',
        whereArgs: [startStr, endStr],
        orderBy: 'date ASC',
      );

      return List.generate(maps.length, (i) {
        return TransactionModel.fromMap(maps[i]);
      });
    } catch (e) {
      print('SQLite GetTransactionsByDateRange Error: $e');
      return [];
    }
  }

  /// Calculate the total spent this current calendar month.
  Future<double> getTotalSpentThisMonth() async {
    try {
      final db = await database;
      final now = DateTime.now();

      final startOfMonth = DateTime(now.year, now.month, 1).toIso8601String();
      final endOfMonth = (now.month < 12 
          ? DateTime(now.year, now.month + 1, 1).subtract(const Duration(seconds: 1))
          : DateTime(now.year + 1, 1, 1).subtract(const Duration(seconds: 1)))
          .toIso8601String();

      final result = await db.rawQuery('''
        SELECT SUM(amount) as total 
        FROM transactions 
        WHERE date >= ? AND date <= ? AND type = 'expense'
      ''', [startOfMonth, endOfMonth]);

      if (result.isNotEmpty && result.first['total'] != null) {
        return (result.first['total'] as num).toDouble();
      }
      return 0.0;
    } catch (e) {
      print('SQLite GetTotalSpentThisMonth Error: $e');
      return 0.0;
    }
  }

  /// Get category-wise spending totals within a date range.
  Future<Map<String, double>> getCategoryWiseSpend(
      DateTime start, DateTime end) async {
    try {
      final db = await database;
      final startStr = start.toIso8601String();
      final endStr = end.toIso8601String();

      final List<Map<String, dynamic>> results = await db.rawQuery('''
        SELECT category, SUM(amount) as total 
        FROM transactions 
        WHERE date >= ? AND date <= ? AND type = 'expense'
        GROUP BY category
      ''', [startStr, endStr]);

      final Map<String, double> categorySpendMap = {};
      for (var row in results) {
        if (row['category'] != null && row['total'] != null) {
          categorySpendMap[row['category'] as String] = (row['total'] as num).toDouble();
        }
      }
      return categorySpendMap;
    } catch (e) {
      print('SQLite GetCategoryWiseSpend Error: $e');
      return {};
    }
  }

  // ==========================================
  // TASK CRUD METHODS
  // ==========================================

  /// Inserts or replaces a task.
  Future<int> insertTask(TaskModel task) async {
    try {
      final db = await database;
      return await db.insert(
        'tasks',
        task.toMap(),
        conflictAlgorithm: ConflictAlgorithm.replace,
      );
    } catch (e) {
      print('SQLite Insert Task Error: $e');
      rethrow;
    }
  }

  /// Retrieves all tasks.
  Future<List<TaskModel>> getAllTasks() async {
    try {
      final db = await database;
      final List<Map<String, dynamic>> maps = await db.query('tasks');
      return List.generate(maps.length, (i) => TaskModel.fromMap(maps[i]));
    } catch (e) {
      print('SQLite GetAllTasks Error: $e');
      return [];
    }
  }

  /// Deletes a task by UUID.
  Future<int> deleteTask(String id) async {
    try {
      final db = await database;
      return await db.delete(
        'tasks',
        where: 'id = ?',
        whereArgs: [id],
      );
    } catch (e) {
      print('SQLite DeleteTask Error: $e');
      rethrow;
    }
  }

  /// Retrieves all tasks that have not yet been synchronized.
  Future<List<TaskModel>> getUnsyncedTasks() async {
    try {
      final db = await database;
      final List<Map<String, dynamic>> maps = await db.query(
        'tasks',
        where: 'isSynced = ?',
        whereArgs: [0],
      );
      return List.generate(maps.length, (i) => TaskModel.fromMap(maps[i]));
    } catch (e) {
      print('SQLite getUnsyncedTasks Error: $e');
      return [];
    }
  }

  // ==========================================
  // ROADMAP CRUD METHODS
  // ==========================================

  /// Inserts or replaces a roadmap.
  Future<int> insertRoadmap(RoadmapModel roadmap) async {
    try {
      final db = await database;
      return await db.insert(
        'roadmaps',
        roadmap.toLocalMap(),
        conflictAlgorithm: ConflictAlgorithm.replace,
      );
    } catch (e) {
      print('SQLite Insert Roadmap Error: $e');
      rethrow;
    }
  }

  /// Retrieves all roadmaps.
  Future<List<RoadmapModel>> getAllRoadmaps() async {
    try {
      final db = await database;
      final List<Map<String, dynamic>> maps = await db.query('roadmaps');
      return List.generate(maps.length, (i) => RoadmapModel.fromMap(maps[i]));
    } catch (e) {
      print('SQLite GetAllRoadmaps Error: $e');
      return [];
    }
  }

  /// Deletes a roadmap by UUID.
  Future<int> deleteRoadmap(String id) async {
    try {
      final db = await database;
      return await db.delete(
        'roadmaps',
        where: 'id = ?',
        whereArgs: [id],
      );
    } catch (e) {
      print('SQLite DeleteRoadmap Error: $e');
      rethrow;
    }
  }

  /// Retrieves all roadmaps that have not yet been synchronized.
  Future<List<RoadmapModel>> getUnsyncedRoadmaps() async {
    try {
      final db = await database;
      final List<Map<String, dynamic>> maps = await db.query(
        'roadmaps',
        where: 'isSynced = ?',
        whereArgs: [0],
      );
      return List.generate(maps.length, (i) => RoadmapModel.fromMap(maps[i]));
    } catch (e) {
      print('SQLite getUnsyncedRoadmaps Error: $e');
      return [];
    }
  }
}
