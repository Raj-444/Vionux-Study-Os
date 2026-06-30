// lib/academic_hub_screen.dart

import 'package:flutter/material.dart';

/// A premium, minimalist 'AcademicHubScreen' for the VioNux app suite.
/// Implements Class Routine (Timeline UI) and Attendance Calculator with live prediction text.
/// Adheres strictly to the off-white premium background and highly rounded card design language (Radius 24).
class AcademicHubScreen extends StatefulWidget {
  const AcademicHubScreen({Key? key}) : super(key: key);

  @override
  State<AcademicHubScreen> createState() => _AcademicHubScreenState();
}

class _AcademicHubScreenState extends State<AcademicHubScreen> {
  // 1. Routine State & Static Data
  String _selectedDay = "Mon";
  final List<String> _days = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

  final Map<String, List<Map<String, String>>> _routineData = {
    "Mon": [
      {
        "time": "09:00 AM - 10:30 AM",
        "subject": "CSE351: Computer Architecture",
        "room": "Room 402",
        "teacher": "Dr. A. K.",
        "alarm": "active"
      },
      {
        "time": "11:00 AM - 12:30 PM",
        "subject": "SWE201: Software Construction",
        "room": "Lab 3",
        "teacher": "Prof. S. R.",
        "alarm": "active"
      },
      {
        "time": "02:00 PM - 03:30 PM",
        "subject": "MGT101: Principles of Mgmt",
        "room": "Room 101",
        "teacher": "Lec. M. H.",
        "alarm": "inactive"
      }
    ],
    "Tue": [
      {
        "time": "10:00 AM - 11:30 AM",
        "subject": "CSE482: Database Systems",
        "room": "Room 501",
        "teacher": "Dr. M. R.",
        "alarm": "active"
      },
      {
        "time": "01:00 PM - 02:30 PM",
        "subject": "CSE351: Computer Architecture",
        "room": "Room 402",
        "teacher": "Dr. A. K.",
        "alarm": "active"
      }
    ],
    "Wed": [
      {
        "time": "09:00 AM - 10:30 AM",
        "subject": "SWE201: Software Construction",
        "room": "Lab 3",
        "teacher": "Prof. S. R.",
        "alarm": "active"
      },
      {
        "time": "03:00 PM - 04:30 PM",
        "subject": "MGT101: Principles of Mgmt",
        "room": "Room 101",
        "teacher": "Lec. M. H.",
        "alarm": "inactive"
      }
    ],
    "Thu": [
      {
        "time": "10:00 AM - 11:30 AM",
        "subject": "CSE482: Database Systems",
        "room": "Room 501",
        "teacher": "Dr. M. R.",
        "alarm": "active"
      }
    ],
    "Fri": [], // No classes
    "Sat": [], // Weekend
    "Sun": [], // Weekend
  };

  // 2. Attendance Tracker State
  final List<Map<String, dynamic>> _attendanceSubjects = [
    {
      "name": "CSE351: Computer Architecture",
      "attended": 17,
      "total": 20,
    },
    {
      "name": "CSE482: Database Systems",
      "attended": 12,
      "total": 15,
    },
    {
      "name": "SWE201: Software Construction",
      "attended": 15,
      "total": 16,
    },
    {
      "name": "MGT101: Principles of Mgmt",
      "attended": 8,
      "total": 12,
    },
  ];

  void _adjustAttendance(int index, bool attended) {
    setState(() {
      if (attended) {
        _attendanceSubjects[index]["attended"] += 1;
        _attendanceSubjects[index]["total"] += 1;
      } else {
        _attendanceSubjects[index]["total"] += 1;
      }
    });
  }

  String _getAttendancePrediction(int attended, int total) {
    if (total == 0) return "No lectures recorded yet.";
    double currentPercentage = (attended / total) * 100;
    
    if (currentPercentage >= 90) {
      return "Excellent! You are safely above the 90% threshold.";
    }

    // Solve for x: (attended + x) / (total + x) >= 0.90
    // => attended + x >= 0.90 * total + 0.90 * x
    // => 0.10 * x >= 0.90 * total - attended
    // => x >= (0.90 * total - attended) / 0.10
    double needed = (0.90 * total - attended) / 0.10;
    int classesToAttend = needed.ceil();
    if (classesToAttend <= 0) {
      return "Attend next 1 class to reach 90%";
    }
    return "Attend next $classesToAttend ${classesToAttend == 1 ? 'class' : 'classes'} to reach 90%";
  }

