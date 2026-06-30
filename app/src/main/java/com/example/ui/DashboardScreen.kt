package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.Transaction
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.components.InsightsAlertsBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
  viewModel: DashboardViewModel,
  plannerViewModel: PlannerViewModel,
  goalViewModel: com.example.ui.GoalViewModel,
  alarmViewModel: com.example.ui.AlarmViewModel,
  academicViewModel: com.example.ui.AcademicViewModel,
  healthViewModel: com.example.ui.HealthViewModel,
  onLogout: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val transactions by viewModel.transactions.collectAsState()
  val totalExpenses by viewModel.totalExpenses.collectAsState()
  val remainingBalance by viewModel.remainingBalance.collectAsState()
  val weeklyTrend by viewModel.weeklyTrend.collectAsState()

  val tasks by plannerViewModel.tasks.collectAsState()
  val completedTasks = tasks.count { it.isCompleted }
  val totalTasks = tasks.size

  var currentTab by remember { mutableStateOf("Home") } // "Home", "List", "Focus", "Stats", "Profile"

  var showAddBottomSheet by remember { mutableStateOf(false) }
  val sheetState = rememberModalBottomSheetState()
  var showInsightsBottomSheet by remember { mutableStateOf(false) }
  val insightsSheetState = rememberModalBottomSheetState()
  val scope = rememberCoroutineScope()

  // Latest transaction details for the banner
  val latestTransaction = transactions.firstOrNull { it.isExpense }
  val latestUpdateText = if (latestTransaction != null) {
    "Latest: ৳${String.format("%.0f", latestTransaction.amount)} spent on ${latestTransaction.title}"
  } else {
    "Latest: No recent spend recorded"
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = Color(0xFFF8F9FA), // premium off-white/light-gray background
    bottomBar = {
      CustomBottomNavWithFab(
        selectedTab = currentTab,
        onTabSelected = { currentTab = it },
        onAddClick = { showAddBottomSheet = true }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentTab) {
        "Home" -> {
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp) // space for floating bottom nav
          ) {
            // 1. Custom App Bar / Header
            item {
              DashboardHeader(
                onNotificationsClick = { showInsightsBottomSheet = true },
                onProfileClick = { currentTab = "Profile" }
              )
            }

            // 2. Balance Section
            item {
              BalanceSection(
                remainingBalance = remainingBalance,
                totalExpenses = totalExpenses
              )
            }

            // 3. Recent Activity Dark Mode Card (Bezier line chart inside)
            item {
              RecentActivityCard(weeklyTrend = weeklyTrend)
            }

            // Dashboard Widgets
            item {
              Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FocusOverviewCard(weeklyTrend = weeklyTrend, modifier = Modifier.weight(1f))
                ProductivityOverviewCard(completed = completedTasks, total = totalTasks, modifier = Modifier.weight(1f))
              }
            }

            // 4. Latest Update Banner
            item {
              LatestUpdateBanner(text = latestUpdateText)
            }

            // 5. Quick Spend Hub
            item {
              QuickSpendHub(
                onQuickSpend = { category, title, amount ->
                  viewModel.addTransaction(
                    title = title,
                    amount = amount,
                    category = category,
                    isExpense = true
                  )
                }
              )
            }

            // 6. Recent Transactions List Header
            item {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Recent Transactions",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                  ),
                  color = TextDark
                )

                if (transactions.isNotEmpty()) {
                  Text(
                    text = "Clear All",
                    style = MaterialTheme.typography.labelLarge.copy(
                      color = AccentRed,
                      fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                      .clickable { viewModel.clearAllTransactions() }
                      .testTag("clear_transactions_btn")
                  )
                }
              }
            }

            // List of transaction items
            if (transactions.isEmpty()) {
              item {
                EmptyTransactionsView()
              }
            } else {
              items(transactions, key = { it.id }) { tx ->
                TransactionItemRow(
                  transaction = tx,
                  onDelete = { viewModel.deleteTransaction(tx) }
                )
              }
            }
          }
        }
        "Stats" -> {
          StatsScreen(
            viewModel = viewModel,
            plannerViewModel = plannerViewModel,
            modifier = Modifier.fillMaxSize()
          )
        }
        "List" -> {
          TransactionLogsScreen(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
          )
        }
        "Focus" -> {
          ProductivityScreen(
            viewModel = plannerViewModel,
            modifier = Modifier.fillMaxSize()
          )
        }
        "Hub" -> {
          AcademicHubScreen(
            viewModel = academicViewModel,
            modifier = Modifier.fillMaxSize()
          )
        }
        "Tools" -> {
          ToolsScreen(
            modifier = Modifier.fillMaxSize()
          )
        }
        "Planner" -> {
          TimeManagementScreen(
            viewModel = plannerViewModel,
            modifier = Modifier.fillMaxSize()
          )
        }
        "Goals" -> {
          GoalTrackerScreen(
            viewModel = goalViewModel,
            modifier = Modifier.fillMaxSize()
          )
        }
        "Finance" -> {
          FinanceScreen(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
          )
        }
        "Health" -> {
          HealthTrackerScreen(
            viewModel = healthViewModel,
            modifier = Modifier.fillMaxSize()
          )
        }
        "Alarms" -> {
          AlarmsScreen(
            viewModel = alarmViewModel,
            modifier = Modifier.fillMaxSize()
          )
        }
        "Profile" -> {
          ProfileScreen(
            viewModel = viewModel,
            onLogout = onLogout,
            modifier = Modifier.fillMaxSize()
          )
        }
      }

      // Add Transaction bottom sheet
      if (showAddBottomSheet) {
        ModalBottomSheet(
          onDismissRequest = { showAddBottomSheet = false },
          sheetState = sheetState,
          containerColor = Color.White,
          shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
          AddTransactionForm(
            onAddTransaction = { title, amount, category ->
              viewModel.addTransaction(title, amount, category, isExpense = true)
              scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                  showAddBottomSheet = false
                }
              }
            },
            onCancel = {
              scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                  showAddBottomSheet = false
                }
              }
            }
          )
        }
      }

      // Insights & Alerts bottom sheet
      if (showInsightsBottomSheet) {
        ModalBottomSheet(
          onDismissRequest = { showInsightsBottomSheet = false },
          sheetState = insightsSheetState,
          containerColor = Color(0xFFF8F9FA), // premium off-white/light-gray background
          shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
          InsightsAlertsBottomSheet(
            onDismiss = {
              scope.launch { insightsSheetState.hide() }.invokeOnCompletion {
                if (!insightsSheetState.isVisible) {
                  showInsightsBottomSheet = false
                }
              }
            }
          )
        }
      }
    }
  }
}

