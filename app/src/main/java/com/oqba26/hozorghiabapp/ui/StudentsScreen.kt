@file:Suppress("AssignedValueIsNeverRead")

package com.oqba26.hozorghiabapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.oqba26.hozorghiabapp.R
import com.oqba26.hozorghiabapp.data.StudentEntity
import com.oqba26.hozorghiabapp.viewmodel.StudentsViewModel
import com.oqba26.hozorghiabapp.util.englishDigitsToPersian

@Composable

fun StudentsScreen(
    viewModel: StudentsViewModel,
    onStudentClick: (Long) -> Unit
) {
    val students by viewModel.students.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = stringResource(R.string.add_new_student_title),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.newStudentName,
                onValueChange = { viewModel.onNewStudentNameChange(it) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text(stringResource(R.string.student_name_placeholder)) }
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.addStudent() },
                enabled = viewModel.newStudentName.isNotBlank()
            ) {
                Text(stringResource(R.string.add_button))
            }

            Spacer(Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.students_list_title),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(students, key = { _, student -> student.id }) { index, student ->
                    StudentRow(
                        student = student,
                        index = index + 1,
                        onClick = { onStudentClick(student.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun StudentRow(
    student: StudentEntity,
    index: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "${index.toString().englishDigitsToPersian()}. ${student.fullName}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
    }
}