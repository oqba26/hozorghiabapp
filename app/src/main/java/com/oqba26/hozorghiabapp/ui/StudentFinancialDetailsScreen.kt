package com.oqba26.hozorghiabapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.oqba26.hozorghiabapp.R
import com.oqba26.hozorghiabapp.util.englishDigitsToPersian
import com.oqba26.hozorghiabapp.util.persianDigitsToEnglish
import com.oqba26.hozorghiabapp.util.toPersianDateFullString
import com.oqba26.hozorghiabapp.util.toPersianDateTimeString
import com.oqba26.hozorghiabapp.util.toPersianPrice
import com.oqba26.hozorghiabapp.util.toPersianDateNumericString
import com.oqba26.hozorghiabapp.viewmodel.MonthFinancialStatus
import com.oqba26.hozorghiabapp.viewmodel.StudentFinancialDetailsViewModel
import saman.zamani.persiandate.PersianDate
import java.util.Date
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun StudentFinancialDetailsScreen(viewModel: StudentFinancialDetailsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showPaymentDialog by remember { mutableStateOf(false) }
    var editingMonth by remember { mutableStateOf<MonthFinancialStatus?>(null) }

    LaunchedEffect(Unit) {
        viewModel.messageFlow.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    if (showPaymentDialog && editingMonth != null) {
        PaymentDialog(
            studentName = uiState.studentName,
            month = editingMonth!!,
            defaultAmount = uiState.monthlyFee,
            onDismiss = {
                showPaymentDialog = false
                editingMonth = null
            },
            onConfirm = { amount, desc ->
                viewModel.savePayment(editingMonth!!.month, amount, desc)
                showPaymentDialog = false
                editingMonth = null
            },
            onDelete = {
                viewModel.deletePayment(editingMonth!!.month)
                showPaymentDialog = false
                editingMonth = null
            }
        )
    }

    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = uiState.studentName,
                        style = MaterialTheme.typography.titleLarge
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
            Spacer(modifier = Modifier.height(16.dp))

            YearSelector(
                year = uiState.year,
                onPrevious = { viewModel.previousYear() },
                onNext = { viewModel.nextYear() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.months) { month ->
                    MonthFinancialRow(
                        month = month,
                        currentYear = uiState.year,
                        monthlyFee = uiState.monthlyFee,
                        onPayClick = {
                            editingMonth = month
                            showPaymentDialog = true
                        },
                        onEditClick = {
                            editingMonth = month
                            showPaymentDialog = true
                        }
                    )
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
private fun MonthFinancialRow(
    month: MonthFinancialStatus,
    currentYear: Int,
    monthlyFee: Int,
    onPayClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (month.isEnabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            if (month.isPaid) {
                // ماه پرداخت شده: اطلاعات در سمت راست (اول در کد)، دکمه ویرایش در سمت چپ (آخر در کد)
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "${month.monthName} ${month.amount.toPersianPrice()} تومان",
                        color = Color(0xFF008D0C),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    val paymentDate = PersianDate(Date(month.paymentTimestamp!!))
                    val hourStr = String.format("%02d", paymentDate.hour).englishDigitsToPersian()
                    val minuteStr = String.format("%02d", paymentDate.minute).englishDigitsToPersian()
                    val timeStr = "$hourStr:$minuteStr"
                    
                    val dayStr = String.format("%02d", paymentDate.shDay).englishDigitsToPersian()
                    val monthStr = String.format("%02d", paymentDate.shMonth).englishDigitsToPersian()
                    val yearStr = paymentDate.shYear.toString().englishDigitsToPersian()
                    val dateStr = "$dayStr / $monthStr / $yearStr"
                    
                    Text(
                        text = "پرداخت در ساعت : $timeStr و تاریخ : $dateStr ثبت شد.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                androidx.compose.material3.Button(
                    onClick = onEditClick,
                    modifier = Modifier
                        .height(32.dp)
                        .defaultMinSize(minWidth = 100.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2),
                        contentColor = Color.White
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "ویرایش",
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1
                    )
                }
            } else if (month.isEnabled) {
                // ماه پرداخت نشده: اسم ماه سمت راست (اول در کد)، دکمه پرداخت سمت چپ (آخر در کد)
                Text(
                    text = month.monthName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.weight(1f))
                androidx.compose.material3.Button(
                    onClick = onPayClick,
                    modifier = Modifier.height(36.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text(text = "پرداخت", style = MaterialTheme.typography.labelLarge)
                }
            } else {
                // غیرفعال: اسم ماه سمت راست
                Text(
                    text = month.monthName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun PaymentDialog(
    studentName: String,
    month: MonthFinancialStatus,
    defaultAmount: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, String?) -> Unit,
    onDelete: () -> Unit
) {
    var amountText by remember { mutableStateOf(if (month.isPaid) month.amount.toString() else defaultAmount.toString()) }
    var description by remember { mutableStateOf(month.description ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F4F9))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (month.isPaid) "ویرایش پرداخت برای $studentName" else "ثبت پرداخت برای $studentName",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = amountText.englishDigitsToPersian(),
                    onValueChange = { 
                        val englishValue = it.persianDigitsToEnglish()
                        if (englishValue.isEmpty() || englishValue.all { it.isDigit() }) {
                            amountText = englishValue
                        }
                    },
                    label = { Text("مبلغ (به تومان)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = MaterialTheme.shapes.large
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("توضیحات (اختیاری)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (month.isPaid) {
                        // Edit Mode: Right to Left -> ویرایش (Blue), حذف (Red), لغو (Red)
                        androidx.compose.material3.Button(
                            onClick = { onConfirm(amountText.toIntOrNull() ?: 0, description.ifBlank { null }) },
                            modifier = Modifier.weight(1.1f).height(54.dp),
                            shape = androidx.compose.foundation.shape.CircleShape,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                        ) {
                            Text("ویرایش", style = MaterialTheme.typography.labelLarge, maxLines = 1)
                        }

                        androidx.compose.material3.Button(
                            onClick = onDelete,
                            modifier = Modifier.weight(1f).height(54.dp),
                            shape = androidx.compose.foundation.shape.CircleShape,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                        ) {
                            Text("حذف", style = MaterialTheme.typography.labelLarge, maxLines = 1)
                        }
                        
                        androidx.compose.material3.Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(54.dp),
                            shape = androidx.compose.foundation.shape.CircleShape,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                        ) {
                            Text("لغو", style = MaterialTheme.typography.labelLarge, maxLines = 1)
                        }
                    } else {
                        // Register Mode: Right to Left -> ثبت (Blue), لغو (Red)
                        androidx.compose.material3.Button(
                            onClick = { onConfirm(amountText.toIntOrNull() ?: 0, description.ifBlank { null }) },
                            modifier = Modifier.weight(1f).height(54.dp),
                            shape = androidx.compose.foundation.shape.CircleShape,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8))
                        ) {
                            Text("ثبت", style = MaterialTheme.typography.labelLarge)
                        }

                        androidx.compose.material3.Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(54.dp),
                            shape = androidx.compose.foundation.shape.CircleShape,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                        ) {
                            Text("لغو", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}
