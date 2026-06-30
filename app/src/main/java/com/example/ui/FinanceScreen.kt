package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun FinanceScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(Color(0xFFF8F9FA)).padding(16.dp)) {
        Text("Finance", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold, fontSize = 28.sp), color = TextDark)
        Spacer(modifier = Modifier.height(16.dp))
        MoneyTrackerExtended(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun MoneyTrackerExtended(modifier: Modifier = Modifier) {
    var selectedTabIndex by remember { mutableIntStateOf(2) } // Expenses is default
    val tabs = listOf("Lent", "Borrowed", "Expenses", "Settled")

    Column(modifier = modifier) {
        TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.Transparent, contentColor = AccentRed) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTabIndex == 2) { // Expenses
            Column {
                // Pie Chart Placeholder
                Surface(color = Color(0xFFF2F2F7), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth().height(180.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Pie Chart Placeholder", color = TextMuted)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Person-wise tracking", style = MaterialTheme.typography.titleMedium, color = TextDark)
                    IconButton(onClick = { /* Export */ }, modifier = Modifier.background(Color(0xFFE5E5EA), RoundedCornerShape(12.dp)).size(36.dp)) {
                        Icon(Icons.Default.FileDownload, null, tint = TextDark, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(3) { index ->
                        Surface(color = Color.White, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Person ${index + 1}", fontWeight = FontWeight.Bold)
                                Text("\$${(index + 1) * 50}.00", color = AccentRed)
                            }
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text("Tab: ${tabs[selectedTabIndex]}", color = TextMuted)
            }
        }
    }
}
