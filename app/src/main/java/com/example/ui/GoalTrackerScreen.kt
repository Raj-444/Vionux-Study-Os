package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
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
import com.example.data.Counter
import com.example.data.Roadmap

@Composable
fun GoalTrackerScreen(
    viewModel: GoalViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val roadmaps by viewModel.roadmaps.collectAsState()
    val counters by viewModel.counters.collectAsState()
    
    var showAddRoadmapDialog by remember { mutableStateOf(false) }
    var showAddCounterDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .padding(bottom = 100.dp)
    ) {
        // Header
        Text("Goal Tracker", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold, fontSize = 28.sp), color = TextDark)
        Spacer(modifier = Modifier.height(24.dp))

        // 1. Roadmaps Section
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("My Roadmaps", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextDark)
            IconButton(
                onClick = { showAddRoadmapDialog = true },
                modifier = Modifier.background(AccentRed, CircleShape).size(32.dp)
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        if (roadmaps.isEmpty()) {
            EmptySectionView("No roadmaps yet", "Plan your future and track your progress here.")
        } else {
            roadmaps.forEach { roadmap ->
                RoadmapCard(roadmap, onUpdate = { viewModel.updateRoadmap(it) }, onDelete = { viewModel.deleteRoadmap(roadmap) })
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Counters Section
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Custom Counters", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextDark)
            IconButton(
                onClick = {
                    showAddCounterDialog = true
                },
                modifier = Modifier.background(Color(0xFFE5E5EA), CircleShape).size(32.dp)
            ) {
                Icon(Icons.Default.Add, null, tint = TextDark, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        if (counters.isEmpty()) {
            EmptySectionView("No counters", "Track daily habits or simple numbers with precision.")
        } else {
            // Simple Grid implementation
            val rowCount = (counters.size + 1) / 2
            for (i in 0 until rowCount) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    val index1 = i * 2
                    val index2 = i * 2 + 1
                    
                    Box(modifier = Modifier.weight(1f)) {
                        val counter = counters[index1]
                        CounterCard(
                            counter = counter,
                            onValueChange = { newVal ->
                                viewModel.updateCounter(counter, newVal)
                            },
                            onDelete = {
                                viewModel.deleteCounter(counter)
                            }
                        )
                    }
                    if (index2 < counters.size) {
                        Box(modifier = Modifier.weight(1f)) {
                            val counter = counters[index2]
                            CounterCard(
                                counter = counter,
                                onValueChange = { newVal ->
                                    viewModel.updateCounter(counter, newVal)
                                },
                                onDelete = {
                                    viewModel.deleteCounter(counter)
                                }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showAddCounterDialog) {
        AddCounterDialog(
            onDismiss = { showAddCounterDialog = false },
            onConfirm = { title ->
                viewModel.addCounter(title)
                showAddCounterDialog = false
            }
        )
    }
}

@Composable
fun AddCounterDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Counter", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Counter Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title) },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun AddRoadmapDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Roadmap", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = premiumInputTextStyle,
                    colors = getTextFieldColors()
                )
                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Deadline (e.g., June 30)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = premiumInputTextStyle,
                    colors = getTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, deadline) },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun EmptySectionView(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            color = Color(0xFFF2F2F7),
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Inbox, null, tint = Color.LightGray, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(title, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 14.sp)
        Text(subtitle, color = TextMuted, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun RoadmapCard(roadmap: com.example.data.Roadmap, onUpdate: (com.example.data.Roadmap) -> Unit, onDelete: () -> Unit) {
    val subtasks = roadmap.subtasks.split(",").filter { it.isNotEmpty() }
    
    Surface(color = Color(0xFFF2F2F7), shape = RoundedCornerShape(24.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(roadmap.title, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
                    Text(roadmap.deadline, color = TextMuted, fontSize = 12.sp)
                }
                Row {
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.DeleteOutline, null, tint = AccentRed, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(AccentRed).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("${(roadmap.progress * 100).toInt()}%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(progress = roadmap.progress, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)), color = AccentRed, trackColor = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            // Subtasks
            subtasks.forEach { subtask ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckBoxOutlineBlank,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextMuted
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(subtask, fontSize = 12.sp, color = TextDark)
                }
            }
            IconButton(onClick = { /* Add subtask logic */ }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun CounterCard(counter: Counter, onValueChange: (Int) -> Unit, onDelete: () -> Unit) {
    var count by remember { mutableIntStateOf(counter.value) }

    LaunchedEffect(counter.value) {
        count = counter.value
    }
    
    Surface(color = Color(0xFFF2F2F7), shape = RoundedCornerShape(24.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(counter.title, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(8.dp))
            Text(count.toString(), fontWeight = FontWeight.Bold, color = TextDark, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { 
                    count++
                    onValueChange(count)
                }, colors = ButtonDefaults.buttonColors(containerColor = AccentRed), shape = CircleShape, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(36.dp)) { Icon(Icons.Default.Add, null) }
                Button(onClick = { 
                    count--
                    onValueChange(count)
                }, colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = TextMuted), shape = CircleShape, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(36.dp)) { Icon(Icons.Default.Remove, null) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { 
                    count = 0
                    onValueChange(count)
                }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextMuted
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = AccentRed
                    )
                }
            }
        }
    }
}
