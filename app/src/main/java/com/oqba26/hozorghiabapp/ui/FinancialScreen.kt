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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.oqba26.hozorghiabapp.FinancialItemUi
import com.oqba26.hozorghiabapp.R
import com.oqba26.hozorghiabapp.util.englishDigitsToPersian
import com.oqba26.hozorghiabapp.viewmodel.FinancialViewModel
import saman.zamani.persiandate.PersianDate

@Composable
fun FinancialScreen(
    viewModel: FinancialViewModel,
    onNavigateToStudentDetails: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val persianDate = remember(uiState.year, uiState.month) {
        PersianDate().apply {
            shYear = uiState.year
            shMonth = uiState.month
            shDay = 1
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            MonthSelector(
                persianDate = persianDate,
                isFullDate = uiState.isFullDate,
                onPrevious = { viewModel.previousMonth() },
                onNext = { viewModel.nextMonth() },
                onToggleDateFormat = { viewModel.toggleDateFormat() }
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(uiState.items, key = { _, item -> item.studentId }) { index, item ->
                    FinancialRow(
                        item = item,
                        index = index + 1,
                        onToggle = { viewModel.togglePayment(item.studentId, !item.paid) },
                        onClick = { onNavigateToStudentDetails(item.studentId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthSelector(
    persianDate: PersianDate,
    isFullDate: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleDateFormat: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onPrevious) {
            Text(stringResource(R.string.previous_month))
        }
        Text(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // Disable ripple to avoid crash
                onClick = onToggleDateFormat
            ),
            text = if (isFullDate) {
                persianDate.toString().englishDigitsToPersian()
            } else {
                "${persianDate.monthName} ${persianDate.shYear}".englishDigitsToPersian()
            },
            style = MaterialTheme.typography.titleMedium
        )
        Button(onClick = onNext) {
            Text(stringResource(R.string.next_month))
        }
    }
}

@Composable
private fun FinancialRow(
    item: FinancialItemUi,
    index: Int,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick, // Use Surface onClick which handles its own ripple
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${index.toString().englishDigitsToPersian()}. ${item.fullName}",
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onToggle) {
                if (item.paid) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF008D0C)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
