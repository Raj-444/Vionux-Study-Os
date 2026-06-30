// lib/task_model.dart

import 'package:uuid/uuid.dart';

/// Data model representing a Task in the application.
/// Formatted to support offline storage (SQLite) and cloud synchronization (Firestore).
class TaskModel {
  final String id;
  final String title;
  final String subject;
  final String priority; // 'high', 'medium', 'low'
  final String dueTime;
  bool isCompleted;
  bool isSynced;
  DateTime lastUpdated;

  TaskModel({
    String? id,
    required this.title,
    required this.subject,
    required this.priority,
    required this.dueTime,
    this.isCompleted = false,
    this.isSynced = false,
    DateTime? lastUpdated,
  })  : id = id ?? const Uuid().v4(),
        lastUpdated = lastUpdated ?? DateTime.now();

  /// Converts a [TaskModel] instance to a Map for storage.
  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'title': title,
      'subject': subject,
      'priority': priority,
      'dueTime': dueTime,
      'isCompleted': isCompleted ? 1 : 0,
      'isSynced': isSynced ? 1 : 0,
      'lastUpdated': lastUpdated.toIso8601String(),
    };
  }

  /// Factory constructor to reconstruct a [TaskModel] from a Map.
  factory TaskModel.fromMap(Map<String, dynamic> map) {
    return TaskModel(
      id: map['id'] as String,
      title: map['title'] as String,
      subject: map['subject'] as String,
      priority: map['priority'] as String,
      dueTime: map['dueTime'] as String,
      isCompleted: (map['isCompleted'] == 1 || map['isCompleted'] == true),
      isSynced: (map['isSynced'] == 1 || map['isSynced'] == true),
      lastUpdated: map['lastUpdated'] != null
          ? DateTime.parse(map['lastUpdated'] as String)
          : DateTime.now(),
    );
  }
}
