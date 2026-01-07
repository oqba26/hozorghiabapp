package com.oqba26.hozorghiabapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oqba26.hozorghiabapp.DayAttendanceStatus
import com.oqba26.hozorghiabapp.R
import com.oqba26.hozorghiabapp.util.englishDigitsToPersian
import com.oqba26.hozorghiabapp.util.toPersianDateFullString
import com.oqba26.hozorghiabapp.viewmodel.StudentAttendanceDetailsViewModel
import saman.zamani.persiandate.PersianDate
import java.time.LocalDate
import java.util.Date
import java.sql.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StudentAttendanceDetailsScreen(viewModel: StudentAttendanceDetailsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "جزئیات حضور و غیاب ${uiState.studentName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            YearSelector(
                year = uiState.year,
                onPrevious = { viewModel.previousYear() },
                onNext = { viewModel.nextYear() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.months.all { it.days.isEmpty() }) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "رکوردی برای سال ${uiState.year.toString().englishDigitsToPersian()} یافت نشد",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.months.forEach { month ->
                        if (month.days.isNotEmpty()) {
                            stickyHeader {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 2.dp
                                ) {
                                    Text(
                                        text = month.monthName,
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            items(month.days) { day ->
                                AttendanceDayRow(day)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YearSelector(
    year: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onPrevious) {
            Text(stringResource(R.string.previous_year))
        }
        Text(
            text = "سال ${year.toString().englishDigitsToPersian()}",
            style = MaterialTheme.typography.titleMedium
        )
        Button(onClick = onNext) {
            Text(stringResource(R.string.next_year))
        }
    }
}

@Composable
private fun AttendanceDayRow(day: DayAttendanceStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (day.isPresent) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val localDate = LocalDate.parse(day.date)
                val pDate = PersianDate(java.sql.Date.valueOf(localDate))
                
                Text(
                    text = pDate.toPersianDateFullString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                if (day.timestamp != null) {
                    val recordDate = PersianDate(Date(day.timestamp))
                    val hour = String.format("%02d", recordDate.hour).englishDigitsToPersian()
                    val minute = String.format("%02d", recordDate.minute).englishDigitsToPersian()
                    Text(
                        text = "ثبت شده در ساعت $hour:$minute",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (day.isPresent) Color(0xFF2E7D32) else Color(0xFFC62828)
            ) {
                Text(
                    text = if (day.isPresent) "حاضر" else "غایب",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