  @override
  Widget build(BuildContext context) {
    const Color accentRed = Color(0xFFE94560);

    return Scaffold(
      backgroundColor: const Color(0xFFF8F9FA), // premium off-white/light-gray background
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Screen Header
              const Text(
                "Academic Hub",
                style: TextStyle(
                  fontSize: 28,
                  fontWeight: FontWeight.bold,
                  color: Color(0xFF0C0C0E),
                  letterSpacing: -0.5,
                ),
              ),
              const SizedBox(height: 4),
              const Text(
                "Keep track of classes and monitor attendance eligibility.",
                style: TextStyle(fontSize: 14, color: Color(0xFF8E8E93)),
              ),
              const SizedBox(height: 24),

              // ==========================================
              // SECTION 1: Class Routine (Timeline View)
              // ==========================================
              const Text(
                "Class Routine",
                style: TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.w800,
                  color: Color(0xFF0C0C0E),
                ),
              ),
              const SizedBox(height: 12),

              // 7-day horizontal scrollable week selector
              SizedBox(
                height: 54,
                child: ListView.builder(
                  scrollDirection: Axis.horizontal,
                  itemCount: _days.length,
                  itemBuilder: (context, index) {
                    final day = _days[index];
                    final isSelected = day == _selectedDay;
                    return GestureDetector(
                      onTap: () {
                        setState(() {
                          _selectedDay = day;
                        });
                      },
                      child: Container(
                        width: 50,
                        margin: const EdgeInsets.only(right: 8),
                        decoration: BoxDecoration(
                          color: isSelected ? accentRed : const Color(0xFFF2F2F7),
                          borderRadius: BorderRadius.circular(16),
                          border: Border.all(
                            color: isSelected ? accentRed : Colors.white,
                            width: 1,
                          ),
                        ),
                        alignment: Alignment.Center,
                        child: Text(
                          day,
                          style: TextStyle(
                            color: isSelected ? Colors.white : const Color(0xFF0C0C0E),
                            fontWeight: FontWeight.bold,
                            fontSize: 13,
                          ),
                        ),
                      ),
                    );
                  },
                ),
              ),
              const SizedBox(height: 16),

              // Routine Timeline list
              _buildTimelineList(accentRed),

              const SizedBox(height: 32),

              // ==========================================
              // SECTION 2: Attendance Calculator
              // ==========================================
              const Text(
                "Attendance Tracker",
                style: TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.w800,
                  color: Color(0xFF0C0C0E),
                ),
              ),
              const SizedBox(height: 4),
              const Text(
                "Instantly calculate target schedules and prediction metrics.",
                style: TextStyle(fontSize: 12, color: Color(0xFF8E8E93)),
              ),
              const SizedBox(height: 16),

              // Grid View of AttendanceSubjectCard
              GridView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 2,
                  crossAxisSpacing: 14,
                  mainAxisSpacing: 14,
                  childAspectRatio: 0.82,
                ),
                itemCount: _attendanceSubjects.length,
                itemBuilder: (context, index) {
                  final subject = _attendanceSubjects[index];
                  final attended = subject["attended"] as int;
                  final total = subject["total"] as int;
                  final double percentage = total > 0 ? (attended / total) : 0.0;
                  final predictionText = _getAttendancePrediction(attended, total);

                  return AttendanceSubjectCard(
                    subjectName: subject["name"],
                    percentage: percentage,
                    prediction: predictionText,
                    onAttended: () => _adjustAttendance(index, true),
                    onMissed: () => _adjustAttendance(index, false),
                  );
                },
              ),
              const SizedBox(height: 100), // Bottom padding
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildTimelineList(Color accentRed) {
    final classes = _routineData[_selectedDay] ?? [];
    if (classes.isEmpty) {
      return Container(
        width: double.infinity,
        padding: const EdgeInsets.symmetric(vertical: 40),
        decoration: BoxDecoration(
          color: const Color(0xFFF2F2F7),
          borderRadius: BorderRadius.circular(24),
        ),
        child: const Column(
          children: [
            Icon(Icons.event_busy_outlined, color: Color(0xFF8E8E93), size: 36),
            SizedBox(height: 12),
            Text(
              "No classes scheduled",
              style: TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF0C0C0E)),
            ),
            SizedBox(height: 4),
            Text(
              "Use this day to complete pending assignments.",
              style: TextStyle(fontSize: 12, color: Color(0xFF8E8E93)),
            ),
          ],
        ),
      );
    }

    return Column(
      children: List.generate(classes.length, (index) {
        final item = classes[index];
        final isLast = index == classes.length - 1;
        final isAlarmActive = item["alarm"] == "active";

        return Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Left block: Time Label
            SizedBox(
              width: 90,
              child: Padding(
                padding: const EdgeInsets.only(top: 8.0),
                child: Text(
                  item["time"]!.split(" - ").join("\n"),
                  style: const TextStyle(
                    fontSize: 11,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF8E8E93),
                    height: 1.4,
                  ),
                  textAlign: TextAlign.right,
                ),
              ),
            ),

            // Middle block: Timeline indicator and vertical line
            const SizedBox(width: 14),
            Column(
              children: [
                // Circle timeline dot
                Container(
                  width: 14,
                  height: 14,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: isAlarmActive ? accentRed : const Color(0xFF8E8E93).withOpacity(0.5),
                    border: Border.all(color: Colors.white, width: 2),
                  ),
                ),
                // Connecting line
                if (!isLast)
                  Container(
                    width: 2,
                    height: 90,
                    color: const Color(0xFFE5E5EA),
                  ),
              ],
            ),
            const SizedBox(width: 14),

            // Right block: Class details card
            Expanded(
              child: Container(
                margin: const EdgeInsets.only(bottom: 16),
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: const Color(0xFFF2F2F7),
                  borderRadius: BorderRadius.circular(24),
                  border: Border.all(color: Colors.white, width: 1),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Expanded(
                          child: Text(
                            item["subject"]!,
                            style: const TextStyle(
                              fontSize: 14,
                              fontWeight: FontWeight.bold,
                              color: Color(0xFF0C0C0E),
                            ),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                        Icon(
                          isAlarmActive ? Icons.notifications_active : Icons.notifications_off_outlined,
                          size: 16,
                          color: isAlarmActive ? accentRed : const Color(0xFF8E8E93),
                        ),
                      ],
                    ),
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                          decoration: BoxDecoration(
                            color: Colors.white,
                            borderRadius: BorderRadius.circular(8),
                            border: Border.all(color: const Color(0xFFE5E5EA)),
                          ),
                          child: Text(
                            item["room"]!,
                            style: const TextStyle(
                              fontSize: 10,
                              fontWeight: FontWeight.bold,
                              color: Color(0xFF8E8E93),
                            ),
                          ),
                        ),
                        const SizedBox(width: 8),
                        Text(
                          "Lecturer: ${item["teacher"]}",
                          style: const TextStyle(fontSize: 11, color: Color(0xFF8E8E93)),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
        );
      }),
    );
  }
}