// 1. Custom App Bar / Header
@Composable
fun DashboardHeader(
  onNotificationsClick: () -> Unit,
  onProfileClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 20.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = "WELCOME BACK",
        style = MaterialTheme.typography.labelSmall.copy(
          letterSpacing = 1.sp,
          fontWeight = FontWeight.Bold
        ),
        color = TextMuted
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Rajes Kumar",
        style = MaterialTheme.typography.displayMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 28.sp,
          letterSpacing = (-0.5).sp
        ),
        color = TextDark,
        modifier = Modifier.testTag("user_name_text")
      )
    }

    // Circular Notification Bell
    Box(
      modifier = Modifier
        .size(44.dp)
        .clip(CircleShape)
        .background(Color.White)
        .border(1.dp, BorderSoft, CircleShape)
        .clickable { onNotificationsClick() }
        .testTag("bell_icon_btn"),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Notifications,
        contentDescription = "Notifications",
        tint = TextDark,
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    // Profile Avatar
    Box(
      modifier = Modifier
        .size(44.dp)
        .clip(CircleShape)
        .background(Color.White)
        .border(1.dp, BorderSoft, CircleShape)
        .clickable { onProfileClick() }
        .padding(2.dp)
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .clip(CircleShape)
          .background(Color(0xFFE2E8F0)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "RK",
          style = MaterialTheme.typography.labelLarge.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          ),
          color = Color(0xFF475569)
        )
      }
    }
  }
}

