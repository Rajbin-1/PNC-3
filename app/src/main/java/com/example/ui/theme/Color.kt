package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// PNC<3 Core Minimalist Palette
val PncDarkBackground = Color(0xFF0D1117)
val PncDarkSurface = Color(0xFF161B22)
val PncDarkCard = Color(0xFF1C2128)
val PncDarkBorder = Color(0xFF30363D)

val PncLightBackground = Color(0xFFF8F9FA)
val PncLightSurface = Color(0xFFFFFFFF)
val PncLightCard = Color(0xFFF0F2F5)
val PncLightBorder = Color(0xFFE1E4E8)

val PncRoseAccent = Color(0xFFE05368)
val PncEmeraldStatus = Color(0xFF2EA043)
val PncAmberStatus = Color(0xFFD29922)

val PncDefaultOutgoingBubble = Color(0xFF1F3A60)
val PncDefaultIncomingBubble = Color(0xFF21262D)

val PncTextPrimaryDark = Color(0xFFF0F6FC)
val PncTextSecondaryDark = Color(0xFF8B949E)
val PncTextPrimaryLight = Color(0xFF111827)
val PncTextSecondaryLight = Color(0xFF6B7280)

@Immutable
data class PncCustomTheme(
    val outgoingBubbleColor: Color = PncDefaultOutgoingBubble,
    val incomingBubbleColor: Color = PncDefaultIncomingBubble,
    val accentColor: Color = PncRoseAccent,
    val bubbleRadius: Dp = 18.dp,
    val bubbleStylePreset: Int = 0,
    val chatBackgroundUri: String? = null,
    val chatBackgroundDim: Float = 0.35f,
    val isDark: Boolean = true
)

val LocalPncTheme = staticCompositionLocalOf { PncCustomTheme() }

fun parseHexColor(hex: String, fallback: Color = Color.Unspecified): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = clean.toLong(16)
        if (clean.length == 6) {
            Color(colorInt or 0x00000000FF000000)
        } else {
            Color(colorInt)
        }
    } catch (_: Exception) {
        fallback
    }
}
