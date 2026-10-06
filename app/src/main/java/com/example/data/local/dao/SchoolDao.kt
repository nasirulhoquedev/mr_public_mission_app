package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AttendanceRecordEntity
import com.example.data.local.entities.BroadcastNoticeEntity
import com.example.data.local.entities.ChatMessageEntity
import com.example.data.local.entities.ClassroomFeedEntity
import com.example.data.local.entities.ExamGradeEntity
import com.example.data.local.entities.HelpDeskTicketEntity
import com.example.data.local.entities.HomeworkEntity
import com.example.data.local.entities.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {

    // Students
    @Query("SELECT * FROM students ORDER BY className, rollNo ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE className = :className ORDER BY rollNo ASC")
    fun getStudentsByClass(className: String): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int

    // Attendance
    @Query("SELECT * FROM attendance_records WHERE date = :date AND className = :className ORDER BY id ASC")
    fun getAttendanceForDateAndClass(date: String, className: String): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance_records WHERE date = :date")
    fun getAllAttendanceForDate(date: String): Flow<List<AttendanceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(records: List<AttendanceRecordEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttendance(record: AttendanceRecordEntity)

    @Query("UPDATE attendance_records SET isPresent = :isPresent WHERE id = :id")
    suspend fun updateAttendanceStatus(id: Long, isPresent: Boolean)

    @Query("UPDATE attendance_records SET smsSent = 1 WHERE id = :id")
    suspend fun markSmsSent(id: Long)

    @Query("UPDATE attendance_records SET isPresent = 1 WHERE date = :date AND className = :className")
    suspend fun markAllPresentForClass(date: String, className: String)

    // Classroom Feed
    @Query("SELECT * FROM classroom_feed ORDER BY timestamp DESC")
    fun getAllFeedPosts(): Flow<List<ClassroomFeedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedPost(post: ClassroomFeedEntity)

    @Query("UPDATE classroom_feed SET likesCount = likesCount + 1 WHERE id = :postId")
    suspend fun incrementPostLikes(postId: Long)

    // Homework
    @Query("SELECT * FROM homework WHERE className = :className ORDER BY id DESC")
    fun getHomeworkForClass(className: String): Flow<List<HomeworkEntity>>

    @Query("SELECT * FROM homework ORDER BY id DESC")
    fun getAllHomework(): Flow<List<HomeworkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomework(homework: HomeworkEntity)

    @Query("UPDATE homework SET approvedByHeadmaster = :approved WHERE id = :homeworkId")
    suspend fun updateHomeworkApproval(homeworkId: Long, approved: Boolean)

    // Broadcasts
    @Query("SELECT * FROM broadcast_notices ORDER BY timestamp DESC")
    fun getAllBroadcasts(): Flow<List<BroadcastNoticeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBroadcast(notice: BroadcastNoticeEntity)

    // Help Desk / Grievance
    @Query("SELECT * FROM help_desk_tickets ORDER BY timestamp DESC")
    fun getAllTickets(): Flow<List<HelpDeskTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: HelpDeskTicketEntity)

    @Query("UPDATE help_desk_tickets SET status = :status, principalReply = :reply WHERE id = :ticketId")
    suspend fun updateTicketResolution(ticketId: Long, status: String, reply: String)

    // Exam Grades
    @Query("SELECT * FROM exam_grades WHERE className = :className ORDER BY studentName ASC, subject ASC")
    fun getGradesForClass(className: String): Flow<List<ExamGradeEntity>>

    @Query("SELECT * FROM exam_grades WHERE studentId = :studentId")
    fun getGradesForStudent(studentId: Long): Flow<List<ExamGradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamGrades(grades: List<ExamGradeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamGrade(grade: ExamGradeEntity)

    // Quiet Chat
    @Query("SELECT * FROM chat_messages WHERE className = :className ORDER BY timestamp ASC")
    fun getMessagesForClass(className: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)
}
