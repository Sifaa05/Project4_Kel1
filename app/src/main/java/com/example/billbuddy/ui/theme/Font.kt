package com.example.billbuddy.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.billbuddy.R

object Font{
    val JomhuriaFontFamily = FontFamily(
        Font(R.font.jomhuria_regular, FontWeight.Normal)
    )

    val KadwaFontFamily = FontFamily(
        Font(R.font.kadwa_regular, FontWeight.Normal)
    )

    val KhulaExtraBoldFontFamily = FontFamily(
        Font(R.font.khula_extrabold, FontWeight.ExtraBold)
    )

    val KhulaRegularFontFamily = FontFamily(
        Font(R.font.khula_regular, FontWeight.Normal)
    )

    val RobotoBoldFontFamily = FontFamily(
        Font(R.font.roboto_bold, FontWeight.Bold)
    )

    val RobotoMediumFontFamily = FontFamily(
        Font(R.font.roboto_medium, FontWeight.Medium)
    )

    val RobotoRegularFontFamily = FontFamily(
        Font(R.font.roboto_regular, FontWeight.Normal)
    )

    fun getFont(fontType: FontType): FontFamily = when (fontType) {
        FontType.JOMHURIA_REGULAR -> JomhuriaFontFamily
        FontType.KADWA_REGULAR -> KadwaFontFamily
        FontType.KHULA_EXTRABOLD -> KhulaExtraBoldFontFamily
        FontType.KHULA_REGULAR -> KhulaRegularFontFamily
        FontType.ROBOTO_BOLD -> RobotoBoldFontFamily
        FontType.ROBOTO_MEDIUM -> RobotoMediumFontFamily
        FontType.ROBOTO_REGULAR -> RobotoRegularFontFamily
    }
}

enum class FontType {
    JOMHURIA_REGULAR,
    KADWA_REGULAR,
    KHULA_EXTRABOLD,
    KHULA_REGULAR,
    ROBOTO_BOLD,
    ROBOTO_MEDIUM,
    ROBOTO_REGULAR
}

