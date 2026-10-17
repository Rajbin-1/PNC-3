package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.db.ThemeConfigEntity

@Composable
fun PncTheme(
    config: ThemeConfigEntity? = null,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (config?.themeMode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> systemDark
    }

    val customBg = if (isDark) {
        config?.darkBackgroundHex?.let { parseHexColor(it, PncDarkBackground) } ?: PncDarkBackground
    } else {
        config?.lightBackgroundHex?.let { parseHexColor(it, PncLightBackground) } ?: PncLightBackground
    }

    val customSurface = if (isDark) {
        config?.darkSurfaceHex?.let { parseHexColor(it, PncDarkSurface) } ?: PncDarkSurface
    } else {
        config?.lightSurfaceHex?.let { parseHexColor(it, PncLightSurface) } ?: PncLightSurface
    }

    val customAccent = config?.accentHex?.let { parseHexColor(it, PncRoseAccent) } ?: PncRoseAccent
    val customOutgoing = config?.outgoingBubbleHex?.let { parseHexColor(it, PncDefaultOutgoingBubble) } ?: PncDefaultOutgoingBubble
    val customIncoming = config?.incomingBubbleHex?.let { parseHexColor(it, PncDefaultIncomingBubble) } ?: PncDefaultIncomingBubble
    val radius = (config?.bubbleRadiusDp ?: 18).dp
    val stylePreset = config?.bubbleStylePreset ?: 0
    val bgUri = config?.chatBackgroundUri
    val bgDim = config?.chatBackgroundDim ?: 0.35f

    val pncCustomTheme = PncCustomTheme(
        outgoingBubbleColor = customOutgoing,
        incomingBubbleColor = customIncoming,
        accentColor = customAccent,
        bubbleRadius = radius,
        bubbleStylePreset = stylePreset,
        chatBackgroundUri = bgUri,
        chatBackgroundDim = bgDim,
        isDark = isDark
    )

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = customAccent,
            onPrimary = Color.White,
            background = customBg,
            onBackground = PncTextPrimaryDark,
            surface = customSurface,
            onSurface = PncTextPrimaryDark,
            surfaceVariant = PncDarkCard,
            onSurfaceVariant = PncTextSecondaryDark,
            outline = PncDarkBorder
        )
    } else {
        lightColorScheme(
            primary = customAccent,
            onPrimary = Color.White,
            background = customBg,
            onBackground = PncTextPrimaryLight,
            surface = customSurface,
            onSurface = PncTextPrimaryLight,
            surfaceVariant = PncLightCard,
            onSurfaceVariant = PncTextSecondaryLight,
            outline = PncLightBorder
        )
    }

    CompositionLocalProvider(LocalPncTheme provides pncCustomTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
