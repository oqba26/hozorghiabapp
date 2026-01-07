package com.oqba26.hozorghiabapp.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val persianSymbols = DecimalFormatSymbols(Locale.Builder().setLanguage("fa").setRegion("IR").build()).apply {
    groupingSeparator = '٬'
    decimalSeparator = '٫'
}

private val persianDecimalFormat = DecimalFormat("#,###", persianSymbols)

private val persianToEnglishMap = mapOf(
    '۰' to '0', '۱' to '1', '۲' to '2', '۳' to '3', '۴' to '4',
    '۵' to '5', '۶' to '6', '۷' to '7', '۸' to '8', '۹' to '9'
)

private val englishToPersianMap = mapOf(
    '0' to '۰', '1' to '۱', '2' to '۲', '3' to '۳', '4' to '۴',
    '5' to '۵', '6' to '۶', '7' to '۷', '8' to '۸', '9' to '۹'
)


// --- توابع کمکی برای اعداد و رشته‌ها ---

/** عدد با جداکننده هزارگان و رقم فارسی (برای پول) */
fun Int.toPersianPrice(): String = persianDecimalFormat.format(this)

/** تبدیل اعداد فارسی داخل رشته به انگلیسی (برای ذخیره) */
fun String.persianDigitsToEnglish(): String {
    return this.replace("٬", "") // جداکننده هزارگان
        .replace("٫", ".") // ممیز
        .map { persianToEnglishMap[it] ?: it } // تبدیل ارقام
        .joinToString("")
}

/** تبدیل اعداد انگلیسی داخل رشته به فارسی (برای نمایش) */
fun String.englishDigitsToPersian(): String {
    return this.map { englishToPersianMap[it] ?: it }.joinToString("")
}

/** عدد معمولی با ارقام فارسی (بدون جداکننده) */
@Suppress("unused")
fun Int.toPersianDigits(): String = this.toString().englishDigitsToPersian()
