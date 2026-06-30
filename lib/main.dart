// lib/main.dart

import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_crashlytics/firebase_crashlytics.dart';
import 'error_handler.dart';
import 'productivity_screen.dart';
import 'health_provider.dart';
import 'notification_provider.dart';
import 'finance_provider.dart';
import 'roadmap_provider.dart';

// Assuming 'provider' package is available
import 'package:provider/provider.dart';
import 'splash_screen.dart';
import 'package:workmanager/workmanager.dart';
import 'sync_worker.dart';

void main() async {
  // 1. Ensure Flutter binding is initialized
  WidgetsFlutterBinding.ensureInitialized();

  // 2. Initialize WorkManager for Offline-First sync
  try {
    await Workmanager().initialize(
      callbackDispatcher,
      isInDebugMode: kDebugMode,
    );
    await Workmanager().registerPeriodicTask(
      "1",
      syncTaskName,
      frequency: const Duration(minutes: 15),
      constraints: Constraints(
        networkType: NetworkType.connected,
      ),
    );
  } catch (e) {
    if (kDebugMode) {
      print("Workmanager registration failed/skipped: $e");
    }
  }

  // 2. Initialize Firebase and configure Firebase Crashlytics
  try {
    // If you have your google-services.json configured inside your app/ module,
    // initializing Firebase without arguments will automatically discover the credentials on Android.
    await Firebase.initializeApp();

    // Pass all uncaught synchronous/asynchronous errors from the framework to Crashlytics
    FlutterError.onError = (FlutterErrorDetails details) {
      FirebaseCrashlytics.instance.recordFlutterError(details);
      // Pass it forward to the system standard log
      FlutterError.presentError(details);
    };

    // Catch errors that occur outside the Flutter framework (asynchronous platform callbacks, isolates, etc.)
    PlatformDispatcher.instance.onError = (Object error, StackTrace stack) {
      FirebaseCrashlytics.instance.recordError(error, stack, fatal: true);
      return true; // Error was handled
    };
  } catch (e) {
    // Fail gracefully if Firebase is not yet provisioned in your workspace
    if (kDebugMode) {
      print("Firebase initialization skipped or failed: $e");
      print("Please configure Firebase Console & download google-services.json to finalize.");
    }
  }

  // 3. Run the app wrapped inside our premium ErrorHandler boundary
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => HealthProvider()),
        ChangeNotifierProvider(create: (_) => NotificationProvider()),
        ChangeNotifierProvider(create: (_) => FinanceProvider()..loadTransactions()),
        ChangeNotifierProvider(create: (_) => RoadmapProvider()..loadRoadmaps()),
      ],
      child: const ErrorHandler(
        child: FocusSpaceApp(),
      ),
    ),
  );
}

class FocusSpaceApp extends StatelessWidget {
  const FocusSpaceApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Focus Space',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        fontFamily: 'SF Pro Display',
        scaffoldBackgroundColor: const Color(0xFFF8F9FA), // Off-white premium surface
        colorScheme: const ColorScheme.light(
          primary: Color(0xFFE94560), // Beautiful Accent Red
          secondary: Color(0xFF0C0C0E), // Slate dark color
          surface: Colors.white,
          background: Color(0xFFF8F9FA),
        ),
        useMaterial3: true,
      ),
      // Set the root route to SplashScreen to handle biometric locks
      home: const SplashScreen(),
    );
  }
}
