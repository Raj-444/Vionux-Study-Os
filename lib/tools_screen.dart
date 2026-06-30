// lib/tools_screen.dart

import 'package:flutter/material.dart';

/// A premium, minimalist 'ToolsScreen' for the VioNux app suite.
/// Implements Keep-Style Notes with checklists, and Health Tracker (Hydration & Workout).
/// Adheres strictly to the off-white premium background and highly rounded card design language (Radius 24-32).
class ToolsScreen extends StatefulWidget {
  const ToolsScreen({Key? key}) : super(key: key);

  @override
  State<ToolsScreen> createState() => _ToolsScreenState();
}

class _ToolsScreenState extends State<ToolsScreen> {
  // 1. Notes State
  String _searchQuery = "";
  String _selectedLabel = "All";
  final List<String> _labels = ["All", "Work", "Personal", "Ideas", "Shopping"];

  final List<Map<String, dynamic>> _notes = [
    {
      "id": 1,
      "title": "Semester Project Deliverables",
      "content": "Make sure to coordinate the database design with the frontend team.",
      "label": "Work",
      "isPinned": true,
      "color": const Color(0xFFFFF3CD), // Light yellow
      "checklist": [
        {"text": "Schema design finalized", "isDone": true},
        {"text": "API specifications document", "isDone": false},
        {"text": "Setup Dev environment", "isDone": false},
      ]
    },
    {
      "id": 2,
      "title": "Startup Pitch Deck Core Ideas",
      "content": "Focus on developer productivity tooling, automated UI validation, and real-time remote emulator stream.",
      "label": "Ideas",
      "isPinned": true,
      "color": const Color(0xFFD1E7DD), // Light green
      "checklist": []
    },
    {
      "id": 3,
      "title": "Weekly Groceries",
      "content": "Organic greens and healthy grains.",
      "label": "Shopping",
      "isPinned": false,
      "color": const Color(0xFFF8D7DA), // Light red
      "checklist": [
        {"text": "Almond milk", "isDone": true},
        {"text": "Whole wheat bread", "isDone": true},
        {"text": "Spinach & Kale mix", "isDone": false},
      ]
    },
    {
      "id": 4,
      "title": "Self Reflection Journal",
      "content": "Spent 45 minutes on focused breathing today. Productivity peaks when digital notifications are entirely muted.",
      "label": "Personal",
      "isPinned": false,
      "color": const Color(0xFFCFE2FF), // Light blue
      "checklist": []
    }
  ];

  // 2. Health Tracker State
  int _waterGlasses = 4;
  final int _targetWaterGlasses = 8;
  final List<Map<String, dynamic>> _workoutStreak = [
    {"day": "M", "completed": true},
    {"day": "T", "completed": true},
    {"day": "W", "completed": false},
    {"day": "T", "completed": true},
    {"day": "F", "completed": true},
    {"day": "S", "completed": false},
    {"day": "S", "completed": false},
  ];

