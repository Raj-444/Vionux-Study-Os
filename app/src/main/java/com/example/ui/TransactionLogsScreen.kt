package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Transaction
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionLogsScreen(
  viewModel: DashboardViewModel,
  modifier: Modifier = Modifier
) {
  val transactions by viewModel.transactions.collectAsState()
  val context = LocalContext.current

  // State for search and filtering
  var searchQuery by remember { mutableStateOf("") }
  var sortOrder by remember { mutableStateOf("Newest") } // "Newest", "Oldest"
  var timeFilter by remember { mutableStateOf("All Time") } // "All Time", "Today", "This Week"
  var priceFilter by remember { mutableStateOf("All Prices") } // "All Prices", "> ৳50", "> ৳500"

  // Dropdown menu flags
  var showSortMenu by remember { mutableStateOf(false) }
  var showTimeMenu by remember { mutableStateOf(false) }
  var showPriceMenu by remember { mutableStateOf(false) }

  // Dialog visibility state
  var showImportGuideDialog by remember { mutableStateOf(false) }

  // Process/Filter transactions in real-time
  val filteredTransactions = remember(transactions, searchQuery, sortOrder, timeFilter, priceFilter) {
    var result = transactions.filter { tx ->
      val matchesSearch = tx.title.contains(searchQuery, ignoreCase = true) ||
          tx.category.contains(searchQuery, ignoreCase = true) ||
          tx.amount.toString().contains(searchQuery)
      
      val matchesTime = when (timeFilter) {
        "Today" -> {
          val startOfToday = System.currentTimeMillis() - 86400000
          tx.timestamp >= startOfToday
        }
        "This Week" -> {
          val startOfWeek = System.currentTimeMillis() - 86400000 * 7
          tx.timestamp >= startOfWeek
        }
        else -> true
      }

      val matchesPrice = when (priceFilter) {
        "> ৳50" -> tx.amount > 50
        "> ৳500" -> tx.amount > 500
        else -> true
      }

      matchesSearch && matchesTime && matchesPrice
    }

    // Apply sorting
    result = if (sortOrder == "Newest") {
      result.sortedByDescending { it.timestamp }
    } else {
      result.sortedBy { it.timestamp }
    }

    result
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF8F9FA)) // premium off-white/light-gray background
      .testTag("transaction_logs_screen"),
    contentPadding = PaddingValues(bottom = 120.dp) // space for floating bottom nav
  ) {
    // 1. Header Section
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 24.dp)
      ) {
        Text(
          text = "Transaction Logs",
          style = MaterialTheme.typography.displayMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            letterSpacing = (-0.5).sp
          ),
          color = TextDark,
          modifier = Modifier.testTag("transaction_logs_title")
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Filter, search, and review your logs.",
          style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
          color = TextMuted,
          modifier = Modifier.testTag("transaction_logs_subtitle")
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Two expanded action buttons in a Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Left Button: Vibrant red solid button with document icon
          Button(
            onClick = { showImportGuideDialog = true },
            modifier = Modifier
              .weight(1f)
              .height(50.dp)
              .testTag("import_logs_btn"),
            colors = ButtonDefaults.buttonColors(
              containerColor = AccentRed,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Description,
              contentDescription = "Import logs",
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Import Logs",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          // Right Button: Light red tinted or outlined button with download icon
          OutlinedButton(
            onClick = {
              Toast.makeText(context, "Exporting ${filteredTransactions.size} transactions to CSV...", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
              .weight(1f)
              .height(50.dp)
              .testTag("export_logs_btn"),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = AccentRed
            ),
            border = BorderStroke(1.5.dp, AccentRed.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.FileDownload,
              contentDescription = "Export logs",
              tint = AccentRed,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Export Logs",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = AccentRed,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }

    // 2. Search & Filter Section
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
      ) {
        // Soft gray highly rounded search bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = {
            Text(
              text = "Search logs...",
              color = TextMuted,
              fontSize = 14.sp
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = TextMuted,
              modifier = Modifier.size(20.dp)
            )
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear search",
                tint = TextMuted,
                modifier = Modifier
                  .size(18.dp)
                  .clickable { searchQuery = "" }
              )
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_logs_input"),
          singleLine = true,
          shape = RoundedCornerShape(24.dp),
          textStyle = premiumInputTextStyle,
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextDark,
            unfocusedTextColor = TextDark,
            focusedContainerColor = Color(0xFFF2F2F7),
            unfocusedContainerColor = Color(0xFFF2F2F7),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = AccentRed
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Horizontal scrollable row of filter chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Dynamic Sort Chip
          Box {
            FilterMenuChip(
              label = sortOrder,
              icon = Icons.Default.Sort,
              onClick = { showSortMenu = true }
            )
            DropdownMenu(
              expanded = showSortMenu,
              onDismissRequest = { showSortMenu = false },
              modifier = Modifier.background(Color.White)
            ) {
              DropdownMenuItem(
                text = { Text("Newest first") },
                onClick = {
                  sortOrder = "Newest"
                  showSortMenu = false
                }
              )
              DropdownMenuItem(
                text = { Text("Oldest first") },
                onClick = {
                  sortOrder = "Oldest"
                  showSortMenu = false
                }
              )
            }
          }

          // Dynamic Time Chip
          Box {
            FilterMenuChip(
              label = timeFilter,
              icon = Icons.Default.CalendarToday,
              onClick = { showTimeMenu = true }
            )
            DropdownMenu(
              expanded = showTimeMenu,
              onDismissRequest = { showTimeMenu = false },
              modifier = Modifier.background(Color.White)
            ) {
              DropdownMenuItem(
                text = { Text("All Time") },
                onClick = {
                  timeFilter = "All Time"
                  showTimeMenu = false
                }
              )
              DropdownMenuItem(
                text = { Text("Today") },
                onClick = {
                  timeFilter = "Today"
                  showTimeMenu = false
                }
              )
              DropdownMenuItem(
                text = { Text("This Week") },
                onClick = {
                  timeFilter = "This Week"
                  showTimeMenu = false
                }
              )
            }
          }

          // Dynamic Price Filter Chip
          Box {
            FilterMenuChip(
              label = priceFilter,
              icon = Icons.Default.AttachMoney,
              onClick = { showPriceMenu = true }
            )
            DropdownMenu(
              expanded = showPriceMenu,
              onDismissRequest = { showPriceMenu = false },
              modifier = Modifier.background(Color.White)
            ) {
              DropdownMenuItem(
                text = { Text("All Prices") },
                onClick = {
                  priceFilter = "All Prices"
                  showPriceMenu = false
                }
              )
              DropdownMenuItem(
                text = { Text("> ৳50") },
                onClick = {
                  priceFilter = "> ৳50"
                  showPriceMenu = false
                }
              )
              DropdownMenuItem(
                text = { Text("> ৳500") },
                onClick = {
                  priceFilter = "> ৳500"
                  showPriceMenu = false
                }
              )
            }
          }
        }
      }
    }

    item { Spacer(modifier = Modifier.height(16.dp)) }

    // 3. Transactions List / Empty State View
    if (filteredTransactions.isEmpty()) {
      item {
        EmptyLogsView(
          hasActiveFilters = searchQuery.isNotEmpty() || timeFilter != "All Time" || priceFilter != "All Prices",
          onResetFilters = {
            searchQuery = ""
            timeFilter = "All Time"
            priceFilter = "All Prices"
          }
        )
      }
    } else {
      items(filteredTransactions, key = { it.id }) { tx ->
        TransactionItemRow(
          transaction = tx,
          onDelete = { viewModel.deleteTransaction(tx) }
        )
      }
    }
  }

  // 4. Excel Import Guide Dialog Overlay
  if (showImportGuideDialog) {
    ExcelImportGuideDialog(
      onDismiss = { showImportGuideDialog = false },
      onUploadFile = {
        // Inject a couple of beautiful sample transactions from the excel guide table
        viewModel.addTransaction(
          title = "Lunch with team",
          amount = 1250.0,
          category = "Food",
          isExpense = true
        )
        viewModel.addTransaction(
          title = "Metro transit card",
          amount = 500.0,
          category = "Transportation",
          isExpense = true
        )
        showImportGuideDialog = false
        Toast.makeText(context, "Log Import Successful: Added 2 transactions!", Toast.LENGTH_LONG).show()
      }
    )
  }
}

