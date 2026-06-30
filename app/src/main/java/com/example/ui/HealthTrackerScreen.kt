package com.example.ui

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun HealthTrackerScreen(
  viewModel: HealthViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val todayLog by viewModel.todayLog.collectAsState()
  val recentLogs by viewModel.recentLogs.collectAsState()

  val waterGlasses = todayLog?.waterGlasses ?: 0
  val targetWaterGlasses = 8
  val workoutCompleted = todayLog?.workoutCompleted ?: false

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF8F9FA))
      .verticalScroll(scrollState)
      .padding(horizontal = 24.dp)
      .padding(top = 24.dp, bottom = 120.dp)
  ) {
    Text(
      text = "Health Tracker",
      style = MaterialTheme.typography.displayMedium.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = (-0.5).sp
      ),
      color = TextDark
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = "Monitor your daily vitals and consistency.",
      style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
      color = TextMuted
    )

    Spacer(modifier = Modifier.height(32.dp))

    // 1. Hydration Card
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("hydration_card"),
      color = Color(0xFFE3F2FD),
      shape = RoundedCornerShape(24.dp),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.LocalDrink,
            contentDescription = "Water",
            tint = Color(0xFF1E88E5),
            modifier = Modifier.size(32.dp)
          )

          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(0xFF1E88E5))
              .clickable {
                if (waterGlasses < targetWaterGlasses) {
                  viewModel.updateWater(waterGlasses + 1)
                } else {
                  Toast.makeText(context, "Fully Hydrated! 💧", Toast.LENGTH_SHORT).show()
                }
              },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Log Water",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Column {
          Text(
            text = "Daily Hydration",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF1E88E5)
          )
          Text(
            text = "Target: $targetWaterGlasses glasses",
            fontSize = 12.sp,
            color = Color(0xFF546E7A)
          )
        }

        Text(
          text = "$waterGlasses / $targetWaterGlasses Cups",
          fontSize = 24.sp,
          fontWeight = FontWeight.ExtraBold,
          color = Color(0xFF1E88E5)
        )

        // Glass fill animation
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.6f))
        ) {
          val ratio by animateFloatAsState(targetValue = waterGlasses.toFloat() / targetWaterGlasses.toFloat())
          Box(
            modifier = Modifier
              .fillMaxHeight()
              .fillMaxWidth(ratio)
              .clip(RoundedCornerShape(12.dp))
              .background(
                Brush.horizontalGradient(
                  colors = listOf(Color(0xFF64B5F6), Color(0xFF1E88E5))
                )
              )
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // 2. Workout Card
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("workout_card"),
      color = if (workoutCompleted) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
      shape = RoundedCornerShape(24.dp),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White)
    ) {
      Row(
        modifier = Modifier
          .padding(20.dp)
          .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(if (workoutCompleted) Color(0xFF34C759) else Color(0xFFFF9500)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (workoutCompleted) Icons.Default.Check else Icons.Default.Bolt,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(16.dp))
          Column {
            Text(
              text = if (workoutCompleted) "Workout Done!" else "Daily Workout",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = TextDark
            )
            Text(
              text = if (workoutCompleted) "Consistency is key!" else "Log your session",
              fontSize = 12.sp,
              color = TextMuted
            )
          }
        }

        Button(
          onClick = { viewModel.toggleWorkout(!workoutCompleted) },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (workoutCompleted) Color.White else Color(0xFF0C0C0E)
          ),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
          Text(
            text = if (workoutCompleted) "Completed" else "Mark Done",
            color = if (workoutCompleted) Color(0xFF34C759) else Color.White,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(32.dp))

    // 3. Weekly Streak Overview
    Text(
      text = "Weekly Consistency",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = TextDark
    )
    Spacer(modifier = Modifier.height(16.dp))

    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = Color.White,
      shape = RoundedCornerShape(24.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
    ) {
      Row(
        modifier = Modifier
          .padding(20.dp)
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        val last7Days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        last7Days.forEach { day ->
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFF2F2F7))
                .border(1.dp, BorderSoft, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              // Placeholder for history
              Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFE5E5EA)))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = day, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
          }
        }
      }
    }
  }
}
