// lib/splash_screen.dart

import 'package:flutter/material.dart';
import 'security_service.dart';
import 'main_app_screen.dart';
import 'sync_service.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({Key? key}) : super(key: key);

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  final SecurityService _securityService = SecurityService();
  bool _isLocked = false;
  bool _isChecking = true;

  @override
  void initState() {
    super.initState();
    _initFlow();
  }

  Future<void> _initFlow() async {
    // Perform initial bidirectional offline-first data sync
    try {
      await SyncService().performInitialSync();
    } catch (e) {
      print("Startup sync skipped/failed: $e");
    }

    final bool isPrivacyEnabled = await _securityService.isBiometricPrivacyEnabled();
    
    if (isPrivacyEnabled) {
      setState(() {
        _isLocked = true;
        _isChecking = false;
      });
      _authenticate();
    } else {
      await Future.delayed(const Duration(milliseconds: 1200));
      _navigateToMain();
    }
  }

  Future<void> _authenticate() async {
    final bool authenticated = await _securityService.authenticateUser();
    if (authenticated) {
      _navigateToMain();
    } else {
      setState(() {
        _isLocked = true;
      });
      _showPremiumErrorSnackBar("Authentication failed. Please verify biometrics.");
    }
  }

  void _navigateToMain() {
    if (!mounted) return;
    Navigator.of(context).pushReplacement(
      PageRouteBuilder(
        pageBuilder: (context, animation, secondaryAnimation) => const MainAppScreen(),
        transitionsBuilder: (context, animation, secondaryAnimation, child) {
          return FadeTransition(opacity: animation, child: child);
        },
        transitionDuration: const Duration(milliseconds: 600),
      ),
    );
  }

  void _showPremiumErrorSnackBar(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Row(
          children: [
            const Icon(Icons.error_outline_rounded, color: Colors.white, size: 20),
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
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF0C0C0E),
      body: Stack(
        children: [
          Positioned(
            top: -100,
            left: -100,
            child: Container(
              width: 300,
              height: 300,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: const Color(0xFFE94560).withOpacity(0.08),
              ),
            ),
          ),
          Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Container(
                  width: 96,
                  height: 96,
                  decoration: BoxDecoration(
                    gradient: const LinearGradient(
                      colors: [Color(0xFFE94560), Color(0xFFC0273F)],
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                    ),
                    borderRadius: BorderRadius.circular(28),
                    boxShadow: [
                      BoxShadow(
                        color: const Color(0xFFE94560).withOpacity(0.3),
                        blurRadius: 24,
                        offset: const Offset(0, 12),
                      ),
                    ],
                  ),
                  child: Icon(
                    _isLocked ? Icons.lock_outline_rounded : Icons.bubble_chart_rounded,
                    color: Colors.white,
                    size: 44,
                  ),
                ),
                const SizedBox(height: 36),
                const Text(
                  "FOCUS PLANNER",
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 26,
                    fontWeight: FontWeight.bold,
                    letterSpacing: 4,
                    fontFamily: 'SF Pro Display',
                  ),
                ),
                const SizedBox(height: 12),
                Text(
                  _isLocked
                      ? "Biometric Privacy Active"
                      : (_isChecking ? "Checking Security..." : "Loading Workspace..."),
                  style: const TextStyle(
                    color: Color(0xFF8E8E93),
                    fontSize: 14,
                    letterSpacing: 1,
                  ),
                ),
                if (_isLocked) ...[
                  const SizedBox(height: 48),
                  ElevatedButton.icon(
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFFE94560),
                      foregroundColor: Colors.white,
                      padding: const EdgeInsets.symmetric(horizontal: 28, vertical: 16),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(20),
                      ),
                      elevation: 6,
                      shadowColor: const Color(0xFFE94560).withOpacity(0.4),
                    ),
                    onPressed: _authenticate,
                    icon: const Icon(Icons.fingerprint_rounded, size: 22),
                    label: const Text(
                      "Unlock with Biometrics",
                      style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                    ),
                  ),
                ] else ...[
                  const SizedBox(height: 48),
                  const SizedBox(
                    width: 24,
                    height: 24,
                    child: CircularProgressIndicator(
                      strokeWidth: 2.5,
                      valueColor: AlwaysStoppedAnimation<Color>(Color(0xFFE94560)),
                    ),
                  ),
                ],
              ],
            ),
          ),
        ],
      ),
    );
  }
}
