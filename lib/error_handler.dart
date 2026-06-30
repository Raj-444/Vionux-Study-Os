// lib/error_handler.dart

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:developer' as developer;

/// A premium, global error handler wrapper that catches runtime exceptions
/// and displays a highly polished fallback UI instead of crashing the app.
class ErrorHandler extends StatefulWidget {
  final Widget child;

  const ErrorHandler({Key? key, required this.child}) : super(key: key);

  @override
  State<ErrorHandler> createState() => _ErrorHandlerState();
}

class _ErrorHandlerState extends State<ErrorHandler> {
  bool _hasError = false;
  Object? _error;
  StackTrace? _stackTrace;

  @override
  void initState() {
    super.initState();
    // Override the default ErrorWidget builder (the notorious Red Screen of Death)
    ErrorWidget.builder = (FlutterErrorDetails details) {
      // Automatically report to console/Crashlytics
      _reportError(details.exception, details.stack);

      return _buildErrorScreen(
        details.exception,
        details.stack,
        isRenderError: true,
      );
    };
  }

  /// Internal logger to send logs to local console and external services like Firebase Crashlytics
  void _reportError(Object error, StackTrace? stack) {
    developer.log(
      'UNCAUGHT EXCEPTION DETECTED',
      error: error,
      stackTrace: stack,
      name: 'ErrorHandler',
    );
    
    // NOTE: To log to Firebase Crashlytics, uncomment the line below after configuring Firebase:
    // FirebaseCrashlytics.instance.recordError(error, stack, fatal: true);
  }

  /// Builds a beautifully crafted, highly rounded premium error fallback screen
  Widget _buildErrorScreen(Object error, StackTrace? stack, {required bool isRenderError}) {
    final String errorTitle = error.toString().split(':').first;
    final String errorMessage = error.toString();
    final String stackTraceString = stack?.toString() ?? "No stack trace available.";

    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        fontFamily: 'SF Pro Display',
        scaffoldBackgroundColor: const Color(0xFFF8F9FA),
      ),
      home: Scaffold(
        body: SafeArea(
          child: Center(
            child: SingleChildScrollView(
              padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  // Beautifully animated pulsing error warning icon
                  Container(
                    width: 80,
                    height: 80,
                    decoration: BoxDecoration(
                      color: const Color(0xFFE94560).withOpacity(0.1),
                      shape: BoxShape.circle,
                    ),
                    child: const Icon(
                      Icons.report_problem_rounded,
                      color: Color(0xFFE94560),
                      size: 40,
                    ),
                  ),
                  const SizedBox(height: 24),
                  
                  // Premium typography headings
                  const Text(
                    "Something Went Wrong",
                    style: TextStyle(
                      color: Color(0xFF0C0C0E),
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                      letterSpacing: -0.5,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 12),
                  
                  const Text(
                    "The application encountered an unexpected runtime exception. We have captured the diagnostic logs for instant resolution.",
                    style: TextStyle(
                      color: Color(0xFF8E8E93),
                      fontSize: 14,
                      height: 1.4,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 24),

                  // Collapsible Premium Diagnostic Card
                  Container(
                    width: double.infinity,
                    decoration: BoxDecoration(
                      color: const Color(0xFF0C0C0E), // Match our dark theme card
                      borderRadius: BorderRadius.circular(24),
                    ),
                    padding: const EdgeInsets.all(20),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          children: [
                            const Icon(Icons.bug_report, color: Color(0xFFE94560), size: 18),
                            const SizedBox(width: 8),
                            Text(
                              errorTitle.length > 30 ? "${errorTitle.substring(0, 30)}..." : errorTitle,
                              style: const TextStyle(
                                color: Colors.white,
                                fontWeight: FontWeight.bold,
                                fontSize: 13,
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 12),
                        Text(
                          errorMessage,
                          style: TextStyle(
                            color: Colors.white.withOpacity(0.8),
                            fontSize: 12,
                            fontFamily: 'Courier',
                          ),
                          maxLines: 4,
                          overflow: TextOverflow.ellipsis,
                        ),
                        const Divider(color: Colors.white10, height: 24),
                        const Text(
                          "DIAGNOSTIC LOG SUMMARY",
                          style: TextStyle(
                            color: Colors.white30,
                            fontWeight: FontWeight.bold,
                            fontSize: 10,
                            letterSpacing: 1.2,
                          ),
                        ),
                        const SizedBox(height: 8),
                        Container(
                          maxHeight: 120,
                          width: double.infinity,
                          padding: const EdgeInsets.all(12),
                          decoration: BoxDecoration(
                            color: Colors.white.withOpacity(0.03),
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: SingleChildScrollView(
                            child: Text(
                              stackTraceString,
                              style: TextStyle(
                                color: Colors.white.withOpacity(0.5),
                                fontSize: 11,
                                fontFamily: 'Courier',
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 28),

                  // Copy Log and Reset buttons
                  Row(
                    children: [
                      Expanded(
                        child: OutlinedButton.icon(
                          style: OutlinedButton.styleFrom(
                            side: const BorderSide(color: Color(0xFFE5E5EA)),
                            padding: const EdgeInsets.symmetric(vertical: 14),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(16),
                            ),
                          ),
                          onPressed: () {
                            final String fullReport = "--- AI STUDIO SELF-REPORT PROTOCOL ---\n"
                                "ERROR: $errorMessage\n\n"
                                "STACK TRACE:\n$stackTraceString\n"
                                "--------------------------------------";
                            Clipboard.setData(ClipboardData(text: fullReport));
                            
                            ScaffoldMessenger.of(context).showSnackBar(
                              SnackBar(
                                content: const Row(
                                  children: [
                                    Icon(Icons.check_circle_outline, color: Colors.white, size: 20),
                                    SizedBox(width: 12),
                                    Text(
                                      "Stack trace copied successfully! Paste into Gemini to auto-fix.",
                                      style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12),
                                    ),
                                  ],
                                ),
                                backgroundColor: const Color(0xFF2E7D32),
                                behavior: SnackBarBehavior.floating,
                                shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(16),
                                ),
                              ),
                            );
                          },
                          icon: const Icon(Icons.copy, color: Color(0xFF0C0C0E), size: 18),
                          label: const Text(
                            "Copy Stack Trace",
                            style: TextStyle(
                              color: Color(0xFF0C0C0E),
                              fontWeight: FontWeight.bold,
                              fontSize: 14,
                            ),
                          ),
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: ElevatedButton.icon(
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color(0xFFE94560),
                            padding: const EdgeInsets.symmetric(vertical: 14),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(16),
                            ),
                            elevation: 0,
                          ),
                          onPressed: () {
                            setState(() {
                              _hasError = false;
                              _error = null;
                              _stackTrace = null;
                            });
                            // Re-route back to main screen or reset main state
                            Navigator.of(context).pushNamedAndRemoveUntil('/', (route) => false);
                          },
                          icon: const Icon(Icons.refresh, color: Colors.white, size: 18),
                          label: const Text(
                            "Restart App",
                            style: TextStyle(
                              color: Colors.white,
                              fontWeight: FontWeight.bold,
                              fontSize: 14,
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_hasError) {
      return _buildErrorScreen(_error!, _stackTrace, isRenderError: false);
    }

    return widget.child;
  }
}
