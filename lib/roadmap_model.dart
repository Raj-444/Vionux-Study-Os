// lib/roadmap_model.dart

import 'dart:convert';
import 'package:uuid/uuid.dart';

/// Data model representing a sub-task milestone.
class SubTask {
  final String title;
  bool isCompleted;

  SubTask({
    required this.title,
    this.isCompleted = false,
  });

  Map<String, dynamic> toMap() {
    return {
      'title': title,
      'isCompleted': isCompleted,
    };
  }

  factory SubTask.fromMap(Map<String, dynamic> map) {
    return SubTask(
      title: map['title'] as String,
      isCompleted: map['isCompleted'] as bool? ?? false,
    );
  }
}

/// Data model representing a Goal Roadmap.
/// Formatted to support offline storage (SQLite) and cloud synchronization (Firestore).
class RoadmapModel {
  final String id;
  final String title;
  final String description;
  final List<SubTask> subTasks;
  double progress;
  bool isSynced;
  DateTime lastUpdated;

  RoadmapModel({
    String? id,
    required this.title,
    required this.description,
    required this.subTasks,
    this.progress = 0.0,
    this.isSynced = false,
    DateTime? lastUpdated,
  })  : id = id ?? const Uuid().v4(),
        lastUpdated = lastUpdated ?? DateTime.now();

  /// Converts to a Map for general / Firestore storage.
  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'title': title,
      'description': description,
      'subTasks': subTasks.map((s) => s.toMap()).toList(),
      'progress': progress,
      'isSynced': isSynced ? 1 : 0,
      'lastUpdated': lastUpdated.toIso8601String(),
    };
  }

  /// Converts to a Map specifically formatted for local SQLite storage (serializes subtasks as JSON).
  Map<String, dynamic> toLocalMap() {
    return {
      'id': id,
      'title': title,
      'description': description,
      'subTasksJson': jsonEncode(subTasks.map((s) => s.toMap()).toList()),
      'progress': progress,
      'isSynced': isSynced ? 1 : 0,
      'lastUpdated': lastUpdated.toIso8601String(),
    };
  }

  /// Reconstructs a [RoadmapModel] from a database Map.
  factory RoadmapModel.fromMap(Map<String, dynamic> map) {
    var subTasksData = map['subTasks'];
    List<SubTask> parsedSubTasks = [];
    
    if (subTasksData is String) {
      final List decoded = jsonDecode(subTasksData);
      parsedSubTasks = decoded.map((s) => SubTask.fromMap(s)).toList();
    } else if (subTasksData is List) {
      parsedSubTasks = subTasksData.map((s) => SubTask.fromMap(Map<String, dynamic>.from(s))).toList();
    } else if (map['subTasksJson'] is String) {
      final List decoded = jsonDecode(map['subTasksJson'] as String);
      parsedSubTasks = decoded.map((s) => SubTask.fromMap(s)).toList();
    }

    return RoadmapModel(
      id: map['id'] as String,
      title: map['title'] as String,
      description: map['description'] as String,
      subTasks: parsedSubTasks,
      progress: (map['progress'] as num?)?.toDouble() ?? 0.0,
      isSynced: (map['isSynced'] == 1 || map['isSynced'] == true),
      lastUpdated: map['lastUpdated'] != null
          ? DateTime.parse(map['lastUpdated'] as String)
          : DateTime.now(),
    );
  }
}
