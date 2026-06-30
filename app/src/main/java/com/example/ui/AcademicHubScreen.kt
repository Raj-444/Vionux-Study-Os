package com.example.ui

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import kotlin.math.ceil

@Composable
fun AcademicHubScreen(
  viewModel: AcademicViewModel,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  val routineItems by viewModel.routineItems.collectAsState()
  val attendanceSubjects by viewModel.attendanceSubjects.collectAsState()

  // 1. Day Selection State
  val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
  var selectedDay by remember { mutableStateOf("Mon") }
  
  var showAddRoutineDialog by remember { mutableStateOf(false) }
  var showAddSubjectDialog by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF8F9FA)) // Premium off-white background
      .verticalScroll(scrollState)
      .padding(horizontal = 24.dp)
      .padding(top = 24.dp, bottom = 120.dp) // Leave space for bottom nav
      .testTag("academic_hub_content")
  ) {
    // ... header ...
    // Screen Title Header
    Column(modifier = Modifier.fillMaxWidth()) {
      Text(
        text = "Academic Hub",
        style = MaterialTheme.typography.displayMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 28.sp,
          letterSpacing = (-0.5).sp
        ),
        color = TextDark,
        modifier = Modifier.testTag("academic_header_title")
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Keep track of classes and monitor attendance eligibility.",
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
        color = TextMuted,
        modifier = Modifier.testTag("academic_header_subtitle")
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // ==========================================
    // SECTION 1: Class Routine (Timeline View)
    // ==========================================
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = "Class Routine",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 20.sp
        ),
        color = TextDark,
        modifier = Modifier.testTag("routine_section_title")
      )
      androidx.compose.material3.IconButton(
        onClick = { showAddRoutineDialog = true },
        modifier = Modifier.background(AccentRed, CircleShape).size(32.dp)
      ) {
        Icon(androidx.compose.material.icons.Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
      }
    }
    Spacer(modifier = Modifier.height(12.dp))

    // Horizontal Scrollable Day selector
    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(days) { day ->
        val isSelected = day == selectedDay
        val bgStateColor by animateColorAsState(
          targetValue = if (isSelected) AccentRed else Color(0xFFF2F2F7),
          label = "day_bg"
        )
        val textStateColor by animateColorAsState(
          targetValue = if (isSelected) Color.White else TextDark,
          label = "day_text"
        )

        Box(
          modifier = Modifier
            .width(52.dp)
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgStateColor)
            .border(
              width = 1.dp,
              color = if (isSelected) AccentRed else Color.White,
              shape = RoundedCornerShape(16.dp)
            )
            .clickable { selectedDay = day }
            .testTag("day_selector_$day"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = day,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = textStateColor
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Routine Cards / Timeline List
    val classes = routineItems.filter { it.day == selectedDay }
    if (classes.isEmpty()) {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("no_classes_view"),
        color = Color(0xFFF2F2F7),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
      ) {
        Column(
          modifier = Modifier.padding(32.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = "No classes",
            tint = TextMuted,
            modifier = Modifier.size(36.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Routine Pristine",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = TextDark
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "No classes scheduled for $selectedDay. Enjoy your focus time.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            textAlign = TextAlign.Center
          )
        }
      }
    } else {
      Column(modifier = Modifier.fillMaxWidth()) {
        classes.forEachIndexed { index, item ->
          val isLast = index == classes.size - 1
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("routine_card_$index"),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            // Left Time Column
            Column(
              modifier = Modifier
                .width(80.dp)
                .padding(top = 10.dp),
              horizontalAlignment = Alignment.End
            ) {
              Text(
                text = item.startTime,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = TextDark,
                textAlign = TextAlign.End
              )
              Text(
                text = item.endTime,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = TextMuted,
                textAlign = TextAlign.End
              )
            }

            // Timeline dot & Canvas connection line
            Box(
              modifier = Modifier
                .width(16.dp)
                .fillMaxHeight(),
              contentAlignment = Alignment.TopCenter
            ) {
              Canvas(
                modifier = Modifier
                  .width(16.dp)
                  .height(if (isLast) 30.dp else 115.dp)
              ) {
                // Connecting line to next card
                if (!isLast) {
                  drawLine(
                    color = Color(0xFFE5E5EA),
                    start = Offset(x = size.width / 2, y = 14.dp.toPx()),
                    end = Offset(x = size.width / 2, y = size.height),
                    strokeWidth = 2.dp.toPx()
                  )
                }

                // Inner dot core
                drawCircle(
                  color = AccentRed,
                  radius = 5.dp.toPx(),
                  center = Offset(x = size.width / 2, y = 12.dp.toPx())
                )

                // Outer dot boundary Ring
                drawCircle(
                  color = Color.White,
                  radius = 7.dp.toPx(),
                  center = Offset(x = size.width / 2, y = 12.dp.toPx()),
                  style = Stroke(width = 2.dp.toPx())
                )
              }
            }

            // Content card details
            Surface(
              modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp),
              color = Color(0xFFF2F2F7),
              shape = RoundedCornerShape(24.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = item.subject,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                  )

                  androidx.compose.material3.IconButton(
                    onClick = { viewModel.deleteRoutine(item) },
                    modifier = Modifier.size(24.dp)
                  ) {
                    Icon(
                      imageVector = androidx.compose.material.icons.Icons.Default.DeleteOutline,
                      contentDescription = "Delete",
                      tint = AccentRed.copy(alpha = 0.6f),
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  // Room location chip
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color.White)
                      .border(1.dp, BorderSoft, RoundedCornerShape(8.dp))
                      .padding(horizontal = 8.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = item.room,
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                      ),
                      color = TextMuted
                    )
                  }

                  Text(
                    text = "Lec: ${item.faculty}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextMuted
                  )
                }
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(32.dp))

    // ==========================================
    // SECTION 2: Attendance Calculator
    // ==========================================
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = "Attendance Tracker",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 20.sp
        ),
        color = TextDark,
        modifier = Modifier.testTag("attendance_section_title")
      )
      androidx.compose.material3.IconButton(
        onClick = { showAddSubjectDialog = true },
        modifier = Modifier.background(Color(0xFFE5E5EA), CircleShape).size(32.dp)
      ) {
        Icon(androidx.compose.material.icons.Icons.Default.Add, null, tint = TextDark, modifier = Modifier.size(20.dp))
      }
    }
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = "Monitor your course eligibility and lecture stats.",
      style = MaterialTheme.typography.bodySmall,
      color = TextMuted
    )
    Spacer(modifier = Modifier.height(16.dp))

    if (attendanceSubjects.isEmpty()) {
       Surface(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("no_attendance_view"),
        color = Color(0xFFF2F2F7),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
      ) {
        Column(
          modifier = Modifier.padding(32.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            imageVector = Icons.Default.School,
            contentDescription = "No courses",
            tint = TextMuted,
            modifier = Modifier.size(36.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "No courses tracked",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = TextDark
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Add your academic subjects to monitor attendance.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            textAlign = TextAlign.Center
          )
        }
      }
    } else {
        // Custom 2-column Grid of AttendanceSubjectCard
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          for (i in attendanceSubjects.indices step 2) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              // Left Card
              Box(modifier = Modifier.weight(1f)) {
                val subject = attendanceSubjects[i]
                AttendanceSubjectCardWidget(
                  subject = subject,
                  onAttended = { viewModel.updateAttendance(subject, 1, 1) },
                  onMissed = { viewModel.updateAttendance(subject, 0, 1) },
                  onDelete = { viewModel.deleteSubject(subject) }
                )
              }

              // Right Card (If exists)
              if (i + 1 < attendanceSubjects.size) {
                Box(modifier = Modifier.weight(1f)) {
                  val subject = attendanceSubjects[i + 1]
                  AttendanceSubjectCardWidget(
                    subject = subject,
                    onAttended = { viewModel.updateAttendance(subject, 1, 1) },
                    onMissed = { viewModel.updateAttendance(subject, 0, 1) },
                    onDelete = { viewModel.deleteSubject(subject) }
                  )
                }
              } else {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        }
    }
  }

  if (showAddRoutineDialog) {
    AddRoutineDialog(
      onDismiss = { showAddRoutineDialog = false },
      onConfirm = { startTime, endTime, subject, room, faculty, isLab ->
        viewModel.addRoutineItem(selectedDay, startTime, endTime, subject, room, faculty, isLab)
        showAddRoutineDialog = false
      }
    )
  }

  if (showAddSubjectDialog) {
    AddSubjectDialog(
      onDismiss = { showAddSubjectDialog = false },
      onConfirm = { name ->
        viewModel.addSubject(name)
        showAddSubjectDialog = false
      }
    )
  }
}

