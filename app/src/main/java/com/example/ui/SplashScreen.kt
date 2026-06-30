package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToAuth: () -> Unit, onNavigateToMain: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(2000) // 2 second premium delay
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            onNavigateToMain()
        } else {
            onNavigateToAuth()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F)), // Obsidian background
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(1000))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Code-driven minimalist logo
                Text(
                    text = "V",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 80.sp,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "VIONUX",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 8.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "STUDIO OS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Thin,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                )
            }
        }
    }
}