  void _addWater() {
    setState(() {
      if (_waterGlasses < _targetWaterGlasses) {
        _waterGlasses++;
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Amazing! Daily hydration goal reached! 💧")),
        );
      }
    });
  }

  void _toggleChecklistItem(int noteId, int checkIndex) {
    setState(() {
      final note = _notes.firstWhere((n) => n["id"] == noteId);
      final checklist = note["checklist"] as List<Map<String, dynamic>>;
      checklist[checkIndex]["isDone"] = !checklist[checkIndex]["isDone"];
    });
  }

  void _addNewNote() {
    showDialog(
      context: context,
      builder: (context) {
        String title = "";
        String content = "";
        String label = "Work";
        Color chosenColor = const Color(0xFFFFF3CD);

        return AlertDialog(
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
          backgroundColor: const Color(0xFFF8F9FA),
          title: const Text('Add Quick Note', style: TextStyle(fontWeight: FontWeight.bold)),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                decoration: const InputDecoration(labelText: 'Title'),
                onChanged: (val) => title = val,
              ),
              const SizedBox(height: 8),
              TextField(
                decoration: const InputDecoration(labelText: 'Content'),
                onChanged: (val) => content = val,
              ),
              const SizedBox(height: 12),
              DropdownButtonFormField<String>(
                value: label,
                items: ["Work", "Personal", "Ideas", "Shopping"]
                    .map((l) => DropdownMenuItem(value: l, child: Text(l)))
                    .toList(),
                onChanged: (val) => label = val ?? "Work",
                decoration: const InputDecoration(labelText: 'Label'),
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
                    _notes.insert(0, {
                      "id": DateTime.now().millisecondsSinceEpoch,
                      "title": title,
                      "content": content,
                      "label": label,
                      "isPinned": false,
                      "color": const Color(0xFFF2F2F7),
                      "checklist": []
                    });
                  });
                }
                Navigator.pop(context);
              },
              child: const Text('Create', style: TextStyle(color: Colors.white)),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    const Color accentRed = Color(0xFFE94560);

    // Filtered Notes
    final filteredNotes = _notes.where((note) {
      final matchesSearch = note["title"].toLowerCase().contains(_searchQuery.toLowerCase()) ||
          note["content"].toLowerCase().contains(_searchQuery.toLowerCase());
      final matchesLabel = _selectedLabel == "All" || note["label"] == _selectedLabel;
      return matchesSearch && matchesLabel;
    }).toList();

    return Scaffold(
      backgroundColor: const Color(0xFFF8F9FA), // premium off-white background
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Screen Header
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        "Workspace Tools",
                        style: TextStyle(
                          fontSize: 28,
                          fontWeight: FontWeight.bold,
                          color: Color(0xFF0C0C0E),
                          letterSpacing: -0.5,
                        ),
                      ),
                      SizedBox(height: 4),
                      Text(
                        "Keep quick thoughts and track core vitals.",
                        style: TextStyle(fontSize: 14, color: Color(0xFF8E8E93)),
                      ),
                    ],
                  ),
                  GestureDetector(
                    onTap: _addNewNote,
                    child: Container(
                      height: 48,
                      width: 48,
                      decoration: BoxDecoration(
                        color: accentRed,
                        shape: BoxShape.circle,
                        boxShadow: [
                          BoxShadow(
                            color: accentRed.withOpacity(0.3),
                            blurRadius: 12,
                            offset: const Offset(0, 4),
                          )
                        ],
                      ),
                      child: const Icon(Icons.add, color: Colors.white),
                    ),
                  )
                ],
              ),
              const SizedBox(height: 24),

              // ==========================================
              // SECTION 1: Keep-Style Notes Section
              // ==========================================
              const Text(
                "Quick Notes",
                style: TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.w800,
                  color: Color(0xFF0C0C0E),
                ),
              ),
              const SizedBox(height: 12),

              // Search Bar
              Container(
                decoration: BoxDecoration(
                  color: const Color(0xFFF2F2F7),
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(color: Colors.white, width: 1),
                ),
                child: TextField(
                  onChanged: (val) {
                    setState(() {
                      _searchQuery = val;
                    });
                  },
                  decoration: const InputDecoration(
                    prefixIcon: Icon(Icons.search, color: Color(0xFF8E8E93)),
                    hintText: "Search notes...",
                    border: InputBorder.none,
                    contentPadding: EdgeInsets.symmetric(vertical: 14),
                  ),
                ),
              ),
              const SizedBox(height: 12),

              // Filter Label Chips
              SizedBox(
                height: 38,
                child: ListView.builder(
                  scrollDirection: Axis.horizontal,
                  itemCount: _labels.length,
                  itemBuilder: (context, index) {
                    final label = _labels[index];
                    final isSelected = label == _selectedLabel;
                    return GestureDetector(
                      onTap: () {
                        setState(() {
                          _selectedLabel = label;
                        });
                      },
                      child: Container(
                        margin: const EdgeInsets.only(right: 8),
                        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                        decoration: BoxDecoration(
                          color: isSelected ? const Color(0xFF0C0C0E) : Colors.white,
                          borderRadius: BorderRadius.circular(20),
                          border: Border.all(
                            color: isSelected ? const Color(0xFF0C0C0E) : const Color(0xFFE5E5EA),
                            width: 1,
                          ),
                        ),
                        child: Text(
                          label,
                          style: TextStyle(
                            color: isSelected ? Colors.white : const Color(0xFF8E8E93),
                            fontWeight: FontWeight.bold,
                            fontSize: 12,
                          ),
                        ),
                      ),
                    );
                  },
                ),
              ),
              const SizedBox(height: 16),

              // Staggered Note Grid (2 columns)
              GridView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 2,
                  crossAxisSpacing: 14,
                  mainAxisSpacing: 14,
                  childAspectRatio: 0.85,
                ),
                itemCount: filteredNotes.length,
                itemBuilder: (context, index) {
                  final note = filteredNotes[index];
                  return NoteCard(
                    title: note["title"],
                    content: note["content"],
                    label: note["label"],
                    isPinned: note["isPinned"],
                    bgColor: note["color"],
                    checklist: note["checklist"],
                    onChecklistToggle: (chkIndex) => _toggleChecklistItem(note["id"], chkIndex),
                  );
                },
              ),

              const SizedBox(height: 32),

              // ==========================================
              // SECTION 2: Health Tracker (Hydration & Workout)
              // ==========================================
              const Text(
                "Vitals Tracker",
                style: TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.w800,
                  color: Color(0xFF0C0C0E),
                ),
              ),
              const SizedBox(height: 16),

              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // 1. Hydration Card
                  Expanded(
                    child: Container(
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(
                        color: const Color(0xFFE3F2FD), // Subtle hydration blue
                        borderRadius: BorderRadius.circular(24),
                        border: Border.all(color: Colors.white, width: 1.5),
                        boxShadow: [
                          BoxShadow(
                            color: Colors.blue.withOpacity(0.05),
                            blurRadius: 10,
                            offset: const Offset(0, 4),
                          )
                        ],
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              const Icon(Icons.local_drink_rounded, color: Colors.blueAccent, size: 28),
                              GestureDetector(
                                onTap: _addWater,
                                child: Container(
                                  padding: const EdgeInsets.all(6),
                                  decoration: const BoxDecoration(
                                    color: Colors.blueAccent,
                                    shape: BoxShape.circle,
                                  ),
                                  child: const Icon(Icons.add, color: Colors.white, size: 16),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 16),
                          const Text(
                            "Hydration Tracker",
                            style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: Colors.blue),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            "Daily Progress: $_waterGlasses / $_targetWaterGlasses Cups",
                            style: const TextStyle(fontSize: 11, color: Colors.blueGrey, fontWeight: FontWeight.bold),
                          ),
                          const SizedBox(height: 12),
                          // Beautiful glass level placeholder indicator
                          Container(
                            height: 24,
                            width: double.infinity,
                            decoration: BoxDecoration(
                              color: Colors.white.withOpacity(0.6),
                              borderRadius: BorderRadius.circular(8),
                            ),
                            child: LayoutBuilder(builder: (context, constraints) {
                              double currentRatio = _waterGlasses / _targetWaterGlasses;
                              return FractionallySizedBox(
                                widthFactor: currentRatio,
                                alignment: Alignment.centerLeft,
                                child: Container(
                                  decoration: BoxDecoration(
                                    color: Colors.blueAccent.withOpacity(0.6),
                                    borderRadius: BorderRadius.circular(8),
                                  ),
                                ),
                              );
                            }),
                          )
                        ],
                      ),
                    ),
                  ),

                  const SizedBox(width: 14),

                  // 2. Workout Streak Card
                  Expanded(
                    child: Container(
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(
                        color: const Color(0xFFF2F2F7),
                        borderRadius: BorderRadius.circular(24),
                        border: Border.all(color: Colors.white, width: 1.5),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Icon(Icons.bolt, color: Colors.orange, size: 28),
                          const SizedBox(height: 16),
                          const Text(
                            "Workout Streak",
                            style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: Color(0xFF0C0C0E)),
                          ),
                          const SizedBox(height: 4),
                          const Text(
                            "Current Streak: 4 Days",
                            style: TextStyle(fontSize: 11, color: Color(0xFF8E8E93), fontWeight: FontWeight.bold),
                          ),
                          const SizedBox(height: 14),
                          // 7-day circular mini metrics indicators
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: _workoutStreak.map((w) {
                              final bool done = w["completed"];
                              return Column(
                                children: [
                                  Container(
                                    height: 16,
                                    width: 16,
                                    decoration: BoxDecoration(
                                      shape: BoxShape.circle,
                                      color: done ? Colors.green : Colors.white,
                                      border: Border.all(color: Colors.green.withOpacity(0.4)),
                                    ),
                                    child: done
                                        ? const Icon(Icons.check, size: 10, color: Colors.white)
                                        : null,
                                  ),
                                  const SizedBox(height: 4),
                                  Text(
                                    w["day"],
                                    style: const TextStyle(fontSize: 9, color: Color(0xFF8E8E93), fontWeight: FontWeight.bold),
                                  )
                                ],
                              );
                            }).toList(),
                          )
                        ],
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 100), // Bottom navigation gap
            ],
          ),
        ),
      ),
    );
  }
}

