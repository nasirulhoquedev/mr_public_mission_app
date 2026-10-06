package com.example.ui.screens.headmaster

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.data.local.entities.HelpDeskTicketEntity
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.EmeraldWisdom
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AppRole
import com.example.ui.viewmodel.SchoolViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PrincipalHelpDeskScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val allTickets by viewModel.allTickets.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var statusFilter by remember { mutableStateOf("All") }
    var showSubmitDialog by remember { mutableStateOf(false) }
    var replyingTicket by remember { mutableStateOf<HelpDeskTicketEntity?>(null) }

    val filteredTickets = when (statusFilter) {
        "Pending" -> allTickets.filter { it.status == "Pending" }
        "In Review" -> allTickets.filter { it.status == "In Review" }
        "Resolved" -> allTickets.filter { it.status == "Resolved" }
        else -> allTickets
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(SecondaryGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = PrimaryNavy,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Principal's Help Desk & Redressal",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Direct Line to Mr. Faruk Abdullah (No Mediators)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SecondaryGold
                                )
                            }
                        }

                        Button(
                            onClick = { showSubmitDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("submit_query_fab")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Query", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Parents and teachers can submit grievances, queries, or requests directly to the Principal for swift resolution.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // Filter Tabs Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Pending", "In Review", "Resolved").forEach { filter ->
                    val isSel = statusFilter == filter
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSel) PrimaryNavy else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable { statusFilter = filter }
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = filter,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Ticket List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredTickets) { ticket ->
                    TicketCard(
                        ticket = ticket,
                        isPrincipal = currentRole == AppRole.HEADMASTER,
                        onReplyClick = { replyingTicket = ticket }
                    )
                }
            }
        }
    }

    // Submit New Query Dialog (For Parent or Teacher)
    if (showSubmitDialog) {
        var name by remember {
            mutableStateOf(if (currentRole == AppRole.TEACHER) "Tr. Aminul Haque" else "Rafiqul Mondal (Father)")
        }
        var role by remember {
            mutableStateOf(if (currentRole == AppRole.TEACHER) "Teacher" else "Parent")
        }
        var studentClass by remember { mutableStateOf("Class IV") }
        var category by remember { mutableStateOf("Transport") }
        var subject by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        val categories = listOf("Academics", "Transport", "Fees", "Facilities", "Discipline")

        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HelpCenter,
                        contentDescription = null,
                        tint = PrimaryNavy
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit Query to Principal")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Submitted directly to Mr. Faruk Abdullah",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Your Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Category:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categories.take(3).forEach { cat ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (category == cat) PrimaryNavy else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clickable { category = cat }
                                    .padding(2.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 10.sp,
                                    color = if (category == cat) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categories.drop(3).forEach { cat ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (category == cat) PrimaryNavy else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clickable { category = cat }
                                    .padding(2.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 10.sp,
                                    color = if (category == cat) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Query Subject") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ticket_subject_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Detailed description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .testTag("ticket_desc_input"),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalSub = subject.ifBlank { "Query regarding $category" }
                        val finalDesc = description.ifBlank { "Submitted via Principal Help Desk portal." }
                        viewModel.submitHelpDeskTicket(
                            name = name,
                            role = role,
                            studentClass = studentClass,
                            category = category,
                            subject = finalSub,
                            description = finalDesc
                        )
                        showSubmitDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                    modifier = Modifier.testTag("submit_ticket_confirm_button")
                ) {
                    Text("Submit to Principal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Principal Redressal / Reply Dialog
    replyingTicket?.let { ticket ->
        var replyText by remember { mutableStateOf(ticket.principalReply) }
        var status by remember { mutableStateOf(ticket.status) }

        AlertDialog(
            onDismissRequest = { replyingTicket = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Reply,
                        contentDescription = null,
                        tint = PrimaryNavy
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Principal Redressal Action")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Ticket: ${ticket.subject}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "From: ${ticket.submittedBy} (${ticket.role})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"${ticket.description}\"",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Update Status:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Pending", "In Review", "Resolved").forEach { st ->
                            val isSel = status == st
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) PrimaryNavy else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { status = st }
                                    .padding(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = st,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        label = { Text("Headmaster Directive / Resolution Notes") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("principal_reply_input"),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.replyToTicket(ticket.id, status, replyText)
                        replyingTicket = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                    modifier = Modifier.testTag("save_redressal_button")
                ) {
                    Text("Save & Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { replyingTicket = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TicketCard(
    ticket: HelpDeskTicketEntity,
    isPrincipal: Boolean,
    onReplyClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault()).format(Date(ticket.timestamp))
    val statusColor = when (ticket.status) {
        "Resolved" -> SuccessGreen
        "In Review" -> PrimaryNavy
        else -> SecondaryGold
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Status and Category Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = ticket.status.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = ticket.category,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = ticket.subject,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Submitted by ${ticket.submittedBy} • ${ticket.studentClass}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = ticket.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Principal Reply Box if available
            if (ticket.principalReply.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = PrimaryNavy.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Principal's Directive / Reply:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryNavy
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ticket.principalReply,
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryNavy
                        )
                    }
                }
            }

            // Action Row for Headmaster
            if (isPrincipal) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onReplyClick,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("action_ticket_${ticket.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Reply,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (ticket.principalReply.isBlank()) "Resolve Query" else "Edit Directive",
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
