package com.oqba26.hozorghiabapp.util

import saman.zamani.persiandate.PersianDate
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date


// --- توابع کمکی برای کلاس PersianDate ---

/** فرمت کامل تاریخ: دوشنبه ۳ آذر ۱۴۰۴ */
fun PersianDate.toPersianDateFullString(): String {
    val dayName = this.dayName()
    val dayOfMonth = this.shDay
    val monthName = this.monthName
    val year = this.shYear
    return "$dayName $dayOfMonth $monthName $year".englishDigitsToPersian()
}

/** فرمت کامل تاریخ و زمان: ساعت ۱۰:۳۰ و تاریخ دوشنبه ۳ آذر ۱۴۰۴ */
fun PersianDate.toPersianDateTimeString(): String {
    val time = "${this.hour}:${String.format("%02d", this.minute)}"
    val date = this.toPersianDateFullString()
    return "ساعت $time و تاریخ $date".englishDigitsToPersian()
}

/** فرمت کوتاه تاریخ: ۱۴۰۴/۹/۳ */
fun PersianDate.toPersianDateShortString(): String {
    val day = this.shDay
    val month = this.shMonth
    val year = this.shYear
    return "$year/$month/$day".englishDigitsToPersian()
}

/** فرمت عددی تاریخ: ۱۴۰۴/۹/۳ */
fun PersianDate.toPersianDateNumericString(): String {
    val day = String.format("%02d", this.shDay)
    val month = String.format("%02d", this.shMonth)
    val year = this.shYear
    return "$day/$month/$year".englishDigitsToPersian()
}


// --- توابع کمکی برای کلاس LocalDate ---

/** فرمت کامل تاریخ: دوشنبه ۳ آذر ۱۴۰۴ */
fun LocalDate.toPersianDateFullString(): String {
    val pd = this.toPersianDate()
    return "${pd.dayName()} ${pd.shDay} ${pd.monthName()} ${pd.shYear}".englishDigitsToPersian()
}

/** فرمت کوتاه تاریخ: ۱۴۰۴/۹/۳ */
fun LocalDate.toPersianDateShortString(): String {
    val pd = this.toPersianDate()
    return "${pd.shYear}/${pd.shMonth}/${pd.shDay}".englishDigitsToPersian()
}

private fun LocalDate.toPersianDate(): PersianDate {
    val date = Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
    return PersianDate(date)
}


// --- تبدیل نوع‌ها ---

object DateConverter {
    fun toPersian(localDate: LocalDate): PersianDate {
        val date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        return PersianDate(date)
    }
}
