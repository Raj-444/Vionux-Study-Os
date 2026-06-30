package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

// Event Types for Exam/Test
enum class ExamType(val displayName: String, val color: Color) {
    Midterm("Midterm", Color(0xFFFF9500)),
    Final("Final", Color(0xFFE94560)),
    Quiz("Quiz", Color(0xFF34C759)),
    Lab("Lab", Color(0xFF5856D6)),
    Assignment("Assignment", Color(0xFF5AC8FA)),
    Presentation("Presentation", Color(0xFFFFCC00)),
    Project("Project", Color(0xFFAF52DE))
}

@Composable
fun TimeManagementScreen(
    viewModel: PlannerViewModel,
    modifier: Modifier = Modifier
) {
    val events by viewModel.plannerEvents.collectAsState()
    val exams by viewModel.examSchedules.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Daily Planner", "Exam Scheduler")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(top = 16.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = AccentRed,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = AccentRed
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> DailyPlannerView(viewModel, events)
            1 -> ExamSchedulerView(viewModel, exams)
        }
    }
}

@Composable
fun DailyPlannerView(viewModel: PlannerViewModel, events: List<com.example.data.PlannerEvent>) {
    val scrollState = rememberScrollState()
    val hours = listOf("08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM")
    
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            hours.forEach { hour ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clickable { 
                            selectedHour = hour
                            showAddDialog = true
                        },
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = hour,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.width(70.dp).padding(top = 4.dp)
                    )

                    val hourEvents = events.filter { it.time == hour }
                    if (hourEvents.isNotEmpty()) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            hourEvents.forEach { event ->
                                Surface(
                                    color = Color(0xFFCFE2FF),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth().height(60.dp)
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = event.title, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 13.sp)
                                            Text(text = event.location, fontSize = 11.sp, color = TextMuted)
                                        }
                                        IconButton(onClick = { viewModel.deleteEvent(event) }) {
                                            Icon(Icons.Default.DeleteOutline, null, tint = AccentRed.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEventDialog(
            time = selectedHour,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, location ->
                viewModel.addEvent(title, selectedHour, location)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddEventDialog(time: String, onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Event at $time", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(title, location) }, colors = ButtonDefaults.buttonColors(containerColor = AccentRed)) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ExamSchedulerView(viewModel: PlannerViewModel, exams: List<com.example.data.ExamSchedule>) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            exams.forEach { exam ->
                ExamCard(exam, onDelete = { viewModel.deleteExam(exam) })
            }
            if (exams.isEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No Exams Scheduled", fontWeight = FontWeight.Bold)
                    Text("Stay prepared. Add your upcoming assessments.", color = TextMuted, fontSize = 14.sp)
                }
            }
        }
        
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).padding(bottom = 80.dp),
            containerColor = AccentRed,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, null)
        }
    }

    if (showAddDialog) {
        AddExamDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { subject, type, time ->
                viewModel.addExam(subject, type, time)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddExamDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var subject by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Final") }
    var time by remember { mutableStateOf("In 14 Days") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Exam Schedule", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type (e.g., Midterm, Final)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time (e.g., In 7 Days)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(subject, type, time) }, colors = ButtonDefaults.buttonColors(containerColor = AccentRed)) { Text("Schedule") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ExamCard(exam: com.example.data.ExamSchedule, onDelete: () -> Unit) {
    val examColor = when(exam.type) {
        "Midterm" -> ExamType.Midterm.color
        "Final" -> ExamType.Final.color
        "Quiz" -> ExamType.Quiz.color
        else -> ExamType.Project.color
    }

    Surface(
        color = Color(0xFFF2F2F7),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(exam.subject, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 15.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(examColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(exam.type, color = examColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.DeleteOutline, null, tint = AccentRed.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(exam.time, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
