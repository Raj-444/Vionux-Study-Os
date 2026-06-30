package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun InsightsAlertsBottomSheet(
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(Color(0xFFF8F9FA)) // premium off-white/light-gray background
      .testTag("insights_alerts_sheet_content")
  ) {
    // 1. Header Section
    InsightsHeader(onClose = onDismiss)

    Spacer(modifier = Modifier.height(16.dp))

    // 2. Budget Alerts Section
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
    ) {
      Text(
        text = "BUDGET ALERTS",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.2.sp
        ),
        color = TextMuted,
        modifier = Modifier.padding(bottom = 12.dp)
      )

      BudgetAlertCard()
    }

    Spacer(modifier = Modifier.height(24.dp))

    // 3. Spending Analysis Section
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
    ) {
      Text(
        text = "SPENDING ANALYSIS",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.2.sp
        ),
        color = TextMuted,
        modifier = Modifier.padding(bottom = 12.dp)
      )

      SpendingAnalysisList()
    }

    Spacer(modifier = Modifier.height(32.dp))
  }
}

// 1. Header Component
@Composable
fun InsightsHeader(
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 20.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = "Insights & Alerts",
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 22.sp,
          letterSpacing = (-0.5).sp
        ),
        color = TextDark,
        modifier = Modifier.testTag("insights_header_title")
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "Live analytics updates",
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
        color = TextMuted,
        modifier = Modifier.testTag("insights_header_subtitle")
      )
    }

    // Small close icon on trailing side
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(Color(0xFFEFEFF4))
        .clickable { onClose() }
        .testTag("insights_close_btn"),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Close,
        contentDescription = "Close",
        tint = TextDark,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

// 2. Budget Alert Card
@Composable
fun BudgetAlertCard(
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("budget_alert_card"),
    color = Color.White,
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(Color(0xFFE8F5E9)), // Very soft light green background
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = "On Track",
          tint = Color(0xFF4CAF50), // Standard green
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column {
        Text(
          text = "Budget On Track",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          ),
          color = TextDark
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "75% used — ৳975 remaining this month.",
          style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 13.sp,
            lineHeight = 18.sp
          ),
          color = TextMuted
        )
      }
    }
  }
}

// 3. Spending Analysis Section List
@Composable
fun SpendingAnalysisList(
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Card 1: Top Category
    SpendingAnalysisCard(
      icon = Icons.Default.BarChart,
      iconBgColor = Color(0xFFE8F0FE), // soft blue background
      iconTintColor = Color(0xFF1A73E8),
      title = "Top Category: Food",
      description = "৳1,715 spent (56% of total). Consider diversifying your spending."
    )

    // Card 2: Avg. Transaction
    SpendingAnalysisCard(
      icon = Icons.Default.Calculate,
      iconBgColor = Color(0xFFF3E5F5), // soft purple background
      iconTintColor = Color(0xFF9C27B0),
      title = "Avg. Transaction: ৳80",
      description = "Across 38 total transactions."
    )

    // Card 3: Active Categories
    SpendingAnalysisCard(
      icon = Icons.Default.Category,
      iconBgColor = Color(0xFFE0F7FA), // soft cyan/teal background
      iconTintColor = Color(0xFF00ACC1),
      title = "4 Active Categories",
      description = "Food, Other, Transportation, Study Materials"
    )
  }
}

// Reusable Card for Spending Analysis Items
@Composable
fun SpendingAnalysisCard(
  icon: ImageVector,
  iconBgColor: Color,
  iconTintColor: Color,
  title: String,
  description: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("spending_analysis_card_${title.lowercase().replace(" ", "_")}"),
    color = Color.White,
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(iconBgColor),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = iconTintColor,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          ),
          color = TextDark
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = description,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 13.sp,
            lineHeight = 18.sp
          ),
          color = TextMuted
        )
      }
    }
  }
}