/// Custom Grid item card for academic attendance tracking.
class AttendanceSubjectCard extends StatelessWidget {
  final String subjectName;
  final double percentage;
  final String prediction;
  final VoidCallback onAttended;
  final VoidCallback onMissed;

  const AttendanceSubjectCard({
    Key? key,
    required this.subjectName,
    required this.percentage,
    required this.prediction,
    required this.onAttended,
    required this.onMissed,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    const Color accentRed = Color(0xFFE94560);
    final String cleanTitle = subjectName.split(":").first;

    return Container(
      decoration: BoxDecoration(
        color: const Color(0xFFF2F2F7),
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: Colors.white, width: 1),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.01),
            blurRadius: 10,
            offset: const Offset(0, 4),
          )
        ],
      ),
      padding: const EdgeInsets.all(12),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          // 1. Subject Name
          Text(
            cleanTitle,
            style: const TextStyle(
              fontSize: 13,
              fontWeight: FontWeight.bold,
              color: Color(0xFF0C0C0E),
            ),
            textAlign: TextAlign.center,
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
          ),

          // 2. Circular percentage indicator
          SizedBox(
            width: 52,
            height: 52,
            child: Stack(
              alignment: Alignment.center,
              children: [
                CircularProgressIndicator(
                  value: percentage,
                  strokeWidth: 4.5,
                  backgroundColor: Colors.white,
                  valueColor: AlwaysStoppedAnimation<Color>(percentage >= 0.90 ? Colors.green : accentRed),
                ),
                Text(
                  "${(percentage * 100).toStringAsFixed(0)}%",
                  style: const TextStyle(
                    fontSize: 11,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF0C0C0E),
                  ),
                ),
              ],
            ),
          ),

          // 3. Increment/Decrement Pills
          Row(
            children: [
              Expanded(
                child: GestureDetector(
                  onTap: onAttended,
                  child: Container(
                    padding: const EdgeInsets.symmetric(vertical: 6),
                    decoration: BoxDecoration(
                      color: accentRed,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    alignment: Alignment.Center,
                    child: const Text(
                      "+ Attended",
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 10,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ),
              ),
              const SizedBox(width: 6),
              Expanded(
                child: GestureDetector(
                  onTap: onMissed,
                  child: Container(
                    padding: const EdgeInsets.symmetric(vertical: 6),
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(color: const Color(0xFFE5E5EA)),
                    ),
                    alignment: Alignment.Center,
                    child: const Text(
                      "- Missed",
                      style: TextStyle(
                        color: Color(0xFF8E8E93),
                        fontSize: 10,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ),
              ),
            ],
          ),

          // 4. Smart Prediction Text at the bottom
          Text(
            prediction,
            style: const TextStyle(
              fontSize: 9,
              color: Color(0xFF8E8E93),
              fontWeight: FontWeight.bold,
            ),
            textAlign: TextAlign.center,
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
          ),
        ],
      ),
    );
  }
}
