// lib/sync_service.dart

import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_core/firebase_core.dart';
import 'database_helper.dart';
import 'transaction_model.dart';
import 'task_model.dart';
import 'roadmap_model.dart';

/// Coordinator Service responsible for syncing data between SQLite and Firebase Firestore.
/// Fully crash-proof: falls back to Local SQLite mode if Firebase is not initialized or configured.
class SyncService {
  static final SyncService _instance = SyncService._internal();
  final DatabaseHelper _dbHelper = DatabaseHelper();

  factory SyncService() => _instance;
  SyncService._internal();

  /// Safe check to detect if Firebase is initialized.
  bool get _isFirebaseInitialized {
    try {
      Firebase.app();
      return true;
    } catch (_) {
      return false;
    }
  }

  /// Resolves the user-specific collection namespace in Firestore.
  CollectionReference<Map<String, dynamic>>? _getCollection(String collectionName) {
    if (!_isFirebaseInitialized) return null;
    try {
      final user = FirebaseAuth.instance.currentUser;
      final userId = user?.uid ?? 'guest_user';
      return FirebaseFirestore.instance.collection('users').doc(userId).collection(collectionName);
    } catch (e) {
      print("Firebase Services not available: $e");
      return null;
    }
  }

  /// Checks if the device has internet connectivity.
  Future<bool> isOnline() async {
    try {
      final connectivityResult = await Connectivity().checkConnectivity();
      return connectivityResult != ConnectivityResult.none;
    } catch (_) {
      return false;
    }
  }

  // ==========================================
  // TRANSACTION SYNC ACTIONS
  // ==========================================

  /// Inserts a transaction locally first (updates UI instantly).
  /// Then triggers Firestore upload if internet connection and Firebase are active.
  Future<void> saveTransaction(TransactionModel transaction) async {
    transaction.isSynced = false;
    transaction.lastUpdated = DateTime.now();
    await _dbHelper.insertTransaction(transaction);

    if (_isFirebaseInitialized && await isOnline()) {
      try {
        final collection = _getCollection('transactions');
        if (collection != null) {
          await collection.doc(transaction.id).set(transaction.toMap());
          transaction.isSynced = true;
          await _dbHelper.insertTransaction(transaction);
        }
      } catch (e) {
        print("Firestore transaction save failed: $e");
      }
    }
  }

  // ==========================================
  // TASK SYNC ACTIONS
  // ==========================================

  /// Inserts a task locally, then pushes it to the cloud database if online.
  Future<void> saveTask(TaskModel task) async {
    task.isSynced = false;
    task.lastUpdated = DateTime.now();
    await _dbHelper.insertTask(task);

    if (_isFirebaseInitialized && await isOnline()) {
      try {
        final collection = _getCollection('tasks');
        if (collection != null) {
          await collection.doc(task.id).set(task.toMap());
          task.isSynced = true;
          await _dbHelper.insertTask(task);
        }
      } catch (e) {
        print("Firestore task save failed: $e");
      }
    }
  }

  // ==========================================
  // ROADMAP SYNC ACTIONS
  // ==========================================

  /// Inserts a roadmap locally, then synchronizes it online if connected.
  Future<void> saveRoadmap(RoadmapModel roadmap) async {
    roadmap.isSynced = false;
    roadmap.lastUpdated = DateTime.now();
    await _dbHelper.insertRoadmap(roadmap);

    if (_isFirebaseInitialized && await isOnline()) {
      try {
        final collection = _getCollection('roadmaps');
        if (collection != null) {
          await collection.doc(roadmap.id).set(roadmap.toMap());
          roadmap.isSynced = true;
          await _dbHelper.insertRoadmap(roadmap);
        }
      } catch (e) {
        print("Firestore roadmap save failed: $e");
      }
    }
  }

  // ==========================================
  // TWO-WAY SYNC ON APP LAUNCH
  // ==========================================

