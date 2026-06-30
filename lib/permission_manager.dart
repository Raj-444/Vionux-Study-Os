// lib/permission_manager.dart

import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';

/// Helper class to request and handle runtime app permissions.
class PermissionManager {
  /// Check and request Notifications and Exact Alarm permissions.
  /// Force-prompts the user and opens system settings if permanently denied.
  static Future<void> checkAndRequestPermissions(BuildContext context) async {
    // 1. Request Notification permission
    PermissionStatus notificationStatus = await Permission.notification.status;
    if (!notificationStatus.isGranted) {
      notificationStatus = await Permission.notification.request();
    }

    // 2. Request Schedule Exact Alarm permission
    PermissionStatus exactAlarmStatus = await Permission.scheduleExactAlarm.status;
    if (!exactAlarmStatus.isGranted) {
      exactAlarmStatus = await Permission.scheduleExactAlarm.request();
    }

    // Check statuses and prompt user accordingly
    if (notificationStatus.isPermanentlyDenied || exactAlarmStatus.isPermanentlyDenied) {
      _showSettingsDialog(context);
    } else if (notificationStatus.isDenied || exactAlarmStatus.isDenied) {
      _showPremiumSnackBar(
        context,
        "Notifications and Alarms permissions are required for daily focus alerts.",
      );
    }
  }

  /// Displays a premium custom snackbar prompting the user to grant permissions.
  static void _showPremiumSnackBar(BuildContext context, String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Row(
          children: [
            const Icon(Icons.info_outline_rounded, color: Colors.white, size: 20),
            const SizedBox(width: 12),
            Expanded(
              child: Text(
                message,
                style: const TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontSize: 13,
                ),
              ),
            ),
          ],
        ),
        backgroundColor: const Color(0xFFE94560),
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(24),
        ),
        margin: const EdgeInsets.all(16),
        duration: const Duration(seconds: 4),
        action: SnackBarAction(
          label: "Grant",
          textColor: Colors.white,
          onPressed: () {
            openAppSettings();
          },
        ),
      ),
    );
  }

  /// Displays a beautiful, premium dialog asking the user to open settings.
  static void _showSettingsDialog(BuildContext context) {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return AlertDialog(
          backgroundColor: const Color(0xFFF8F9FA),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(24),
          ),
          title: const Row(
            children: [
              Icon(Icons.security_rounded, color: Color(0xFFE94560)),
              SizedBox(width: 12),
              Text(
                "Permissions Required",
                style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
              ),
            ],
          ),
          content: const Text(
            "To send loud daily workspace notifications and sound Pomodoro alarms accurately, please enable Notification and Alarm permissions in system settings.",
            style: TextStyle(color: Color(0xFF8E8E93), height: 1.4, fontSize: 14),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text("Dismiss", style: TextStyle(color: Colors.grey, fontWeight: FontWeight.bold)),
            ),
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFFE94560),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(16),
                ),
              ),
              onPressed: () {
                Navigator.pop(context);
                openAppSettings();
              },
              child: const Text("Open Settings", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
            ),
          ],
        );
      },
    );
  }
}
