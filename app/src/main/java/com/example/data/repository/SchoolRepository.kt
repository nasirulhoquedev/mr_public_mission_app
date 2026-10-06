package com.example.data.repository

import com.example.data.local.dao.SchoolDao
import com.example.data.local.entities.AttendanceRecordEntity
import com.example.data.local.entities.BroadcastNoticeEntity
import com.example.data.local.entities.ChatMessageEntity
import com.example.data.local.entities.ClassroomFeedEntity
import com.example.data.local.entities.ExamGradeEntity
import com.example.data.local.entities.HelpDeskTicketEntity
import com.example.data.local.entities.HomeworkEntity
import com.example.data.local.entities.StudentEntity
import kotlinx.coroutines.flow.Flow

class SchoolRepository(private val schoolDao: SchoolDao) {

    val allStudents: Flow<List<StudentEntity>> = schoolDao.getAllStudents()
    val allFeedPosts: Flow<List<ClassroomFeedEntity>> = schoolDao.getAllFeedPosts()
    val allBroadcasts: Flow<List<BroadcastNoticeEntity>> = schoolDao.getAllBroadcasts()
    val allTickets: Flow<List<HelpDeskTicketEntity>> = schoolDao.getAllTickets()
    val allHomework: Flow<List<HomeworkEntity>> = schoolDao.getAllHomework()

    fun getStudentsByClass(className: String): Flow<List<StudentEntity>> {
        return schoolDao.getStudentsByClass(className)
    }

    fun getAttendanceForDateAndClass(date: String, className: String): Flow<List<AttendanceRecordEntity>> {
        return schoolDao.getAttendanceForDateAndClass(date, className)
    }

    fun getAllAttendanceForDate(date: String): Flow<List<AttendanceRecordEntity>> {
        return schoolDao.getAllAttendanceForDate(date)
    }

    suspend fun toggleAttendance(id: Long, currentPresent: Boolean) {
        schoolDao.updateAttendanceStatus(id, !currentPresent)
    }

    suspend fun markAllPresent(date: String, className: String) {
        schoolDao.markAllPresentForClass(date, className)
    }

    suspend fun markSmsSent(id: Long) {
        schoolDao.markSmsSent(id)
    }

    suspend fun addAttendanceRecord(record: AttendanceRecordEntity) {
        schoolDao.insertOrUpdateAttendance(record)
    }

    fun getHomeworkForClass(className: String): Flow<List<HomeworkEntity>> {
        return schoolDao.getHomeworkForClass(className)
    }

    suspend fun addHomework(homework: HomeworkEntity) {
        schoolDao.insertHomework(homework)
    }

    suspend fun updateHomeworkApproval(homeworkId: Long, approved: Boolean) {
        schoolDao.updateHomeworkApproval(homeworkId, approved)
    }

    suspend fun addFeedPost(post: ClassroomFeedEntity) {
        schoolDao.insertFeedPost(post)
    }

    suspend fun likeFeedPost(postId: Long) {
        schoolDao.incrementPostLikes(postId)
    }

    suspend fun addBroadcast(notice: BroadcastNoticeEntity) {
        schoolDao.insertBroadcast(notice)
    }

    suspend fun addHelpDeskTicket(ticket: HelpDeskTicketEntity) {
        schoolDao.insertTicket(ticket)
    }

    suspend fun updateTicketResolution(ticketId: Long, status: String, reply: String) {
        schoolDao.updateTicketResolution(ticketId, status, reply)
    }

    fun getGradesForClass(className: String): Flow<List<ExamGradeEntity>> {
        return schoolDao.getGradesForClass(className)
    }

    fun getGradesForStudent(studentId: Long): Flow<List<ExamGradeEntity>> {
        return schoolDao.getGradesForStudent(studentId)
    }

    suspend fun addExamGrade(grade: ExamGradeEntity) {
        schoolDao.insertExamGrade(grade)
    }

    fun getMessagesForClass(className: String): Flow<List<ChatMessageEntity>> {
        return schoolDao.getMessagesForClass(className)
    }

    suspend fun sendMessage(message: ChatMessageEntity, isQuietHoursActive: Boolean = false) {
        schoolDao.insertChatMessage(message)
        // If message is from a parent and quiet hours are enabled, auto-reply immediately
        if (message.senderRole == "Parent" && isQuietHoursActive) {
            val autoReply = ChatMessageEntity(
                className = message.className,
                senderName = "M.R. Mission Auto-Responder",
                senderRole = "Teacher",
                messageText = "[Quiet Hours Active] School operating hours have ended. Your query has been logged and the class teacher will reply during official hours tomorrow (8:30 AM - 2:30 PM).",
                timestamp = System.currentTimeMillis() + 500,
                isAutoResponse = true
            )
            schoolDao.insertChatMessage(autoReply)
        }
    }

    suspend fun ensureDataPopulated() {
        if (schoolDao.getStudentCount() == 0) {
            com.example.data.local.SchoolDatabase.populateInitialData(schoolDao)
        }
    }
}
