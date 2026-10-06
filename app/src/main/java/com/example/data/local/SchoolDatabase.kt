package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.SchoolDao
import com.example.data.local.entities.AttendanceRecordEntity
import com.example.data.local.entities.BroadcastNoticeEntity
import com.example.data.local.entities.ChatMessageEntity
import com.example.data.local.entities.ClassroomFeedEntity
import com.example.data.local.entities.ExamGradeEntity
import com.example.data.local.entities.HelpDeskTicketEntity
import com.example.data.local.entities.HomeworkEntity
import com.example.data.local.entities.StudentEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        StudentEntity::class,
        AttendanceRecordEntity::class,
        ClassroomFeedEntity::class,
        HomeworkEntity::class,
        BroadcastNoticeEntity::class,
        HelpDeskTicketEntity::class,
        ExamGradeEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SchoolDatabase : RoomDatabase() {

    abstract fun schoolDao(): SchoolDao

    companion object {
        @Volatile
        private var INSTANCE: SchoolDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SchoolDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SchoolDatabase::class.java,
                    "mr_public_mission_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.schoolDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: SchoolDao) {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val students = listOf(
                // Class 0 (KG)
                StudentEntity(1, 1, "Ayaan Mondal", "Class 0 (KG)", "Rafiqul Mondal", "+91 9832101011", true, 0.0),
                StudentEntity(2, 2, "Zainab Khatun", "Class 0 (KG)", "Abdul Latif", "+91 9832101012", true, 0.0),
                StudentEntity(3, 3, "Samiul Hoque", "Class 0 (KG)", "Enamul Hoque", "+91 9832101013", false, 650.0),
                StudentEntity(4, 4, "Afifa Parveen", "Class 0 (KG)", "Nurul Islam", "+91 9832101014", true, 0.0),
                StudentEntity(5, 5, "Rehan Mallick", "Class 0 (KG)", "Jahangir Mallick", "+91 9832101015", true, 0.0),

                // Class I
                StudentEntity(6, 1, "Arif Billah", "Class I", "Motiur Rahman", "+91 9832102021", true, 0.0),
                StudentEntity(7, 2, "Tasneem Sultana", "Class I", "Masud Alam", "+91 9832102022", true, 0.0),
                StudentEntity(8, 3, "Farhan Akhtar", "Class I", "Kamrul Hassan", "+91 9832102023", false, 800.0),
                StudentEntity(9, 4, "Sumaiya Yasmin", "Class I", "Sirajul Islam", "+91 9832102024", true, 0.0),
                StudentEntity(10, 5, "Wasim Akram", "Class I", "Mahbubur Rahman", "+91 9832102025", true, 0.0),

                // Class II
                StudentEntity(11, 1, "Nahid Hasan", "Class II", "Nazrul Islam", "+91 9832103031", true, 0.0),
                StudentEntity(12, 2, "Mehek Fatima", "Class II", "Imran Hossain", "+91 9832103032", true, 0.0),
                StudentEntity(13, 3, "Shahid Afridi", "Class II", "Ashraful Haque", "+91 9832103033", true, 0.0),
                StudentEntity(14, 4, "Rukhsana Begum", "Class II", "Abdur Rouf", "+91 9832103034", false, 850.0),

                // Class III
                StudentEntity(15, 1, "Tanvir Ahmed", "Class III", "Shahjahan Ali", "+91 9832104041", true, 0.0),
                StudentEntity(16, 2, "Nasreen Banu", "Class III", "Ziaul Haque", "+91 9832104042", true, 0.0),
                StudentEntity(17, 3, "Mustafizur Rahman", "Class III", "Golam Kibria", "+91 9832104043", true, 0.0),
                StudentEntity(18, 4, "Amina Sheikh", "Class III", "Habibur Rahman", "+91 9832104044", true, 0.0),

                // Class IV
                StudentEntity(19, 1, "Sohail Rana", "Class IV", "Delowar Hossain", "+91 9832105051", true, 0.0),
                StudentEntity(20, 2, "Faiza Naaz", "Class IV", "Mustaque Ahmed", "+91 9832105052", false, 950.0),
                StudentEntity(21, 3, "Rashid Khan", "Class IV", "Anwar Ali", "+91 9832105053", true, 0.0),
                StudentEntity(22, 4, "Mubashira Tabassum", "Class IV", "Shahidul Islam", "+91 9832105054", true, 0.0),

                // Class V
                StudentEntity(23, 1, "Hasan Mahmud", "Class V", "Lutfar Rahman", "+91 9832106061", true, 0.0),
                StudentEntity(24, 2, "Ayesha Siddiqa", "Class V", "Manzur Elahi", "+91 9832106062", true, 0.0),
                StudentEntity(25, 3, "Imtiaz Ali", "Class V", "Babar Ali", "+91 9832106063", true, 0.0),
                StudentEntity(26, 4, "Sadiya Afrin", "Class V", "Rezaul Karim", "+91 9832106064", false, 1100.0),

                // Class VI
                StudentEntity(27, 1, "Tariq Aziz", "Class VI", "Farhad Hossain", "+91 9832107071", true, 0.0),
                StudentEntity(28, 2, "Rukaiya Jahan", "Class VI", "Shamim Osman", "+91 9832107072", true, 0.0),
                StudentEntity(29, 3, "Shakib Al Hasan", "Class VI", "Kawsar Ahmed", "+91 9832107073", true, 0.0),
                StudentEntity(30, 4, "Mahira Khan", "Class VI", "Giasuddin Mollah", "+91 9832107074", true, 0.0)
            )
            dao.insertStudents(students)

            // Seed attendance records for today
            val attendanceRecords = mutableListOf<AttendanceRecordEntity>()
            students.forEachIndexed { index, student ->
                // Make a few students absent intentionally for demonstration
                val isAbsent = (index == 2 || index == 7 || index == 19)
                attendanceRecords.add(
                    AttendanceRecordEntity(
                        studentId = student.id,
                        studentName = student.name,
                        className = student.className,
                        date = today,
                        isPresent = !isAbsent,
                        smsSent = false
                    )
                )
            }
            dao.insertAttendance(attendanceRecords)

            // Seed Classroom Feed Posts with pre-set tags
            val feedPosts = listOf(
                ClassroomFeedEntity(
                    className = "Class IV",
                    teacherName = "Tr. Aminul Haque",
                    caption = "Science Practical: Students observed stomata & leaf structures under magnifying lenses today! Brilliant engagement.",
                    presetTag = "Science Practical",
                    likesCount = 28,
                    imageDescription = "Microscope & plant leaf examination activity"
                ),
                ClassroomFeedEntity(
                    className = "Class 0 (KG)",
                    teacherName = "Tr. Sabina Yasmin",
                    caption = "Morning Assembly & Alphabet Block Building: Little stars learning phonics and teamwork.",
                    presetTag = "Morning Assembly",
                    likesCount = 34,
                    imageDescription = "Colorful alphabet wooden blocks on classroom carpet"
                ),
                ClassroomFeedEntity(
                    className = "Class II",
                    teacherName = "Tr. Rokeya Begum",
                    caption = "Handwriting & Drawing Practice: Bengali vowel strokes and creative water coloring competition.",
                    presetTag = "Handwriting & Drawing",
                    likesCount = 19,
                    imageDescription = "Neat cursive notebooks and watercolor drawings"
                ),
                ClassroomFeedEntity(
                    className = "Class VI",
                    teacherName = "Tr. Mahfuzur Rahman",
                    caption = "Math Olympiad Speed Calculation: Group drill solving fractions and algebraic expressions.",
                    presetTag = "Math Olympiad",
                    likesCount = 22,
                    imageDescription = "Blackboard math solving and students working in pairs"
                )
            )
            feedPosts.forEach { dao.insertFeedPost(it) }

            // Seed Homework with voice dictation & worksheet snapshots
            val homeworkList = listOf(
                HomeworkEntity(
                    className = "Class IV",
                    subject = "Mathematics",
                    title = "Fraction Addition & Word Problems",
                    description = "Dictated: Complete Exercise 4.2 from textbook page 56. Questions 1 to 8 in school fair notebook. Draw neat diagrams.",
                    assignedDate = today,
                    dueDate = "Tomorrow, 9:00 AM",
                    isVoiceDictated = true,
                    hasWorksheetImage = true,
                    worksheetSnippet = "Ex 4.2: Q1. Find 3/4 + 2/5. Q2. If Ali drinks 1/2 L milk..."
                ),
                HomeworkEntity(
                    className = "Class 0 (KG)",
                    subject = "English / Phonics",
                    title = "Tracing Letters A to E & Object Identification",
                    description = "Practice tracing uppercase and lowercase letters 'A, B, C, D, E' on worksheet sheet #4. Color the apple and balloon.",
                    assignedDate = today,
                    dueDate = "Tomorrow, 9:30 AM",
                    isVoiceDictated = false,
                    hasWorksheetImage = true,
                    worksheetSnippet = "Trace dotted lines: [A a] [B b] [C c] [D d] [E e]"
                ),
                HomeworkEntity(
                    className = "Class I",
                    subject = "Bengali",
                    title = "Barnamala Writing Practice",
                    description = "Dictated by Teacher: Write Svaravarna (অ to ঔ) 2 times neatly. Memorize 2 words for each letter.",
                    assignedDate = today,
                    dueDate = "Tomorrow, 9:00 AM",
                    isVoiceDictated = true,
                    hasWorksheetImage = false,
                    worksheetSnippet = "অ - অজগর, আ - আম, ই - ইঁদুর..."
                ),
                HomeworkEntity(
                    className = "Class VI",
                    subject = "Science",
                    title = "Photosynthesis Diagram & Short Notes",
                    description = "Draw labelled diagram of chloroplast and describe the light-dependent reaction in 5 bullet points.",
                    assignedDate = today,
                    dueDate = "Thursday, 9:00 AM",
                    isVoiceDictated = false,
                    hasWorksheetImage = true,
                    worksheetSnippet = "Diagram reference: NCERT Chapter 3, Fig 3.4"
                )
            )
            homeworkList.forEach { dao.insertHomework(it) }

            // Seed Broadcasts from Headmaster Mr. Faruk Abdullah
            val broadcasts = listOf(
                BroadcastNoticeEntity(
                    title = "Upcoming 2nd Periodic Assessment & Revision Schedule",
                    content = "Respected Parents and Teachers, 2nd Periodic Assessment will commence from next Monday. Please ensure complete revision of syllabus up to Chapter 5. Regular attendance is mandatory.",
                    audience = "All School",
                    senderName = "Headmaster (Mr. Faruk Abdullah)",
                    isUrgentSos = false,
                    hasVoiceNote = true
                ),
                BroadcastNoticeEntity(
                    title = "URGENT: All Teachers Staff Coordination Meeting",
                    content = "All Class 0 to VI teachers are requested to assemble in Conference Hall at 2:45 PM sharp for Term Evaluation & Sports Day preparations.",
                    audience = "Teachers Only",
                    senderName = "Headmaster (Mr. Faruk Abdullah)",
                    isUrgentSos = true,
                    hasVoiceNote = false
                ),
                BroadcastNoticeEntity(
                    title = "Winter Timing & Uniform Advisory",
                    content = "Kindly send students in complete winter navy blue blazer/sweater from next Monday. School gates open at 8:45 AM and assembly begins promptly at 9:00 AM.",
                    audience = "Parents Only",
                    senderName = "Headmaster (Mr. Faruk Abdullah)",
                    isUrgentSos = false,
                    hasVoiceNote = false
                )
            )
            broadcasts.forEach { dao.insertBroadcast(it) }

            // Seed Help Desk / Grievance tickets for Principal Mr. Faruk Abdullah
            val tickets = listOf(
                HelpDeskTicketEntity(
                    submittedBy = "Rafiqul Mondal (Father of Ayaan)",
                    role = "Parent",
                    studentClass = "Class 0 (KG)",
                    category = "Transport",
                    subject = "School Van Route 3 Morning Delay",
                    description = "Honorable Principal Sir, Van #3 has been arriving 20 minutes late for the past three mornings near Basirhat junction. Requesting your kind intervention.",
                    status = "Resolved",
                    principalReply = "Action taken: Driver was notified and route re-adjusted. Route 3 is now monitored daily by admin supervisor."
                ),
                HelpDeskTicketEntity(
                    submittedBy = "Kamrul Hassan (Father of Farhan)",
                    role = "Parent",
                    studentClass = "Class I",
                    category = "Academics",
                    subject = "Extra Bengali Reading Support",
                    description = "Respected Sir, Farhan needs extra practice with conjunct letters (Juktakkhor). Could the subject teacher guide him during zero period?",
                    status = "In Review",
                    principalReply = "Discussed with Bengali teacher Tr. Rokeya. Special remedial reading session arranged on Tuesdays & Thursdays."
                ),
                HelpDeskTicketEntity(
                    submittedBy = "Tr. Aminul Haque (Science)",
                    role = "Teacher",
                    studentClass = "Class IV",
                    category = "Facilities",
                    subject = "Science Lab Microscope Magnification Lens",
                    description = "Sir, two compound lenses require servicing before next week's biology practical session.",
                    status = "Pending",
                    principalReply = ""
                )
            )
            tickets.forEach { dao.insertTicket(it) }

            // Seed Exam Grades
            val grades = listOf(
                // KG Template
                ExamGradeEntity(studentId = 1, studentName = "Ayaan Mondal", className = "Class 0 (KG)", subject = "Rhymes & Oral", gradeOrScore = "Excellent", scorePercent = 95, remarks = "Very energetic & expressive"),
                ExamGradeEntity(studentId = 2, studentName = "Zainab Khatun", className = "Class 0 (KG)", subject = "Number Concept", gradeOrScore = "Good", scorePercent = 88, remarks = "Recognizes digits 1-50 accurately"),
                ExamGradeEntity(studentId = 3, studentName = "Samiul Hoque", className = "Class 0 (KG)", subject = "Color & Drawing", gradeOrScore = "Needs Improvement", scorePercent = 65, remarks = "Needs hand coordination practice"),

                // Class I-VI Template
                ExamGradeEntity(studentId = 6, studentName = "Arif Billah", className = "Class I", subject = "Mathematics", gradeOrScore = "A+ (96/100)", scorePercent = 96, remarks = "Top performer in mental math"),
                ExamGradeEntity(studentId = 6, studentName = "Arif Billah", className = "Class I", subject = "English", gradeOrScore = "A (88/100)", scorePercent = 88, remarks = "Fluent reading ability"),
                ExamGradeEntity(studentId = 19, studentName = "Sohail Rana", className = "Class IV", subject = "Science", gradeOrScore = "A+ (92/100)", scorePercent = 92, remarks = "Deep curiosity in experiments"),
                ExamGradeEntity(studentId = 19, studentName = "Sohail Rana", className = "Class IV", subject = "Social Studies", gradeOrScore = "B (74/100)", scorePercent = 74, remarks = "Map pointing requires revision"),
                ExamGradeEntity(studentId = 27, studentName = "Tariq Aziz", className = "Class VI", subject = "Mathematics", gradeOrScore = "A+ (98/100)", scorePercent = 98, remarks = "Exceptional speed and precision")
            )
            dao.insertExamGrades(grades)

            // Seed Chat Messages (Quiet hours testing)
            val chatMessages = listOf(
                ChatMessageEntity(
                    className = "Class IV",
                    senderName = "Mustaque Ahmed (Faiza's Father)",
                    senderRole = "Parent",
                    messageText = "Good evening teacher, will tomorrow's science test include chapter 3 exercises?",
                    timestamp = System.currentTimeMillis() - 7200000,
                    isAutoResponse = false
                ),
                ChatMessageEntity(
                    className = "Class IV",
                    senderName = "M.R. Mission Auto-Assistant",
                    senderRole = "Teacher",
                    messageText = "[Quiet Hours Active] School working hours have ended for today. Your message has been safely received. Tr. Aminul will respond tomorrow morning between 8:30 AM and 2:30 PM.",
                    timestamp = System.currentTimeMillis() - 7190000,
                    isAutoResponse = true
                ),
                ChatMessageEntity(
                    className = "Class IV",
                    senderName = "Tr. Aminul Haque",
                    senderRole = "Teacher",
                    messageText = "Assalamu Alaikum Mr. Ahmed. Yes, only questions 1 to 5 from chapter 3 will be included. Kindly ensure Faiza brings her geometry set.",
                    timestamp = System.currentTimeMillis() - 3600000,
                    isAutoResponse = false
                )
            )
            chatMessages.forEach { dao.insertChatMessage(it) }
        }
    }
}