  /// Performs full initial bidirectional sync (typically invoked in Splash Screen).
  Future<void> performInitialSync() async {
    if (!_isFirebaseInitialized) {
      print("Firebase is not initialized. Sync skipped.");
      return;
    }
    if (!(await isOnline())) {
      print("Offline. Initial synchronization bypassed.");
      return;
    }

    try {
      print("Starting initial two-way synchronization...");
      await syncTransactionsCollection();
      await syncTasksCollection();
      await syncRoadmapsCollection();
      print("Two-way synchronization completed successfully.");
    } catch (e) {
      print("Synchronization failed: $e");
    }
  }

  /// Syncs local and remote Transactions.
  Future<void> syncTransactionsCollection() async {
    final collection = _getCollection('transactions');
    if (collection == null) return;

    try {
      final remoteSnap = await collection.get();
      final localTxList = await _dbHelper.getAllTransactions();
      final localTxMap = {for (var tx in localTxList) tx.id: tx};

      // Remote to Local
      for (var doc in remoteSnap.docs) {
        final remoteTx = TransactionModel.fromMap(doc.data());
        final localTx = localTxMap[remoteTx.id];

        if (localTx == null) {
          remoteTx.isSynced = true;
          await _dbHelper.insertTransaction(remoteTx);
        } else {
          if (remoteTx.lastUpdated.isAfter(localTx.lastUpdated)) {
            remoteTx.isSynced = true;
            await _dbHelper.insertTransaction(remoteTx);
          }
        }
      }

      // Local to Remote
      final unsynced = await _dbHelper.getUnsyncedTransactions();
      for (var tx in unsynced) {
        tx.isSynced = true;
        tx.lastUpdated = DateTime.now();
        await collection.doc(tx.id).set(tx.toMap());
        await _dbHelper.insertTransaction(tx);
      }
    } catch (e) {
      print("Error syncing transactions: $e");
    }
  }

  /// Syncs local and remote Tasks.
  Future<void> syncTasksCollection() async {
    final collection = _getCollection('tasks');
    if (collection == null) return;

    try {
      final remoteSnap = await collection.get();
      final localTasks = await _dbHelper.getAllTasks();
      final localTaskMap = {for (var t in localTasks) t.id: t};

      for (var doc in remoteSnap.docs) {
        final remoteTask = TaskModel.fromMap(doc.data());
        final localTask = localTaskMap[remoteTask.id];

        if (localTask == null) {
          remoteTask.isSynced = true;
          await _dbHelper.insertTask(remoteTask);
        } else {
          if (remoteTask.lastUpdated.isAfter(localTask.lastUpdated)) {
            remoteTask.isSynced = true;
            await _dbHelper.insertTask(remoteTask);
          }
        }
      }

      final unsynced = await _dbHelper.getUnsyncedTasks();
      for (var task in unsynced) {
        task.isSynced = true;
        task.lastUpdated = DateTime.now();
        await collection.doc(task.id).set(task.toMap());
        await _dbHelper.insertTask(task);
      }
    } catch (e) {
      print("Error syncing tasks: $e");
    }
  }

  /// Syncs local and remote Roadmaps.
  Future<void> syncRoadmapsCollection() async {
    final collection = _getCollection('roadmaps');
    if (collection == null) return;

    try {
      final remoteSnap = await collection.get();
      final localRoadmaps = await _dbHelper.getAllRoadmaps();
      final localRoadmapMap = {for (var r in localRoadmaps) r.id: r};

      for (var doc in remoteSnap.docs) {
        final remoteRoadmap = RoadmapModel.fromMap(doc.data());
        final localRoadmap = localRoadmapMap[remoteRoadmap.id];

        if (localRoadmap == null) {
          remoteRoadmap.isSynced = true;
          await _dbHelper.insertRoadmap(remoteRoadmap);
        } else {
          if (remoteRoadmap.lastUpdated.isAfter(localRoadmap.lastUpdated)) {
            remoteRoadmap.isSynced = true;
            await _dbHelper.insertRoadmap(remoteRoadmap);
          }
        }
      }

      final unsynced = await _dbHelper.getUnsyncedRoadmaps();
      for (var rm in unsynced) {
        rm.isSynced = true;
        rm.lastUpdated = DateTime.now();
        await collection.doc(rm.id).set(rm.toMap());
        await _dbHelper.insertRoadmap(rm);
      }
    } catch (e) {
      print("Error syncing roadmaps: $e");
    }
  }
}
