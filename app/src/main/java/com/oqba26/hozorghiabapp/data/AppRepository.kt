package com.oqba26.hozorghiabapp.data

import com.oqba26.hozorghiabapp.AttendanceItemUi
import com.oqba26.hozorghiabapp.AttendanceStatus
import com.oqba26.hozorghiabapp.FinancialItemUi
import com.oqba26.hozorghiabapp.DayAttendanceStatus
import com.oqba26.hozorghiabapp.MonthAttendanceSummary
import com.oqba26.hozorghiabapp.StudentAttendanceDetailsUiState
import com.oqba26.hozorghiabapp.viewmodel.MonthFinancialStatus
import com.oqba26.hozorghiabapp.viewmodel.StudentFinancialDetailsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import saman.zamani.persiandate.PersianDate
import java.time.LocalDate

interface AppRepository {
    fun getAttendanceForDate(date: LocalDate): Flow<List<AttendanceItemUi>>
    suspend fun setAttendance(studentId: Long, date: LocalDate, isPresent: Boolean)

    fun getFinancialForMonth(year: Int, month: Int): Flow<List<FinancialItemUi>>
    suspend fun savePayment(studentId: Long, year: Int, month: Int, amount: Int, description: String?)
    suspend fun deletePayment(studentId: Long, year: Int, month: Int)

    fun getAllStudents(): Flow<List<StudentEntity>>
    suspend fun addStudent(fullName: String)
    suspend fun deleteStudent(student: StudentEntity)

    fun getStudentFinancialDetails(studentId: Long, year: Int): Flow<StudentFinancialDetailsUiState>
    fun getStudentAttendanceDetails(studentId: Long, year: Int): Flow<StudentAttendanceDetailsUiState>
    fun getMonthlyFee(): Flow<Int>
}

class AppRepositoryImpl(
    private val studentDao: StudentDao,
    private val attendanceDao: AttendanceDao,
    private val paymentDao: PaymentDao,
    private val context: android.content.Context
) : AppRepository {

    override fun getAttendanceForDate(date: LocalDate): Flow<List<AttendanceItemUi>> {
        return combine(
            studentDao.getAllStudentsFlow(),
            attendanceDao.getAttendanceForDateFlow(date.toString())
        ) { students, records ->
            val attendanceMap = records.associateBy { it.studentId }
            students.map { student ->
                val record = attendanceMap[student.id]
                val status = when (record?.present) {
                    true -> AttendanceStatus.PRESENT
                    false -> AttendanceStatus.ABSENT
                    null -> AttendanceStatus.UNKNOWN
                }
                AttendanceItemUi(
                    studentId = student.id,
                    fullName = student.fullName,
                    status = status
                )
            }
        }
    }

    override suspend fun setAttendance(studentId: Long, date: LocalDate, isPresent: Boolean) {
        val record = AttendanceEntity(
            studentId = studentId,
            date = date.toString(),
            present = isPresent,
            attendanceTimestamp = System.currentTimeMillis()
        )
        attendanceDao.upsertAttendance(record)
    }

    override fun getFinancialForMonth(year: Int, month: Int): Flow<List<FinancialItemUi>> {
        return combine(
            studentDao.getAllStudentsFlow(),
            paymentDao.getPaymentsForMonthFlow(year, month)
        ) { students, payments ->
            val paymentMap = payments.associateBy { it.studentId }
            students.map {
                val payment = paymentMap[it.id]
                FinancialItemUi(
                    studentId = it.id,
                    fullName = it.fullName,
                    paid = payment != null,
                    paymentTimestamp = payment?.paymentTimestamp,
                    amount = payment?.amount ?: 0,
                    description = payment?.description,
                    year = year,
                    month = month
                )
            }
        }
    }

    override suspend fun savePayment(studentId: Long, year: Int, month: Int, amount: Int, description: String?) {
        paymentDao.insertPayment(
            PaymentEntity(
                studentId = studentId,
                year = year,
                month = month,
                amount = amount,
                description = description
            )
        )
    }

    override suspend fun deletePayment(studentId: Long, year: Int, month: Int) {
        paymentDao.deletePayment(PaymentEntity(studentId, year, month, 0))
    }

    override fun getAllStudents(): Flow<List<StudentEntity>> {
        return studentDao.getAllStudentsFlow()
    }

    override suspend fun addStudent(fullName: String) {
        studentDao.insertStudent(StudentEntity(fullName = fullName))
    }

    override suspend fun deleteStudent(student: StudentEntity) {
        studentDao.deleteStudent(student)
    }

    override fun getStudentFinancialDetails(studentId: Long, year: Int): Flow<StudentFinancialDetailsUiState> {
        val today = PersianDate()
        return combine(
            studentDao.getStudentByIdFlow(studentId),
            paymentDao.getPaymentsForStudentAndYearFlow(studentId, year)
        ) { student, payments ->
            val paymentMap = payments.associateBy { it.month }
            val months = (1..12).map { month ->
                val payment = paymentMap[month]
                val isEnabled = year < today.shYear || (year == today.shYear && month <= today.shMonth)
                MonthFinancialStatus(
                    month = month,
                    monthName = PersianDate().apply { shMonth = month }.monthName,
                    isPaid = payment != null,
                    paymentTimestamp = payment?.paymentTimestamp,
                    amount = payment?.amount ?: 0,
                    description = payment?.description,
                    isEnabled = isEnabled
                )
            }
            StudentFinancialDetailsUiState(year, months, student.fullName)
        }
    }

    override fun getStudentAttendanceDetails(studentId: Long, year: Int): Flow<StudentAttendanceDetailsUiState> {
        return combine(
            studentDao.getStudentByIdFlow(studentId),
            attendanceDao.getAttendanceForStudentFlow(studentId)
        ) { student, allRecords ->
            val filteredRecords = allRecords.mapNotNull { record ->
                try {
                    val pDate = PersianDate(java.sql.Date.valueOf(record.date))
                    if (pDate.shYear == year) {
                        record to pDate
                    } else null
                } catch (_: Exception) { null }
            }

            val groupedByMonth = filteredRecords.groupBy { it.second.shMonth }

            val monthsList = (1..12).map { month ->
                val monthName = PersianDate().apply { shMonth = month }.monthName
                val days = groupedByMonth[month]?.map { (record, _) ->
                    DayAttendanceStatus(
                        date = record.date,
                        isPresent = record.present,
                        timestamp = record.attendanceTimestamp
                    )
                }?.sortedBy { it.date } ?: emptyList()
                
                MonthAttendanceSummary(month, monthName, days)
            }

            StudentAttendanceDetailsUiState(
                studentId = student.id,
                studentName = student.fullName,
                year = year,
                months = monthsList
            )
        }
    }

    override fun getMonthlyFee(): Flow<Int> {
        return context.settingsFlow().map { it.monthlyFee }
    }
}