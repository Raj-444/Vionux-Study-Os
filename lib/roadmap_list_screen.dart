// lib/roadmap_list_screen.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'roadmap_model.dart';
import 'roadmap_provider.dart';
import 'roadmap_detail_screen.dart';

class RoadmapListScreen extends StatelessWidget {
  const RoadmapListScreen({Key? key}) : super(key: key);

  void _showAddRoadmapDialog(BuildContext context) {
    final titleController = TextEditingController();
    final descController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          backgroundColor: const Color(0xFFF8F9FA),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
          title: const Text("New Goal Roadmap", style: TextStyle(fontWeight: FontWeight.bold)),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: titleController,
                decoration: InputDecoration(
                  labelText: "Goal Title",
                  filled: true,
                  fillColor: Colors.white,
                  border: OutlineInputBorder(borderRadius: BorderRadius.circular(16), borderSide: BorderSide.none),
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: descController,
                decoration: InputDecoration(
                  labelText: "Description",
                  filled: true,
                  fillColor: Colors.white,
                  border: OutlineInputBorder(borderRadius: BorderRadius.circular(16), borderSide: BorderSide.none),
                ),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text("Cancel", style: TextStyle(color: Colors.grey)),
            ),
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFFE94560),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
              onPressed: () {
                final title = titleController.text.trim();
                final desc = descController.text.trim();
                if (title.isNotEmpty) {
                  Provider.of<RoadmapProvider>(context, listen: false).addRoadmap(title, desc);
                  Navigator.pop(context);
                }
              },
              child: const Text("Create", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8F9FA),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        iconTheme: const IconThemeData(color: Color(0xFF0C0C0E)),
        title: const Text(
          "Goal Roadmaps",
          style: TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF0C0C0E)),
        ),
        centerTitle: true,
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _showAddRoadmapDialog(context),
        backgroundColor: const Color(0xFFE94560),
        shape: const CircleBorder(),
        child: const Icon(Icons.add, color: Colors.white),
      ),
      body: Consumer<RoadmapProvider>(
        builder: (context, provider, child) {
          final roadmaps = provider.roadmaps;
          if (roadmaps.isEmpty) {
            return const Center(
              child: Text("No goal roadmaps defined.", style: TextStyle(color: Colors.grey)),
            );
          }

          return ListView.builder(
            padding: const EdgeInsets.all(20),
            itemCount: roadmaps.length,
            itemBuilder: (context, index) {
              final rm = roadmaps[index];

              return Card(
                color: Colors.white,
                elevation: 0,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
                margin: const EdgeInsets.symmetric(vertical: 8),
                child: InkWell(
                  borderRadius: BorderRadius.circular(24),
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (context) => RoadmapDetailScreen(roadmap: rm)),
                    );
                  },
                  child: Padding(
                    padding: const EdgeInsets.all(20.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Expanded(
                              child: Text(
                                rm.title,
                                style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                                overflow: TextOverflow.ellipsis,
                              ),
                            ),
                            Text(
                              "${rm.progress.toStringAsFixed(0)}%",
                              style: const TextStyle(fontWeight: FontWeight.bold, color: Color(0xFFE94560)),
                            ),
                          ],
                        ),
                        const SizedBox(height: 8),
                        Text(
                          rm.description,
                          style: const TextStyle(color: Colors.grey, fontSize: 13),
                          maxLines: 2,
                          overflow: TextOverflow.ellipsis,
                        ),
                        const SizedBox(height: 16),
                        ClipRRect(
                          borderRadius: BorderRadius.circular(8),
                          child: LinearProgressIndicator(
                            value: rm.progress / 100.0,
                            minHeight: 6,
                            backgroundColor: const Color(0xFFF2F2F7),
                            valueColor: const AlwaysStoppedAnimation<Color>(Color(0xFFE94560)),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              );
            },
          );
        },
      ),
    );
  }
}
