package com.example.ui.screens.teacher

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.local.entities.AttendanceRecordEntity
import com.example.ui.components.ClassSelectorRow
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.SchoolViewModel

@Composable
fun AttendanceScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val selectedClass by viewModel.selectedClass.collectAsState()
    val attendanceRecords by viewModel.getAttendanceForSelectedClass().collectAsState()

    var showSmsPreviewDialog by remember { mutableStateOf(false) }
    var singleStudentSmsTarget by remember { mutableStateOf<AttendanceRecordEntity?>(null) }

    val totalCount = attendanceRecords.size
    val presentCount = attendanceRecords.count { it.isPresent }
    val absentCount = totalCount - presentCount
    val attendanceRate = if (totalCount > 0) (presentCount * 100) / totalCount else 0

    val absentStudents = attendanceRecords.filter { !it.isPresent }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Class Selector
        ClassSelectorRow(
            classes = viewModel.classesList,
            selectedClass = selectedClass,
            onClassSelected = { viewModel.selectClass(it) }
        )

        // Summary Card & Fast Action Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = PrimaryNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Super Fast Digital Attendance",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryNavy
                            )
                        }
                        Text(
                            text = "One-tap toggle for Absent / Present without paper register",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Attendance Rate Pill
                    Surface(
                        color = if (attendanceRate >= 85) SuccessGreen.copy(alpha = 0.15f) else AlertCrimson.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "$attendanceRate% Present",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (attendanceRate >= 85) SuccessGreen else AlertCrimson,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AttendanceStatPill("Total Enrolled", "$totalCount", PrimaryNavy)
                    AttendanceStatPill("Present (P)", "$presentCount", SuccessGreen)
                    AttendanceStatPill("Absent (A)", "$absentCount", AlertCrimson)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: Mark All Present & Send Instant Absent SMS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.markAllPresentForClass() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mark_all_present_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = SuccessGreen
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("All Present", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            if (absentStudents.isNotEmpty()) {
                                showSmsPreviewDialog = true
                            } else {
                                viewModel.showToast("No absent students in this class today!")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AlertCrimson
                        ),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("send_absent_sms_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Instant Absent SMS (${absentCount})", fontSize = 12.sp)
                    }
                }
            }
        }

        // Students List with single-tap toggle
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(attendanceRecords) { record ->
                AttendanceItemRow(
                    record = record,
                    onToggle = { viewModel.toggleAttendance(record.id, record.isPresent) },
                    onSendIndividualSms = {
                        singleStudentSmsTarget = record
                    }
                )
            }
        }
    }

    // Mass Absent SMS Confirmation Dialog
    if (showSmsPreviewDialog) {
        AlertDialog(
            onDismissRequest = { showSmsPreviewDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = AlertCrimson
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Instant Absent SMS Dispatch")
                }
            },
            text = {
                Column {
                    Text(
                        text = "You are sending instant absent SMS notification to parents of ${absentStudents.size} student(s) in $selectedClass:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Message Template Preview:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryNavy
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"M.R. PUBLIC MISSION: Dear Guardian, your ward was marked ABSENT today (${viewModel.todayDate}). If unnotified, please reach out to school.\"",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    absentStudents.take(4).forEach {
                        Text(
                            text = "• ${it.studentName}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (absentStudents.size > 4) {
                        Text(
                            text = "and ${absentStudents.size - 4} more...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendAllAbsentSms(absentStudents)
                        showSmsPreviewDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertCrimson),
                    modifier = Modifier.testTag("confirm_sms_dispatch_button")
                ) {
                    Text("Dispatch SMS Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSmsPreviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Individual SMS Target Dialog
    singleStudentSmsTarget?.let { record ->
        AlertDialog(
            onDismissRequest = { singleStudentSmsTarget = null },
            title = { Text("Send Absent Notice") },
            text = {
                Text("Send instant SMS to parent of ${record.studentName} for absence on ${viewModel.todayDate}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendAbsentSms(record.id, record.studentName, "+91 983210XXXX")
                        singleStudentSmsTarget = null
                    }
                ) {
                    Text("Send SMS")
                }
            },
            dismissButton = {
                TextButton(onClick = { singleStudentSmsTarget = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AttendanceItemRow(
    record: AttendanceRecordEntity,
    onToggle: () -> Unit,
    onSendIndividualSms: () -> Unit
) {
    val isPresent = record.isPresent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .testTag("student_row_${record.studentId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPresent) MaterialTheme.colorScheme.surface else Color(0xFFFEF2F2)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Status Avatar
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (isPresent) SuccessGreen.copy(alpha = 0.15f) else AlertCrimson.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isPresent) "P" else "A",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isPresent) SuccessGreen else AlertCrimson
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = record.studentName,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isPresent) MaterialTheme.colorScheme.onSurface else AlertCrimson
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isPresent) "Present in class" else "Absent today",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isPresent) SuccessGreen else AlertCrimson
                        )
                        if (!isPresent && record.smsSent) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• SMS Dispatched ✓",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Quick Toggle Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isPresent) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AlertCrimson.copy(alpha = 0.12f))
                            .clickable { onSendIndividualSms() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "SMS",
                                tint = AlertCrimson,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (record.smsSent) "Re-send SMS" else "Send SMS",
                                fontSize = 11.sp,
                                color = AlertCrimson,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isPresent) SuccessGreen else AlertCrimson,
                    modifier = Modifier.clickable { onToggle() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPresent) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPresent) "PRESENT" else "ABSENT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceStatPill(
    label: String,
    value: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}
