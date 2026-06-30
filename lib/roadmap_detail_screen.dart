// lib/roadmap_detail_screen.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'roadmap_model.dart';
import 'roadmap_provider.dart';

class RoadmapDetailScreen extends StatefulWidget {
  final RoadmapModel roadmap;
  const RoadmapDetailScreen({Key? key, required this.roadmap}) : super(key: key);

  @override
  State<RoadmapDetailScreen> createState() => _RoadmapDetailScreenState();
}

class _RoadmapDetailScreenState extends State<RoadmapDetailScreen> {
  final TextEditingController _subTaskController = TextEditingController();

  @override
  void dispose() {
    _subTaskController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8F9FA),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        iconTheme: const IconThemeData(color: Color(0xFF0C0C0E)),
        title: Text(
          widget.roadmap.title,
          style: const TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF0C0C0E)),
        ),
      ),
      body: Consumer<RoadmapProvider>(
        builder: (context, provider, child) {
          final currentRoadmap = provider.roadmaps.firstWhere(
            (r) => r.id == widget.roadmap.id,
            orElse: () => widget.roadmap,
          );

          return Padding(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Description Header Card
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.all(20),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(24),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.black.withOpacity(0.02),
                        blurRadius: 16,
                        offset: const Offset(0, 8),
                      )
                    ],
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        "GOAL OBJECTIVE",
                        style: TextStyle(
                          fontSize: 11,
                          fontWeight: FontWeight.bold,
                          letterSpacing: 1.2,
                          color: Color(0xFF8E8E93),
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        currentRoadmap.description,
                        style: const TextStyle(
                          fontSize: 14,
                          color: Color(0xFF0C0C0E),
                          height: 1.4,
                        ),
                      ),
                      const SizedBox(height: 24),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          const Text(
                            "Completion Progress",
                            style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: Color(0xFF0C0C0E)),
                          ),
                          Text(
                            "${currentRoadmap.progress.toStringAsFixed(1)}%",
                            style: const TextStyle(
                              fontWeight: FontWeight.bold,
                              fontSize: 13,
                              color: Color(0xFFE94560),
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 10),
                      // Premium Animated Progress Bar
                      TweenAnimationBuilder<double>(
                        tween: Tween<double>(begin: 0.0, end: currentRoadmap.progress / 100.0),
                        duration: const Duration(milliseconds: 600),
                        curve: Curves.easeOutCubic,
                        builder: (context, value, child) {
                          return ClipRRect(
                            borderRadius: BorderRadius.circular(10),
                            child: LinearProgressIndicator(
                              value: value,
                              minHeight: 10,
                              backgroundColor: const Color(0xFFF2F2F7),
                              valueColor: const AlwaysStoppedAnimation<Color>(Color(0xFFE94560)),
                            ),
                          );
                        },
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 28),

                // Add Subtask Header
                const Text(
                  "MILESTONE SUB-TASKS",
                  style: TextStyle(
                    fontSize: 12,
                    fontWeight: FontWeight.bold,
                    letterSpacing: 1.5,
                    color: Color(0xFF8E8E93),
                  ),
                ),
                const SizedBox(height: 12),

                // Text field input for adding new sub-tasks
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(20),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.black.withOpacity(0.02),
                        blurRadius: 16,
                        offset: const Offset(0, 8),
                      )
                    ],
                  ),
                  child: Row(
                    children: [
                      Expanded(
                        child: TextField(
                          controller: _subTaskController,
                          decoration: const InputDecoration(
                            hintText: "Add new subtask milestone...",
                            border: InputBorder.none,
                          ),
                        ),
                      ),
                      IconButton(
                        icon: const Icon(Icons.add_circle, color: Color(0xFFE94560), size: 28),
                        onPressed: () {
                          final text = _subTaskController.text.trim();
                          if (text.isNotEmpty) {
                            provider.addSubTask(currentRoadmap, text);
                            _subTaskController.clear();
                          }
                        },
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 16),

                // Subtasks checklist list
                Expanded(
                  child: Container(
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(24),
                      boxShadow: [
                        BoxShadow(
                          color: Colors.black.withOpacity(0.02),
                          blurRadius: 16,
                          offset: const Offset(0, 8),
                        )
                      ],
                    ),
                    child: currentRoadmap.subTasks.isEmpty
                        ? const Center(
                            child: Text(
                              "No subtasks added yet.",
                              style: TextStyle(color: Colors.grey),
                            ),
                          )
                        : ListView.separated(
                            padding: const EdgeInsets.symmetric(vertical: 12),
                            itemCount: currentRoadmap.subTasks.length,
                            separatorBuilder: (context, index) => const Divider(height: 1, indent: 24, endIndent: 24),
                            itemBuilder: (context, index) {
                              final sub = currentRoadmap.subTasks[index];

                              return CheckboxListTile(
                                value: sub.isCompleted,
                                activeColor: const Color(0xFFE94560),
                                checkboxShape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(6)),
                                title: Text(
                                  sub.title,
                                  style: TextStyle(
                                    fontSize: 14,
                                    fontWeight: FontWeight.w600,
                                    decoration: sub.isCompleted ? TextDecoration.lineThrough : null,
                                    color: sub.isCompleted ? Colors.grey : const Color(0xFF0C0C0E),
                                  ),
                                ),
                                onChanged: (val) {
                                  provider.toggleSubTask(currentRoadmap, sub);
                                },
                              );
                            },
                          ),
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}
