package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun FocusOverviewCard(weeklyTrend: List<Double>, modifier: Modifier = Modifier) {
    Surface(color = Color(0xFFF2F2F7), shape = RoundedCornerShape(24.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White), modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Weekly Spend", fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(16.dp))
            // Bar chart
            Row(modifier = Modifier.fillMaxWidth().height(80.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                val maxTrend = weeklyTrend.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0
                weeklyTrend.forEach { value ->
                    val heightRatio = (value / maxTrend).toFloat().coerceIn(0.1f, 1f)
                    Box(modifier = Modifier.weight(1f).fillMaxHeight(heightRatio).background(AccentRed.copy(alpha = 0.5f), RoundedCornerShape(4.dp)))
                }
            }
        }
    }
}

@Composable
fun ProductivityOverviewCard(completed: Int, total: Int, modifier: Modifier = Modifier) {
    Surface(color = Color(0xFFF2F2F7), shape = RoundedCornerShape(24.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White), modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            val percentage = if (total > 0) (completed * 100 / total) else 0
            Box(modifier = Modifier.size(60.dp).background(AccentRed.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                Text("$percentage%", fontWeight = FontWeight.Bold, color = AccentRed)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Daily Target", fontWeight = FontWeight.Bold, color = TextDark)
                Text("$completed/$total tasks achieved", fontSize = 12.sp, color = TextMuted)
            }
        }
    }
}
