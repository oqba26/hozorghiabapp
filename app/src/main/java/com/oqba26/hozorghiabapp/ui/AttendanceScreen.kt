package com.oqba26.hozorghiabapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.oqba26.hozorghiabapp.AttendanceItemUi
import com.oqba26.hozorghiabapp.AttendanceStatus
import com.oqba26.hozorghiabapp.AttendanceUiState
import com.oqba26.hozorghiabapp.R
import com.oqba26.hozorghiabapp.util.englishDigitsToPersian
import com.oqba26.hozorghiabapp.util.toPersianDateFullString
import com.oqba26.hozorghiabapp.util.toPersianDateShortString
import com.oqba26.hozorghiabapp.viewmodel.AttendanceViewModel

@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel,
    onStudentClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val dateText = remember(uiState.date, uiState.isFullDate) {
                if (uiState.isFullDate) uiState.date.toPersianDateFullString() else uiState.date.toPersianDateShortString()
            }

            DaySelector(
                dateText = dateText,
                onPrevious = viewModel::previousDay,
                onNext = viewModel::nextDay,
                onToggleFormat = viewModel::toggleDateFormat
            )

            Spacer(Modifier.height(16.dp))

            if (uiState.unknown.isEmpty() && uiState.present.isEmpty() && uiState.absent.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_students_registered),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (uiState.unknown.isNotEmpty()) {
                        item(key = "header_u") { SectionHeader(title = stringResource(R.string.attendance_section_unknown)) }
                        itemsIndexed(uiState.unknown, key = { _, item -> "u_${item.studentId}" }) { index, item ->
                            AttendanceRow(
                                item = item,
                                index = index + 1,
                                onSetAttendance = viewModel::setAttendance,
                                onClick = { onStudentClick(item.studentId) }
                            )
                        }
                    }

                    if (uiState.present.isNotEmpty()) {
                        item(key = "header_p") { SectionHeader(title = stringResource(R.string.attendance_section_present)) }
                        itemsIndexed(uiState.present, key = { _, item -> "p_${item.studentId}" }) { index, item ->
                            AttendanceRow(
                                item = item,
                                index = index + 1,
                                onSetAttendance = viewModel::setAttendance,
                                onClick = { onStudentClick(item.studentId) }
                            )
                        }
                    }

                    if (uiState.absent.isNotEmpty()) {
                        item(key = "header_a") { SectionHeader(title = stringResource(R.string.attendance_section_absent)) }
                        itemsIndexed(uiState.absent, key = { _, item -> "a_${item.studentId}" }) { index, item ->
                            AttendanceRow(
                                item = item,
                                index = index + 1,
                                onSetAttendance = viewModel::setAttendance,
                                onClick = { onStudentClick(item.studentId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DaySelector(
    dateText: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleFormat: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onPrevious) {
            Text(stringResource(R.string.previous_day))
        }
        Text(
            text = dateText,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // Disable ripple to avoid crash in Compose 1.7.0 mismatch
                onClick = onToggleFormat
            )
        )
        Button(onClick = onNext) {
            Text(stringResource(R.string.next_day))
        }
    }
}

@Composable
private fun AttendanceRow(
    item: AttendanceItemUi,
    index: Int,
    onSetAttendance: (Long, Boolean) -> Unit,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${index.toString().englishDigitsToPersian()}. ${item.fullName}",
                modifier = Modifier.weight(1f)
            )

            when (item.status) {
                AttendanceStatus.UNKNOWN -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { onSetAttendance(item.studentId, true) }) {
                            Text(stringResource(R.string.present))
                        }
                        OutlinedButton(onClick = { onSetAttendance(item.studentId, false) }) {
                            Text(stringResource(R.string.absent))
                        }
                    }
                }
                AttendanceStatus.PRESENT -> {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF008D0C))
                }
                AttendanceStatus.ABSENT -> {
                    Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }
}