package com.oqba26.hozorghiabapp.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oqba26.hozorghiabapp.R
import com.oqba26.hozorghiabapp.util.englishDigitsToPersian
import com.oqba26.hozorghiabapp.util.toPersianPrice
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
    
    // Calculate Stats
    val totalPresent by remember(attendanceUiState) {
        derivedStateOf {
            attendanceUiState.months.sumOf { m -> m.days.count { it.isPresent } }
        }
    }
    val totalAbsent by remember(attendanceUiState) {
        derivedStateOf {
            attendanceUiState.months.sumOf { m -> m.days.count { !it.isPresent } }
        }
    }
    val totalPaidAmount by remember(financialUiState) {
        derivedStateOf {
            financialUiState.months.filter { it.isPaid }.sumOf { it.amount }
        }
    }

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
                title = { Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleSmall) }, // Small title or empty
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
            
            // --- Profile Header Section ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = attendanceUiState.studentName.take(1),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = attendanceUiState.studentName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "حاضر / غایب",
                        value = "${totalPresent.toString().englishDigitsToPersian()} / ${totalAbsent.toString().englishDigitsToPersian()}",
                        icon = Icons.Default.Person,
                        color = Color(0xFF2E7D32)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "پرداختی سال",
                        value = "${totalPaidAmount.toPersianPrice()} ت",
                        icon = Icons.Default.List, // Ideally a money icon
                        color = Color(0xFF1976D2)
                    )
                }
            }

            // --- Custom Tabs ---
            CustomTabRow(
                selectedIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )

            // --- Content ---
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

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CustomTabRow(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        TabRow(
            selectedTabIndex = selectedIndex,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    height = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            Tab(
                selected = selectedIndex == 0,
                onClick = { onTabSelected(0) },
                text = { Text(stringResource(R.string.tab_attendance)) }
            )
            Tab(
                selected = selectedIndex == 1,
                onClick = { onTabSelected(1) },
                text = { Text(stringResource(R.string.tab_financial)) }
            )
        }
    }
}
