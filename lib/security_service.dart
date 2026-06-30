// lib/security_service.dart

import 'package:local_auth/local_auth.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Service to handle biometric registration, status, and authentication.
class SecurityService {
  static final SecurityService _instance = SecurityService._internal();
  final LocalAuthentication _auth = LocalAuthentication();

  factory SecurityService() {
    return _instance;
  }

  SecurityService._internal();

  /// Check if biometric hardware is available and enabled on the device.
  Future<bool> isBiometricSupported() async {
    final bool canAuthenticateWithBiometrics = await _auth.canCheckBiometrics;
    final bool canAuthenticate = canAuthenticateWithBiometrics || await _auth.isDeviceSupported();
    return canAuthenticate;
  }

  /// Perform strict biometric authentication.
  Future<bool> authenticateUser() async {
    try {
      final bool isSupported = await isBiometricSupported();
      if (!isSupported) {
        return false;
      }
      return await _auth.authenticate(
        localizedReason: 'Please authenticate to unlock Focus Planner',
        options: const AuthenticationOptions(
          stickyAuth: true,
          biometricOnly: true,
        ),
      );
    } catch (e) {
      return false;
    }
  }

  /// Check if the 'Biometric Privacy' setting is toggled ON.
  Future<bool> isBiometricPrivacyEnabled() async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    return prefs.getBool('biometric_privacy') ?? false;
  }

  /// Set the status of 'Biometric Privacy'.
  Future<void> setBiometricPrivacy(bool enabled) async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setBool('biometric_privacy', enabled);
  }
}
