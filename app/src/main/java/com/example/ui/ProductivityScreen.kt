package com.example.ui

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Task
import com.example.ui.components.AddTaskDialog
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun ProductivityScreen(
  viewModel: PlannerViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val tasks by viewModel.tasks.collectAsState()
  val timerMode by viewModel.timerMode.collectAsState()
  val timerRemainingSeconds by viewModel.timerRemainingSeconds.collectAsState()
  val isTimerRunning by viewModel.isTimerRunning.collectAsState()

  var selectedTab by remember { mutableStateOf("tasks") } // "tasks" or "timer"
  var showAddTaskDialog by remember { mutableStateOf(false) }

  var focusLockEnabled by remember { mutableStateOf(true) }
  var showCompletionDialog by remember { mutableStateOf(false) }
  var ringtonePlayer by remember { mutableStateOf<android.media.Ringtone?>(null) }
  var showBrokenDialog by remember { mutableStateOf(false) }

  val isFocusLockActive = focusLockEnabled && isTimerRunning && timerMode == TimerMode.FOCUS

  // Observe timer completed event
  LaunchedEffect(Unit) {
    viewModel.timerCompleted.collect {
      showCompletionDialog = true
      try {
        val alarmUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)
          ?: android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_RINGTONE)
        val r = android.media.RingtoneManager.getRingtone(context, alarmUri)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
          r.audioAttributes = android.media.AudioAttributes.Builder()
            .setUsage(android.media.AudioAttributes.USAGE_ALARM)
            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        }
        r.play()
        ringtonePlayer = r
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  // Handle Immersive Fullscreen and Screen Security (FLAG_SECURE)
  val activity = context as? android.app.Activity
  val window = activity?.window

  LaunchedEffect(isFocusLockActive) {
    if (window != null) {
      val controller = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
      if (isFocusLockActive) {
        // Hide status and navigation bars
        controller.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        
        // Prevent screenshots and recents preview exposure
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        
        // Keep screen on
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
      } else {
        // Show status and navigation bars
        controller.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
        
        // Clear secure flags
        window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        
        // Clear keep screen on flag
        window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
      }
    }
  }

  // Clean up secure flags and system UI settings on dispose
  DisposableEffect(Unit) {
    onDispose {
      if (window != null) {
        val controller = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
        controller.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
        window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
      }
      ringtonePlayer?.stop()
    }
  }

  // Hardware back press blocker when focus lock is active
  androidx.activity.compose.BackHandler(enabled = isFocusLockActive) {
    Toast.makeText(context, "Focus Lock is Active! Complete your session to leave.", Toast.LENGTH_SHORT).show()
  }

  // Observe Lifecycle Events to detect user minimizing the app (ON_PAUSE)
  val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
  DisposableEffect(lifecycleOwner, isFocusLockActive) {
    val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
      if (event == androidx.lifecycle.Lifecycle.Event.ON_PAUSE) {
        if (isFocusLockActive) {
          // Failure condition met: User left the app!
          viewModel.resetTimer()
          showBrokenDialog = true
          
          // Play loud distinct failure alert tone using ToneGenerator
          try {
            val toneGen = android.media.ToneGenerator(android.media.AudioManager.STREAM_ALARM, 100)
            toneGen.startTone(android.media.ToneGenerator.TONE_CDMA_ABBR_ALERT, 1000)
          } catch (e: Exception) {
            e.printStackTrace()
          }
        }
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF8F9FA)) // Premium off-white background
      .padding(horizontal = 24.dp)
      .padding(top = 24.dp, bottom = 120.dp) // Leave padding for the navigation bar
  ) {
    // Premium Segmented Controls
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xFFE5E5EA).copy(alpha = 0.5f))
        .padding(4.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(if (selectedTab == "tasks") Color.White else Color.Transparent)
          .clickable { selectedTab = "tasks" }
          .padding(vertical = 10.dp)
          .testTag("prod_tab_tasks"),
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = if (selectedTab == "tasks") AccentRed else TextMuted,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Objectives",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (selectedTab == "tasks") TextDark else TextMuted
          )
        }
      }

      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(if (selectedTab == "timer") Color.White else Color.Transparent)
          .clickable { selectedTab = "timer" }
          .padding(vertical = 10.dp)
          .testTag("prod_tab_timer"),
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Timer,
            contentDescription = null,
            tint = if (selectedTab == "timer") AccentRed else TextMuted,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Focus Space",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (selectedTab == "timer") TextDark else TextMuted
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    if (selectedTab == "tasks") {
      // 1. Task Manager Section
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Task Manager",
            style = MaterialTheme.typography.displayMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 24.sp
            ),
            color = TextDark
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Track daily work priorities.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
          )
        }

        Button(
          onClick = { showAddTaskDialog = true },
          colors = ButtonDefaults.buttonColors(
            containerColor = AccentRed,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(14.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp),
          modifier = Modifier.testTag("add_task_btn_productivity")
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Task",
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "+ Add Task",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        if (tasks.isEmpty()) {
          item {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 48.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(16.dp))
              Text(
                text = "All focus goals achieved!",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextDark
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Create a new task to get aligned.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                textAlign = TextAlign.Center
              )
            }
          }
        } else {
          items(tasks, key = { it.id }) { task ->
            TaskCardWidget(
              task = task,
              onToggle = { viewModel.toggleTaskCompletion(task) }
            )
          }
        }
      }
    } else {
      // 2. Pomodoro Timer + Clock Section
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Focus Timer",
          style = MaterialTheme.typography.displayMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
          ),
          color = TextDark
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "Mute visual noise and trigger cognitive alignment.",
          style = MaterialTheme.typography.bodySmall,
          color = TextMuted
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Stunning Pomodoro card with circular countdown
        PomodoroCardWidget(
          remainingSeconds = timerRemainingSeconds,
          isRunning = isTimerRunning,
          onStartPause = {
            if (isTimerRunning) viewModel.pauseTimer() else viewModel.startTimer()
          },
          onReset = { viewModel.resetTimer() },
          onDurationSelected = { minutes ->
            // Update time remaining in secondary duration options
            viewModel.selectTimerMode(
              when (minutes) {
                25 -> TimerMode.FOCUS
                5 -> TimerMode.SHORT_BREAK
                15 -> TimerMode.LONG_BREAK
                else -> TimerMode.FOCUS
              }
            )
          },
          focusLockEnabled = focusLockEnabled,
          onFocusLockToggle = { focusLockEnabled = it }
        )
      }
    }

    if (showAddTaskDialog) {
      AddTaskDialog(
        onDismiss = { showAddTaskDialog = false },
        onConfirm = { title, notes, category, priority ->
          viewModel.addTask(title, notes, category, priority)
          showAddTaskDialog = false
          Toast.makeText(context, "Task created successfully!", Toast.LENGTH_SHORT).show()
        }
      )
    }

    if (showCompletionDialog) {
      AlertDialog(
        onDismissRequest = {
          ringtonePlayer?.stop()
          showCompletionDialog = false
        },
        title = { Text("Focus Session Complete", fontWeight = FontWeight.Bold, color = AccentRed) },
        text = { Text("Amazing! You successfully completed your focus session and protected your rhythm from external distractions.", color = TextDark) },
        confirmButton = {
          Button(
            onClick = {
              ringtonePlayer?.stop()
              showCompletionDialog = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
          ) {
            Text("Dismiss")
          }
        }
      )
    }

    if (showBrokenDialog) {
      AlertDialog(
        onDismissRequest = { showBrokenDialog = false },
        title = { Text("Broken Focus Detected", fontWeight = FontWeight.Bold, color = AccentRed) },
        text = { Text("Your focus session has failed because you left or minimized the app. Stay aligned and try again next time!", color = TextDark) },
        confirmButton = {
          Button(
            onClick = { showBrokenDialog = false },
            colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
          ) {
            Text("Acknowledge")
          }
        }
      )
    }
  }
}

