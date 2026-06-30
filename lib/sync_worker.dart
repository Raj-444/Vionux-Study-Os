// lib/sync_worker.dart

import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:workmanager/workmanager.dart';
import 'sync_service.dart';
import 'database_helper.dart';

/// Periodic Sync Task unique identifier
const String syncTaskName = "com.focusplanner.offlineSyncTask";

/// Callback dispatcher for WorkManager execution in a background isolate.
@pragma('vm:entry-point')
void callbackDispatcher() {
  Workmanager().executeTask((task, inputData) async {
    try {
      // 1. Ensure Flutter bindings and Firebase are initialized in the background isolate
      WidgetsFlutterBinding.ensureInitialized();
      await Firebase.initializeApp();

      final syncService = SyncService();
      
      // 2. Perform synchronization if the device is currently online
      if (await syncService.isOnline()) {
        final dbHelper = DatabaseHelper();

        // Query and push unsynced transactions
        final unsyncedTx = await dbHelper.getUnsyncedTransactions();
        for (var tx in unsyncedTx) {
          tx.isSynced = true;
          tx.lastUpdated = DateTime.now();
          await syncService.saveTransaction(tx);
        }

        // Query and push unsynced tasks
        final unsyncedTasks = await dbHelper.getUnsyncedTasks();
        for (var t in unsyncedTasks) {
          t.isSynced = true;
          t.lastUpdated = DateTime.now();
          await syncService.saveTask(t);
        }

        // Query and push unsynced roadmaps
        final unsyncedRoadmaps = await dbHelper.getUnsyncedRoadmaps();
        for (var rm in unsyncedRoadmaps) {
          rm.isSynced = true;
          rm.lastUpdated = DateTime.now();
          await syncService.saveRoadmap(rm);
        }
      }
      return Future.value(true);
    } catch (e) {
      print("Sync Worker background task execution failed: $e");
      return Future.value(false);
    }
  });
}
