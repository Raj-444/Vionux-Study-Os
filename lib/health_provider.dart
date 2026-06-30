import 'package:flutter/material.dart';

class HealthProvider extends ChangeNotifier {
  // Map of weekday (1-7) to hydration (glasses)
  Map<int, int> _weeklyHydration = {1: 0, 2: 0, 3: 0, 4: 0, 5: 0, 6: 0, 7: 0};
  // Map of weekday (1-7) to workout completed
  Map<int, bool> _weeklyWorkouts = {1: false, 2: false, 3: false, 4: false, 5: false, 6: false, 7: false};

  Map<int, int> get weeklyHydration => _weeklyHydration;
  Map<int, bool> get weeklyWorkouts => _weeklyWorkouts;

  void addWater(int glasses) {
    int day = DateTime.now().weekday;
    _weeklyHydration[day] = (_weeklyHydration[day] ?? 0) + glasses;
    notifyListeners();
  }

  void logWorkout(bool completed) {
    int day = DateTime.now().weekday;
    _weeklyWorkouts[day] = completed;
    notifyListeners();
  }
}
