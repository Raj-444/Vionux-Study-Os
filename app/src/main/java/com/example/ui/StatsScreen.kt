package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TextDark
import androidx.compose.runtime.collectAsState
import com.example.ui.theme.TextMuted

@Composable
fun StatsScreen(
  viewModel: DashboardViewModel,
  plannerViewModel: PlannerViewModel,
  modifier: Modifier = Modifier
) {
  val transactions by viewModel.transactions.collectAsState()
  val weeklyTrend by viewModel.weeklyTrend.collectAsState()
  val monthlyForecast by viewModel.monthlyForecast.collectAsState()
  val totalExpenses by viewModel.totalExpenses.collectAsState()
  val monthlyTarget = viewModel.monthlyTarget

  val tasks by plannerViewModel.tasks.collectAsState()
  val completedTasks = tasks.count { it.isCompleted }
  val totalTasks = tasks.size

  var selectedTab by remember { mutableStateOf("Week") } // Week or Month Toggle

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF8F9FA)) // premium light gray background
      .statusBarsPadding() // Top SafeArea padding
      .verticalScroll(rememberScrollState())
      .padding(bottom = 120.dp) // space for custom floating bottom nav
  ) {
    // 1. Header Section
    StatsHeader(
      selectedTab = selectedTab,
      onTabSelected = { selectedTab = it }
    )

    Spacer(modifier = Modifier.height(16.dp))

    // 2. Line Chart Section (Light Mode)
    ChartSection(weeklyTrend = weeklyTrend)

    Spacer(modifier = Modifier.height(12.dp))

    // 3. Peak & Low Indicator
    val peakValue = weeklyTrend.maxOrNull() ?: 0.0
    val lowValue = weeklyTrend.minOrNull() ?: 0.0
    PeakLowCard(peak = peakValue, low = lowValue)

    Spacer(modifier = Modifier.height(24.dp))

    // 4, 5, 6. Monthly Spending Forecast Card
    ForecastCard(
      spent = totalExpenses,
      forecast = monthlyForecast,
      target = monthlyTarget
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Productivity Stats
    ProductivityStatsSection(completed = completedTasks, total = totalTasks)
  }
}

@Composable
fun ProductivityStatsSection(completed: Int, total: Int) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp),
    color = Color.White,
    shape = RoundedCornerShape(24.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Text(
        text = "Productivity Overview",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = TextDark
      )
      Spacer(modifier = Modifier.height(16.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(60.dp)
            .drawBehind {
              drawCircle(color = AccentRed.copy(alpha = 0.1f))
              val sweep = if (total > 0) (completed.toFloat() / total.toFloat()) * 360f else 0f
              drawArc(
                color = AccentRed,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
              )
            },
          contentAlignment = Alignment.Center
        ) {
          val percentage = if (total > 0) (completed * 100 / total) else 0
          Text("$percentage%", fontWeight = FontWeight.Bold, color = AccentRed, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
          Text("$completed / $total tasks completed", fontWeight = FontWeight.Bold, color = TextDark)
          Text("Keep up the momentum!", fontSize = 12.sp, color = TextMuted)
        }
      }
    }
  }
}

// 1. Header Section
@Composable
fun StatsHeader(
  selectedTab: String,
  onTabSelected: (String) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 20.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = "Daily Spend Trend",
        style = MaterialTheme.typography.displayMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 28.sp,
          letterSpacing = (-0.5).sp
        ),
        color = TextDark,
        modifier = Modifier.testTag("stats_header_title")
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Last 7 days spend activity",
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
        color = TextMuted,
        modifier = Modifier.testTag("stats_header_subtitle")
      )
    }

    // Toggle switch pill
    Row(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xFFEFEFF4))
        .padding(4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Week Tab
      val isWeekSelected = selectedTab == "Week"
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(if (isWeekSelected) Color(0xFF0C0C0E) else Color.Transparent)
          .clickable { onTabSelected("Week") }
          .padding(horizontal = 14.dp, vertical = 6.dp)
          .testTag("stats_toggle_week"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Week",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          ),
          color = if (isWeekSelected) Color.White else Color(0xFF0C0C0E)
        )
      }

      // Month Tab
      val isMonthSelected = selectedTab == "Month"
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(if (isMonthSelected) Color(0xFF0C0C0E) else Color.Transparent)
          .clickable { onTabSelected("Month") }
          .padding(horizontal = 14.dp, vertical = 6.dp)
          .testTag("stats_toggle_month"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Month",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          ),
          color = if (isMonthSelected) Color.White else Color(0xFF0C0C0E)
        )
      }
    }
  }
}

