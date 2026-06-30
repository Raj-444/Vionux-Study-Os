// lib/productivity_screen.dart

import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_ringtone_player/flutter_ringtone_player.dart';
import 'package:flutter_windowmanager/flutter_windowmanager.dart';
import 'permission_helper.dart';
import 'alarm_helper.dart';
import 'finance_screen.dart';
import 'roadmap_list_screen.dart';
import 'profile_screen.dart';

/// A premium, minimalist 'ProductivityScreen' for the VioNux app suite.
/// This screen implements a Task Manager and a visually stunning Pomodoro Focus Timer.
/// Adheres strictly to an off-white premium background with highly rounded cards (Radius 24).
class ProductivityScreen extends StatefulWidget {
  const ProductivityScreen({Key? key}) : super(key: key);

  @override
  State<ProductivityScreen> createState() => _ProductivityScreenState();
}

class _ProductivityScreenState extends State<ProductivityScreen> with SingleTickerProviderStateMixin {
  late TabController _tabController;

  // Simulated state for tasks list
  final List<Map<String, dynamic>> _tasks = [
    {
      'id': 1,
      'title': 'Complete API Integration',
      'subject': 'CSE351',
      'priority': 'high',
      'dueTime': 'Tomorrow, 10:00 AM',
      'isCompleted': false,
    },
    {
      'id': 2,
      'title': 'Compile Finance DB Queries',
      'subject': 'CSE482',
      'priority': 'medium',
      'dueTime': 'Today, 5:30 PM',
      'isCompleted': true,
    },
    {
      'id': 3,
      'title': 'Review Profile Screen UI PR',
      'subject': 'SWE201',
      'priority': 'low',
      'dueTime': 'June 30, 2:00 PM',
      'isCompleted': false,
    },
    {
      'id': 4,
      'title': 'Prepare Presentation Slides',
      'subject': 'MGT101',
      'priority': 'high',
      'dueTime': 'Tomorrow, 9:00 AM',
      'isCompleted': false,
    }
  ];

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  // Method to toggle task completion status
  void _toggleTask(int id) {
    setState(() {
      final task = _tasks.firstWhere((t) => t['id'] == id);
      task['isCompleted'] = !task['isCompleted'];
    });
  }

