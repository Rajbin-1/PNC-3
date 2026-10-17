package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.db.CatEntity

data class CatCoatColors(
    val primary: Color,
    val secondary: Color,
    val innerEar: Color,
    val eyes: Color,
    val nose: Color,
    val blush: Color
)

@Composable
fun LivingCatView(
    cat: CatEntity?,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 190.dp
) {
    val appearance = cat?.appearance ?: "TUXEDO"
    val accessory = cat?.activeAccessory ?: "BELL_COLLAR"
    val mood = cat?.mood ?: "CONTENT"

    val transition = rememberInfiniteTransition(label = "CatLivingAnim")

    val breatheScale by transition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CatBreathe"
    )

    val tailAngle by transition.animateFloat(
        initialValue = -14f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CatTail"
    )

    val blinkState by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CatBlink"
    )

    val coatColors = when (appearance) {
        "GINGER" -> CatCoatColors(
            primary = Color(0xFFE67E22),
            secondary = Color(0xFFFDEBD0),
            innerEar = Color(0xFFFFB6C1),
            eyes = Color(0xFF27AE60),
            nose = Color(0xFFE05368),
            blush = Color(0xFFFF8599)
        )
        "ONYX" -> CatCoatColors(
            primary = Color(0xFF1B1F23),
            secondary = Color(0xFF2F363D),
            innerEar = Color(0xFF586069),
            eyes = Color(0xFFF1C40F),
            nose = Color(0xFF24292E),
            blush = Color(0xFFE05368)
        )
        "SILVER" -> CatCoatColors(
            primary = Color(0xFF7F8C8D),
            secondary = Color(0xFFECF0F1),
            innerEar = Color(0xFFFFB6C1),
            eyes = Color(0xFF2980B9),
            nose = Color(0xFFE05368),
            blush = Color(0xFFFFB6C1)
        )
        else -> CatCoatColors( // TUXEDO
            primary = Color(0xFF21262D),
            secondary = Color(0xFFF6F8FA),
            innerEar = Color(0xFFFFB6C1),
            eyes = Color(0xFF2ECC71),
            nose = Color(0xFFE05368),
            blush = Color(0xFFFF8599)
        )
    }

    Box(
        modifier = modifier
            .size(size)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = androidx.compose.material3.ripple(bounded = false, radius = 90.dp),
                onClick = onTap
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.value * density
            val h = size.value * density
            val centerX = w / 2f
            val centerY = h / 2f + 8f

            // 1. Tail with gentle sway
            rotate(degrees = tailAngle, pivot = Offset(centerX + 55f, centerY + 45f)) {
                val tailPath = Path().apply {
                    moveTo(centerX + 45f, centerY + 40f)
                    cubicTo(
                        centerX + 90f, centerY + 25f,
                        centerX + 95f, centerY - 20f,
                        centerX + 80f, centerY - 35f
                    )
                }
                drawPath(
                    path = tailPath,
                    color = coatColors.primary,
                    style = Stroke(width = 14f, cap = StrokeCap.Round)
                )
                if (appearance == "TUXEDO") {
                    drawCircle(
                        color = coatColors.secondary,
                        radius = 7f,
                        center = Offset(centerX + 80f, centerY - 35f)
                    )
                }
            }

            // 2. Body (soft breathing scale)
            val bodyW = 100f * breatheScale
            val bodyH = 82f * breatheScale
            drawRoundRect(
                color = coatColors.primary,
                topLeft = Offset(centerX - bodyW / 2f, centerY),
                size = Size(bodyW, bodyH),
                cornerRadius = CornerRadius(40f, 40f)
            )

            // Cute belly chest patch
            val chestW = 50f * breatheScale
            val chestH = 62f * breatheScale
            drawRoundRect(
                color = coatColors.secondary,
                topLeft = Offset(centerX - chestW / 2f, centerY + 12f),
                size = Size(chestW, chestH),
                cornerRadius = CornerRadius(26f, 26f)
            )

            // Paws
            drawRoundRect(
                color = coatColors.secondary,
                topLeft = Offset(centerX - 38f, centerY + bodyH - 10f),
                size = Size(28f, 18f),
                cornerRadius = CornerRadius(10f, 10f)
            )
            drawRoundRect(
                color = coatColors.secondary,
                topLeft = Offset(centerX + 10f, centerY + bodyH - 10f),
                size = Size(28f, 18f),
                cornerRadius = CornerRadius(10f, 10f)
            )

            // 3. Head & Ears
            val headY = centerY - 32f
            val headR = 48f

            // Left Ear
            val leftEar = Path().apply {
                moveTo(centerX - 44f, headY - 18f)
                lineTo(centerX - 38f, headY - 62f)
                lineTo(centerX - 8f, headY - 40f)
                close()
            }
            drawPath(leftEar, coatColors.primary)
            val leftInner = Path().apply {
                moveTo(centerX - 40f, headY - 22f)
                lineTo(centerX - 35f, headY - 54f)
                lineTo(centerX - 14f, headY - 38f)
                close()
            }
            drawPath(leftInner, coatColors.innerEar)

            // Right Ear
            val rightEar = Path().apply {
                moveTo(centerX + 8f, headY - 40f)
                lineTo(centerX + 38f, headY - 62f)
                lineTo(centerX + 44f, headY - 18f)
                close()
            }
            drawPath(rightEar, coatColors.primary)
            val rightInner = Path().apply {
                moveTo(centerX + 14f, headY - 38f)
                lineTo(centerX + 35f, headY - 54f)
                lineTo(centerX + 40f, headY - 22f)
                close()
            }
            drawPath(rightInner, coatColors.innerEar)

            // Head circle
            drawCircle(coatColors.primary, headR, Offset(centerX, headY))

            // Muzzle patch
            drawRoundRect(
                color = coatColors.secondary,
                topLeft = Offset(centerX - 26f, headY + 4f),
                size = Size(52f, 32f),
                cornerRadius = CornerRadius(16f, 16f)
            )

            // Soft blush spots on cheeks
            drawCircle(coatColors.blush.copy(alpha = 0.5f), 7f, Offset(centerX - 24f, headY + 14f))
            drawCircle(coatColors.blush.copy(alpha = 0.5f), 7f, Offset(centerX + 24f, headY + 14f))

            // Eyes (happy, blinking or open)
            val isSleeping = mood == "SLEEPY"
            val isHappy = mood == "HAPPY" || mood == "PURRING"
            val isBlinking = !isSleeping && !isHappy && (blinkState < 0.15f)

            if (isSleeping || isHappy || isBlinking) {
                // Happy closed eyes (^ ^)
                val leftEye = Path().apply {
                    moveTo(centerX - 26f, headY - 4f)
                    cubicTo(centerX - 20f, headY - 12f, centerX - 14f, headY - 12f, centerX - 8f, headY - 4f)
                }
                drawPath(leftEye, coatColors.primary, style = Stroke(width = 3.5f, cap = StrokeCap.Round))

                val rightEye = Path().apply {
                    moveTo(centerX + 8f, headY - 4f)
                    cubicTo(centerX + 14f, headY - 12f, centerX + 20f, headY - 12f, centerX + 26f, headY - 4f)
                }
                drawPath(rightEye, coatColors.primary, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
            } else {
                // Big expressive eyes with sparkle glint
                drawCircle(coatColors.eyes, 8.5f, Offset(centerX - 18f, headY - 6f))
                drawCircle(Color.Black, 5f, Offset(centerX - 18f, headY - 6f))
                drawCircle(Color.White, 2.2f, Offset(centerX - 20f, headY - 8f))

                drawCircle(coatColors.eyes, 8.5f, Offset(centerX + 18f, headY - 6f))
                drawCircle(Color.Black, 5f, Offset(centerX + 18f, headY - 6f))
                drawCircle(Color.White, 2.2f, Offset(centerX + 16f, headY - 8f))
            }

            // Nose & cute smile
            val noseY = headY + 10f
            val nose = Path().apply {
                moveTo(centerX - 5f, noseY)
                lineTo(centerX + 5f, noseY)
                lineTo(centerX, noseY + 5f)
                close()
            }
            drawPath(nose, coatColors.nose)

            val smile = Path().apply {
                moveTo(centerX - 8f, noseY + 8f)
                quadraticBezierTo(centerX - 4f, noseY + 12f, centerX, noseY + 6f)
                quadraticBezierTo(centerX + 4f, noseY + 12f, centerX + 8f, noseY + 8f)
            }
            drawPath(smile, coatColors.primary, style = Stroke(width = 2.5f, cap = StrokeCap.Round))

            // Accessory
            drawCatAccessory(accessory, centerX, headY)
        }
    }
}

