// lib/main_app_screen.dart

import 'package:flutter/material.dart';
import 'productivity_screen.dart';
import 'permission_manager.dart';

/// Container/dashboard screen that hosts the productivity widgets and requests runtime permissions on startup.
class MainAppScreen extends StatefulWidget {
  const MainAppScreen({Key? key}) : super(key: key);

  @override
  State<MainAppScreen> createState() => _MainAppScreenState();
}

class _MainAppScreenState extends State<MainAppScreen> {
  @override
  void initState() {
    super.initState();
    // Trigger the global permission handler immediately after screen mounts
    WidgetsBinding.instance.addPostFrameCallback((_) {
      PermissionManager.checkAndRequestPermissions(context);
    });
  }

  @override
  Widget build(BuildContext context) {
    return const ProductivityScreen();
  }
}
