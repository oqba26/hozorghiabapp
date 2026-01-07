package com.oqba26.hozorghiabapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.oqba26.hozorghiabapp.R

// فونت وزیرمتن
val VazirmatnFontFamily = FontFamily(
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_medium, FontWeight.Medium),
    Font(R.font.vazirmatn_bold, FontWeight.Bold)
)

// فونت بی‌یکان
val ByekanFontFamily = FontFamily(
    Font(R.font.byekan, FontWeight.Normal),
    Font(R.font.byekan_bold, FontWeight.Bold)
)

// فونت ایران‌سنس
val IranSansFontFamily = FontFamily(
    Font(R.font.iraniansans, FontWeight.Normal)
)

/** براساس کلید فونت، تایپوگرافی می‌سازد */
fun createTypography(fontKey: String): Typography {
    val family = when (fontKey) {
        "byekan" -> ByekanFontFamily
        "iraniansans" -> IranSansFontFamily
        else -> VazirmatnFontFamily
    }
    val defaultTypography = Typography()
    return Typography(
        displayLarge = defaultTypography.displayLarge.copy(fontFamily = family),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = family),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = family),
        headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = family),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = family),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = family),
        titleLarge = defaultTypography.titleLarge.copy(fontFamily = family),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = family),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = family),
        bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = family),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = family),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = family),
        labelLarge = defaultTypography.labelLarge.copy(fontFamily = family),
        labelMedium = defaultTypography.labelMedium.copy(fontFamily = family),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = family)
    )
}