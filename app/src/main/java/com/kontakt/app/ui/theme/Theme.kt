package com.kontakt.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape

/* ---------- Light & Dark schemes ---------- */
private val LightColors = lightColorScheme(
    primary              = CambridgeBlue,
    onPrimary            = White,
    primaryContainer     = CambridgeBlueLight,
    onPrimaryContainer   = Black,

    secondary            = Copper,
    onSecondary          = White,
    secondaryContainer   = CopperLight,
    onSecondaryContainer = Black,

    tertiary             = PayneGray,
    onTertiary           = White,
    tertiaryContainer    = PayneGrayLight,
    onTertiaryContainer  = Black,

    background           = White,
    surface              = White,
    onBackground         = Black,
    onSurface            = Black
)

private val DarkColors = darkColorScheme(
    primary              = CambridgeBlue,
    onPrimary            = Black,
    primaryContainer     = CambridgeBlueDark,
    onPrimaryContainer   = White,

    secondary            = Copper,
    onSecondary          = Black,
    secondaryContainer   = CopperDark,
    onSecondaryContainer = White,

    tertiary             = PayneGray,
    onTertiary           = White,
    tertiaryContainer    = PayneGrayDark,
    onTertiaryContainer  = White,

    background           = Black,
    surface              = Color(0xFF121212),
    onBackground         = White,
    onSurface            = White
)

/* ---------- Tema global ---------- */
@Composable
fun KontaKtTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColors
        else      -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = KontaktTypography,
        shapes      = Shapes(
            small  = RoundedCornerShape(6.dp),
            medium = RoundedCornerShape(12.dp),
            large  = RoundedCornerShape(24.dp)
        ),
        content     = content
    )
}