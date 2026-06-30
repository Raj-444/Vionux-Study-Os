package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

data class ComposeChecklistItem(
  val text: String,
  val isDone: Boolean
)

data class ComposeNote(
  val id: Long,
  val title: String,
  val content: String,
  val label: String,
  val isPinned: Boolean,
  val color: Color,
  val checklist: List<ComposeChecklistItem>
)

@Composable
fun ToolsScreen(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  // 1. Search & Filter State
  var searchQuery by remember { mutableStateOf("") }
  var selectedLabel by remember { mutableStateOf("All") }
  val labels = listOf("All", "Work", "Personal", "Ideas", "Shopping")

  // 2. Notes State (MutableStateList)
  val notesList = remember {
    mutableStateListOf(
      ComposeNote(
        1L,
        "Semester Project Deliverables",
        "Make sure to coordinate the database design with the frontend team.",
        "Work",
        true,
        Color(0xFFFFF3CD), // Light Yellow
        listOf(
          ComposeChecklistItem("Schema design finalized", true),
          ComposeChecklistItem("API specifications document", false),
          ComposeChecklistItem("Setup Dev environment", false)
        )
      ),
      ComposeNote(
        2L,
        "Startup Pitch Deck Ideas",
        "Focus on developer productivity tooling, automated UI validation, and real-time remote emulator stream.",
        "Ideas",
        true,
        Color(0xFFD1E7DD), // Light Green
        emptyList()
      ),
      ComposeNote(
        3L,
        "Weekly Groceries",
        "Organic greens and healthy grains.",
        "Shopping",
        false,
        Color(0xFFF8D7DA), // Light Red
        listOf(
          ComposeChecklistItem("Almond milk", true),
          ComposeChecklistItem("Whole wheat bread", true),
          ComposeChecklistItem("Spinach & Kale mix", false)
        )
      ),
      ComposeNote(
        4L,
        "Self Reflection Journal",
        "Spent 45 minutes on focused breathing today. Productivity peaks when digital notifications are entirely muted.",
        "Personal",
        false,
        Color(0xFFCFE2FF), // Light Blue
        emptyList()
      )
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF8F9FA)) // Premium off-white background
      .verticalScroll(scrollState)
      .padding(horizontal = 24.dp)
      .padding(top = 24.dp, bottom = 120.dp) // Leave navigation space
      .testTag("tools_screen_content")
  ) {
    // Workspace Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Workspace Tools",
          style = MaterialTheme.typography.displayMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            letterSpacing = (-0.5).sp
          ),
          color = TextDark,
          modifier = Modifier.testTag("tools_header_title")
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Keep quick thoughts and track core vitals.",
          style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
          color = TextMuted,
          modifier = Modifier.testTag("tools_header_subtitle")
        )
      }

      // Add Note button
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(AccentRed)
          .clickable {
            // Dynamic insertion of simple note
            val newNoteId = System.currentTimeMillis()
            notesList.add(
              0,
              ComposeNote(
                newNoteId,
                "New General Note",
                "Start writing details here...",
                "Work",
                false,
                Color(0xFFF2F2F7),
                emptyList()
              )
            )
            Toast.makeText(context, "Note Created! Tap to write details.", Toast.LENGTH_SHORT).show()
          },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Add Note",
          tint = Color.White,
          modifier = Modifier.size(24.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // ==========================================
    // SECTION 1: Keep-Style Notes Section
    // ==========================================
    Text(
      text = "Quick Notes",
      style = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
      ),
      color = TextDark,
      modifier = Modifier.testTag("notes_section_title")
    )
    Spacer(modifier = Modifier.height(12.dp))

    // Rounded Search Input Field
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("notes_search_input"),
      placeholder = { Text("Search notes...", color = TextMuted) },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search icon",
          tint = TextMuted
        )
      },
      singleLine = true,
      textStyle = premiumInputTextStyle,
      colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextDark,
        unfocusedTextColor = TextDark,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White,
        focusedContainerColor = Color(0xFFF2F2F7),
        unfocusedContainerColor = Color(0xFFF2F2F7),
        cursorColor = AccentRed
      ),
      shape = RoundedCornerShape(16.dp),
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Horizontal Chips Row for labels
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      labels.forEach { label ->
        val isSelected = label == selectedLabel
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xFF0C0C0E) else Color.White)
            .border(
              width = 1.dp,
              color = if (isSelected) Color(0xFF0C0C0E) else Color(0xFFE5E5EA),
              shape = RoundedCornerShape(20.dp)
            )
            .clickable { selectedLabel = label }
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("label_chip_$label"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else TextMuted
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Grid representing two columns of notes
    val filteredNotes = notesList.filter { note ->
      val matchesSearch = note.title.contains(searchQuery, ignoreCase = true) ||
          note.content.contains(searchQuery, ignoreCase = true)
      val matchesLabel = selectedLabel == "All" || note.label == selectedLabel
      matchesSearch && matchesLabel
    }

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      for (i in filteredNotes.indices step 2) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Note Card Left
          Box(modifier = Modifier.weight(1f)) {
            val note = filteredNotes[i]
            var showDialog by remember { mutableStateOf(false) }
            
            if (showDialog) {
              EditNoteDialog(
                note = note,
                onDismiss = { showDialog = false },
                onSave = { updatedNote ->
                  val indexInOriginal = notesList.indexOfFirst { it.id == note.id }
                  if (indexInOriginal != -1) {
                    notesList[indexInOriginal] = updatedNote
                  }
                }
              )
            }
            
            NoteCardWidget(
              note = note,
              onChecklistToggle = { idx ->
                val updatedChecklist = note.checklist.mapIndexed { checkIdx, item ->
                  if (checkIdx == idx) item.copy(isDone = !item.isDone) else item
                }
                val indexInOriginal = notesList.indexOfFirst { it.id == note.id }
                if (indexInOriginal != -1) {
                  notesList[indexInOriginal] = note.copy(checklist = updatedChecklist)
                }
              },
              onClick = { showDialog = true }
            )
          }

          // Note Card Right
          if (i + 1 < filteredNotes.size) {
            Box(modifier = Modifier.weight(1f)) {
              val note = filteredNotes[i + 1]
              var showDialog by remember { mutableStateOf(false) }
            
              if (showDialog) {
                EditNoteDialog(
                  note = note,
                  onDismiss = { showDialog = false },
                  onSave = { updatedNote ->
                    val indexInOriginal = notesList.indexOfFirst { it.id == note.id }
                    if (indexInOriginal != -1) {
                      notesList[indexInOriginal] = updatedNote
                    }
                  }
                )
              }
              
              NoteCardWidget(
                note = note,
                onChecklistToggle = { idx ->
                  val updatedChecklist = note.checklist.mapIndexed { checkIdx, item ->
                    if (checkIdx == idx) item.copy(isDone = !item.isDone) else item
                  }
                  val indexInOriginal = notesList.indexOfFirst { it.id == note.id }
                  if (indexInOriginal != -1) {
                    notesList[indexInOriginal] = note.copy(checklist = updatedChecklist)
                  }
                },
                onClick = { showDialog = true }
              )
            }
          } else {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }
}

