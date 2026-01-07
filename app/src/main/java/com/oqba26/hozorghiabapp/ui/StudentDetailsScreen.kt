package com.oqba26.hozorghiabapp.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.oqba26.hozorghiabapp.R
import com.oqba26.hozorghiabapp.viewmodel.MonthFinancialStatus
import com.oqba26.hozorghiabapp.viewmodel.StudentAttendanceDetailsViewModel
import com.oqba26.hozorghiabapp.viewmodel.StudentFinancialDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailsScreen(
    attendanceViewModel: StudentAttendanceDetailsViewModel,
    financialViewModel: StudentFinancialDetailsViewModel,
    onBack: () -> Unit
) {
    val attendanceUiState by attendanceViewModel.uiState.collectAsState()
    val financialUiState by financialViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.tab_attendance),
        stringResource(R.string.tab_financial)
    )

    // Financial Dialog State
    var editingMonth by remember { mutableStateOf<MonthFinancialStatus?>(null) }

    LaunchedEffect(Unit) {
        financialViewModel.messageFlow.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = attendanceUiState.studentName, // Assuming same name in both
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTabIndex) {
                    0 -> {
                        StudentAttendanceDetailsContent(
                            uiState = attendanceUiState,
                            onPreviousYear = { attendanceViewModel.previousYear() },
                            onNextYear = { attendanceViewModel.nextYear() }
                        )
                    }
                    1 -> {
                        StudentFinancialDetailsContent(
                            uiState = financialUiState,
                            onPreviousYear = { financialViewModel.previousYear() },
                            onNextYear = { financialViewModel.nextYear() },
                            onPayClick = { editingMonth = it },
                            onEditClick = { editingMonth = it }
                        )
                    }
                }
            }
        }
    }

    editingMonth?.let { month ->
        PaymentDialog(
            studentName = financialUiState.studentName,
            month = month,
            defaultAmount = financialUiState.monthlyFee,
            onDismiss = {
                editingMonth = null
            },
            onConfirm = { amount, desc ->
                financialViewModel.savePayment(month.month, amount, desc)
                editingMonth = null
            },
            onDelete = {
                financialViewModel.deletePayment(month.month)
                editingMonth = null
            }
        )
    }
}
