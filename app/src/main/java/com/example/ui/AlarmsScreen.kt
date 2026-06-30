package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun AlarmsScreen(
    viewModel: AlarmViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val alarms by viewModel.alarms.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(alarms) {
        alarms.forEach { alarm ->
            if (alarm.isEnabled) {
                com.example.receiver.AlarmHelper.scheduleAlarm(context, alarm)
            } else {
                com.example.receiver.AlarmHelper.cancelAlarm(context, alarm)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { 
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Alarms", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold, fontSize = 28.sp), color = TextDark)
                    IconButton(onClick = { showAddDialog = true }, modifier = Modifier.background(AccentRed, androidx.compose.foundation.shape.CircleShape).size(40.dp)) {
                        Icon(androidx.compose.material.icons.Icons.Default.Add, null, tint = Color.White)
                    }
                }
            }
            if (alarms.isEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No Alarms Set", fontWeight = FontWeight.Bold, color = TextDark)
                        Text("Never miss a session. Set your rhythm.", color = TextMuted, fontSize = 14.sp)
                    }
                }
            } else {
                items(alarms.size) { index ->
                    val alarm = alarms[index]
                    AlarmCard(
                        alarm = alarm,
                        onToggle = { viewModel.toggleAlarm(alarm) },
                        onDelete = {
                            com.example.receiver.AlarmHelper.cancelAlarm(context, alarm)
                            viewModel.deleteAlarm(alarm)
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddAlarmDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { time, label, days ->
                viewModel.addAlarm(time, label, days)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddAlarmDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var time by remember { mutableStateOf("07:00 AM") }
    var label by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("Everyday") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Alarm", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time (e.g., 07:00 AM)") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = premiumInputTextStyle,
                    colors = getTextFieldColors()
                )
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Label") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = premiumInputTextStyle,
                    colors = getTextFieldColors()
                )
                OutlinedTextField(
                    value = days,
                    onValueChange = { days = it },
                    label = { Text("Repeat Days") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = premiumInputTextStyle,
                    colors = getTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(time, label, days) }, colors = ButtonDefaults.buttonColors(containerColor = AccentRed)) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AlarmCard(alarm: com.example.data.Alarm, onToggle: () -> Unit, onDelete: () -> Unit) {
    Surface(color = Color(0xFFF2F2F7), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(alarm.time, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text(alarm.label, fontSize = 14.sp, color = TextMuted)
                Text(alarm.repeatDays, fontSize = 10.sp, color = TextMuted, modifier = Modifier.padding(top = 4.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDelete) {
                    Icon(androidx.compose.material.icons.Icons.Default.DeleteOutline, null, tint = AccentRed.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                }
                Switch(checked = alarm.isEnabled, onCheckedChange = { onToggle() }, colors = SwitchDefaults.colors(checkedThumbColor = AccentRed, checkedTrackColor = AccentRed.copy(alpha = 0.5f)))
            }
        }
    }
}
