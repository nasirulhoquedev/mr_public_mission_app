package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rollNo: Int,
    val name: String,
    val className: String,
    val guardianName: String,
    val parentPhone: String,
    val isFeePaid: Boolean = false,
    val feeDueAmount: Double = 0.0
)

@Entity(tableName = "attendance_records")
data class AttendanceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val className: String,
    val date: String,
    val isPresent: Boolean = true,
    val smsSent: Boolean = false
)

@Entity(tableName = "classroom_feed")
data class ClassroomFeedEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val className: String,
    val teacherName: String,
    val caption: String,
    val presetTag: String,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val imageDescription: String = ""
)

@Entity(tableName = "homework")
data class HomeworkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val className: String,
    val subject: String,
    val title: String,
    val description: String,
    val assignedDate: String,
    val dueDate: String,
    val isVoiceDictated: Boolean = false,
    val hasWorksheetImage: Boolean = false,
    val worksheetSnippet: String = "",
    val approvedByHeadmaster: Boolean = true
)

@Entity(tableName = "broadcast_notices")
data class BroadcastNoticeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val audience: String = "All School", // "All School", "Teachers Only", "Parents Only"
    val senderName: String = "Headmaster (Mr. Faruk Abdullah)",
    val timestamp: Long = System.currentTimeMillis(),
    val isUrgentSos: Boolean = false,
    val hasVoiceNote: Boolean = false
)

@Entity(tableName = "help_desk_tickets")
data class HelpDeskTicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val submittedBy: String,
    val role: String, // "Parent" or "Teacher"
    val studentClass: String,
    val category: String, // "Academics", "Transport", "Fees", "Facilities", "Urgent"
    val subject: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending", // "Pending", "In Review", "Resolved"
    val principalReply: String = ""
)

@Entity(tableName = "exam_grades")
data class ExamGradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val className: String,
    val subject: String,
    val examType: String = "Term Exam",
    val gradeOrScore: String, // "Good", "Needs Improvement" or "A+", "92/100"
    val scorePercent: Int = 85,
    val remarks: String = "Good effort"
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val className: String,
    val senderName: String,
    val senderRole: String, // "Teacher" or "Parent"
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isAutoResponse: Boolean = false
)
