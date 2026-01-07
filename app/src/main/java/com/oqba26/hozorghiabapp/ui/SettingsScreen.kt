package com.oqba26.hozorghiabapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.oqba26.hozorghiabapp.R
import com.oqba26.hozorghiabapp.viewmodel.SettingsViewModel
import com.oqba26.hozorghiabapp.AppSettings
import com.oqba26.hozorghiabapp.util.toPersianPrice

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val settingsState by viewModel.settingsFlow.collectAsState(
        initial = AppSettings()
    )

    // متن ورودی مستمری، هر وقت مبلغ تنظیمات عوض شد، این هم آپدیت شود
    LaunchedEffect(settingsState.monthlyFee) {
        viewModel.onMonthlyFeeTextChange(settingsState.monthlyFee.toPersianPrice())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = stringResource(R.string.tab_settings),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(24.dp))

        // بخش مستمری ماهانه
        Text(
            text = stringResource(R.string.monthly_fee_label),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.monthlyFeeText,
            onValueChange = { viewModel.onMonthlyFeeTextChange(it) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(stringResource(R.string.monthly_fee_placeholder)) }
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { viewModel.saveMonthlyFee() }
        ) {
            Text(stringResource(R.string.save_button))
        }

        Spacer(Modifier.height(32.dp))

        // بخش انتخاب فونت
        Text(
            text = stringResource(R.string.font_label),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            FontChip(
                label = stringResource(R.string.font_vazirmatn),
                selected = settingsState.fontKey == "vazirmatn",
                onClick = { viewModel.updateFont("vazirmatn") }
            )

            Spacer(Modifier.height(0.dp)) // فقط برای فاصله افقی بعدی
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            FontChip(
                label = stringResource(R.string.font_byekan),
                selected = settingsState.fontKey == "byekan",
                onClick = { viewModel.updateFont("byekan") }
            )

            Spacer(Modifier.height(0.dp))

            FontChip(
                label = stringResource(R.string.font_iraniansans),
                selected = settingsState.fontKey == "iraniansans",
                onClick = { viewModel.updateFont("iraniansans") }
            )
        }
    }
}

@Composable
fun FontChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) }
    )
}