// 2. Balance Section
@Composable
fun BalanceSection(
  remainingBalance: Double,
  totalExpenses: Double
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 12.dp)
  ) {
    Text(
      text = "REMAINING BALANCE",
      style = MaterialTheme.typography.labelSmall.copy(
        letterSpacing = 1.5.sp,
        fontWeight = FontWeight.Bold
      ),
      color = TextMuted
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "৳${String.format("%,.2f", remainingBalance)}",
      style = MaterialTheme.typography.displayLarge.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        letterSpacing = (-1).sp
      ),
      color = TextDark,
      modifier = Modifier.testTag("balance_amount_text")
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "৳${String.format("%,.2f", totalExpenses)} spent of monthly target",
      style = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
      ),
      color = TextMuted
    )
  }
}

// 3. Recent Activity Dark Mode Card (Cubic Bezier Line Chart Inside)
@Composable
fun RecentActivityCard(weeklyTrend: List<Double>) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 16.dp)
      .shadow(
        elevation = 20.dp,
        shape = RoundedCornerShape(32.dp),
        ambientColor = Color.Black.copy(alpha = 0.25f),
        spotColor = Color.Black.copy(alpha = 0.35f)
      )
      .testTag("recent_activity_dark_card"),
    color = Color.Transparent,
    shape = RoundedCornerShape(32.dp)
  ) {
    Column(
      modifier = Modifier
        .drawBehind {
          // Stark Black / deep slate background (matching design)
          drawRect(color = Color(0xFF0C0C0E))
          // Radiant red ambient glow
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(AccentRed.copy(alpha = 0.4f), Color.Transparent),
              center = Offset(size.width * 1.0f, size.height * 0.0f),
              radius = size.width * 0.8f
            )
          )
        }
        .padding(24.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            ),
            color = Color.White
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Last 7 days spend trend",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
            color = Color.White.copy(alpha = 0.5f)
          )
        }

        // Chip: 7D
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text(
            text = "7D",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            ),
            color = Color.White
          )
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Bezier curve chart
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(110.dp)
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val width = size.width
          val height = size.height
          val maxTrend = (weeklyTrend.maxOrNull() ?: 1.0).coerceAtLeast(1.0).toFloat()

          val points = weeklyTrend.mapIndexed { index, value ->
            Offset(
              x = index * (width / 6f),
              y = height - (value.toFloat() / maxTrend * height * 0.7f).coerceAtMost(height) - (height * 0.1f)
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

            // Ambient glowing gradient underneath path
            val fillPath = Path().apply {
              addPath(strokePath)
              lineTo(width, height)
              lineTo(0f, height)
              close()
            }

            drawPath(
              path = fillPath,
              brush = Brush.verticalGradient(
                colors = listOf(AccentRed.copy(alpha = 0.25f), Color.Transparent)
              )
            )

            // Smooth line stroke
            drawPath(
              path = strokePath,
              color = AccentRed,
              style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Glowing dot on high point
            val highPointIndex = weeklyTrend.indexOfMax() ?: 0
            val p4 = points[highPointIndex]
            
            drawCircle(color = AccentRed.copy(alpha = 0.35f), radius = 12.dp.toPx(), center = p4)
            drawCircle(color = AccentRed.copy(alpha = 0.6f), radius = 8.dp.toPx(), center = p4)
            drawCircle(color = Color.White, radius = 4.5.dp.toPx(), center = p4)
          }
        }
      }
    }
  }
}

// 4. Latest Update Banner
@Composable
fun LatestUpdateBanner(text: String) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 4.dp)
      .testTag("latest_update_banner"),
    color = Color.White,
    shape = RoundedCornerShape(20.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // • UPDATE Pill with Red dot
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF0C0C0E))
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          // Little red indicator dot
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(AccentRed)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "UPDATE",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            ),
            color = Color.White
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp
        ),
        color = TextDark,
        modifier = Modifier.weight(1f)
      )

      Icon(
        imageVector = Icons.Default.ArrowForwardIos,
        contentDescription = "Details",
        tint = TextMuted,
        modifier = Modifier.size(12.dp)
      )
    }
  }
}

