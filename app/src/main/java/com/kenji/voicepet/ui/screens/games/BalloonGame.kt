package com.kenji.voicepet.ui.screens.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kenji.voicepet.ui.components.GameOverlay
import com.kenji.voicepet.ui.theme.AccentPurple
import com.kenji.voicepet.ui.theme.MainBackgroundBrush
import com.kenji.voicepet.ui.theme.TextWhite

@Composable
fun BalloonGame(
    voiceLevel: Float,
    command: String = "",
    onClearCommand: () -> Unit = {},
    onExit: () -> Unit
) {
    var balloonSize by remember { mutableFloatStateOf(50f) }
    var gameState by remember { mutableStateOf("WAITING") }
    val targetSize = 600f
    val limitSize = 750f

    LaunchedEffect(command) {
        if (command.isNotEmpty()) {
            if (gameState == "WAITING" && command.contains("iniciar")) {
                gameState = "PLAYING"
                onClearCommand()
            }
            if (gameState == "PLAYING" && (command.contains("parar") || command.contains("agora"))) {
                if (balloonSize > targetSize - 100 && balloonSize < limitSize) gameState = "WON"
                else gameState = "POPPED"
                onClearCommand()
            }
            if ((gameState == "WON" || gameState == "POPPED") && (command.contains("jogar") || command.contains("novo"))) {
                balloonSize = 50f; gameState = "PLAYING"
                onClearCommand()
            }
            if (command.contains("sair")) {
                onExit()
                onClearCommand()
            }
        }
    }

    LaunchedEffect(voiceLevel, gameState) {
        if (gameState == "PLAYING") {
            if (voiceLevel > 0.1f) {
                balloonSize += voiceLevel * 12f
            }
            if (balloonSize >= limitSize) gameState = "POPPED"
        }
    }

    val animatedSize by animateFloatAsState(targetValue = balloonSize, label = "size")

    Box(
        modifier = Modifier.fillMaxSize().background(MainBackgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (gameState == "PLAYING") {
                Text("Sopre... Diga 'PARAR' para vencer.", color = TextWhite.copy(alpha = 0.5f), fontSize = 18.sp, modifier = Modifier.padding(top = 80.dp))
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2
                    val cy = size.height / 2

                    drawCircle(
                        color = Color.Cyan.copy(alpha = 0.5f),
                        radius = targetSize / 2,
                        center = androidx.compose.ui.geometry.Offset(cx, cy),
                        style = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f))
                    )

                    if (gameState != "POPPED") {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFE040FB), Color(0xFF4A148C)),
                                center = androidx.compose.ui.geometry.Offset(cx - 50, cy - 50),
                                radius = animatedSize
                            ),
                            radius = animatedSize / 2,
                            center = androidx.compose.ui.geometry.Offset(cx, cy)
                        )
                    } else {
                        drawLine(Color.Red, androidx.compose.ui.geometry.Offset(cx - 200, cy - 200), androidx.compose.ui.geometry.Offset(cx + 200, cy + 200), strokeWidth = 10f)
                        drawLine(Color.Red, androidx.compose.ui.geometry.Offset(cx + 200, cy - 200), androidx.compose.ui.geometry.Offset(cx - 200, cy + 200), strokeWidth = 10f)
                    }
                }
            }

            if (gameState == "PLAYING") {
                Button(
                    onClick = { if (balloonSize > targetSize - 100 && balloonSize < limitSize) gameState = "WON" else gameState = "POPPED" },
                    modifier = Modifier.padding(bottom = 60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                ) { Text("PARAR") }
            }
        }

        if (gameState == "WAITING" || gameState == "WON" || gameState == "POPPED") {
            GameOverlay(
                title = when(gameState) { "WON" -> "PERFEITO!"; "POPPED" -> "ESTOUROU!"; else -> "BALÃO BOOM" },
                subtitle = if (gameState == "WAITING") "Diga 'INICIAR'.\nSopre para encher, diga 'PARAR' na linha." else "Diga 'JOGAR' ou 'SAIR'.",
                onRetry = { balloonSize = 50f; gameState = "PLAYING" },
                onExit = onExit
            )
        }

        if (command.isNotEmpty()) {
            Text("Comando: $command", color = Color.White, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp))
        }
    }
}