private fun DrawScope.drawCatAccessory(accessory: String, cx: Float, headY: Float) {
    val collarY = headY + 38f
    when (accessory) {
        "BELL_COLLAR" -> {
            drawRoundRect(Color(0xFFD4AC0D), Offset(cx - 32f, collarY - 3f), Size(64f, 8f), CornerRadius(4f, 4f))
            drawCircle(Color(0xFFF1C40F), 7f, Offset(cx, collarY + 6f))
            drawCircle(Color(0xFF7D6608), 2f, Offset(cx, collarY + 7f))
        }
        "RIBBON" -> {
            val c = Color(0xFFE05368)
            drawPath(Path().apply { moveTo(cx, collarY + 4f); lineTo(cx - 15f, collarY - 4f); lineTo(cx - 15f, collarY + 12f); close() }, c)
            drawPath(Path().apply { moveTo(cx, collarY + 4f); lineTo(cx + 15f, collarY - 4f); lineTo(cx + 15f, collarY + 12f); close() }, c)
            drawCircle(Color(0xFFFF8599), 4.5f, Offset(cx, collarY + 4f))
        }
        "COZY_SCARF" -> {
            val sc = Color(0xFF1ABC9C)
            drawRoundRect(sc, Offset(cx - 36f, collarY - 6f), Size(72f, 14f), CornerRadius(7f, 7f))
            drawRoundRect(sc, Offset(cx + 10f, collarY + 4f), Size(14f, 26f), CornerRadius(5f, 5f))
        }
        "STAR_CROWN" -> {
            val cy = headY - 42f
            val crown = Path().apply {
                moveTo(cx - 20f, cy)
                lineTo(cx - 16f, cy - 16f)
                lineTo(cx - 6f, cy - 6f)
                lineTo(cx, cy - 20f)
                lineTo(cx + 6f, cy - 6f)
                lineTo(cx + 16f, cy - 16f)
                lineTo(cx + 20f, cy)
                close()
            }
            drawPath(crown, color = Color(0xFFF39C12), style = Fill)
        }
        "BERET" -> {
            drawOval(Color(0xFF8E44AD), Offset(cx - 32f, headY - 48f), Size(50f, 20f))
        }
    }
}
