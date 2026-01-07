package com.oqba26.hozorghiabapp.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// ---------- حضور و غیاب ----------

@Entity(
    tableName = "attendance",
    primaryKeys = ["studentId", "date"]   // هر هنرجو در هر روز یک رکورد
)
data class AttendanceEntity(
    val studentId: Long,
    val date: String,   // فرم yyyy-MM-dd (خود LocalDate.toString)
    val present: Boolean,
    val attendanceTimestamp: Long? = null
)

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM attendance WHERE date = :date")
    fun getAttendanceForDateFlow(date: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId")
    fun getAttendanceForStudentFlow(studentId: Long): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAttendance(record: AttendanceEntity)
}