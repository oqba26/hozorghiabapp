package com.oqba26.hozorghiabapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oqba26.hozorghiabapp.FinancialUiState
import com.oqba26.hozorghiabapp.data.AppRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import saman.zamani.persiandate.PersianDate

@OptIn(ExperimentalCoroutinesApi::class)
class FinancialViewModel(private val repository: AppRepository) : ViewModel() {

    private val selectedDateFlow = MutableStateFlow(PersianDate())
    private val isFullDateFlow = MutableStateFlow(false)

    val uiState: StateFlow<FinancialUiState> = combine(
        selectedDateFlow, isFullDateFlow
    ) { date, isFullDate ->
        Pair(date, isFullDate)
    }.flatMapLatest { (date, isFullDate) ->
        // Using Persian date directly for storage consistency
        repository.getFinancialForMonth(date.shYear, date.shMonth).combine(isFullDateFlow) { items, fullDate ->
            FinancialUiState(date.shYear, date.shMonth, items, fullDate)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinancialUiState(PersianDate().shYear, PersianDate().shMonth)
    )

    fun togglePayment(studentId: Long, newPaidState: Boolean) {
        viewModelScope.launch {
            val date = selectedDateFlow.value
            if (newPaidState) {
                // For quick toggle, we use default fee
                repository.savePayment(studentId, date.shYear, date.shMonth, 200_000, null)
            } else {
                repository.deletePayment(studentId, date.shYear, date.shMonth)
            }
        }
    }

    fun previousMonth() {
        val current = selectedDateFlow.value
        var newYear = current.shYear
        var newMonth = current.shMonth - 1
        if (newMonth < 1) {
            newMonth = 12
            newYear -= 1
        }
        
        val newDate = PersianDate()
        newDate.shYear = newYear
        newDate.shMonth = newMonth
        newDate.shDay = 1
        selectedDateFlow.value = newDate
    }

    fun nextMonth() {
        val current = selectedDateFlow.value
        var newYear = current.shYear
        var newMonth = current.shMonth + 1
        if (newMonth > 12) {
            newMonth = 1
            newYear += 1
        }
        
        val newDate = PersianDate()
        newDate.shYear = newYear
        newDate.shMonth = newMonth
        newDate.shDay = 1
        selectedDateFlow.value = newDate
    }

    fun toggleDateFormat() {
        isFullDateFlow.value = !isFullDateFlow.value
    }
}