// 5. Quick Spend Hub
@Composable
fun QuickSpendHub(
  onQuickSpend: (category: String, title: String, amount: Double) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 24.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.TrendingUp,
        contentDescription = null,
        tint = AccentRed,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "QUICK SPEND HUB",
        style = MaterialTheme.typography.labelSmall.copy(
          letterSpacing = 1.5.sp,
          fontWeight = FontWeight.Bold
        ),
        color = TextMuted
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Horizontal Scroll Row of 4 squarish buttons
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 20.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      val buttons = listOf(
        QuickSpendBtnInfo("Food", "Food", 15.0, Icons.Default.Restaurant),
        QuickSpendBtnInfo("Transportation", "Ride", 25.0, Icons.Default.DirectionsBus),
        QuickSpendBtnInfo("Study Materials", "Books", 60.0, Icons.Default.Book),
        QuickSpendBtnInfo("Other", "Snacks", 10.0, Icons.Default.Add)
      )

      buttons.forEach { info ->
        Surface(
          modifier = Modifier
            .size(105.dp)
            .shadow(
              elevation = 2.dp,
              shape = RoundedCornerShape(24.dp),
              ambientColor = Color.Black.copy(alpha = 0.02f),
              spotColor = Color.Black.copy(alpha = 0.04f)
            )
            .testTag("quick_spend_btn_${info.category}"),
          color = Color.White,
          shape = RoundedCornerShape(24.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
        ) {
          Column(
            modifier = Modifier
              .clickable { onQuickSpend(info.category, info.title, info.amount) }
              .padding(14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFF2F2F7)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = info.icon,
                contentDescription = null,
                tint = TextDark,
                modifier = Modifier.size(18.dp)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = info.category,
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              ),
              color = TextDark,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    }
  }
}

data class QuickSpendBtnInfo(
  val category: String,
  val title: String,
  val amount: Double,
  val icon: androidx.compose.ui.graphics.vector.ImageVector
)

// 6. Custom Floating Bottom Navigation Bar + FAB
@Composable
fun CustomBottomNavWithFab(
  selectedTab: String,
  onTabSelected: (String) -> Unit,
  onAddClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(start = 24.dp, end = 24.dp, bottom = 20.dp),
    contentAlignment = Alignment.BottomCenter
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      // Highly rounded dark container
      Surface(
        modifier = Modifier
          .weight(1f)
          .shadow(
            elevation = 16.dp,
            shape = RoundedCornerShape(32.dp),
            ambientColor = Color.Black.copy(alpha = 0.15f),
            spotColor = Color.Black.copy(alpha = 0.25f)
          ),
        color = Color(0xFF0C0C0E),
        shape = RoundedCornerShape(32.dp)
      ) {
        Row(
          modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .horizontalScroll(rememberScrollState()),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val tabs = listOf(
            Triple("Home", Icons.Default.Home, "Home"),
            Triple("List", Icons.Default.ListAlt, "List"),
            Triple("Focus", Icons.Default.Timer, "Focus"),
            Triple("Hub", Icons.Default.School, "Hub"),
            Triple("Tools", Icons.Default.Widgets, "Tools"),
            Triple("Planner", Icons.Default.DateRange, "Planner"),
            Triple("Goals", Icons.Default.Flag, "Goals"),
            Triple("Finance", Icons.Default.AttachMoney, "Finance"),
            Triple("Health", Icons.Default.Favorite, "Health"),
            Triple("Alarms", Icons.Default.Alarm, "Alarms"),
            Triple("Stats", Icons.Default.PieChart, "Stats"),
            Triple("Profile", Icons.Default.Person, "Profile")
          )

          tabs.forEach { (tabId, icon, label) ->
            val isActive = selectedTab == tabId
            if (isActive) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(24.dp))
                  .background(Color.White)
                  .padding(horizontal = 14.dp, vertical = 8.dp)
                  .clickable { onTabSelected(tabId) }
                  .testTag("nav_tab_active_$tabId")
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF0C0C0E),
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge.copy(
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp
                    ),
                    color = Color(0xFF0C0C0E)
                  )
                }
              }
            } else {
              IconButton(
                onClick = { onTabSelected(tabId) },
                modifier = Modifier.testTag("nav_tab_inactive_$tabId")
              ) {
                Icon(
                  imageVector = icon,
                  contentDescription = label,
                  tint = Color.White.copy(alpha = 0.45f),
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Floating Action Button
      FloatingActionButton(
        onClick = onAddClick,
        containerColor = AccentRed,
        contentColor = Color.White,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(12.dp),
        modifier = Modifier
          .size(60.dp)
          .testTag("floating_add_transaction_btn")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Add custom transaction",
          modifier = Modifier.size(26.dp)
        )
      }
    }
  }
}

