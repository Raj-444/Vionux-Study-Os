package com.example.ui

import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

fun List<Double>.indexOfMax(): Int? {
  if (isEmpty()) return null
  var maxIndex = 0
  var maxVal = this[0]
  for (i in 1 until size) {
    if (this[i] > maxVal) {
      maxVal = this[i]
      maxIndex = i
    }
  }
  return maxIndex
}

@Composable
fun getTextFieldColors(): TextFieldColors {
  return OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextDark,
    unfocusedTextColor = TextDark,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    cursorColor = AccentRed,
    focusedBorderColor = AccentRed,
    unfocusedBorderColor = BorderSoft,
    focusedLabelColor = AccentRed,
    unfocusedLabelColor = TextMuted,
    focusedPlaceholderColor = TextMuted,
    unfocusedPlaceholderColor = TextMuted
  )
}

val premiumInputTextStyle = TextStyle(
  fontFamily = FontFamily.SansSerif,
  fontWeight = FontWeight.Normal,
  fontSize = 16.sp,
  color = TextDark
)
