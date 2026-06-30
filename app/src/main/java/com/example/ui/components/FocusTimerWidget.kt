package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TimerMode
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.drawBehind

@Composable
fun FocusTimerWidget(
  mode: TimerMode,
  remainingSeconds: Int,
  isRunning: Boolean,
  onModeSelected: (TimerMode) -> Unit,
  onStartPauseToggle: () -> Unit,
  onReset: () -> Unit,
  modifier: Modifier = Modifier
) {
  val totalSeconds = mode.durationMinutes * 60
  val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds.toFloat() else 0f

  // Smooth animation for progress bar
  val animatedProgress by animateFloatAsState(
    targetValue = progress,
    animationSpec = tween(durationMillis = 500),
    label = "TimerProgress"
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .shadow(
        elevation = 20.dp,
        shape = RoundedCornerShape(32.dp),
        ambientColor = Color.Black.copy(alpha = 0.3f),
        spotColor = Color.Black.copy(alpha = 0.4f)
      )
      .testTag("focus_timer_card"),
    color = Color.Transparent, // Drawn via custom background brush for glowing glass effect
    shape = RoundedCornerShape(32.dp)
  ) {
    Column(
      modifier = Modifier
        .drawBehind {
          // Deep slate-black base
          drawRect(color = Color(0xFF0C0C0E))
          // Radiant red ambient glow (matching absolute blur-60px red circle in HTML)
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(AccentRed.copy(alpha = 0.45f), Color.Transparent),
              center = androidx.compose.ui.geometry.Offset(size.width * 1.0f, size.height * 0.0f),
              radius = size.width * 0.8f
            )
          )
        }
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Timer Mode Segmented Tab Selector (Glassmorphic Dark style)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(24.dp))
          .background(Color.White.copy(alpha = 0.08f))
          .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        TimerMode.values().forEach { timerMode ->
          val isSelected = timerMode == mode
          val backgroundModifier = if (isSelected) {
            Modifier
              .weight(1f)
              .clip(RoundedCornerShape(20.dp))
              .background(Color.White)
              .clickable { onModeSelected(timerMode) }
              .padding(vertical = 8.dp)
          } else {
            Modifier
              .weight(1f)
              .clip(RoundedCornerShape(20.dp))
              .clickable { onModeSelected(timerMode) }
              .padding(vertical = 8.dp)
          }

          Box(
            modifier = backgroundModifier,
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = timerMode.title,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp
              ),
              color = if (isSelected) Color(0xFF0C0C0E) else Color.White.copy(alpha = 0.6f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // 2. Custom Canvas Arc Progress Clock
      Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.size(190.dp)) {
          val strokeWidthValue = 7.dp.toPx()
          val innerPadding = strokeWidthValue / 2
          val diameter = size.width - strokeWidthValue

          // Background track (subtle gray on dark)
          drawCircle(
            color = Color.White.copy(alpha = 0.12f),
            radius = diameter / 2,
            style = Stroke(width = strokeWidthValue)
          )

          // Active countdown arc (Accent red, starting from top)
          drawArc(
            color = AccentRed,
            startAngle = -90f,
            sweepAngle = animatedProgress * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidthValue, cap = StrokeCap.Round),
            size = Size(diameter, diameter),
            topLeft = androidx.compose.ui.geometry.Offset(innerPadding, innerPadding)
          )
        }

        // Inner Time digital digits
        Column(
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          val minutes = remainingSeconds / 60
          val seconds = remainingSeconds % 60
          val timeFormatted = String.format("%02d:%02d", minutes, seconds)

          Text(
            text = timeFormatted,
            style = MaterialTheme.typography.displayLarge.copy(
              fontSize = 44.sp,
              fontWeight = FontWeight.Bold
            ),
            color = Color.White,
            modifier = Modifier.testTag("timer_countdown_text")
          )

          Text(
            text = if (isRunning) "Active Focus" else "Paused",
            style = MaterialTheme.typography.labelSmall,
            color = if (isRunning) AccentRed else Color.White.copy(alpha = 0.5f)
          )
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // 3. Simple Elegant Control Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Reset Button
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.15f))
            .clickable { onReset() }
            .testTag("timer_reset_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reset Session",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(24.dp))

        // Play / Pause Toggle Button (Vibrant red accent)
        Box(
          modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(AccentRed)
            .clickable { onStartPauseToggle() }
            .testTag("timer_toggle_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
            contentDescription = if (isRunning) "Pause Focus" else "Start Focus",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
          )
        }
      }
    }
  }
}
