package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun YarnBallView(
    stage: Int, // 1 to 7
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    val transition = rememberInfiniteTransition(label = "YarnBallPulse")
    val scale by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "YarnScale"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.toPx()
            val h = size.toPx()
            val cx = w / 2f
            val cy = h / 2f
            val radius = (w / 2.2f) * scale.coerceIn(0.9f, 1.1f)

            // Base yarn ball circle
            val baseColor = when (stage) {
                1 -> Color(0xFFE05368) // Soft Rose
                2 -> Color(0xFFD98880) // Coral Rose
                3 -> Color(0xFFC0392B) // Rich Crimson
                4 -> Color(0xFF8E44AD) // Deep Amethyst
                5 -> Color(0xFF2980B9) // Sapphire Blue
                6 -> Color(0xFFF39C12) // Golden Amber
                else -> Color(0xFFF1C40F) // Majestic Gold
            }

            // Draw filled ball
            drawCircle(color = baseColor, radius = radius, center = Offset(cx, cy))

            // Draw yarn winding textures (arcs & intersecting lines)
            val strokeCol = Color.White.copy(alpha = 0.35f)
            val strokeWidth = (radius / 8f).coerceAtLeast(3f)

            // Winding strands
            drawArc(
                color = strokeCol,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cx - radius * 0.7f, cy - radius * 0.7f),
                size = androidx.compose.ui.geometry.Size(radius * 1.4f, radius * 1.4f),
                style = Stroke(width = strokeWidth)
            )

            drawArc(
                color = strokeCol,
                startAngle = 90f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cx - radius * 0.6f, cy - radius * 0.6f),
                size = androidx.compose.ui.geometry.Size(radius * 1.2f, radius * 1.2f),
                style = Stroke(width = strokeWidth * 0.8f)
            )

            if (stage >= 3) {
                // Diagonal cross strand
                drawLine(
                    color = strokeCol,
                    start = Offset(cx - radius * 0.5f, cy - radius * 0.5f),
                    end = Offset(cx + radius * 0.5f, cy + radius * 0.5f),
                    strokeWidth = strokeWidth * 0.9f
                )
            }

            if (stage >= 5) {
                // Sparkling core accent
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = radius * 0.18f,
                    center = Offset(cx, cy)
                )
            }

            if (stage >= 7) {
                // Legendary crown/star on top
                val crownPath = Path().apply {
                    moveTo(cx, cy - radius - 10f)
                    lineTo(cx - 8f, cy - radius - 22f)
                    lineTo(cx, cy - radius - 16f)
                    lineTo(cx + 8f, cy - radius - 22f)
                    close()
                }
                drawPath(crownPath, color = Color(0xFFF1C40F))
            }
        }
    }
}
