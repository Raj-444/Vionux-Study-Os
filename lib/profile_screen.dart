// lib/profile_screen.dart

import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'auth_service.dart';
import 'security_service.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({Key? key}) : super(key: key);

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  final AuthService _authService = AuthService();
  final SecurityService _securityService = SecurityService();
  final TextEditingController _nameController = TextEditingController();
  
  bool _isBiometricOn = false;
  String _displayName = "User";
  String _email = "no-session@focusplanner.io";
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _loadUserData();
  }

  @override
  void dispose() {
    _nameController.dispose();
    super.dispose();
  }

  Future<void> _loadUserData() async {
    // 1. Fetch biometric settings
    final bool bioStatus = await _securityService.isBiometricPrivacyEnabled();
    
    // 2. Fetch Firebase user info
    final User? user = _authService.currentUser;
    setState(() {
      _isBiometricOn = bioStatus;
      if (user != null) {
        _displayName = user.displayName ?? "User";
        _email = user.email ?? "authenticated@gmail.com";
      } else {
        // Fallback for demo/development environments
        _displayName = "User";
      }
      _nameController.text = _displayName;
    });
  }

  Future<void> _updateProfileName() async {
    final String newName = _nameController.text.trim();
    if (newName.isEmpty) return;

    setState(() {
      _isLoading = true;
    });

    try {
      final User? user = _authService.currentUser;
      if (user != null) {
        // Strict firebase name update
        await user.updateDisplayName(newName);
        // Force refresh user credentials state
        await user.reload();
      }
      
      setState(() {
        _displayName = newName;
        _isLoading = false;
      });

      _showPremiumSnackBar("Profile display name updated successfully!", isError: false);
    } catch (e) {
      setState(() {
        _isLoading = false;
      });
      _showPremiumSnackBar("Failed to update profile name on server.", isError: true);
    }
  }

  Future<void> _toggleBiometric(bool value) async {
    // Check if biometric is supported on hardware first
    final bool supported = await _securityService.isBiometricSupported();
    if (!supported && value) {
      _showPremiumSnackBar("Biometric authentication is not supported on this device.", isError: true);
      return;
    }

    await _securityService.setBiometricPrivacy(value);
    setState(() {
      _isBiometricOn = value;
    });
    
    _showPremiumSnackBar(
      value ? "Biometric Privacy Lock Enabled" : "Biometric Privacy Lock Disabled",
      isError: false,
    );
  }

  void _showPremiumSnackBar(String message, {required bool isError}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Row(
          children: [
            Icon(
              isError ? Icons.error_outline_rounded : Icons.check_circle_outline_rounded,
              color: Colors.white,
              size: 20,
            ),
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
        backgroundColor: isError ? const Color(0xFFE94560) : const Color(0xFF2E7D32),
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(24),
        ),
        margin: const EdgeInsets.all(16),
        duration: const Duration(seconds: 3),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final User? user = _authService.currentUser;

    return Scaffold(
      backgroundColor: const Color(0xFFF8F9FA), // Premium off-white
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        title: const Text(
          "Settings & Profile",
          style: TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF0C0C0E)),
        ),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // User Profile Header Card
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(24),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(24),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.03),
                    blurRadius: 16,
                    offset: const Offset(0, 8),
                  ),
                ],
              ),
              child: Column(
                children: [
                  // Profile Avatar Circle
                  Container(
                    width: 80,
                    height: 80,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      gradient: const LinearGradient(
                        colors: [Color(0xFFE94560), Color(0xFFC0273F)],
                        begin: Alignment.topLeft,
                        end: Alignment.bottomRight,
                      ),
                      boxShadow: [
                        BoxShadow(
                          color: const Color(0xFFE94560).withOpacity(0.2),
                          blurRadius: 12,
                          offset: const Offset(0, 6),
                        ),
                      ],
                    ),
                    child: Center(
                      child: Text(
                        _displayName.isNotEmpty ? _displayName[0].toUpperCase() : "U",
                        style: const TextStyle(
                          color: Colors.white,
                          fontSize: 32,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(height: 16),
                  Text(
                    _displayName,
                    style: const TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFF0C0C0E),
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    _email,
                    style: const TextStyle(
                      fontSize: 14,
                      color: Color(0xFF8E8E93),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 28),
            
            // Section Title
            const Text(
              "PROFILE SETTINGS",
              style: TextStyle(
                fontSize: 12,
                fontWeight: FontWeight.bold,
                letterSpacing: 1.5,
                color: Color(0xFF8E8E93),
              ),
            ),
            const SizedBox(height: 12),

            // Profile Edit Input Card
            Container(
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(24),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.03),
                    blurRadius: 16,
                    offset: const Offset(0, 8),
                  ),
                ],
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    "Display Name",
                    style: TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFF0C0C0E),
                    ),
                  ),
                  const SizedBox(height: 8),
                  TextField(
                    controller: _nameController,
                    decoration: InputDecoration(
                      hintText: "Enter your display name",
                      filled: true,
                      fillColor: const Color(0xFFF2F2F7),
                      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(16),
                        borderSide: BorderSide.none,
                      ),
                    ),
                  ),
                  const SizedBox(height: 16),
                  SizedBox(
                    width: double.infinity,
                    height: 52,
                    child: ElevatedButton(
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFFE94560),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(18),
                        ),
                      ),
                      onPressed: _isLoading ? null : _updateProfileName,
                      child: _isLoading
                          ? const SizedBox(
                              width: 24,
                              height: 24,
                              child: CircularProgressIndicator(
                                strokeWidth: 2,
                                valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                              ),
                            )
                          : const Text(
                              "Save Name",
                              style: TextStyle(
                                color: Colors.white,
                                fontWeight: FontWeight.bold,
                                fontSize: 15,
                              ),
                            ),
                    ),
                  ),
                ],
              ),
            ),
            
            const SizedBox(height: 28),
            
            // Security settings Section
            const Text(
              "SECURITY & PRIVACY",
              style: TextStyle(
                fontSize: 12,
                fontWeight: FontWeight.bold,
                letterSpacing: 1.5,
                color: Color(0xFF8E8E93),
              ),
            ),
            const SizedBox(height: 12),

            // Security preferences Card
            Container(
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(24),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.03),
                    blurRadius: 16,
                    offset: const Offset(0, 8),
                  ),
                ],
              ),
              child: SwitchListTile(
                value: _isBiometricOn,
                onChanged: _toggleBiometric,
                activeColor: const Color(0xFFE94560),
                secondary: const Icon(Icons.fingerprint_rounded, color: Color(0xFFE94560)),
                title: const Text(
                  "Biometric Privacy Lock",
                  style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                ),
                subtitle: const Text("Block screen and force unlock on app launch"),
              ),
            ),
            const SizedBox(height: 48),

            // Google sign-in/sign-out Section
            if (user == null) ...[
              SizedBox(
                width: double.infinity,
                height: 54,
                child: ElevatedButton.icon(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.white,
                    foregroundColor: const Color(0xFF0C0C0E),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(18),
                      side: BorderSide(color: Colors.grey.shade300),
                    ),
                  ),
                  onPressed: () async {
                    final User? loggedUser = await _authService.signInWithGoogle();
                    if (loggedUser != null) {
                      _loadUserData();
                    }
                  },
                  icon: const Icon(Icons.login_rounded, color: Color(0xFFE94560)),
                  label: const Text(
                    "Sign In with Google",
                    style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                  ),
                ),
              ),
            ] else ...[
              SizedBox(
                width: double.infinity,
                height: 54,
                child: ElevatedButton.icon(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFFF2F2F7),
                    foregroundColor: const Color(0xFFE94560),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(18),
                    ),
                  ),
                  onPressed: () async {
                    await _authService.signOut();
                    _loadUserData();
                  },
                  icon: const Icon(Icons.logout_rounded),
                  label: const Text(
                    "Sign Out",
                    style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                  ),
                ),
              ),
            ],
            const SizedBox(height: 24),
          ],
        ),
      ),
    );
  }
}
