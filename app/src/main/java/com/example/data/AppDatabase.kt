package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    Task::class,
    Transaction::class,
    Roadmap::class,
    Alarm::class,
    RoutineItem::class,
    AttendanceSubject::class,
    PlannerEvent::class,
    ExamSchedule::class,
    Counter::class,
    HealthLog::class
  ],
  version = 5,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun taskDao(): TaskDao
  abstract fun transactionDao(): TransactionDao
  abstract fun roadmapDao(): RoadmapDao
  abstract fun alarmDao(): AlarmDao
  abstract fun routineDao(): RoutineDao
  abstract fun attendanceDao(): AttendanceDao
  abstract fun plannerDao(): PlannerDao
  abstract fun counterDao(): CounterDao
  abstract fun healthDao(): HealthDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "focus_planner_database"
        )
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