// 2. Line Chart Section
@Composable
fun ChartSection(weeklyTrend: List<Double>) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp)
      .shadow(
        elevation = 2.dp,
        shape = RoundedCornerShape(24.dp),
        ambientColor = Color.Black.copy(alpha = 0.01f),
        spotColor = Color.Black.copy(alpha = 0.02f)
      )
      .testTag("stats_chart_surface"),
    color = Color.White,
    shape = RoundedCornerShape(24.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
  ) {
    Column(
      modifier = Modifier.padding(20.dp)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val width = size.width
          val height = size.height
          val maxTrend = (weeklyTrend.maxOrNull() ?: 1.0).coerceAtLeast(1.0).toFloat()

          val points = weeklyTrend.mapIndexed { index, value ->
            Offset(
              x = index * (width / 6f),
              y = height - (value.toFloat() / maxTrend * height * 0.8f).coerceAtMost(height)
            )
          }

          if (points.size >= 2) {
            val strokePath = Path().apply {
              moveTo(points[0].x, points[0].y)
              for (i in 1 until points.size) {
                val prev = points[i - 1]
                val curr = points[i]
                cubicTo(
                  (prev.x + curr.x) / 2, prev.y,
                  (prev.x + curr.x) / 2, curr.y,
                  curr.x, curr.y
                )
              }
            }

            val fillPath = Path().apply {
              addPath(strokePath)
              lineTo(width, height)
              lineTo(0f, height)
              close()
            }

            drawPath(
              path = fillPath,
              brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0C0C0E).copy(alpha = 0.05f), Color.Transparent)
              )
            )

            drawPath(
              path = strokePath,
              color = Color(0xFF0C0C0E),
              style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // High point indicator
            val highPointIndex = weeklyTrend.indexOfMax() ?: 0
            val p4 = points[highPointIndex]
            
            drawCircle(color = AccentRed.copy(alpha = 0.25f), radius = 12.dp.toPx(), center = p4)
            drawCircle(color = AccentRed.copy(alpha = 0.5f), radius = 8.dp.toPx(), center = p4)
            drawCircle(color = AccentRed, radius = 4.5.dp.toPx(), center = p4)
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        val days = listOf("Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon")
        days.forEach { day ->
          Text(
            text = day,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }
  }
}

// 3. Peak & Low Indicator
@Composable
fun PeakLowCard(peak: Double, low: Double) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp)
      .testTag("stats_peak_low_card"),
    color = Color(0xFFF2F2F7), // very light gray
    shape = RoundedCornerShape(16.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Peak", tint = AccentRed, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Peak: ৳${String.format("%.0f", peak)}",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
          color = AccentRed
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Low", tint = Color(0xFF8E8E93), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Low: ৳${String.format("%.0f", low)}",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
          color = Color(0xFF8E8E93)
        )
      }
    }
  }
}

// 4, 5, 6. Monthly Spending Forecast Card
@Composable
fun ForecastCard(spent: Double, forecast: Double, target: Double) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp)
      .testTag("stats_forecast_card"),
    color = Color(0xFFF2F2F7), // slightly darker light-gray card
    shape = RoundedCornerShape(24.dp)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Color.White), contentAlignment = Alignment.Center) {
          Icon(imageVector = Icons.Default.Warning, contentDescription = "Forecast Warning", tint = Color(0xFFFF9500), modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
          Text(text = "Monthly Spending Forecast", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp), color = TextDark)
          Spacer(modifier = Modifier.height(2.dp))
          Text(text = "Est. month-end spend based on your daily speed", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp), color = TextMuted)
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Actual Spend (So Far)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp), color = TextDark)
        Text(text = "৳${String.format("%.0f", spent)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp), color = Color(0xFFD00000))
      }

      Spacer(modifier = Modifier.height(8.dp))

      Box(modifier = Modifier.fillMaxWidth().height(14.dp).clip(RoundedCornerShape(7.dp)).background(Color(0xFFE5E5EA))) {
        Row(modifier = Modifier.fillMaxSize()) {
          val spentRatio = (spent / target).toFloat().coerceIn(0f, 1f)
          val forecastRatio = ((forecast - spent) / target).toFloat().coerceIn(0f, 1f - spentRatio)
          
          Box(modifier = Modifier.weight(spentRatio.coerceAtLeast(0.01f)).fillMaxHeight().background(Color(0xFFD00000)))
          if (forecastRatio > 0) {
            Box(modifier = Modifier.weight(forecastRatio).fillMaxHeight().background(Color(0xFFFFA29B)))
          }
          val remaining = 1f - spentRatio - forecastRatio
          if (remaining > 0) {
             Spacer(modifier = Modifier.weight(remaining.coerceAtLeast(0.01f)))
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "0", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp), color = TextMuted)
        Text(text = "Monthly Budget Limit: ৳${String.format("%.0f", target)}", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold), color = TextDark)
      }

      Spacer(modifier = Modifier.height(24.dp))

      val dailyPace = spent / 30 // Simplified
      val estOverrun = forecast - target

      Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
          Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.Speed, contentDescription = "Pace", tint = TextDark, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "৳${String.format("%.0f", dailyPace)} / day", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp), color = TextDark)
            Text(text = "Daily Pace", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp), color = TextMuted)
          }
          Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFE5E5EA)))
          Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Month End", tint = TextDark, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "৳${String.format("%.0f", forecast)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp), color = TextDark)
            Text(text = "Est. Month End", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp), color = TextMuted)
          }
          Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFE5E5EA)))
          Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.Warning, contentDescription = "Overrun", tint = if (estOverrun > 0) Color(0xFFD00000) else Color(0xFF34C759), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "${if (estOverrun > 0) "-" else "+"}৳${String.format("%.0f", Math.abs(estOverrun))}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp), color = if (estOverrun > 0) Color(0xFFD00000) else Color(0xFF34C759))
            Text(text = "Est. ${if (estOverrun > 0) "Overrun" else "Savings"}", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp), color = TextMuted)
          }
        }
      }

      if (estOverrun > 0) {
        Spacer(modifier = Modifier.height(16.dp))
        Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFFFECEB), shape = RoundedCornerShape(16.dp)) {
          Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Icon(imageVector = Icons.Default.Warning, contentDescription = "Alert", tint = Color(0xFFD00000), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Warning: You're spending too fast! At this pace, you'll exceed your budget by ৳${String.format("%.0f", estOverrun)}.", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp), color = Color(0xFFD00000))
          }
        }
      }
    }
  }
}