@Composable
fun AddRoutineDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, String, String, Boolean) -> Unit) {
  var startTime by remember { mutableStateOf("09:00 AM") }
  var endTime by remember { mutableStateOf("10:30 AM") }
  var subject by remember { mutableStateOf("") }
  var room by remember { mutableStateOf("") }
  var faculty by remember { mutableStateOf("") }
  var isLab by remember { mutableStateOf(false) }

  androidx.compose.material3.AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Add Class", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        androidx.compose.material3.OutlinedTextField(value = startTime, onValueChange = { startTime = it }, label = { Text("Start Time") }, modifier = Modifier.fillMaxWidth())
        androidx.compose.material3.OutlinedTextField(value = endTime, onValueChange = { endTime = it }, label = { Text("End Time") }, modifier = Modifier.fillMaxWidth())
        androidx.compose.material3.OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject Name") }, modifier = Modifier.fillMaxWidth())
        androidx.compose.material3.OutlinedTextField(value = room, onValueChange = { room = it }, label = { Text("Room/Lab") }, modifier = Modifier.fillMaxWidth())
        androidx.compose.material3.OutlinedTextField(value = faculty, onValueChange = { faculty = it }, label = { Text("Lecturer") }, modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      androidx.compose.material3.Button(onClick = { onConfirm(startTime, endTime, subject, room, faculty, isLab) }, colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AccentRed)) { Text("Add") }
    },
    dismissButton = {
      androidx.compose.material3.TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}

