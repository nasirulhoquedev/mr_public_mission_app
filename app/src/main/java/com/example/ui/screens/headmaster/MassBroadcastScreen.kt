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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.local.entities.BroadcastNoticeEntity
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryGold
import com.example.ui.viewmodel.SchoolViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MassBroadcastScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val allBroadcasts by viewModel.allBroadcasts.collectAsState()
    var showCreateBroadcastDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Hero Banner
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
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = PrimaryNavy,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "The Headmaster's Voice",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Mass Institutional Broadcast Hub",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SecondaryGold
                                )
                            }
                        }

                        Button(
                            onClick = { showCreateBroadcastDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("new_broadcast_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Broadcast", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Single-tap delivery of text, photo notices, and official voice messages to teachers, staff, and parents across all classes (0 to VI).",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // Broadcasts List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(allBroadcasts) { notice ->
                    BroadcastCard(notice = notice, viewModel = viewModel)
                }
            }
        }
    }

    // Create Broadcast Dialog
    if (showCreateBroadcastDialog) {
        var audience by remember { mutableStateOf("All School") }
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var hasVoiceNote by remember { mutableStateOf(false) }

        val templateNotices = listOf(
            "Upcoming PTM Meeting" to "Dear Parents & Staff, The term Parent-Teacher Meeting is scheduled this Saturday from 10:00 AM. Academic progress cards will be distributed.",
            "Weather / Rain Advisory" to "Heavy rain forecast: School timings adjusted tomorrow. First bell at 9:30 AM. Van pickup routes delayed by 30 mins.",
            "Annual Sports Day Announcement" to "M.R. Public Mission Annual Sports Meet entries are open for Classes 0 to VI. Submit participant names to class teachers by Wednesday."
        )

        AlertDialog(
            onDismissRequest = { showCreateBroadcastDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = PrimaryNavy
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("The Headmaster's Voice")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Target Audience:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("All School", "Teachers Only", "Parents Only").forEach { aud ->
                            val isSel = audience == aud
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) PrimaryNavy else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { audience = aud }
                                    .padding(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = aud,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Quick Headmaster Templates:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    templateNotices.forEach { (tmplTitle, tmplContent) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    title = tmplTitle
                                    content = tmplContent
                                }
                                .padding(vertical = 2.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "⚡ $tmplTitle",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = PrimaryNavy,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Broadcast Subject / Title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("broadcast_title_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Message Body") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("broadcast_content_input"),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Voice Note Toggle
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { hasVoiceNote = !hasVoiceNote },
                        color = if (hasVoiceNote) SecondaryGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = if (hasVoiceNote) SecondaryGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (hasVoiceNote) "Attached: Voice Note by Mr. Faruk Abdullah (1:24)" else "Attach Audio Message / Voice Broadcast",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (hasVoiceNote) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalTitle = title.ifBlank { "Notice from Headmaster" }
                        val finalContent = content.ifBlank { "Important communication from school administration." }
                        viewModel.dispatchBroadcast(
                            title = finalTitle,
                            content = finalContent,
                            audience = audience,
                            isUrgentSos = false,
                            hasVoiceNote = hasVoiceNote
                        )
                        showCreateBroadcastDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                    modifier = Modifier.testTag("send_broadcast_confirm_button")
                ) {
                    Text("Publish Broadcast")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateBroadcastDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun BroadcastCard(
    notice: BroadcastNoticeEntity,
    viewModel: SchoolViewModel
) {
    var isPlayingAudio by remember { mutableStateOf(false) }
    val dateStr = SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault()).format(Date(notice.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notice.isUrgentSos) Color(0xFFFEF2F2) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Audience & Urgent badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (notice.isUrgentSos) AlertCrimson else PrimaryNavy.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = if (notice.isUrgentSos) "🚨 SOS ALERT" else "📢 ${notice.audience}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (notice.isUrgentSos) Color.White else PrimaryNavy,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = notice.senderName,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = PrimaryNavy
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = notice.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (notice.isUrgentSos) AlertCrimson else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = notice.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Voice Note Player Simulation if present
            if (notice.hasVoiceNote) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = PrimaryNavy.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    isPlayingAudio = !isPlayingAudio
                                    if (isPlayingAudio) {
                                        viewModel.showToast("Playing Headmaster's audio broadcast...")
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryNavy)
                            ) {
                                Icon(
                                    imageVector = if (isPlayingAudio) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = "Play Audio",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isPlayingAudio) "Playing Audio Voice Note... 0:45" else "Voice Message from Mr. Faruk Abdullah",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = PrimaryNavy
                                )
                                Text(
                                    text = "Duration: 1 min 24 sec • High Quality Voice Recording",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
