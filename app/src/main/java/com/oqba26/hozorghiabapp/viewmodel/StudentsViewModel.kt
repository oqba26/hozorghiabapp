package com.oqba26.hozorghiabapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oqba26.hozorghiabapp.data.AppRepository
import com.oqba26.hozorghiabapp.data.StudentEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentsViewModel(private val repository: AppRepository) : ViewModel() {

    val students: StateFlow<List<StudentEntity>> =
        repository.getAllStudents()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    var newStudentName by mutableStateOf("")
        private set

    fun onNewStudentNameChange(newValue: String) {
        newStudentName = newValue
    }

    fun addStudent() {
        val name = newStudentName.trim()
        if (name.isEmpty()) return

        viewModelScope.launch {
            repository.addStudent(name)
            newStudentName = "" // Clear input after saving
        }
    }

    fun deleteStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.deleteStudent(student)
        }
    }
}