@Composable
fun NoteCardWidget(
  note: ComposeNote,
  onChecklistToggle: (Int) -> Unit,
  onClick: () -> Unit, // Add onClick
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .height(180.dp)
      .clickable(onClick = onClick), // Make clickable
    color = note.color,
    shape = RoundedCornerShape(24.dp),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.6f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(14.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Label Badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color.White.copy(alpha = 0.6f))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = note.label,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp,
              color = TextDark
            )
          }

          if (note.isPinned) {
            Icon(
              imageVector = Icons.Default.PushPin,
              contentDescription = "Pinned note",
              tint = TextDark,
              modifier = Modifier.size(14.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
          text = note.title,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = TextDark,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Content
        Text(
          text = note.content,
          fontSize = 11.sp,
          color = Color(0xFF555555),
          maxLines = if (note.checklist.isEmpty()) 4 else 2,
          overflow = TextOverflow.Ellipsis
        )
      }

      // Checklists (if exists)
      if (note.checklist.isNotEmpty()) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          note.checklist.take(2).forEachIndexed { idx, item ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = if (item.isDone) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                contentDescription = null,
                tint = if (item.isDone) Color.Black.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.4f),
                modifier = Modifier
                  .size(14.dp)
                  .clickable { onChecklistToggle(idx) }
              )

              Text(
                text = item.text,
                fontSize = 10.sp,
                color = if (item.isDone) Color.Gray else TextDark,
                textDecoration = if (item.isDone) TextDecoration.LineThrough else null,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteDialog(
  note: ComposeNote,
  onDismiss: () -> Unit,
  onSave: (ComposeNote) -> Unit
) {
  var title by remember { mutableStateOf(note.title) }
  var content by remember { mutableStateOf(note.content) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Edit Note") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Title") },
          textStyle = premiumInputTextStyle,
          colors = getTextFieldColors()
        )
        OutlinedTextField(
          value = content,
          onValueChange = { content = it },
          label = { Text("Content") },
          maxLines = 5,
          textStyle = premiumInputTextStyle,
          colors = getTextFieldColors()
        )
      }
    },
    confirmButton = {
      Button(onClick = { onSave(note.copy(title = title, content = content)); onDismiss() }) {
        Text("Save")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}
