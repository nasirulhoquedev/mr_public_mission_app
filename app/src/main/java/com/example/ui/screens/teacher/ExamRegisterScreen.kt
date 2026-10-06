package com.example.ui.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ExamGradeEntity
import com.example.ui.components.ClassSelectorRow
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.EmeraldWisdom
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.SchoolViewModel

@Composable
fun ExamRegisterScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val selectedClass by viewModel.selectedClass.collectAsState()
    val gradesList by viewModel.getGradesForSelectedClass().collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val studentsInClass = allStudents.filter { it.className == selectedClass }

    val isKgClass = selectedClass.contains("KG", ignoreCase = true) || selectedClass.contains("0", ignoreCase = true)
    var showGradeEntryDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ClassSelectorRow(
            classes = viewModel.classesList,
            selectedClass = selectedClass,
            onClassSelected = { viewModel.selectClass(it) }
        )

        // Template Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = null,
                                tint = PrimaryNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Digital Exam Register & Report Card",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryNavy
                            )
                        }
                        Text(
                            text = if (isKgClass) "Class 0-KG Template: 'Good' / 'Needs Improvement' verbal indicators"
                            else "Class I-VI Template: Grades A-D & Numerical Marks Register",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showGradeEntryDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("key_grade_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Key Grade", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Template Indicator Pill
                Surface(
                    color = if (isKgClass) SecondaryGold.copy(alpha = 0.15f) else PrimaryNavy.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Grade,
                            contentDescription = null,
                            tint = if (isKgClass) SecondaryGold else PrimaryNavy,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isKgClass) "Active Template: KG Evaluation Matrix" else "Active Template: Standard M.R. Grade Register (A to D)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isKgClass) Color(0xFFB45309) else PrimaryNavy
                        )
                    }
                }
            }
        }

        // Grades List
        if (gradesList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No exam marks keyed yet for $selectedClass",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showGradeEntryDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                    ) {
                        Text("Input First Exam Marks")
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(gradesList) { grade ->
                    GradeReportItemCard(grade = grade)
                }
            }
        }
    }

    // Key Grades Dialog
    if (showGradeEntryDialog) {
        val firstStudent = studentsInClass.firstOrNull()
        var studentName by remember(studentsInClass) {
            mutableStateOf(firstStudent?.name ?: "Student")
        }
        var studentId by remember(studentsInClass) {
            mutableStateOf(firstStudent?.id ?: 1L)
        }
        var subject by remember { mutableStateOf("Mathematics") }
        var selectedKgGrade by remember { mutableStateOf("Good") }
        var selectedClassGrade by remember { mutableStateOf("A") }
        var marksText by remember { mutableStateOf("85") }
        var remarksText by remember { mutableStateOf("Well performed in test") }

        AlertDialog(
            onDismissRequest = { showGradeEntryDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        tint = PrimaryNavy
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Key Exam Grade ($selectedClass)")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Select Student:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Student picker row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        studentsInClass.take(3).forEach { s ->
                            val isSel = s.name == studentName
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) PrimaryNavy else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clickable {
                                        studentName = s.name
                                        studentId = s.id
                                    }
                                    .padding(2.dp)
                            ) {
                                Text(
                                    text = s.name,
                                    fontSize = 11.sp,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject (e.g. Science, Bengali, Math)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isKgClass) {
                        // Class 0-KG Template
                        Text(
                            text = "KG Evaluation Template:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = SecondaryGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val kgOptions = listOf("Excellent", "Good", "Satisfactory", "Needs Improvement")
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            kgOptions.forEach { opt ->
                                val isSel = selectedKgGrade == opt
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) SecondaryGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedKgGrade = opt }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isSel) Icons.Default.Check else Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (isSel) SecondaryGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = opt,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Class I to VI Template: Grades A to D
                        Text(
                            text = "Class I-VI Grade Scale (A-D):",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryNavy
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("A+", "A", "B", "C", "D").forEach { g ->
                                val isSel = selectedClassGrade == g
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) PrimaryNavy else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedClassGrade = g }
                                        .padding(2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = g,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = marksText,
                            onValueChange = { marksText = it },
                            label = { Text("Score Marks (e.g. 88/100)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = remarksText,
                        onValueChange = { remarksText = it },
                        label = { Text("Teacher Remarks / Feedback") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalGradeOrScore = if (isKgClass) {
                            selectedKgGrade
                        } else {
                            "$selectedClassGrade (${marksText}/100)"
                        }
                        val scorePercent = if (isKgClass) {
                            when (selectedKgGrade) {
                                "Excellent" -> 95
                                "Good" -> 85
                                "Satisfactory" -> 70
                                else -> 55
                            }
                        } else {
                            marksText.filter { it.isDigit() }.toIntOrNull() ?: 80
                        }

                        viewModel.addExamGrade(
                            studentId = studentId,
                            studentName = studentName,
                            subject = subject,
                            gradeOrScore = finalGradeOrScore,
                            scorePercent = scorePercent,
                            remarks = remarksText
                        )
                        showGradeEntryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                    modifier = Modifier.testTag("save_grade_button")
                ) {
                    Text("Save to Register")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGradeEntryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun GradeReportItemCard(grade: ExamGradeEntity) {
    val progress = (grade.scorePercent / 100f).coerceIn(0f, 1f)
    val color = when {
        grade.scorePercent >= 80 -> SuccessGreen
        grade.scorePercent >= 60 -> SecondaryGold
        else -> AlertCrimson
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = grade.studentName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${grade.className} • ${grade.subject}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = color.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = grade.gradeOrScore,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = color,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Performance Progress Meter
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Remarks: ${grade.remarks}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${grade.scorePercent}% Score",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = color
                )
            }
        }
    }
}
