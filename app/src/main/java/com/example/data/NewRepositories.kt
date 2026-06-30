package com.example.data

import kotlinx.coroutines.flow.Flow

class GoalRepository(private val roadmapDao: RoadmapDao, private val counterDao: CounterDao) {
    val allRoadmaps: Flow<List<Roadmap>> = roadmapDao.getAllRoadmaps()
    val allCounters: Flow<List<Counter>> = counterDao.getAllCounters()

    suspend fun insertRoadmap(roadmap: Roadmap) {
        roadmapDao.insertRoadmap(roadmap)
    }

    suspend fun updateRoadmap(roadmap: Roadmap) {
        roadmapDao.updateRoadmap(roadmap)
    }

    suspend fun deleteRoadmap(roadmap: Roadmap) {
        roadmapDao.deleteRoadmap(roadmap)
    }

    suspend fun insertCounter(counter: Counter) {
        counterDao.insertCounter(counter)
    }

    suspend fun updateCounter(counter: Counter) {
        counterDao.updateCounter(counter)
    }

    suspend fun deleteCounter(counter: Counter) {
        counterDao.deleteCounter(counter)
    }
}

class AlarmRepository(private val alarmDao: AlarmDao) {
    val allAlarms: Flow<List<Alarm>> = alarmDao.getAllAlarms()

    suspend fun insertAlarm(alarm: Alarm) {
        alarmDao.insertAlarm(alarm)
    }

    suspend fun updateAlarm(alarm: Alarm) {
        alarmDao.updateAlarm(alarm)
    }

    suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm)
    }
}

class AcademicRepository(private val routineDao: RoutineDao, private val attendanceDao: AttendanceDao) {
    val allRoutineItems: Flow<List<RoutineItem>> = routineDao.getAllRoutineItems()
    val allAttendanceSubjects: Flow<List<AttendanceSubject>> = attendanceDao.getAllSubjects()

    suspend fun insertRoutine(item: RoutineItem) {
        routineDao.insertRoutine(item)
    }

    suspend fun updateRoutine(item: RoutineItem) {
        routineDao.updateRoutine(item)
    }

    suspend fun deleteRoutine(item: RoutineItem) {
        routineDao.deleteRoutine(item)
    }

    suspend fun insertSubject(subject: AttendanceSubject) {
        attendanceDao.insertSubject(subject)
    }

    suspend fun updateSubject(subject: AttendanceSubject) {
        attendanceDao.updateSubject(subject)
    }

    suspend fun deleteSubject(subject: AttendanceSubject) {
        attendanceDao.deleteSubject(subject)
    }
}

class PlannerRepository(private val plannerDao: PlannerDao) {
    val allEvents: Flow<List<PlannerEvent>> = plannerDao.getAllEvents()
    val allExams: Flow<List<ExamSchedule>> = plannerDao.getAllExams()

    suspend fun insertEvent(event: PlannerEvent) {
        plannerDao.insertEvent(event)
    }

    suspend fun deleteEvent(event: PlannerEvent) {
        plannerDao.deleteEvent(event)
    }

    suspend fun insertExam(exam: ExamSchedule) {
        plannerDao.insertExam(exam)
    }

    suspend fun deleteExam(exam: ExamSchedule) {
        plannerDao.deleteExam(exam)
    }
}

class CounterRepository(private val counterDao: CounterDao) {
    val allCounters: Flow<List<Counter>> = counterDao.getAllCounters()

    suspend fun insertCounter(counter: Counter) {
        counterDao.insertCounter(counter)
    }

    suspend fun updateCounter(counter: Counter) {
        counterDao.updateCounter(counter)
    }

    suspend fun deleteCounter(counter: Counter) {
        counterDao.deleteCounter(counter)
    }
}
