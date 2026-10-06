package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SchoolDatabase
import com.example.data.local.entities.AttendanceRecordEntity
import com.example.data.local.entities.BroadcastNoticeEntity
import com.example.data.local.entities.ChatMessageEntity
import com.example.data.local.entities.ClassroomFeedEntity
import com.example.data.local.entities.ExamGradeEntity
import com.example.data.local.entities.HelpDeskTicketEntity
import com.example.data.local.entities.HomeworkEntity
import com.example.data.local.entities.StudentEntity
import com.example.data.repository.SchoolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppRole(val label: String, val badge: String) {
    HEADMASTER("Headmaster Portal", "Mr. Faruk Abdullah"),
    TEACHER("Teacher Workspace", "Class 0 to VI"),
    PARENT("Parent & Student Hub", "Family Portal")
}

enum class TeacherTab(val label: String) {
    ATTENDANCE("Smart Attendance"),
    FEED("Classroom Camera"),
    HOMEWORK("Homework Hub"),
    CHAT("Quiet Chat"),
    EXAMS("Exam Register")
}

enum class HeadmasterTab(val label: String) {
    PULSE("Pulse Dashboard"),
    BROADCAST("The Headmaster's Voice"),
    INSIGHTS("Classes & Syllabus"),
    SOS("Staff SOS"),
    HELP_DESK("Principal Help Desk")
}

enum class ParentTab(val label: String) {
    STUDENT_FEED("Class Feed"),
    MY_ATTENDANCE("Attendance"),
    MY_HOMEWORK("Homework & Tasks"),
    REPORT_CARD("Report Card"),
    PRINCIPAL_DESK("Submit Query")
}

class SchoolViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SchoolRepository
    val todayDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val classesList = listOf(
        "Class 0 (KG)",
        "Class I",
        "Class II",
        "Class III",
        "Class IV",
        "Class V",
        "Class VI"
    )

    private val _currentRole = MutableStateFlow(AppRole.HEADMASTER)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    private val _selectedClass = MutableStateFlow("Class IV")
    val selectedClass: StateFlow<String> = _selectedClass.asStateFlow()

    private val _teacherTab = MutableStateFlow(TeacherTab.ATTENDANCE)
    val teacherTab: StateFlow<TeacherTab> = _teacherTab.asStateFlow()

    private val _headmasterTab = MutableStateFlow(HeadmasterTab.PULSE)
    val headmasterTab: StateFlow<HeadmasterTab> = _headmasterTab.asStateFlow()

    private val _parentTab = MutableStateFlow(ParentTab.STUDENT_FEED)
    val parentTab: StateFlow<ParentTab> = _parentTab.asStateFlow()

    private val _isQuietHoursActive = MutableStateFlow(true)
    val isQuietHoursActive: StateFlow<Boolean> = _isQuietHoursActive.asStateFlow()

    // Notification / Toast Message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Data streams
    val allStudents: StateFlow<List<StudentEntity>>
    val allFeedPosts: StateFlow<List<ClassroomFeedEntity>>
    val allBroadcasts: StateFlow<List<BroadcastNoticeEntity>>
    val allTickets: StateFlow<List<HelpDeskTicketEntity>>
    val allHomework: StateFlow<List<HomeworkEntity>>
    val todayAllAttendance: StateFlow<List<AttendanceRecordEntity>>

    init {
        val database = SchoolDatabase.getDatabase(application, viewModelScope)
        repository = SchoolRepository(database.schoolDao())

        allStudents = repository.allStudents.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allFeedPosts = repository.allFeedPosts.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allBroadcasts = repository.allBroadcasts.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allTickets = repository.allTickets.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allHomework = repository.allHomework.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        todayAllAttendance = repository.getAllAttendanceForDate(todayDate).stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        viewModelScope.launch {
            repository.ensureDataPopulated()
        }
    }

    fun selectRole(role: AppRole) {
        _currentRole.value = role
    }

    fun selectClass(className: String) {
        _selectedClass.value = className
    }

    fun selectTeacherTab(tab: TeacherTab) {
        _teacherTab.value = tab
    }

    fun selectHeadmasterTab(tab: HeadmasterTab) {
        _headmasterTab.value = tab
    }

    fun selectParentTab(tab: ParentTab) {
        _parentTab.value = tab
    }

    fun toggleQuietHours() {
        _isQuietHoursActive.value = !_isQuietHoursActive.value
        val status = if (_isQuietHoursActive.value) "Active (Auto-responder ON)" else "Disabled"
        _userMessage.value = "Quiet Hours are now $status"
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun showToast(msg: String) {
        _userMessage.value = msg
    }

    // Attendance Methods
    fun getAttendanceForSelectedClass(): StateFlow<List<AttendanceRecordEntity>> {
        return repository.getAttendanceForDateAndClass(todayDate, _selectedClass.value)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun toggleAttendance(recordId: Long, currentPresent: Boolean) {
        viewModelScope.launch {
            repository.toggleAttendance(recordId, currentPresent)
        }
    }

    fun markAllPresentForClass() {
        viewModelScope.launch {
            repository.markAllPresent(todayDate, _selectedClass.value)
            _userMessage.value = "All students in ${_selectedClass.value} marked PRESENT"
        }
    }

    fun sendAbsentSms(recordId: Long, studentName: String, parentPhone: String) {
        viewModelScope.launch {
            repository.markSmsSent(recordId)
            _userMessage.value = "Instant Absent SMS dispatched to $studentName's parent ($parentPhone)"
        }
    }

    fun sendAllAbsentSms(absentList: List<AttendanceRecordEntity>) {
        viewModelScope.launch {
            absentList.forEach {
                repository.markSmsSent(it.id)
            }
            _userMessage.value = "Instant Absent SMS dispatched to ${absentList.size} parent(s)"
        }
    }

    // Classroom Activity Camera
    fun postClassroomActivity(
        caption: String,
        presetTag: String,
        teacherName: String = "Tr. Aminul Haque",
        imageDescription: String = "Classroom activity photo"
    ) {
        viewModelScope.launch {
            val post = ClassroomFeedEntity(
                className = _selectedClass.value,
                teacherName = teacherName,
                caption = caption,
                presetTag = presetTag,
                timestamp = System.currentTimeMillis(),
                likesCount = 0,
                imageDescription = imageDescription
            )
            repository.addFeedPost(post)
            _userMessage.value = "Posted to Classroom Feed for parents!"
        }
    }

    fun likePost(postId: Long) {
        viewModelScope.launch {
            repository.likeFeedPost(postId)
        }
    }

    // Homework Hub
    fun getHomeworkForSelectedClass(): StateFlow<List<HomeworkEntity>> {
        return repository.getHomeworkForClass(_selectedClass.value)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun dispatchHomework(
        title: String,
        subject: String,
        description: String,
        dueDate: String,
        isVoiceDictated: Boolean,
        hasWorksheet: Boolean,
        worksheetSnippet: String
    ) {
        viewModelScope.launch {
            val hw = HomeworkEntity(
                className = _selectedClass.value,
                subject = subject,
                title = title,
                description = description,
                assignedDate = todayDate,
                dueDate = dueDate,
                isVoiceDictated = isVoiceDictated,
                hasWorksheetImage = hasWorksheet,
                worksheetSnippet = worksheetSnippet,
                approvedByHeadmaster = true
            )
            repository.addHomework(hw)
            _userMessage.value = "Homework dispatched to ${_selectedClass.value} students & parents!"
        }
    }

    // Chat
    fun getChatMessagesForSelectedClass(): StateFlow<List<ChatMessageEntity>> {
        return repository.getMessagesForClass(_selectedClass.value)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun sendChatMessage(senderName: String, senderRole: String, text: String) {
        viewModelScope.launch {
            val msg = ChatMessageEntity(
                className = _selectedClass.value,
                senderName = senderName,
                senderRole = senderRole,
                messageText = text,
                timestamp = System.currentTimeMillis(),
                isAutoResponse = false
            )
            repository.sendMessage(msg, _isQuietHoursActive.value)
        }
    }

    // Headmaster Broadcast & SOS
    fun dispatchBroadcast(
        title: String,
        content: String,
        audience: String,
        isUrgentSos: Boolean,
        hasVoiceNote: Boolean
    ) {
        viewModelScope.launch {
            val broadcast = BroadcastNoticeEntity(
                title = title,
                content = content,
                audience = audience,
                senderName = "Headmaster (Mr. Faruk Abdullah)",
                timestamp = System.currentTimeMillis(),
                isUrgentSos = isUrgentSos,
                hasVoiceNote = hasVoiceNote
            )
            repository.addBroadcast(broadcast)
            val tag = if (isUrgentSos) "🚨 SOS ALERT" else "📢 Broadcast"
            _userMessage.value = "$tag dispatched to $audience!"
        }
    }

    // Principal Help Desk
    fun submitHelpDeskTicket(
        name: String,
        role: String,
        studentClass: String,
        category: String,
        subject: String,
        description: String
    ) {
        viewModelScope.launch {
            val ticket = HelpDeskTicketEntity(
                submittedBy = name,
                role = role,
                studentClass = studentClass,
                category = category,
                subject = subject,
                description = description,
                status = "Pending"
            )
            repository.addHelpDeskTicket(ticket)
            _userMessage.value = "Ticket submitted directly to Principal Mr. Faruk Abdullah!"
        }
    }

    fun replyToTicket(ticketId: Long, status: String, reply: String) {
        viewModelScope.launch {
            repository.updateTicketResolution(ticketId, status, reply)
            _userMessage.value = "Resolution updated for ticket #$ticketId"
        }
    }

    // Exam Grades
    fun getGradesForSelectedClass(): StateFlow<List<ExamGradeEntity>> {
        return repository.getGradesForClass(_selectedClass.value)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun addExamGrade(
        studentId: Long,
        studentName: String,
        subject: String,
        gradeOrScore: String,
        scorePercent: Int,
        remarks: String
    ) {
        viewModelScope.launch {
            val grade = ExamGradeEntity(
                studentId = studentId,
                studentName = studentName,
                className = _selectedClass.value,
                subject = subject,
                gradeOrScore = gradeOrScore,
                scorePercent = scorePercent,
                remarks = remarks
            )
            repository.addExamGrade(grade)
            _userMessage.value = "Grade registered for $studentName"
        }
    }
}
