package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

import androidx.compose.ui.draw.shadow
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment

@Composable
fun CategoryChips(
  selectedCategory: String?,
  onCategorySelected: (String?) -> Unit,
  modifier: Modifier = Modifier
) {
  val categories = listOf(
    null to "All Focus",
    "Work" to "Work",
    "Personal" to "Personal",
    "Study" to "Study",
    "Creative" to "Creative"
  )

  LazyRow(
    modifier = modifier.testTag("category_filter_row"),
    contentPadding = PaddingValues(horizontal = 20.dp),
    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
  ) {
    items(categories) { (catKey, catLabel) ->
      val isSelected = selectedCategory == catKey

      val chipBgColor by animateColorAsState(
        targetValue = if (isSelected) DarkSurface else Color.White,
        label = "chipBg"
      )

      val chipTextColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else TextDark,
        label = "chipText"
      )

      Surface(
        modifier = Modifier
          .shadow(
            elevation = if (isSelected) 4.dp else 1.dp,
            shape = RoundedCornerShape(24.dp),
            ambientColor = Color.Black.copy(alpha = 0.02f),
            spotColor = Color.Black.copy(alpha = 0.03f)
          )
          .testTag("category_chip_${catKey ?: "all"}"),
        color = chipBgColor,
        shape = RoundedCornerShape(24.dp),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.White)
      ) {
        Box(
          modifier = Modifier
            .clickable { onCategorySelected(catKey) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = catLabel,
            style = MaterialTheme.typography.labelLarge.copy(
              fontSize = 13.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = chipTextColor
          )
        }
      }
    }
  }
}
