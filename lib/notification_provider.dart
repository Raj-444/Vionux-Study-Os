import 'package:flutter/material.dart';

class NotificationModel {
  final String title;
  final String body;
  final DateTime time;
  bool isRead;

  NotificationModel({required this.title, required this.body, required this.time, this.isRead = false});
}

class NotificationProvider extends ChangeNotifier {
  List<NotificationModel> _notifications = [];

  List<NotificationModel> get notifications => _notifications;
  int get unreadCount => _notifications.where((n) => !n.isRead).length;

  void addNotification(String title, String body) {
    _notifications.add(NotificationModel(title: title, body: body, time: DateTime.now()));
    notifyListeners();
  }

  void markAsRead(int index) {
    _notifications[index].isRead = true;
    notifyListeners();
  }
}