// Individual Transaction row
@Composable
fun TransactionItemRow(
  transaction: Transaction,
  onDelete: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 6.dp)
      .shadow(
        elevation = 2.dp,
        shape = RoundedCornerShape(24.dp),
        ambientColor = Color.Black.copy(alpha = 0.01f),
        spotColor = Color.Black.copy(alpha = 0.02f)
      )
      .testTag("transaction_item_${transaction.id}"),
    color = Color.White,
    shape = RoundedCornerShape(24.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Category Icon base
      val icon = when (transaction.category) {
        "Food" -> Icons.Default.Restaurant
        "Transportation" -> Icons.Default.DirectionsBus
        "Study Materials" -> Icons.Default.Book
        else -> Icons.Default.Category
      }

      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0xFFF2F2F7)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = TextDark,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.width(16.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = transaction.title,
          style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          ),
          color = TextDark
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${transaction.category} • Today",
          style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
          color = TextMuted
        )
      }

      // Amount with - sign or + sign
      Text(
        text = if (transaction.isExpense) "-৳${String.format("%.2f", transaction.amount)}" else "+৳${String.format("%.2f", transaction.amount)}",
        style = MaterialTheme.typography.bodyLarge.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        ),
        color = if (transaction.isExpense) TextDark else Color(0xFF10B981) // emerald green
      )

      Spacer(modifier = Modifier.width(8.dp))

      IconButton(
        onClick = onDelete,
        modifier = Modifier.testTag("delete_tx_btn_${transaction.id}")
      ) {
        Icon(
          imageVector = Icons.Default.DeleteOutline,
          contentDescription = "Delete transaction",
          tint = TextMuted.copy(alpha = 0.6f),
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

// Bottom sheet input Form
@Composable
fun AddTransactionForm(
  onAddTransaction: (title: String, amount: Double, category: String) -> Unit,
  onCancel: () -> Unit
) {
  var title by remember { mutableStateOf("") }
  var amountStr by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("Food") }

  val categories = listOf("Food", "Transportation", "Study Materials", "Other")

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(24.dp)
  ) {
    Text(
      text = "Add Transaction",
      style = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
      ),
      color = TextDark
    )

    Spacer(modifier = Modifier.height(20.dp))

    OutlinedTextField(
      value = title,
      onValueChange = { title = it },
      label = { Text("Where did you spend?") },
      placeholder = { Text("e.g., Starbucks Coffee") },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("input_tx_title"),
      shape = RoundedCornerShape(16.dp),
      textStyle = premiumInputTextStyle,
      colors = getTextFieldColors(),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(14.dp))

    OutlinedTextField(
      value = amountStr,
      onValueChange = { amountStr = it },
      label = { Text("Amount (৳)") },
      placeholder = { Text("e.g., 250.00") },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("input_tx_amount"),
      shape = RoundedCornerShape(16.dp),
      textStyle = premiumInputTextStyle,
      colors = getTextFieldColors(),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(20.dp))

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
            .testTag("tx_sheet_cat_$category"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = when (category) {
              "Study Materials" -> "Study"
              else -> category
            },
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

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.End
    ) {
      Button(
        onClick = onCancel,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = Modifier.testTag("btn_cancel_tx_sheet")
      ) {
        Text(text = "Cancel", color = TextMuted)
      }

      Spacer(modifier = Modifier.width(12.dp))

      val amount = amountStr.toDoubleOrNull() ?: 0.0
      Button(
        onClick = {
          if (title.isNotBlank() && amount > 0) {
            onAddTransaction(title, amount, selectedCategory)
          }
        },
        enabled = title.isNotBlank() && amount > 0,
        colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("btn_confirm_tx_sheet")
      ) {
        Text(text = "Add Spend", color = Color.White)
      }
    }
  }
}

@Composable
fun EmptyTransactionsView() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 32.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "All Pristine",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = TextDark
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = "Tap Quick Spend Hub or the red action button to add logs.",
      style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
      color = TextMuted,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 24.dp)
    )
  }
}

