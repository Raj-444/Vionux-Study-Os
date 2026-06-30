package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.TaskRepository
import com.example.data.FinanceRepository
import com.example.ui.DashboardScreen
import com.example.ui.DashboardViewModel
import com.example.ui.DashboardViewModelFactory
import com.example.ui.PlannerViewModel
import com.example.ui.PlannerViewModelFactory
import com.example.ui.components.AddTaskDialog
import com.example.ui.components.CategoryChips
import com.example.ui.components.FocusTimerWidget
import com.example.ui.components.TaskCard
import com.example.ui.components.WorkspaceHeader
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.SplashScreen
import com.example.ui.AuthScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = AppDatabase.getDatabase(applicationContext)
    val taskRepository = TaskRepository(database.taskDao())
    val financeRepository = FinanceRepository(database.transactionDao())
    val roadmapRepository = com.example.data.GoalRepository(database.roadmapDao(), database.counterDao())
    val alarmRepository = com.example.data.AlarmRepository(database.alarmDao())
    val academicRepository = com.example.data.AcademicRepository(database.routineDao(), database.attendanceDao())
    val plannerRepository = com.example.data.PlannerRepository(database.plannerDao())
    val healthRepository = com.example.data.HealthRepository(database.healthDao())

    setContent {
      MyApplicationTheme {
        val navController = rememberNavController()
        
        val dashboardViewModel: DashboardViewModel = viewModel(
          factory = DashboardViewModelFactory(financeRepository)
        )
        val plannerViewModel: PlannerViewModel = viewModel(
          factory = PlannerViewModelFactory(taskRepository, plannerRepository)
        )
        val goalViewModel: com.example.ui.GoalViewModel = viewModel(
          factory = com.example.ui.GoalViewModelFactory(roadmapRepository)
        )
        val alarmViewModel: com.example.ui.AlarmViewModel = viewModel(
          factory = com.example.ui.AlarmViewModelFactory(alarmRepository)
        )
        val academicViewModel: com.example.ui.AcademicViewModel = viewModel(
          factory = com.example.ui.AcademicViewModelFactory(academicRepository)
        )
        val healthViewModel: com.example.ui.HealthViewModel = viewModel(
          factory = com.example.ui.HealthViewModelFactory(healthRepository)
        )

        NavHost(navController = navController, startDestination = "splash") {
          composable("splash") {
            SplashScreen(
              onNavigateToAuth = {
                navController.navigate("auth") {
                  popUpTo("splash") { inclusive = true }
                }
              },
              onNavigateToMain = {
                navController.navigate("main") {
                  popUpTo("splash") { inclusive = true }
                }
              }
            )
          }
          composable("auth") {
            AuthScreen(
              onAuthSuccess = {
                navController.navigate("main") {
                  popUpTo("auth") { inclusive = true }
                }
              }
            )
          }
          composable("main") {
            DashboardScreen(
              viewModel = dashboardViewModel,
              plannerViewModel = plannerViewModel,
              goalViewModel = goalViewModel,
              alarmViewModel = alarmViewModel,
              academicViewModel = academicViewModel,
              healthViewModel = healthViewModel,
              onLogout = {
                navController.navigate("auth") {
                  popUpTo("main") { inclusive = true }
                }
              }
            )
          }
        }
      }
    }
  }
}

@Composable
fun PlannerAppScreen(viewModel: PlannerViewModel) {
  val tasks by viewModel.tasks.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val timerMode by viewModel.timerMode.collectAsState()
  val timerRemainingSeconds by viewModel.timerRemainingSeconds.collectAsState()
  val isTimerRunning by viewModel.isTimerRunning.collectAsState()

  var showAddDialog by remember { mutableStateOf(false) }

  // Derive stats for header (all tasks in database)
  val completedTasksCount = tasks.count { it.isCompleted }
  val totalTasksCount = tasks.size

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentWindowInsets = WindowInsets.safeDrawing,
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = AccentRed,
        contentColor = Color.White,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(8.dp),
        modifier = Modifier
          .padding(bottom = 16.dp, end = 8.dp)
          .navigationBarsPadding()
          .testTag("add_task_fab")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Add task objective",
          modifier = Modifier.size(28.dp)
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 88.dp)
      ) {
        // 1. Premium Header
        item {
          WorkspaceHeader(
            completedCount = completedTasksCount,
            totalCount = totalTasksCount
          )
        }

        // 2. Interactive Pomodoro timer
        item {
          FocusTimerWidget(
            mode = timerMode,
            remainingSeconds = timerRemainingSeconds,
            isRunning = isTimerRunning,
            onModeSelected = { viewModel.selectTimerMode(it) },
            onStartPauseToggle = {
              if (isTimerRunning) viewModel.pauseTimer() else viewModel.startTimer()
            },
            onReset = { viewModel.resetTimer() },
            modifier = Modifier.padding(horizontal = 24.dp)
          )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // 3. Category Horizontal sliding filters
        item {
          CategoryChips(
            selectedCategory = selectedCategory,
            onCategorySelected = { viewModel.selectCategory(it) }
          )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Section Title: "Focus Objectives" + Quick actions (Clear completed)
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 24.dp, vertical = 8.dp)
          ) {
            Text(
              text = "Focus Objectives",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
              ),
              color = TextDark,
              modifier = Modifier.align(Alignment.CenterStart)
            )

            if (tasks.any { it.isCompleted }) {
              IconButton(
                onClick = { viewModel.clearCompletedTasks() },
                modifier = Modifier
                  .align(Alignment.CenterEnd)
                  .testTag("clear_completed_button")
              ) {
                Icon(
                  imageVector = Icons.Default.ClearAll,
                  contentDescription = "Clear completed focus cards",
                  tint = AccentRed,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }

        // 4. Task list items
        if (tasks.isEmpty()) {
          item {
            EmptyStateView()
          }
        } else {
          items(tasks, key = { it.id }) { task ->
            TaskCard(
              task = task,
              onToggleComplete = { viewModel.toggleTaskCompletion(task) },
              onDelete = { viewModel.deleteTask(task) },
              modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
            )
          }
        }
      }

      // 5. Task Input Modal Overlay
      if (showAddDialog) {
        AddTaskDialog(
          onDismiss = { showAddDialog = false },
          onConfirm = { title, notes, category, priority ->
            viewModel.addTask(title, notes, category, priority)
            showAddDialog = false
          }
        )
      }
    }
  }
}

@Composable
fun EmptyStateView() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 60.dp, horizontal = 24.dp)
      .testTag("empty_state_view"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Surface(
      modifier = Modifier
        .size(80.dp),
      shape = CircleShape,
      color = Color(0xFFF2F2F7),
      border = BorderStroke(1.dp, Color.White)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = Icons.Outlined.CheckCircle,
          contentDescription = "Pristine state",
          tint = AccentRed.copy(alpha = 0.5f),
          modifier = Modifier.size(32.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = "All Pristine",
      style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
      color = TextDark,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "Your workspace is clear. No active focus objectives detected. Click '+' to define a new alignment.",
      style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
      color = TextMuted,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 24.dp)
    )
  }
}
