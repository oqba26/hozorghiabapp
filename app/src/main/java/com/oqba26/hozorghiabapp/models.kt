package com.oqba26.hozorghiabapp

import java.time.LocalDate

// --- Enums for State ---
enum class AttendanceStatus {
    PRESENT, ABSENT, UNKNOWN
}

// --- UI Models ---

data class AttendanceItemUi(
    val studentId: Long,
    val fullName: String,
    val status: AttendanceStatus
)

data class FinancialItemUi(
    val studentId: Long,
    val fullName: String,
    val paid: Boolean,
    val paymentTimestamp: Long?,
    val amount: Int = 0,
    val description: String? = null,
    val year: Int,
    val month: Int
)

data class AppSettings(
    val monthlyFee: Int = 200_000,
    val fontKey: String = "vazirmatn"
)

// --- UI State Models ---

data class AttendanceUiState(
    val date: LocalDate = LocalDate.now(),
    val unknown: List<AttendanceItemUi> = emptyList(),
    val present: List<AttendanceItemUi> = emptyList(),
    val absent: List<AttendanceItemUi> = emptyList(),
    val isFullDate: Boolean = true
)

data class FinancialUiState(
    val year: Int,
    val month: Int,
    val items: List<FinancialItemUi> = emptyList(),
    val isFullDate: Boolean = true
)

data class DayAttendanceStatus(
    val date: String, // yyyy-MM-dd
    val isPresent: Boolean,
    val timestamp: Long?
)

data class MonthAttendanceSummary(
    val month: Int,
    val monthName: String,
    val days: List<DayAttendanceStatus>
)

data class StudentAttendanceDetailsUiState(
    val studentId: Long = 0,
    val studentName: String = "",
    val year: Int = 0,
    val months: List<MonthAttendanceSummary> = emptyList()
)
