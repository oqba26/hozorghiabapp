package com.oqba26.hozorghiabapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oqba26.hozorghiabapp.data.AppRepository
import com.oqba26.hozorghiabapp.AttendanceUiState
import com.oqba26.hozorghiabapp.AttendanceStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AttendanceViewModel(private val repository: AppRepository) : ViewModel() {

    private val selectedDateFlow = MutableStateFlow(LocalDate.now())
    private val isFullDateFlow = MutableStateFlow(true)

    val uiState: StateFlow<AttendanceUiState> = combine(
        selectedDateFlow, isFullDateFlow
    ) { date, isFullDate ->
        Pair(date, isFullDate)
    }.flatMapLatest { (date, isFullDate) ->
        repository.getAttendanceForDate(date).combine(isFullDateFlow) { items, fullDate ->
            val grouped = items.groupBy { it.status }
            AttendanceUiState(
                date = date,
                isFullDate = fullDate,
                unknown = grouped[AttendanceStatus.UNKNOWN] ?: emptyList(),
                present = grouped[AttendanceStatus.PRESENT] ?: emptyList(),
                absent = grouped[AttendanceStatus.ABSENT] ?: emptyList()
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AttendanceUiState()
    )

    fun setAttendance(studentId: Long, isPresent: Boolean) {
        val date = selectedDateFlow.value
        viewModelScope.launch {
            repository.setAttendance(studentId, date, isPresent)
        }
    }

    fun previousDay() {
        selectedDateFlow.value = selectedDateFlow.value.minusDays(1)
    }

    fun nextDay() {
        selectedDateFlow.value = selectedDateFlow.value.plusDays(1)
    }

    fun toggleDateFormat() {
        isFullDateFlow.value = !isFullDateFlow.value
    }
}