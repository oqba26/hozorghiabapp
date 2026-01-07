package com.oqba26.hozorghiabapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// ---------- پرداخت شهریه ----------

@Entity(
    tableName = "payments",
    primaryKeys = ["studentId", "year", "month"]
)
data class PaymentEntity(
    val studentId: Long,
    val year: Int,
    val month: Int, // 1-12
    val paymentTimestamp: Long = System.currentTimeMillis(),
    val amount: Int = 0,
    val description: String? = null
)

@Dao
interface PaymentDao {

    @Query("SELECT * FROM payments WHERE year = :year AND month = :month")
    fun getPaymentsForMonthFlow(year: Int, month: Int): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE studentId = :studentId AND year = :year")
    fun getPaymentsForStudentAndYearFlow(studentId: Long, year: Int): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Delete
    suspend fun deletePayment(payment: PaymentEntity)
}