  // Method to simulate adding a task
  void _addNewTask() {
    showDialog(
      context: context,
      builder: (context) {
        String title = '';
        String subject = 'CSE351';
        String priority = 'high';
        return AlertDialog(
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
          backgroundColor: const Color(0xFFF8F9FA),
          title: const Text('New Focus Objective', style: TextStyle(fontWeight: FontWeight.bold)),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                decoration: const InputDecoration(
                  labelText: 'Task Title',
                  hintText: 'e.g., Complete API Integration',
                ),
                onChanged: (val) => title = val,
              ),
              const SizedBox(height: 12),
              DropdownButtonFormField<String>(
                value: subject,
                items: ['CSE351', 'CSE482', 'SWE201', 'MGT101']
                    .map((s) => DropdownMenuItem(value: s, child: Text(s)))
                    .toList(),
                onChanged: (val) => subject = val ?? 'CSE351',
                decoration: const InputDecoration(labelText: 'Subject Tag'),
              ),
              const SizedBox(height: 12),
              DropdownButtonFormField<String>(
                value: priority,
                items: ['high', 'medium', 'low']
                    .map((p) => DropdownMenuItem(value: p, child: Text(p.toUpperCase())))
                    .toList(),
                onChanged: (val) => priority = val ?? 'high',
                decoration: const InputDecoration(labelText: 'Priority Level'),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Cancel', style: TextStyle(color: Colors.grey)),
            ),
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFFE94560),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
              onPressed: () {
                if (title.isNotEmpty) {
                  setState(() {
                    _tasks.insert(0, {
                      'id': DateTime.now().millisecondsSinceEpoch,
                      'title': title,
                      'subject': subject,
                      'priority': priority,
                      'dueTime': 'Just now',
                      'isCompleted': false,
                    });
                  });
                }
                Navigator.pop(context);
              },
              child: const Text('Add', style: TextStyle(color: Colors.white)),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8F9FA), // Premium off-white background
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        centerTitle: false,
        title: const Text(
          'VioNux Productivity',
          style: TextStyle(
            color: Color(0xFF0C0C0E),
            fontWeight: FontWeight.w900,
            fontSize: 22,
            letterSpacing: -0.5,
          ),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.show_chart_rounded, color: Color(0xFF0C0C0E)),
            tooltip: "Finance Tracker",
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const FinanceScreen()),
              );
            },
          ),
          IconButton(
            icon: const Icon(Icons.alt_route_rounded, color: Color(0xFF0C0C0E)),
            tooltip: "Goal Roadmaps",
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const RoadmapListScreen()),
              );
            },
          ),
          IconButton(
            icon: const Icon(Icons.settings_rounded, color: Color(0xFF0C0C0E)),
            tooltip: "Profile & Settings",
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const ProfileScreen()),
              );
            },
          ),
          const SizedBox(width: 8),
        ],
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: const Color(0xFFE94560),
          labelColor: const Color(0xFFE94560),
          unselectedLabelColor: const Color(0xFF8E8E93),
          labelStyle: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14),
          tabs: const [
            Tab(text: "Objectives", icon: Icon(Icons.check_circle_outline, size: 20)),
            Tab(text: "Pomodoro Timer", icon: Icon(Icons.timer_outlined, size: 20)),
          ],
        ),
      ),
      body: SafeArea(
        child: TabBarView(
          controller: _tabController,
          children: [
            // Tab 1: Task Manager Section
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const SizedBox(height: 20),
                  // Header Row with Button
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text(
                        "Task Manager",
                        style: TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.w800,
                          color: Color(0xFF0C0C0E),
                        ),
                      ),
                      ElevatedButton.icon(
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFFE94560),
                          foregroundColor: Colors.white,
                          elevation: 0,
                          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(16),
                          ),
                        ),
                        onPressed: _addNewTask,
                        icon: const Icon(Icons.add, size: 16),
                        label: const Text(
                          "Add Task",
                          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 16),
                  // Task List View
                  Expanded(
                    child: _tasks.isEmpty
                        ? const Center(
                            child: Text(
                              "All focus goals achieved!\nCreate a new task to get aligned.",
                              textAlign: TextAlign.center,
                              style: TextStyle(color: Color(0xFF8E8E93), height: 1.4),
                            ),
                          )
                        : ListView.separated(
                            itemCount: _tasks.length,
                            separatorBuilder: (context, index) => const SizedBox(height: 12),
                            itemBuilder: (context, index) {
                              final task = _tasks[index];
                              return TaskCard(
                                title: task['title'],
                                subject: task['subject'],
                                priority: task['priority'],
                                dueTime: task['dueTime'],
                                isCompleted: task['isCompleted'],
                                onToggle: () => _toggleTask(task['id']),
                              );
                            },
                          ),
                  ),
                  const SizedBox(height: 20),
                ],
              ),
            ),

            // Tab 2: Pomodoro Timer + Clock Section
            SingleChildScrollView(
              padding: const EdgeInsets.all(20.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    "Focus Space",
                    style: TextStyle(
                      fontSize: 24,
                      fontWeight: FontWeight.w800,
                      color: Color(0xFF0C0C0E),
                    ),
                  ),
                  const SizedBox(height: 4),
                  const Text(
                    "Mute visual noise and trigger cognitive alignment.",
                    style: TextStyle(fontSize: 13, color: Color(0xFF8E8E93)),
                  ),
                  const SizedBox(height: 20),
                  // PomodoroCard Widget
                  const PomodoroCard(),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

/// Custom stateless TaskCard Widget with premium minimalist styling.
class TaskCard extends StatelessWidget {
  final String title;
  final String subject;
  final String priority;
  final String dueTime;
  final bool isCompleted;
  final VoidCallback onToggle;

  const TaskCard({
    Key? key,
    required this.title,
    required this.subject,
    required this.priority,
    required this.dueTime,
    required this.isCompleted,
    required this.onToggle,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    // Red color for priority/accent
    const Color accentRed = Color(0xFFE94560);
    final Color priorityColor = priority.toLowerCase() == 'high'
        ? accentRed
        : (priority.toLowerCase() == 'medium' ? Colors.orange : Colors.blue);

    return Opacity(
      opacity: isCompleted ? 0.6 : 1.0,
      child: Container(
        decoration: BoxDecoration(
          color: isCompleted ? const Color(0xFFF2F2F7).withOpacity(0.6) : const Color(0xFFF2F2F7),
          borderRadius: BorderRadius.circular(24),
          border: Border.all(color: Colors.white, width: 1),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.02),
              blurRadius: 10,
              offset: const Offset(0, 4),
            )
          ],
        ),
        padding: const EdgeInsets.all(18),
        child: Row(
          children: [
            // 1. Circular checkbox on the left
            GestureDetector(
              onTap: onToggle,
              child: Container(
                width: 26,
                height: 26,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: isCompleted ? accentRed : Colors.transparent,
                  border: Border.all(
                    color: isCompleted ? accentRed : const Color(0xFF8E8E93).withOpacity(0.6),
                    width: 2,
                  ),
                ),
                child: isCompleted
                    ? const Icon(Icons.check, color: Colors.white, size: 16)
                    : null,
              ),
            ),
            const SizedBox(width: 16),
            // 2. Task Details
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: TextStyle(
                      fontSize: 15,
                      fontWeight: FontWeight.bold,
                      color: const Color(0xFF0C0C0E),
                      decoration: isCompleted ? TextDecoration.lineThrough : null,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      // Subject Chip
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                        decoration: BoxDecoration(
                          color: Colors.white.withOpacity(0.8),
                          borderRadius: BorderRadius.circular(12),
                          border: Border.all(color: const Color(0xFFE5E5EA)),
                        ),
                        child: Text(
                          subject,
                          style: const TextStyle(
                            fontSize: 10,
                            fontWeight: FontWeight.w700,
                            color: Color(0xFF8E8E93),
                          ),
                        ),
                      ),
                      const SizedBox(width: 8),
                      // Priority Indicator (Small dot)
                      Container(
                        width: 8,
                        height: 8,
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          color: priorityColor,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            // 3. Due Date/Time Trailing Text
            Text(
              dueTime,
              style: const TextStyle(
                fontSize: 11,
                color: Color(0xFF8E8E93),
                fontWeight: FontWeight.w500,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

/// Custom stateful PomodoroCard Widget featuring circular timer with dark design.
class PomodoroCard extends StatefulWidget {
  const PomodoroCard({Key? key}) : super(key: key);

  @override
  State<PomodoroCard> createState() => _PomodoroCardState();
}

class _PomodoroCardState extends State<PomodoroCard> with WidgetsBindingObserver {
  int _selectedMinutes = 25;
  int _secondsRemaining = 25 * 60;
  bool _isRunning = false;
  bool _focusLock = true;
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    // Request runtime permissions on screen open
    Future.microtask(() => PermissionHelper.requestAllPermissions(context));
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _timer?.cancel();
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if ((state == AppLifecycleState.paused || state == AppLifecycleState.inactive) && _isRunning && _focusLock) {
      _handleBrokenFocus();
    }
  }

  void _onChipSelected(int minutes) {
    setState(() {
      _selectedMinutes = minutes;
      _secondsRemaining = minutes * 60;
      _stopTimer();
    });
  }

  void _toggleTimer() {
    if (_isRunning) {
      _stopTimer();
    } else {
      _startTimer();
    }
  }

  void _startTimer() {
    setState(() {
      _isRunning = true;
    });

    if (_focusLock) {
      SystemChrome.setEnabledSystemUIMode(SystemUiMode.immersiveSticky);
      _updateScreenSecurity();
    }

    // Schedule exact notification alarm for the end of the session
    final DateTime scheduledEndTime = DateTime.now().add(Duration(seconds: _secondsRemaining));
    AlarmHelper.scheduleAlarm(
      id: 999, // Fixed Pomodoro notification ID
      title: "Focus Session Complete",
      body: "Amazing! You successfully protected your focus window.",
      scheduledTime: scheduledEndTime,
    );

    _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      setState(() {
        if (_secondsRemaining > 0) {
          _secondsRemaining--;
        } else {
          _stopTimer();
          _triggerSessionComplete();
        }
      });
    });
  }

  void _stopTimer() {
    _timer?.cancel();
    setState(() {
      _isRunning = false;
    });
    SystemChrome.setEnabledSystemUIMode(SystemUiMode.edgeToEdge);
    _updateScreenSecurity();
    // Cancel any pending exact notification alarms
    AlarmHelper.cancelAlarm(999);
  }

  void _resetTimer() {
    _stopTimer();
    setState(() {
      _secondsRemaining = _selectedMinutes * 60;
    });
  }

  void _updateScreenSecurity() {
    try {
      if (_isRunning && _focusLock) {
        FlutterWindowManager.addFlags(FlutterWindowManager.FLAG_SECURE);
      } else {
        FlutterWindowManager.clearFlags(FlutterWindowManager.FLAG_SECURE);
      }
    } catch (e) {
      // Safe fallback if not running on Android
    }
  }

  void _triggerSessionComplete() {
    // 1. Play loopable system alarm tone
    try {
      FlutterRingtonePlayer().playAlarm(looping: true, volume: 1.0);
    } catch (e) {
      // Fallback
    }

    // 2. Show beautiful focus completion dialog
    showDialog(
      context: context,
      barrierDismissible: false, // Force manual dismissal
      builder: (BuildContext context) {
        return AlertDialog(
          backgroundColor: const Color(0xFFF8F9FA),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
          title: const Row(
            children: [
              Icon(Icons.check_circle, color: Color(0xFFE94560), size: 28),
              SizedBox(width: 12),
              Text(
                "Session Complete",
                style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
              ),
            ],
          ),
          content: const Text(
            "Sensational work! You successfully guarded your rhythm against all external noise. Click below to stop the alarm.",
            style: TextStyle(color: Color(0xFF0C0C0E), height: 1.4),
          ),
          actions: [
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFFE94560),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
              ),
              onPressed: () {
                try {
                  FlutterRingtonePlayer().stop();
                } catch (e) {
                  // Catch any stop audio error
                }
                Navigator.pop(context);
                _resetTimer();
              },
              child: const Text(
                "Dismiss Alarm",
                style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
              ),
            ),
          ],
        );
      },
    );
  }

  void _handleBrokenFocus() {
    _stopTimer();

    // Play distinct failure/warning notification tone
    try {
      FlutterRingtonePlayer().play(
        android: AndroidSounds.notification,
        ios: IosSounds.glass,
        looping: false,
        volume: 1.0,
      );
    } catch (e) {
      // Fallback
    }

    // Show failure warning dialog
    showDialog(
      context: context,
      barrierDismissible: true,
      builder: (BuildContext context) {
        return AlertDialog(
          backgroundColor: const Color(0xFFF8F9FA),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
          title: const Row(
            children: [
              Icon(Icons.error_outline, color: Color(0xFFE94560), size: 28),
              SizedBox(width: 12),
              Text(
                "Broken Focus Detected",
                style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
              ),
            ],
          ),
          content: const Text(
            "Your focus session was compromised because you left or minimized the app. Stay aligned and protect your rhythm next time!",
            style: TextStyle(color: Color(0xFF0C0C0E), height: 1.4),
          ),
          actions: [
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFFE94560),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              ),
              onPressed: () {
                Navigator.pop(context);
              },
              child: const Text(
                "Acknowledge",
                style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
              ),
            ),
          ],
        );
      },
    );
  }

  String _formatTime(int totalSeconds) {
    final minutes = totalSeconds ~/ 60;
    final seconds = totalSeconds % 60;
    return '${minutes.toString().padLeft(2, '0')}:${seconds.toString().padLeft(2, '0')}';
  }

  @override
  Widget build(BuildContext context) {
    const Color accentRed = Color(0xFFE94560);
    double progress = _secondsRemaining / (_selectedMinutes * 60);

    return PopScope(
      canPop: !(_isRunning && _focusLock),
      onPopInvoked: (didPop) {
        if (didPop) return;
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text("Focus Lock is Active! Complete your session to leave."),
            backgroundColor: Color(0xFFE94560),
            behavior: SnackBarBehavior.floating,
          ),
        );
      },
      child: Container(
        width: double.infinity,
        decoration: BoxDecoration(
          color: const Color(0xFF0C0C0E), // Highly rounded dark card (black background)
          borderRadius: BorderRadius.circular(24),
          boxShadow: [
            BoxShadow(
              color: accentRed.withOpacity(0.08),
              blurRadius: 20,
              spreadRadius: 2,
            )
          ],
        ),
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 24),
        child: Column(
          children: [
            // 1. Circular Progress Indicator containing the countdown
            SizedBox(
              width: 170,
              height: 170,
              child: Stack(
                alignment: Alignment.center,
                children: [
                  SizedBox(
                    width: 160,
                    height: 160,
                    child: CircularProgressIndicator(
                      value: progress,
                      strokeWidth: 6,
                      backgroundColor: Colors.white.withOpacity(0.05),
                      valueColor: const AlwaysStoppedAnimation<Color>(accentRed),
                    ),
                  ),
                  Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        _formatTime(_secondsRemaining),
                        style: const TextStyle(
                          color: Colors.white,
                          fontSize: 36,
                          fontWeight: FontWeight.bold,
                          fontFamily: 'Courier', // Monospace feel
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        _isRunning ? "focusing..." : "ready",
                        style: TextStyle(
                          color: Colors.white.withOpacity(0.4),
                          fontSize: 11,
                          letterSpacing: 1.5,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),

            // 2. Duration selector chips
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                _buildDurationChip(25),
                const SizedBox(width: 12),
                _buildDurationChip(45),
                const SizedBox(width: 12),
                _buildDurationChip(60),
              ],
            ),
            const SizedBox(height: 24),

            // 3. Focus Lock Toggle inside the card
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
              decoration: BoxDecoration(
                color: Colors.white.withOpacity(0.03),
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: Colors.white.withOpacity(0.05)),
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Row(
                    children: [
                      Icon(
                        Icons.lock_outline,
                        color: _focusLock ? accentRed : Colors.white.withOpacity(0.4),
                        size: 18,
                      ),
                      const SizedBox(width: 12),
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text(
                            "Focus Lock (App-block mode)",
                            style: TextStyle(
                              color: Colors.white,
                              fontSize: 12,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          Text(
                            "Prevents leaving app while timing",
                            style: TextStyle(
                              color: Colors.white.withOpacity(0.4),
                              fontSize: 10,
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                  Switch(
                    value: _focusLock,
                    onChanged: (val) {
                      setState(() {
                        _focusLock = val;
                      });
                    },
                    activeColor: Colors.white,
                    activeTrackColor: accentRed,
                    inactiveThumbColor: Colors.grey,
                    inactiveTrackColor: Colors.white.withOpacity(0.1),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),

            // 4. Start & Reset Control Buttons
            Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      backgroundColor: accentRed,
                      padding: const EdgeInsets.symmetric(vertical: 14),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(16),
                      ),
                      elevation: 0,
                    ),
                    onPressed: _toggleTimer,
                    child: Text(
                      _isRunning ? "Pause Session" : "Start Focus",
                      style: const TextStyle(
                        color: Colors.white,
                        fontWeight: FontWeight.bold,
                        fontSize: 14,
                      ),
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                GestureDetector(
                  onTap: _resetTimer,
                  child: Container(
                    height: 48,
                    width: 48,
                    decoration: BoxDecoration(
                      color: Colors.white.withOpacity(0.08),
                      borderRadius: BorderRadius.circular(16),
                    ),
                    child: const Icon(
                      Icons.refresh,
                      color: Colors.white,
                      size: 20,
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildDurationChip(int minutes) {
    final bool isSelected = _selectedMinutes == minutes;
    return ChoiceChip(
      label: Text(
        "${minutes}m",
        style: TextStyle(
          color: isSelected ? Colors.white : Colors.white.withOpacity(0.6),
          fontWeight: FontWeight.bold,
          fontSize: 12,
        ),
      ),
      selected: isSelected,
      selectedColor: const Color(0xFFE94560),
      backgroundColor: Colors.white.withOpacity(0.05),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: const BorderSide(color: Colors.transparent),
      ),
      onSelected: (bool selected) {
        if (selected) {
          _onChipSelected(minutes);
        }
      },
    );
  }
}
