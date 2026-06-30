package com.example.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  viewModel: DashboardViewModel,
  onLogout: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  // State values for profile customization
  var profileName by remember { mutableStateOf("Rajes Kumar") }
  var isEditingName by remember { mutableStateOf(false) }
  var budgetTargetText by remember { mutableStateOf("4000.0") }
  var biometricEnabled by remember { mutableStateOf(true) }

  val auth = com.google.firebase.auth.FirebaseAuth.getInstance()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF8F9FA)) // Premium off-white background
      .verticalScroll(scrollState)
      .padding(horizontal = 24.dp)
      .padding(top = 24.dp, bottom = 120.dp) // padding at bottom for bottom nav
      .testTag("profile_screen_content")
  ) {
    // ... header ...
    // 1. Header Section
    Column(modifier = Modifier.fillMaxWidth()) {
      Text(
        text = "Personal Profile",
        style = MaterialTheme.typography.displayMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 28.sp,
          letterSpacing = (-0.5).sp
        ),
        color = TextDark,
        modifier = Modifier.testTag("profile_header_title")
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Customize settings, security, and developer credits.",
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
        color = TextMuted,
        modifier = Modifier.testTag("profile_header_subtitle")
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // 2. User Profile Card
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("user_profile_card"),
      color = Color(0xFFF2F2F7), // Light gray card background
      shape = RoundedCornerShape(24.dp)
    ) {
      Row(
        modifier = Modifier.padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Circular profile avatar on the left
        Box(
          modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(AccentRed)
            .testTag("user_avatar_badge"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "RK",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White,
              fontSize = 20.sp
            )
          )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
          if (isEditingName) {
            OutlinedTextField(
              value = profileName,
              onValueChange = { profileName = it },
              singleLine = true,
              textStyle = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextDark
              ),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextDark,
                unfocusedTextColor = TextDark,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = AccentRed,
                unfocusedBorderColor = BorderSoft,
                cursorColor = AccentRed
              ),
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("edit_profile_name_input"),
              trailingIcon = {
                Icon(
                  imageVector = Icons.Default.Edit,
                  contentDescription = "Save name",
                  tint = AccentRed,
                  modifier = Modifier
                    .size(18.dp)
                    .clickable { isEditingName = false }
                )
              }
            )
          } else {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = profileName,
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 20.sp
                ),
                color = TextDark,
                modifier = Modifier.testTag("profile_name_text")
              )
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Name",
                tint = AccentRed,
                modifier = Modifier
                  .size(16.dp)
                  .clickable { isEditingName = true }
                  .testTag("edit_name_pen_icon")
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Smart Saver Elite Badge with red shield icon
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(AccentRed.copy(alpha = 0.1f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .testTag("smart_saver_elite_badge")
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = "Elite tier",
              tint = AccentRed,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Smart Saver Elite",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              ),
              color = AccentRed
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 3. Monthly Savings Target Card
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("savings_target_card"),
      color = Color(0xFFF2F2F7), // Light gray card
      shape = RoundedCornerShape(24.dp)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          text = "Monthly Savings Target",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          ),
          color = TextDark
        )

        Spacer(modifier = Modifier.height(14.dp))

        // White text field resembling an input box
        OutlinedTextField(
          value = budgetTargetText,
          onValueChange = { budgetTargetText = it },
          label = { Text("Budget Target (৳)", color = TextMuted) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          textStyle = premiumInputTextStyle,
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = AccentRed,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = TextDark,
            unfocusedTextColor = TextDark,
            cursorColor = AccentRed
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("budget_target_input")
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 4. App Security Card
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("app_security_card"),
      color = Color(0xFFF2F2F7), // Light gray card
      shape = RoundedCornerShape(24.dp)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          text = "App Security",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          ),
          color = TextDark
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AccentRed.copy(alpha = 0.12f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = "Biometrics icon",
                tint = AccentRed,
                modifier = Modifier.size(20.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = "Biometric Privacy",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = TextDark
              )
              Text(
                text = "Require scan on app start",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
              )
            }
          }

          // Red active switch/toggle on the far right
          Switch(
            checked = biometricEnabled,
            onCheckedChange = { biometricEnabled = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = AccentRed,
              uncheckedThumbColor = Color.White,
              uncheckedTrackColor = Color(0xFFE5E5EA)
            ),
            modifier = Modifier.testTag("biometric_privacy_switch")
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 5. Account Management Card
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("account_management_card"),
      color = Color(0xFFF2F2F7), // Light gray card
      shape = RoundedCornerShape(24.dp)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          text = "Account Management",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          ),
          color = TextDark
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Full-width vibrant red button with trash icon and white text "Wipe & Reset App Data"
        Button(
          onClick = {
            viewModel.clearAllTransactions()
            Toast.makeText(context, "All local app data cleared successfully!", Toast.LENGTH_LONG).show()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = AccentRed,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("wipe_data_btn"),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Wipe logs",
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Wipe & Reset App Data",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Functional Logout Button
        Button(
          onClick = {
            auth.signOut()
            onLogout()
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = AccentRed
          ),
          border = BorderStroke(1.dp, AccentRed.copy(alpha = 0.3f)),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("logout_btn"),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
          Text(
            text = "Logout from VioNux",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // 6. Developer Credits Card
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("developer_credits_card"),
      color = Color(0xFFF2F2F7), // Light gray card
      shape = RoundedCornerShape(24.dp)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "VioNux",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            letterSpacing = 0.5.sp
          ),
          color = AccentRed,
          modifier = Modifier.testTag("developer_title")
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Developed & Created by Rajes Kumar",
          style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
          color = TextMuted,
          modifier = Modifier.testTag("developer_subtitle")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Small textual links in red
        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "GitHub",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = AccentRed,
            modifier = Modifier
              .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Raj-444"))
                context.startActivity(intent)
              }
              .testTag("link_github")
          )

          Box(
            modifier = Modifier
              .size(4.dp)
              .clip(CircleShape)
              .background(TextMuted)
          )

          Text(
            text = "Portfolio",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = AccentRed,
            modifier = Modifier
              .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://portfolliobyraj.vercel.app/"))
                context.startActivity(intent)
              }
              .testTag("link_portfolio")
          )
        }
      }
    }
  }
}
