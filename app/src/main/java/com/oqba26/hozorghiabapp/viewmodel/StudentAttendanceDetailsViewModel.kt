package com.oqba26.hozorghiabapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.oqba26.hozorghiabapp.StudentAttendanceDetailsUiState
import com.oqba26.hozorghiabapp.data.AppRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import saman.zamani.persiandate.PersianDate

@OptIn(ExperimentalCoroutinesApi::class)
class StudentAttendanceDetailsViewModel(
    private val studentId: Long,
    private val repository: AppRepository
) : ViewModel() {

    private val selectedYearFlow = MutableStateFlow(PersianDate().shYear)

    val uiState: StateFlow<StudentAttendanceDetailsUiState> = selectedYearFlow.flatMapLatest { year ->
        repository.getStudentAttendanceDetails(studentId, year)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StudentAttendanceDetailsUiState(studentId = studentId, year = PersianDate().shYear)
    )

    fun previousYear() {
        selectedYearFlow.value -= 1
    }

    fun nextYear() {
        if (selectedYearFlow.value < PersianDate().shYear) {
            selectedYearFlow.value += 1
        }
    }
}

class StudentAttendanceDetailsViewModelFactory(
    private val studentId: Long,
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentAttendanceDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudentAttendanceDetailsViewModel(studentId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