@Composable
fun AddSubjectDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
  var name by remember { mutableStateOf("") }

  androidx.compose.material3.AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Track New Course", fontWeight = FontWeight.Bold) },
    text = {
      androidx.compose.material3.OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Subject Name") }, modifier = Modifier.fillMaxWidth())
    },
    confirmButton = {
      androidx.compose.material3.Button(onClick = { onConfirm(name) }, colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AccentRed)) { Text("Track") }
    },
    dismissButton = {
      androidx.compose.material3.TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}

@Composable
fun AttendanceSubjectCardWidget(
  subject: com.example.data.AttendanceSubject,
  onAttended: () -> Unit,
  onMissed: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val cleanTitle = subject.name.substringBefore(":")
  val percentage = if (subject.total > 0) subject.attended.toFloat() / subject.total.toFloat() else 0.0f
  val percentageText = "${(percentage * 100).toInt()}%"

  // Calculation for prediction string
  val predictionText = remember(subject.attended, subject.total) {
    if (subject.total == 0) {
      "No lectures logged yet."
    } else if (percentage >= 0.90f) {
      "Excellent! You are safely above the 90% threshold."
    } else {
      val needed = (0.90f * subject.total - subject.attended) / 0.10f
      val classesToAttend = ceil(needed).toInt()
      val classesLabel = if (classesToAttend == 1) "class" else "classes"
      if (classesToAttend <= 0) {
        "Attend next 1 class to reach 90%"
      } else {
        "Attend next $classesToAttend $classesLabel to reach 90%"
      }
    }
  }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .height(220.dp),
    color = Color(0xFFF2F2F7),
    shape = RoundedCornerShape(24.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Subject Title & Delete
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(24.dp))
        Text(
          text = cleanTitle,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          ),
          color = TextDark,
          textAlign = TextAlign.Center,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )
        androidx.compose.material3.IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
          Icon(androidx.compose.material.icons.Icons.Default.DeleteOutline, null, tint = AccentRed.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
        }
      }

      // 2. Circular percentage indicator
      Box(
        modifier = Modifier.size(54.dp),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator(
          progress = percentage,
          strokeWidth = 4.5.dp,
          color = if (percentage >= 0.90f) Color(0xFF34C759) else AccentRed,
          trackColor = Color.White,
          modifier = Modifier.fillMaxSize()
        )
        Text(
          text = percentageText,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          color = TextDark
        )
      }

      // 3. Increments/Decrements Pills
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Red Attended pill
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(AccentRed)
            .clickable { onAttended() }
            .padding(vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "+ Attended",
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          )
        }

        // Gray Missed pill
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(12.dp))
            .clickable { onMissed() }
            .padding(vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "- Missed",
            style = MaterialTheme.typography.labelSmall.copy(
              color = TextMuted,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          )
        }
      }

      // 4. Smart Prediction Text
      Text(
        text = predictionText,
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = TextMuted
        ),
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
