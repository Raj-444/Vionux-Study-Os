package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class GoalViewModel(private val repository: GoalRepository) : ViewModel() {
    val roadmaps: StateFlow<List<Roadmap>> = repository.allRoadmaps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addRoadmap(title: String, deadline: String, subtasks: String) {
        viewModelScope.launch {
            repository.insertRoadmap(Roadmap(title = title, deadline = deadline, subtasks = subtasks))
        }
    }

    fun updateRoadmap(roadmap: Roadmap) {
        viewModelScope.launch {
            repository.updateRoadmap(roadmap)
        }
    }

    fun deleteRoadmap(roadmap: Roadmap) {
        viewModelScope.launch {
            repository.deleteRoadmap(roadmap)
        }
    }

    // Counter Management
    val counters: StateFlow<List<Counter>> = repository.allCounters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCounter(title: String) {
        viewModelScope.launch {
            repository.insertCounter(Counter(title = title))
        }
    }

    fun updateCounter(counter: Counter, newValue: Int) {
        viewModelScope.launch {
            repository.updateCounter(counter.copy(value = newValue))
        }
    }

    fun deleteCounter(counter: Counter) {
        viewModelScope.launch {
            repository.deleteCounter(counter)
        }
    }
}

class AlarmViewModel(private val repository: AlarmRepository) : ViewModel() {
    val alarms: StateFlow<List<Alarm>> = repository.allAlarms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addAlarm(time: String, label: String, repeatDays: String) {
        viewModelScope.launch {
            repository.insertAlarm(Alarm(time = time, label = label, repeatDays = repeatDays))
        }
    }

    fun toggleAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.updateAlarm(alarm.copy(isEnabled = !alarm.isEnabled))
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.deleteAlarm(alarm)
        }
    }
}

class AcademicViewModel(private val repository: AcademicRepository) : ViewModel() {
    val routineItems: StateFlow<List<RoutineItem>> = repository.allRoutineItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendanceSubjects: StateFlow<List<AttendanceSubject>> = repository.allAttendanceSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addRoutineItem(day: String, startTime: String, endTime: String, subject: String, room: String, faculty: String, isLab: Boolean) {
        viewModelScope.launch {
            repository.insertRoutine(RoutineItem(day = day, startTime = startTime, endTime = endTime, subject = subject, room = room, faculty = faculty, isLab = isLab))
        }
    }

    fun deleteRoutine(item: RoutineItem) {
        viewModelScope.launch {
            repository.deleteRoutine(item)
        }
    }

    fun addSubject(name: String) {
        viewModelScope.launch {
            repository.insertSubject(AttendanceSubject(name = name))
        }
    }

    fun updateAttendance(subject: AttendanceSubject, attendedInc: Int, totalInc: Int) {
        viewModelScope.launch {
            repository.updateSubject(subject.copy(
                attended = (subject.attended + attendedInc).coerceAtLeast(0),
                total = (subject.total + totalInc).coerceAtLeast(0)
            ))
        }
    }

    fun deleteSubject(subject: AttendanceSubject) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }
}

class AcademicViewModelFactory(private val repository: AcademicRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AcademicViewModel(repository) as T
    }
}

class GoalViewModelFactory(private val repository: GoalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GoalViewModel(repository) as T
    }
}

class AlarmViewModelFactory(private val repository: AlarmRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlarmViewModel(repository) as T
    }
}

enum class TimerMode(val title: String, val durationMinutes: Int) {
  FOCUS("Focus", 25),
  SHORT_BREAK("Short Break", 5),
  LONG_BREAK("Long Break", 15)
}

class PlannerViewModel(
    private val taskRepository: TaskRepository,
    private val plannerRepository: PlannerRepository
) : ViewModel() {

    private val syncService = SyncService(taskRepository)

    init {
        syncService.startRealtimeTaskSync()
        viewModelScope.launch {
            taskRepository.allTasks.collect { list ->
                syncService.syncLocalTasksToRemote(list)
            }
        }
    }

    // Task Management
    private val _selectedCategory = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val tasks: StateFlow<List<Task>> = kotlinx.coroutines.flow.combine(taskRepository.allTasks, _selectedCategory) { list, category ->
        if (category == null) list else list.filter { it.category == category }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTask(title: String, notes: String, category: String, priority: String) {
        viewModelScope.launch {
            taskRepository.insertTask(Task(title = title.trim(), notes = notes.trim(), category = category, priority = priority))
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isCompleted = !task.isCompleted, completedAt = if (!task.isCompleted) System.currentTimeMillis() else null))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            taskRepository.clearCompletedTasks()
        }
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    // Timer States
    private val _timerCompleted = kotlinx.coroutines.flow.MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val timerCompleted = _timerCompleted.asSharedFlow()

    private val _timerMode = kotlinx.coroutines.flow.MutableStateFlow(TimerMode.FOCUS)
    val timerMode = _timerMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimerMode.FOCUS)

    private val _timerRemainingSeconds = kotlinx.coroutines.flow.MutableStateFlow(TimerMode.FOCUS.durationMinutes * 60)
    val timerRemainingSeconds = _timerRemainingSeconds.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimerMode.FOCUS.durationMinutes * 60)

    private val _isTimerRunning = kotlinx.coroutines.flow.MutableStateFlow(false)
    val isTimerRunning = _isTimerRunning.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private var timerJob: kotlinx.coroutines.Job? = null

    fun selectTimerMode(mode: TimerMode) {
        _timerMode.value = mode
        pauseTimer()
        _timerRemainingSeconds.value = mode.durationMinutes * 60
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_timerRemainingSeconds.value > 0) {
                kotlinx.coroutines.delay(1000)
                _timerRemainingSeconds.value -= 1
            }
            onTimerComplete()
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        timerJob = null
    }

    fun resetTimer() {
        pauseTimer()
        _timerRemainingSeconds.value = _timerMode.value.durationMinutes * 60
    }

    private fun onTimerComplete() {
        pauseTimer()
        _timerCompleted.tryEmit(Unit)
        val nextMode = when (_timerMode.value) {
            TimerMode.FOCUS -> TimerMode.SHORT_BREAK
            TimerMode.SHORT_BREAK -> TimerMode.FOCUS
            TimerMode.LONG_BREAK -> TimerMode.FOCUS
        }
        selectTimerMode(nextMode)
    }

    // Daily Planner & Exam Schedule
    val plannerEvents: StateFlow<List<PlannerEvent>> = plannerRepository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val examSchedules: StateFlow<List<ExamSchedule>> = plannerRepository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addEvent(title: String, time: String, location: String) {
        viewModelScope.launch {
            plannerRepository.insertEvent(PlannerEvent(title = title, time = time, location = location))
        }
    }

    fun deleteEvent(event: PlannerEvent) {
        viewModelScope.launch {
            plannerRepository.deleteEvent(event)
        }
    }

    fun addExam(subject: String, type: String, time: String) {
        viewModelScope.launch {
            plannerRepository.insertExam(ExamSchedule(subject = subject, type = type, time = time))
        }
    }

    fun deleteExam(exam: ExamSchedule) {
        viewModelScope.launch {
            plannerRepository.deleteExam(exam)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

class PlannerViewModelFactory(
    private val taskRepository: TaskRepository,
    private val plannerRepository: PlannerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlannerViewModel(taskRepository, plannerRepository) as T
    }
}
