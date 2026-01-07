package com.oqba26.hozorghiabapp

import android.app.Application
import com.oqba26.hozorghiabapp.data.AppDatabase
import com.oqba26.hozorghiabapp.data.AppRepository
import com.oqba26.hozorghiabapp.data.AppRepositoryImpl

class HozorGhiabApplication : Application() {

    /**
     * The repository is initialized lazily, so the database is only created when
     * the repository is first accessed.
     */
    val repository: AppRepository by lazy {
        val db = AppDatabase.getInstance(this)
        AppRepositoryImpl(
            studentDao = db.studentDao(),
            attendanceDao = db.attendanceDao(),
            paymentDao = db.paymentDao(),
            context = this
        )
    }
}