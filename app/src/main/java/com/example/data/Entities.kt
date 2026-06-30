package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "roadmaps")
data class Roadmap(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val progress: Float = 0f,
    val deadline: String = "",
    val subtasks: String = ""
)

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val time: String,
    val label: String,
    val isEnabled: Boolean = true,
    val repeatDays: String = "Once"
)

@Entity(tableName = "routine_items")
data class RoutineItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val day: String,
    val startTime: String,
    val endTime: String,
    val subject: String,
    val room: String,
    val faculty: String,
    val isLab: Boolean = false
)

@Entity(tableName = "attendance_subjects")
data class AttendanceSubject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val attended: Int = 0,
    val total: Int = 0
)

@Dao
interface RoadmapDao {
    @Query("SELECT * FROM roadmaps ORDER BY id DESC")
    fun getAllRoadmaps(): Flow<List<Roadmap>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoadmap(roadmap: Roadmap)

    @Update
    suspend fun updateRoadmap(roadmap: Roadmap)

    @Delete
    suspend fun deleteRoadmap(roadmap: Roadmap)
}

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms ORDER BY time ASC")
    fun getAllAlarms(): Flow<List<Alarm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: Alarm)

    @Update
    suspend fun updateAlarm(alarm: Alarm)

    @Delete
    suspend fun deleteAlarm(alarm: Alarm)
}

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine_items ORDER BY startTime ASC")
    fun getAllRoutineItems(): Flow<List<RoutineItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(item: RoutineItem)

    @Update
    suspend fun updateRoutine(item: RoutineItem)

    @Delete
    suspend fun deleteRoutine(item: RoutineItem)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<AttendanceSubject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: AttendanceSubject)

    @Update
    suspend fun updateSubject(subject: AttendanceSubject)

    @Delete
    suspend fun deleteSubject(subject: AttendanceSubject)
}

@Entity(tableName = "planner_events")
data class PlannerEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val time: String,
    val location: String
)

@Entity(tableName = "exam_schedules")
data class ExamSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val type: String,
    val time: String
)

@Entity(tableName = "counters")
data class Counter(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val value: Int = 0
)

@Dao
interface PlannerDao {
    @Query("SELECT * FROM planner_events ORDER BY time ASC")
    fun getAllEvents(): Flow<List<PlannerEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: PlannerEvent)

    @Delete
    suspend fun deleteEvent(event: PlannerEvent)

    @Query("SELECT * FROM exam_schedules ORDER BY time ASC")
    fun getAllExams(): Flow<List<ExamSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamSchedule)

    @Delete
    suspend fun deleteExam(exam: ExamSchedule)
}

@Dao
interface CounterDao {
    @Query("SELECT * FROM counters ORDER BY id DESC")
    fun getAllCounters(): Flow<List<Counter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCounter(counter: Counter)

    @Update
    suspend fun updateCounter(counter: Counter)

    @Delete
    suspend fun deleteCounter(counter: Counter)
}

@Entity(tableName = "health_logs")
data class HealthLog(
    @PrimaryKey val date: String, // Format: YYYY-MM-DD
    val waterGlasses: Int = 0,
    val workoutCompleted: Boolean = false
)

@Dao
interface HealthDao {
    @Query("SELECT * FROM health_logs WHERE date = :date")
    fun getLogForDate(date: String): Flow<HealthLog?>

    @Query("SELECT * FROM health_logs ORDER BY date DESC LIMIT 7")
    fun getRecentLogs(): Flow<List<HealthLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HealthLog)
}
