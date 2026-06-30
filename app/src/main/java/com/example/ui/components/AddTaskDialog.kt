package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.premiumInputTextStyle
import com.example.ui.getTextFieldColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
  onDismiss: () -> Unit,
  onConfirm: (title: String, notes: String, category: String, priority: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var title by remember { mutableStateOf("") }
  var notes by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("Work") }
  var selectedPriority by remember { mutableStateOf("Medium") }

  val categories = listOf("Work", "Personal", "Study", "Creative")
  val priorities = listOf("High", "Medium", "Low")

  BasicAlertDialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
    modifier = modifier.padding(24.dp)
  ) {
    Surface(
      shape = RoundedCornerShape(28.dp),
      color = Color.White,
      tonalElevation = 12.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("add_task_dialog")
    ) {
      Column(
        modifier = Modifier.padding(24.dp)
      ) {
        // Title
        Text(
          text = "New Focus Card",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
          ),
          color = TextDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title Input
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("What are we focusing on?") },
          placeholder = { Text("e.g., Code App Architecture") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_task_title"),
          shape = RoundedCornerShape(16.dp),
          textStyle = premiumInputTextStyle,
          colors = getTextFieldColors(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Notes Input
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Notes (Optional)") },
          placeholder = { Text("Add secondary thoughts...") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          textStyle = premiumInputTextStyle,
          colors = getTextFieldColors(),
          maxLines = 3
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Category Selector Section
        Text(
          text = "Category",
          style = MaterialTheme.typography.labelLarge,
          color = TextDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.forEach { category ->
            val isSelected = selectedCategory == category
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) DarkSurface else BorderSoft.copy(alpha = 0.4f))
                .clickable { selectedCategory = category }
                .padding(vertical = 10.dp)
                .testTag("dialog_cat_$category"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = category,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) Color.White else TextMuted
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Priority Selector Section
        Text(
          text = "Priority Level",
          style = MaterialTheme.typography.labelLarge,
          color = TextDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          priorities.forEach { priority ->
            val isSelected = selectedPriority == priority
            val activeColor = when (priority.lowercase()) {
              "high" -> AccentRed
              "medium" -> AccentRed.copy(alpha = 0.8f)
              else -> AccentRed.copy(alpha = 0.6f)
            }
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) activeColor else BorderSoft.copy(alpha = 0.4f))
                .clickable { selectedPriority = priority }
                .padding(vertical = 10.dp)
                .testTag("dialog_priority_$priority"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = priority,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) Color.White else TextMuted
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Action Buttons Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier.testTag("btn_cancel_task")
          ) {
            Text(text = "Cancel", color = TextMuted)
          }

          Spacer(modifier = Modifier.width(12.dp))

          Button(
            onClick = {
              if (title.isNotBlank()) {
                onConfirm(title, notes, selectedCategory, selectedPriority)
              }
            },
            enabled = title.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.testTag("btn_confirm_task")
          ) {
            Text(text = "Add Focus", color = Color.White)
          }
        }
      }
    }
  }
}
