// lib/auth_service.dart

import 'package:firebase_auth/firebase_auth.dart';
import 'package:google_sign_in/google_sign_in.dart';

/// Service class handling authentication logic with Firebase and Google Sign-In.
class AuthService {
  static final AuthService _instance = AuthService._internal();
  final FirebaseAuth _auth = FirebaseAuth.instance;
  final GoogleSignIn _googleSignIn = GoogleSignIn();

  factory AuthService() {
    return _instance;
  }

  AuthService._internal();

  /// Performs Google Sign-In.
  /// Preemptively runs [GoogleSignIn.signOut] to force the account chooser dialog on every click.
  Future<User?> signInWithGoogle() async {
    try {
      // Force account chooser by logging out of Google Sign-in session first
      await _googleSignIn.signOut();

      // Initiate Google Sign-In flow
      final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();
      if (googleUser == null) {
        // User cancelled the sign-in flow
        return null;
      }

      // Obtain authentication details from the request
      final GoogleSignInAuthentication googleAuth = await googleUser.authentication;

      // Create a new credential for Firebase
      final AuthCredential credential = GoogleAuthProvider.credential(
        accessToken: googleAuth.accessToken,
        idToken: googleAuth.idToken,
      );

      // Sign in to Firebase with the credential
      final UserCredential userCredential = await _auth.signInWithCredential(credential);
      return userCredential.user;
    } catch (e) {
      print("Google Sign-In Error: $e");
      return null;
    }
  }

  /// Sign out from Firebase and Google Sign-In.
  Future<void> signOut() async {
    try {
      await _auth.signOut();
      await _googleSignIn.signOut();
    } catch (e) {
      print("Sign-Out Error: $e");
    }
  }

  /// Retrieves the current authenticated Firebase user.
  User? get currentUser => _auth.currentUser;

  /// Stream of authentication state changes.
  Stream<User?> get authStateChanges => _auth.authStateChanges();
}