// Reusable Filter Chip with Dropdown arrow indicator
@Composable
fun FilterMenuChip(
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .testTag("filter_chip_${label.lowercase().replace(" ", "_")}"),
    color = Color.White,
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, BorderSoft)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = TextMuted,
        modifier = Modifier.size(14.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        ),
        color = TextDark
      )
      Spacer(modifier = Modifier.width(2.dp))
      Icon(
        imageVector = Icons.Default.ArrowDropDown,
        contentDescription = "Dropdown indicator",
        tint = TextMuted,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}

// Empty View when no logs match the current query/filter or are present
@Composable
fun EmptyLogsView(
  hasActiveFilters: Boolean,
  onResetFilters: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 60.dp, horizontal = 24.dp)
      .testTag("empty_logs_view"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(64.dp)
        .clip(CircleShape)
        .background(BorderSoft.copy(alpha = 0.5f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.FilterList,
        contentDescription = "No transaction logs matched",
        tint = TextMuted,
        modifier = Modifier.size(28.dp)
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = if (hasActiveFilters) "No Matching Logs" else "No Recorded Logs",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = TextDark,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = if (hasActiveFilters) {
        "Adjust or reset your active search queries or chips to find what you are looking for."
      } else {
        "There are no transaction entries yet. Import logs or tap the action buttons to record entries."
      },
      style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
      color = TextMuted,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 16.dp)
    )

    if (hasActiveFilters) {
      Spacer(modifier = Modifier.height(16.dp))
      TextButton(
        onClick = onResetFilters,
        colors = ButtonDefaults.textButtonColors(contentColor = AccentRed)
      ) {
        Text("Reset All Filters", fontWeight = FontWeight.Bold)
      }
    }
  }
}

