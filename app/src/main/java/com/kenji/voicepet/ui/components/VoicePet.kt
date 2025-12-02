package com.kenji.voicepet.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun VoicePet(
    voiceLevel: Float, // 0.0 a 1.0 (Vem do microfone)
    modifier: Modifier = Modifier
) {
    // ESTADO: Piscar os olhos
    var isBlinking by remember { mutableStateOf(false) }

    // Animação dos olhos (Loop infinito que sorteia quando piscar)
    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(2000, 5000)) // Espera 2 a 5 segundos
            isBlinking = true
            delay(150) // Fecha o olho
            isBlinking = false
        }
    }

    // Animação Suave da Boca (Interpolação)
    // Se o voiceLevel pular de 0.1 para 0.9, isso faz a transição ficar bonita
    val mouthHeight by animateFloatAsState(
        targetValue = 10f + (voiceLevel * 150f), // Mínimo 10px, Máximo +150px
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "Mouth"
    )

    // Animação de "Squash & Stretch" do corpo quando fala alto
    val bodyScale by animateFloatAsState(
        targetValue = 1f + (voiceLevel * 0.1f), // Cresce 10% quando fala
        label = "Body"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2
            val cy = h / 2

            val bodyColor = Color(0xFFEDB458)

            scale(scaleX = 1f + (voiceLevel * 0.05f), scaleY = bodyScale, pivot = Offset(cx, h)) {
                drawRoundRect(
                    color = bodyColor,
                    topLeft = Offset(w * 0.1f, h * 0.2f),
                    size = Size(w * 0.8f, h * 0.7f),
                    cornerRadius = CornerRadius(100f, 100f)
                )

                drawRoundRect(
                    color = Color(0xFF5D4037),
                    topLeft = Offset(w * 0.1f, h * 0.2f),
                    size = Size(w * 0.8f, h * 0.7f),
                    cornerRadius = CornerRadius(100f, 100f),
                    style = Stroke(width = 12f)
                )
            }

            val eyeRadius = 40f
            val eyeY = cy - 50f
            val leftEyeX = cx - 70f
            val rightEyeX = cx + 70f

            if (isBlinking) {
                drawLine(
                    color = Color.Black,
                    start = Offset(leftEyeX - 30f, eyeY),
                    end = Offset(leftEyeX + 30f, eyeY),
                    strokeWidth = 10f
                )
                drawLine(
                    color = Color.Black,
                    start = Offset(rightEyeX - 30f, eyeY),
                    end = Offset(rightEyeX + 30f, eyeY),
                    strokeWidth = 10f
                )
            } else {
                drawCircle(Color.White, radius = eyeRadius, center = Offset(leftEyeX, eyeY))
                drawCircle(Color.White, radius = eyeRadius, center = Offset(rightEyeX, eyeY))

                val pupilSize = if (voiceLevel > 0.3f) 18f else 12f
                drawCircle(Color.Black, radius = pupilSize, center = Offset(leftEyeX, eyeY))
                drawCircle(Color.Black, radius = pupilSize, center = Offset(rightEyeX, eyeY))
            }


            val mouthY = cy + 60f

            val mouthPath = Path().apply {
                moveTo(cx - 40f, mouthY)

                quadraticBezierTo(
                    cx, mouthY + mouthHeight,
                    cx + 40f, mouthY
                )
            }

            if (voiceLevel > 0.1f) {
                drawPath(path = mouthPath, color = Color(0xFF6D2424))
            }

            drawPath(
                path = mouthPath,
                color = Color.Black,
                style = Stroke(width = 8f)
            )
        }
    }
}