/// Custom modular Compose widget for a Task Item in the lists
@Composable
fun TaskCardWidget(
  task: Task,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isCompleted = task.isCompleted
  val opacity by animateFloatAsState(targetValue = if (isCompleted) 0.6f else 1f, label = "opacity")

  val checkboxBgState by animateColorAsState(
    targetValue = if (isCompleted) AccentRed else Color.Transparent,
    label = "checkboxBg"
  )

  val checkboxBorderState by animateColorAsState(
    targetValue = if (isCompleted) AccentRed else TextMuted.copy(alpha = 0.6f),
    label = "checkboxBorder"
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .alpha(opacity)
      .shadow(
        elevation = if (isCompleted) 1.dp else 4.dp,
        shape = RoundedCornerShape(24.dp),
        ambientColor = Color.Black.copy(alpha = 0.02f),
        spotColor = Color.Black.copy(alpha = 0.03f)
      ),
    color = Color(0xFFF2F2F7), // Light gray card background
    shape = RoundedCornerShape(24.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Circular checkbox on the left
      Box(
        modifier = Modifier
          .size(26.dp)
          .clip(CircleShape)
          .background(checkboxBgState)
          .border(2.dp, checkboxBorderState, CircleShape)
          .clickable { onToggle() },
        contentAlignment = Alignment.Center
      ) {
        if (isCompleted) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Completed",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(16.dp))

      // 2. Task title and tags
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = task.title,
          style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            textDecoration = if (isCompleted) TextDecoration.LineThrough else null
          ),
          color = TextDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Subject chip
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color.White)
              .border(1.dp, BorderSoft, RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(
              text = task.category, // Reused category as the Subject tag
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              ),
              color = TextMuted
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Small red dot priority indicator
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(if (task.priority.lowercase() == "high") AccentRed else Color(0xFFFF9500))
          )
        }
      }

      // 3. Trailing date text
      Text(
        text = "Tomorrow, 10:00 AM",
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
        color = TextMuted
      )
    }
  }
}

