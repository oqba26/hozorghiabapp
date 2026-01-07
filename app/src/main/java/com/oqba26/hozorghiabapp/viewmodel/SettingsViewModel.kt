package com.oqba26.hozorghiabapp.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.oqba26.hozorghiabapp.data.settingsFlow
import com.oqba26.hozorghiabapp.data.setMonthlyFee
import com.oqba26.hozorghiabapp.data.setFontKey
import com.oqba26.hozorghiabapp.AppSettings
import com.oqba26.hozorghiabapp.util.persianDigitsToEnglish
import com.oqba26.hozorghiabapp.util.toPersianPrice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = application.applicationContext

    // جریان تنظیمات (برای فونت و مبلغ مستمری)
    val settingsFlow: Flow<AppSettings> = appContext.settingsFlow()

    // متن ورودی مستمری (برای TextField)
    var monthlyFeeText by mutableStateOf("")
        private set

    fun onMonthlyFeeTextChange(newText: String) {
        monthlyFeeText = newText
    }

    /** ذخیره مبلغ مستمری براساس متنی که کاربر وارد کرده */
    fun saveMonthlyFee() {
        val english = monthlyFeeText.persianDigitsToEnglish()
        val onlyDigits = english.filter { it.isDigit() }
        val value = onlyDigits.toIntOrNull() ?: return

        viewModelScope.launch {
            appContext.setMonthlyFee(value)
            // بعد از ذخیره، متن رو با فرمت قشنگ فارسی کن
            monthlyFeeText = value.toPersianPrice()
        }
    }

    /** تنظیم فونت */
    fun updateFont(key: String) {
        viewModelScope.launch {
            appContext.setFontKey(key)
        }
    }
}