/// A highly reusable custom card representing Google Keep-style notes
class NoteCard extends StatelessWidget {
  final String title;
  final String content;
  final String label;
  final bool isPinned;
  final Color bgColor;
  final List<dynamic> checklist;
  final Function(int) onChecklistToggle;

  const NoteCard({
    Key? key,
    required this.title,
    required this.content,
    required this.label,
    required this.isPinned,
    required this.bgColor,
    required this.checklist,
    required this.onChecklistToggle,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: bgColor,
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: Colors.white.withOpacity(0.6), width: 1.5),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.01),
            blurRadius: 8,
            offset: const Offset(0, 4),
          )
        ],
      ),
      padding: const EdgeInsets.all(14),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header of note card
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.6),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Text(
                  label,
                  style: const TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: Color(0xFF0C0C0E)),
                ),
              ),
              if (isPinned)
                const Icon(Icons.push_pin, size: 14, color: Color(0xFF0C0C0E))
            ],
          ),
          const SizedBox(height: 8),

          // Title
          Text(
            title,
            style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: Color(0xFF0C0C0E)),
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
          ),
          const SizedBox(height: 4),

          // Text content
          Text(
            content,
            style: const TextStyle(fontSize: 11, color: Color(0xFF555555), height: 1.3),
            maxLines: 3,
            overflow: TextOverflow.ellipsis,
          ),

          // Checklist subset rendering
          if (checklist.isNotEmpty) ...[
            const SizedBox(height: 8),
            Expanded(
              child: ListView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: checklist.length > 2 ? 2 : checklist.length,
                itemBuilder: (context, idx) {
                  final item = checklist[idx];
                  final isDone = item["isDone"] as bool;
                  return Row(
                    children: [
                      GestureDetector(
                        onTap: () => onChecklistToggle(idx),
                        child: Icon(
                          isDone ? Icons.check_box : Icons.check_box_outline_blank,
                          size: 14,
                          color: isDone ? Colors.black87 : Colors.black45,
                        ),
                      ),
                      const SizedBox(width: 4),
                      Expanded(
                        child: Text(
                          item["text"],
                          style: TextStyle(
                            fontSize: 10,
                            color: isDone ? Colors.grey : Colors.black87,
                            decoration: isDone ? TextDecoration.lineThrough : null,
                          ),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                      )
                    ],
                  );
                },
              ),
            ),
          ]
        ],
      ),
    );
  }
}
