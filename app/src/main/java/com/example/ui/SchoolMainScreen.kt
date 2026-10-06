package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SchoolTopBar
import com.example.ui.screens.headmaster.MassBroadcastScreen
import com.example.ui.screens.headmaster.PrincipalHelpDeskScreen
import com.example.ui.screens.headmaster.SchoolPulseScreen
import com.example.ui.screens.headmaster.StaffSosScreen
import com.example.ui.screens.parent.ParentPortalScreen
import com.example.ui.screens.teacher.AttendanceScreen
import com.example.ui.screens.teacher.ClassroomCameraFeedScreen
import com.example.ui.screens.teacher.ExamRegisterScreen
import com.example.ui.screens.teacher.HomeworkHubScreen
import com.example.ui.screens.teacher.QuietChatScreen
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryGold
import com.example.ui.viewmodel.AppRole
import com.example.ui.viewmodel.HeadmasterTab
import com.example.ui.viewmodel.SchoolViewModel
import com.example.ui.viewmodel.TeacherTab

@Composable
fun SchoolMainScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val teacherTab by viewModel.teacherTab.collectAsState()
    val headmasterTab by viewModel.headmasterTab.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val allBroadcasts by viewModel.allBroadcasts.collectAsState()

    val urgentNoticesCount = allBroadcasts.count { it.isUrgentSos }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SchoolTopBar(
                currentRole = currentRole,
                onRoleSelect = { viewModel.selectRole(it) },
                todayDate = viewModel.todayDate,
                urgentNoticeCount = urgentNoticesCount,
                onSosClick = {
                    viewModel.selectRole(AppRole.HEADMASTER)
                    viewModel.selectHeadmasterTab(HeadmasterTab.SOS)
                }
            )
        },
        bottomBar = {
            if (currentRole == AppRole.TEACHER) {
                TeacherBottomNavigation(
                    currentTab = teacherTab,
                    onTabSelected = { viewModel.selectTeacherTab(it) }
                )
            } else if (currentRole == AppRole.HEADMASTER) {
                HeadmasterBottomNavigation(
                    currentTab = headmasterTab,
                    onTabSelected = { viewModel.selectHeadmasterTab(it) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRole) {
                AppRole.TEACHER -> {
                    AnimatedContent(
                        targetState = teacherTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "TeacherScreenAnimation"
                    ) { tab ->
                        when (tab) {
                            TeacherTab.ATTENDANCE -> AttendanceScreen(viewModel = viewModel)
                            TeacherTab.FEED -> ClassroomCameraFeedScreen(viewModel = viewModel)
                            TeacherTab.HOMEWORK -> HomeworkHubScreen(viewModel = viewModel)
                            TeacherTab.CHAT -> QuietChatScreen(viewModel = viewModel)
                            TeacherTab.EXAMS -> ExamRegisterScreen(viewModel = viewModel)
                        }
                    }
                }

                AppRole.HEADMASTER -> {
                    AnimatedContent(
                        targetState = headmasterTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "HeadmasterScreenAnimation"
                    ) { tab ->
                        when (tab) {
                            HeadmasterTab.PULSE -> SchoolPulseScreen(viewModel = viewModel)
                            HeadmasterTab.BROADCAST -> MassBroadcastScreen(viewModel = viewModel)
                            HeadmasterTab.INSIGHTS -> SchoolPulseScreen(viewModel = viewModel)
                            HeadmasterTab.SOS -> StaffSosScreen(viewModel = viewModel)
                            HeadmasterTab.HELP_DESK -> PrincipalHelpDeskScreen(viewModel = viewModel)
                        }
                    }
                }

                AppRole.PARENT -> {
                    ParentPortalScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun TeacherBottomNavigation(
    currentTab: TeacherTab,
    onTabSelected: (TeacherTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.navigationBarsPadding(),
        tonalElevation = 6.dp
    ) {
        val items = listOf(
            Triple(TeacherTab.ATTENDANCE, Icons.Default.Speed, "Attendance"),
            Triple(TeacherTab.FEED, Icons.Default.PhotoCamera, "Feed"),
            Triple(TeacherTab.HOMEWORK, Icons.Default.Assignment, "Homework"),
            Triple(TeacherTab.CHAT, Icons.Default.Chat, "Quiet Chat"),
            Triple(TeacherTab.EXAMS, Icons.Default.Grade, "Exams")
        )

        items.forEach { (tab, icon, label) ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryNavy,
                    selectedTextColor = PrimaryNavy,
                    indicatorColor = PrimaryNavy.copy(alpha = 0.15f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}

@Composable
private fun HeadmasterBottomNavigation(
    currentTab: HeadmasterTab,
    onTabSelected: (HeadmasterTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.navigationBarsPadding(),
        tonalElevation = 6.dp
    ) {
        val items = listOf(
            Triple(HeadmasterTab.PULSE, Icons.Default.TrendingUp, "Pulse"),
            Triple(HeadmasterTab.BROADCAST, Icons.Default.Campaign, "Broadcast"),
            Triple(HeadmasterTab.SOS, Icons.Default.Emergency, "Staff SOS"),
            Triple(HeadmasterTab.HELP_DESK, Icons.Default.HelpCenter, "Help Desk")
        )

        items.forEach { (tab, icon, label) ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (tab == HeadmasterTab.SOS && isSelected) AlertCrimson else if (isSelected) PrimaryNavy else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = if (tab == HeadmasterTab.SOS) AlertCrimson else PrimaryNavy,
                    selectedTextColor = if (tab == HeadmasterTab.SOS) AlertCrimson else PrimaryNavy,
                    indicatorColor = if (tab == HeadmasterTab.SOS) AlertCrimson.copy(alpha = 0.15f) else PrimaryNavy.copy(alpha = 0.15f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_headmaster_${tab.name.lowercase()}")
            )
        }
    }
}
