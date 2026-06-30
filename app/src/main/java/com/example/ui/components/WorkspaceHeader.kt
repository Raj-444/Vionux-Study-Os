package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WorkspaceHeader(
  completedCount: Int,
  totalCount: Int,
  modifier: Modifier = Modifier
) {
  // Format current date: "Monday, June 29"
  val formattedDate = remember {
    val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    sdf.format(Date())
  }

  val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
  val animatedProgress by animateFloatAsState(
    targetValue = progress,
    animationSpec = tween(durationMillis = 600),
    label = "progressAnimation"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 16.dp)
  ) {
    // Top Row with Title & Welcome Info + Profile Avatar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = formattedDate.uppercase(),
          style = MaterialTheme.typography.labelSmall,
          color = AccentRed,
          modifier = Modifier.testTag("workspace_header_date")
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Focus Workspace",
          style = MaterialTheme.typography.displayMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            letterSpacing = (-0.5).sp
          ),
          color = TextDark,
          modifier = Modifier.testTag("workspace_header_title")
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = "Welcome back, Alex",
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
          ),
          color = TextMuted
        )
      }

      // Premium Profile Avatar Placeholder (matching HTML design)
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(Color.White)
          .border(1.dp, BorderSoft, CircleShape)
          .padding(2.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(Color(0xFFE2E8F0)), // slate-200
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "AM",
            style = MaterialTheme.typography.labelLarge.copy(
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            ),
            color = Color(0xFF475569) // slate-600
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Completion Status capsule (Glassmorphic)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .background(Color.White.copy(alpha = 0.65f))
        .border(1.dp, Color.White, RoundedCornerShape(24.dp))
        .padding(18.dp)
        .testTag("completion_status_box")
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Daily Alignment",
            style = MaterialTheme.typography.bodyLarge.copy(
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp
            ),
            color = TextDark
          )

          Spacer(modifier = Modifier.weight(1f))

          Text(
            text = if (totalCount > 0) "${(progress * 100).toInt()}% Done" else "Idle",
            style = MaterialTheme.typography.labelLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            ),
            color = AccentRed
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = if (totalCount > 0) {
            "$completedCount of $totalCount focus objectives completed"
          } else {
            "No objectives active. Tap + to begin planning."
          },
          style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
          color = TextMuted
        )

        if (totalCount > 0) {
          Spacer(modifier = Modifier.height(12.dp))
          LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = AccentRed,
            trackColor = BorderSoft,
            strokeCap = StrokeCap.Round
          )
        }
      }
    }
  }
}