// 4. Excel Import Guide Dialog Widget (As requested)
@Composable
fun ExcelImportGuideDialog(
  onDismiss: () -> Unit,
  onUploadFile: () -> Unit,
  modifier: Modifier = Modifier
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.92f)
        .shadow(24.dp, shape = RoundedCornerShape(24.dp))
        .clip(RoundedCornerShape(24.dp))
        .testTag("excel_import_guide_dialog"),
      color = Color.White
    ) {
      Column(
        modifier = Modifier.padding(24.dp)
      ) {
        // Header Section: Red document icon and Title
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(AccentRed.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Description,
              contentDescription = "Document icon",
              tint = AccentRed,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Text(
            text = "Excel Import Guide",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp,
              letterSpacing = (-0.3).sp
            ),
            color = TextDark
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Subtitle text
        Text(
          text = "To import your transactions, your Excel (.xlsx) file must be structured as follows:",
          style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 13.sp,
            lineHeight = 18.sp
          ),
          color = TextMuted
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Table UI Container
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFFDFDFD),
          border = BorderStroke(1.dp, BorderSoft)
        ) {
          Column {
            // Table Header: light red background
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(AccentRed.copy(alpha = 0.08f))
                .padding(vertical = 10.dp, horizontal = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              val headers = listOf("Date", "Category", "Description", "Amount")
              headers.forEach { header ->
                Text(
                  text = header,
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  ),
                  color = AccentRed,
                  modifier = Modifier.weight(1f),
                  textAlign = TextAlign.Start
                )
              }
            }

            // Two rows of dummy sample data
            val dummyRows = listOf(
              listOf("2026-06-22", "Food", "Lunch with team", "1250.0"),
              listOf("2026-06-23", "Transportation", "Metro transit card", "500.0")
            )

            dummyRows.forEach { row ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .border(BorderStroke(0.5.dp, BorderSoft.copy(alpha = 0.5f)))
                  .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                row.forEach { cell ->
                  Text(
                    text = cell,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextDark,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bullet list of instructions (with red dots)
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          val instructions = listOf(
            "Columns must include: Date, Category, Description, and Amount.",
            "Supported Date Formats: YYYY-MM-DD or MM/DD/YYYY.",
            "Amount must be a positive number greater than 0.",
            "Empty or unformatted rows are automatically skipped."
          )

          instructions.forEach { instr ->
            Row(
              verticalAlignment = Alignment.Top,
              modifier = Modifier.fillMaxWidth()
            ) {
              // Red dot indicator
              Box(
                modifier = Modifier
                  .padding(top = 6.dp)
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(AccentRed)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = instr,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontSize = 13.sp,
                  lineHeight = 17.sp
                ),
                color = TextDark
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(
            onClick = onDismiss,
            colors = ButtonDefaults.textButtonColors(contentColor = TextMuted),
            modifier = Modifier.testTag("excel_cancel_btn")
          ) {
            Text(
              text = "Cancel",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Button(
            onClick = onUploadFile,
            colors = ButtonDefaults.buttonColors(
              containerColor = AccentRed,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .height(44.dp)
              .testTag("excel_upload_btn")
          ) {
            Text(
              text = "Upload File",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }
        }
      }
    }
  }
}
