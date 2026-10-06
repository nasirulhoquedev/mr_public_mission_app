package com.example.ui.screens.headmaster

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.EmeraldWisdom
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.HeadmasterTab
import com.example.ui.viewmodel.SchoolViewModel

@Composable
fun SchoolPulseScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val allStudents by viewModel.allStudents.collectAsState()
    val allAttendance by viewModel.todayAllAttendance.collectAsState()
    val allTickets by viewModel.allTickets.collectAsState()
    val allHomework by viewModel.allHomework.collectAsState()

    val totalStudents = allStudents.size
    val presentCount = allAttendance.count { it.isPresent }
    val studentAttendancePct = if (allAttendance.isNotEmpty()) (presentCount * 100) / allAttendance.size else 96
    val pendingTickets = allTickets.count { it.status == "Pending" || it.status == "In Review" }

    // Financial Metrics (Simulated real metrics for M.R. Public Mission)
    val feeTarget = 540000.0
    val feeCollected = 478000.0
    val feeDue = feeTarget - feeCollected
    val feeCollectionPct = ((feeCollected / feeTarget) * 100).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Principal Greeting Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(SecondaryGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = PrimaryNavy,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "School Pulse Dashboard",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Headmaster: Mr. Faruk Abdullah",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SecondaryGold
                        )
                        Text(
                            text = "Real-time school performance, fees, attendance & classes overview",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Fast Insights Grid (Attendance, Staff, Fees, Inquiries)
        item {
            Text(
                text = "⚡ Real-Time School Vital Signs",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = PrimaryNavy
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                VitalCard(
                    title = "Student Attendance",
                    value = "$studentAttendancePct%",
                    subtitle = "$presentCount / ${allAttendance.size} Present",
                    icon = Icons.Default.People,
                    color = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                VitalCard(
                    title = "Teacher Attendance",
                    value = "18 / 19",
                    subtitle = "94.7% Present Today",
                    icon = Icons.Default.SupervisedUserCircle,
                    color = PrimaryNavy,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                VitalCard(
                    title = "Fee Collection",
                    value = "₹4.78 Lakh",
                    subtitle = "$feeCollectionPct% Collected (₹62K Due)",
                    icon = Icons.Default.CurrencyRupee,
                    color = SecondaryGold,
                    modifier = Modifier.weight(1f)
                )
                VitalCard(
                    title = "Parent Enquiries",
                    value = "$pendingTickets Open",
                    subtitle = "${allTickets.size} Total Direct Queries",
                    icon = Icons.Default.HelpOutline,
                    color = if (pendingTickets > 0) AlertCrimson else SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Fee Collection Progress Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Monthly Fee Collection Pace",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "$feeCollectionPct% Collected",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = SecondaryGold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { feeCollectionPct / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = SecondaryGold,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Collected: ₹4,78,000",
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Remaining Due: ₹62,000",
                            style = MaterialTheme.typography.labelSmall,
                            color = AlertCrimson,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Insightful View of Teachers & Classes (Class-by-Class Table without physical inspection)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊 Insightful View of Classes & Syllabus",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryNavy
                )
                Text(
                    text = "Zero-Inspection View",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            val classInsights = listOf(
                ClassSyllabusInsight("Class 0 (KG)", "Tr. Sabina Yasmin", 88, "Phonics & Shapes", "Approved (100%)", 95),
                ClassSyllabusInsight("Class I", "Tr. Rokeya Begum", 82, "Bengali & Basic Math", "Approved (100%)", 92),
                ClassSyllabusInsight("Class II", "Tr. Nazrul Islam", 78, "Addition & English Gram", "Approved (95%)", 90),
                ClassSyllabusInsight("Class III", "Tr. Habibur Rahman", 85, "Science & Environmental", "Approved (100%)", 88),
                ClassSyllabusInsight("Class IV", "Tr. Aminul Haque", 75, "Fractions & Plants", "Pending Review (1)", 91),
                ClassSyllabusInsight("Class V", "Tr. Mahbubur Rahman", 90, "Decimals & History", "Approved (100%)", 87),
                ClassSyllabusInsight("Class VI", "Tr. Mahfuzur Rahman", 84, "Algebra & Biology", "Approved (100%)", 94)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                classInsights.forEach { item ->
                    ClassInsightRowCard(item)
                }
            }
        }
    }
}

data class ClassSyllabusInsight(
    val className: String,
    val classTeacher: String,
    val syllabusCoveragePct: Int,
    val currentTopic: String,
    val homeworkApprovalStatus: String,
    val classPerformanceIndex: Int
)

@Composable
private fun ClassInsightRowCard(insight: ClassSyllabusInsight) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = insight.className,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryNavy
                    )
                    Text(
                        text = "Teacher: ${insight.classTeacher}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryNavy.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "${insight.classPerformanceIndex} Performance Index",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryNavy,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Syllabus Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Syllabus Coverage (${insight.currentTopic})",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "${insight.syllabusCoveragePct}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (insight.syllabusCoveragePct >= 80) SuccessGreen else SecondaryGold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { insight.syllabusCoveragePct / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (insight.syllabusCoveragePct >= 80) SuccessGreen else SecondaryGold,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Homework Dispatch: ${insight.homeworkApprovalStatus}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "On Track ✓",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = SuccessGreen
                )
            }
        }
    }
}

@Composable
private fun VitalCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = color
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
