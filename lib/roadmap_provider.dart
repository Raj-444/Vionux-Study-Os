// lib/roadmap_provider.dart

import 'package:flutter/material.dart';
import 'roadmap_model.dart';
import 'database_helper.dart';
import 'sync_service.dart';

/// Provider handling state, task manipulation, and progress recalculation for Goal Roadmaps.
/// Persistent via SQLite database local caching and Firebase Firestore sync.
class RoadmapProvider extends ChangeNotifier {
  final DatabaseHelper _dbHelper = DatabaseHelper();
  
  // Local list backing of roadmaps
  List<RoadmapModel> _roadmaps = [];

  /// Retrieves the current list of roadmaps.
  List<RoadmapModel> get roadmaps => _roadmaps;

  /// Loads all roadmaps from the SQLite database.
  Future<void> loadRoadmaps() async {
    try {
      final list = await _dbHelper.getAllRoadmaps();
      _roadmaps = List<RoadmapModel>.from(list);
      notifyListeners();
    } catch (e) {
      print("Error loading roadmaps in provider: $e");
    }
  }

  /// Recalculates the progress percentage of a roadmap and saves the changes.
  /// Formula: (completedSubtasks / totalSubtasks) * 100
  Future<void> updateProgress(RoadmapModel roadmap) async {
    if (roadmap.subTasks.isEmpty) {
      roadmap.progress = 0.0;
    } else {
      final int completedCount = roadmap.subTasks.where((s) => s.isCompleted).length;
      roadmap.progress = (completedCount / roadmap.subTasks.length) * 100.0;
    }
    // Save locally first, then upload to Firestore if online
    await SyncService().saveRoadmap(roadmap);
    await loadRoadmaps();
  }

  /// Appends a new subtask to a target roadmap.
  Future<void> addSubTask(RoadmapModel roadmap, String title) async {
    if (title.trim().isEmpty) return;
    roadmap.subTasks.add(SubTask(title: title.trim()));
    await updateProgress(roadmap);
  }

  /// Toggles the completion status of a subtask and updates progress.
  Future<void> toggleSubTask(RoadmapModel roadmap, SubTask subtask) async {
    subtask.isCompleted = !subtask.isCompleted;
    await updateProgress(roadmap);
  }

  /// Creates and registers a new roadmap.
  Future<void> addRoadmap(String title, String description) async {
    if (title.trim().isEmpty) return;
    final rm = RoadmapModel(
      title: title.trim(),
      description: description.trim(),
      subTasks: [],
      progress: 0.0,
    );
    await SyncService().saveRoadmap(rm);
    await loadRoadmaps();
  }
}