/// Custom modular Pomodoro timer widget with a dark gorgeous background card
@Composable
fun PomodoroCardWidget(
  remainingSeconds: Int,
  isRunning: Boolean,
  onStartPause: () -> Unit,
  onReset: () -> Unit,
  onDurationSelected: (Int) -> Unit,
  focusLockEnabled: Boolean,
  onFocusLockToggle: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  // Format countdown string
  val minutes = remainingSeconds / 60
  val seconds = remainingSeconds % 60
  val timeText = String.format("%02d:%02d", minutes, seconds)

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .shadow(
        elevation = 16.dp,
        shape = RoundedCornerShape(24.dp),
        ambientColor = AccentRed.copy(alpha = 0.1f),
        spotColor = AccentRed.copy(alpha = 0.2f)
      ),
    color = Color(0xFF0C0C0E), // Highly rounded dark card (black background)
    shape = RoundedCornerShape(24.dp)
  ) {
    Column(
      modifier = Modifier.padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Beautiful Circular Progress Indicator containing the countdown
      Box(
        modifier = Modifier.size(180.dp),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.size(160.dp)) {
          // Track
          drawCircle(
            color = Color.White.copy(alpha = 0.05f),
            style = Stroke(width = 6.dp.toPx())
          )
          // Progress arc
          val sweepAngle = (remainingSeconds.toFloat() / (25 * 60)) * 360f
          drawArc(
            color = AccentRed,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = 6.dp.toPx())
          )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = timeText,
            style = MaterialTheme.typography.displayLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 38.sp,
              fontFamily = FontFamily.Monospace
            ),
            color = Color.White
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = if (isRunning) "FOCUSING..." else "READY",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              letterSpacing = 1.sp
            ),
            color = Color.White.copy(alpha = 0.4f)
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 2. Row of three small duration selector chips
      Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        listOf(25, 5, 15).forEach { mins ->
          val label = when (mins) {
            25 -> "25m"
            5 -> "5m"
            15 -> "15m"
            else -> "${mins}m"
          }
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(if (minutes == mins) AccentRed else Color.White.copy(alpha = 0.05f))
              .clickable { onDurationSelected(mins) }
              .padding(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Text(
              text = label,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = Color.White
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // 3. Focus Lock switch with subtle glow
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.03f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (focusLockEnabled) Icons.Default.Lock else Icons.Default.LockOpen,
              contentDescription = null,
              tint = if (focusLockEnabled) AccentRed else Color.White.copy(alpha = 0.4f),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Focus Lock (App-block mode)",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.White
              )
              Text(
                text = "Prevents leaving app while timing",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = Color.White.copy(alpha = 0.4f)
              )
            }
          }

          Switch(
            checked = focusLockEnabled,
            onCheckedChange = onFocusLockToggle,
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = AccentRed,
              uncheckedThumbColor = Color.White.copy(alpha = 0.4f),
              uncheckedTrackColor = Color.White.copy(alpha = 0.08f)
            ),
            modifier = Modifier.testTag("focus_lock_toggle")
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // 4. Solid buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Button(
          onClick = onStartPause,
          colors = ButtonDefaults.buttonColors(
            containerColor = AccentRed,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("start_focus_btn"),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
          Text(
            text = if (isRunning) "Pause Session" else "Start Focus",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }

        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .clickable { onReset() },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reset",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}
