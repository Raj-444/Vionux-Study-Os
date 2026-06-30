package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Task
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun TaskCard(
  task: Task,
  onToggleComplete: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isCompleted = task.isCompleted
  val opacity by animateFloatAsState(targetValue = if (isCompleted) 0.6f else 1f, label = "opacity")

  // Color matching for priority indicators
  val priorityColor = when (task.priority.lowercase()) {
    "high" -> AccentRed
    "medium" -> AccentOrange
    "low" -> AccentBlue
    else -> TextMuted
  }

  val priorityBg = when (task.priority.lowercase()) {
    "high" -> AccentRed.copy(alpha = 0.08f)
    "medium" -> AccentOrange.copy(alpha = 0.08f)
    "low" -> AccentBlue.copy(alpha = 0.08f)
    else -> BorderSoft.copy(alpha = 0.3f)
  }

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
        elevation = if (isCompleted) 1.dp else 6.dp,
        shape = RoundedCornerShape(24.dp),
        ambientColor = Color.Black.copy(alpha = 0.03f),
        spotColor = Color.Black.copy(alpha = 0.05f)
      )
      .testTag("task_item_card_${task.id}"),
    color = if (isCompleted) Color.White.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.65f),
    shape = RoundedCornerShape(24.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Apple Reminders-style Circular Checkbox
      Box(
        modifier = Modifier
          .size(26.dp)
          .clip(CircleShape)
          .background(checkboxBgState)
          .border(2.dp, checkboxBorderState, CircleShape)
          .clickable { onToggleComplete() }
          .testTag("task_checkbox_${task.id}"),
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

      // 2. Task Text details (Title & Notes)
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge.copy(
              fontWeight = FontWeight.SemiBold,
              fontSize = 16.sp,
              textDecoration = if (isCompleted) TextDecoration.LineThrough else null
            ),
            color = if (isCompleted) TextMuted else TextDark,
            modifier = Modifier.testTag("task_title_${task.id}")
          )
        }

        if (task.notes.isNotEmpty()) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = task.notes,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontSize = 13.sp,
              color = TextMuted,
              textDecoration = if (isCompleted) TextDecoration.LineThrough else null
            )
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Mini Accent Chips: Category & Priority side-by-side
        Row {
          // Category tag
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(BorderSoft.copy(alpha = 0.4f))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = task.category,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
              color = TextMuted
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Priority badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(priorityBg)
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = task.priority.uppercase(),
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              ),
              color = priorityColor
            )
          }
        }
      }

      // 4. Clean Minimalist Trash Can button
      IconButton(
        onClick = onDelete,
        modifier = Modifier.testTag("task_delete_${task.id}")
      ) {
        Icon(
          imageVector = Icons.Default.DeleteOutline,
          contentDescription = "Delete task",
          tint = TextMuted.copy(alpha = 0.7f),
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
