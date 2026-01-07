package com.oqba26.hozorghiabapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.oqba26.hozorghiabapp.data.AppRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import saman.zamani.persiandate.PersianDate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class StudentFinancialDetailsUiState(
    val year: Int = PersianDate().shYear,
    val months: List<MonthFinancialStatus> = emptyList(),
    val studentName: String = "",
    val monthlyFee: Int = 0
)

data class MonthFinancialStatus(
    val month: Int,
    val monthName: String,
    val isPaid: Boolean,
    val paymentTimestamp: Long?,
    val amount: Int = 0,
    val description: String? = null,
    val isEnabled: Boolean
)

@OptIn(ExperimentalCoroutinesApi::class)
class StudentFinancialDetailsViewModel(
    private val studentId: Long,
    private val repository: AppRepository
) : ViewModel() {

    private val _messageFlow = MutableSharedFlow<String>()
    val messageFlow = _messageFlow.asSharedFlow()

    private val selectedYearFlow = MutableStateFlow(PersianDate().shYear)

    val uiState: StateFlow<StudentFinancialDetailsUiState> = kotlinx.coroutines.flow.combine(
        selectedYearFlow.flatMapLatest { year -> repository.getStudentFinancialDetails(studentId, year) },
        repository.getMonthlyFee()
    ) { details, fee ->
        details.copy(monthlyFee = fee)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StudentFinancialDetailsUiState()
    )

    fun previousYear() {
        viewModelScope.launch {
            val prevYear = selectedYearFlow.value - 1
            val details = repository.getStudentFinancialDetails(studentId, prevYear).first()
            if (details.months.any { it.isPaid }) {
                selectedYearFlow.value = prevYear
            } else {
                _messageFlow.emit("پرداختی برای سال $prevYear یافت نشد")
            }
        }
    }

    fun nextYear() {
        viewModelScope.launch {
            _messageFlow.emit("نمیتوان برای سال بعد پرداختی ثبت کرد")
        }
    }

    fun savePayment(month: Int, amount: Int, description: String?) {
        viewModelScope.launch {
            val year = selectedYearFlow.value
            repository.savePayment(studentId, year, month, amount, description)
        }
    }

    fun deletePayment(month: Int) {
        viewModelScope.launch {
            val year = selectedYearFlow.value
            repository.deletePayment(studentId, year, month)
        }
    }
}

class StudentFinancialDetailsViewModelFactory(
    private val studentId: Long,
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentFinancialDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudentFinancialDetailsViewModel(studentId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}