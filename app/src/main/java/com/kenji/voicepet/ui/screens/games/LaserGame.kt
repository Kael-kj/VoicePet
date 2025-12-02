package com.kenji.voicepet.ui.screens.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kenji.voicepet.ui.components.GameOverlay
import com.kenji.voicepet.ui.components.GameScore
import com.kenji.voicepet.ui.theme.MainBackgroundBrush
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Meteor(var x: Float, var y: Float, val size: Float)

@Composable
fun LaserGame(
    voiceLevel: Float,
    command: String = "",
    onClearCommand: () -> Unit = {},
    onExit: () -> Unit
) {
    val meteors = remember { mutableStateListOf<Meteor>() }
    var score by remember { mutableIntStateOf(0) }
    var gameState by remember { mutableStateOf("WAITING") }
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }
    val stars = remember { List(60) { Offset(Random.nextFloat(), Random.nextFloat()) } }

    LaunchedEffect(command) {
        if (command.isNotEmpty()) {
            if (gameState == "WAITING" && command.contains("iniciar")) {
                gameState = "PLAYING"
                onClearCommand()
            }
            if (command.contains("sair")) {
                onExit()
                onClearCommand()
            }
            if (gameState == "GAMEOVER" && (command.contains("jogar") || command.contains("novo"))) {
                meteors.clear(); score = 0; gameState = "PLAYING"
                onClearCommand()
            }
        }
    }

    LaunchedEffect(gameState) {
        if (gameState == "PLAYING") {
            while (gameState == "PLAYING") {
                if (Random.nextFloat() < 0.05f && screenWidth > 0) {
                    meteors.add(Meteor(Random.nextFloat() * screenWidth, -50f, Random.nextFloat() * 40f + 20f))
                }

                val iterator = meteors.listIterator()
                while (iterator.hasNext()) {
                    val m = iterator.next()
                    m.y += 12f

                    if (voiceLevel > 0.3f) {
                        val laserWidth = voiceLevel * screenWidth
                        val laserLeft = (screenWidth / 2) - (laserWidth / 2)
                        val laserRight = (screenWidth / 2) + (laserWidth / 2)

                        if (m.x > laserLeft && m.x < laserRight) {
                            iterator.remove()
                            score++
                        }
                    }
                    if (m.y > screenHeight) gameState = "GAMEOVER"
                }
                delay(30)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackgroundBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height
            val cx = size.width / 2

            stars.forEach { star ->
                drawCircle(Color.White.copy(alpha = Random.nextFloat() * 0.8f), radius = 3f, center = Offset(star.x * size.width, star.y * size.height))
            }

            if (gameState == "PLAYING" && voiceLevel > 0.1f) {
                val laserWidth = voiceLevel * screenWidth
                drawRect(
                    brush = Brush.horizontalGradient(listOf(Color.Transparent, Color.Cyan.copy(alpha = 0.6f), Color.Transparent)),
                    topLeft = Offset(cx - laserWidth/2, 0f),
                    size = Size(laserWidth, size.height)
                )
                drawRect(Color.White, topLeft = Offset(cx - (laserWidth * 0.1f), 0f), size = Size(laserWidth * 0.2f, size.height))
            }

            meteors.forEach { m ->
                drawCircle(Color.DarkGray, radius = m.size, center = Offset(m.x, m.y))
                drawCircle(Color.Red.copy(alpha = 0.3f), radius = m.size, center = Offset(m.x, m.y))
            }

            drawCircle(Color(0xFF2196F3), radius = 50f, center = Offset(cx, size.height))
        }

        Box(modifier = Modifier.align(Alignment.TopCenter)) { GameScore(score) }

        if (gameState != "PLAYING") {
            GameOverlay(
                title = if (gameState == "WAITING") "LASER DEFENSE" else "FALHA NA MISSÃO",
                subtitle = if (gameState == "WAITING") "Diga 'INICIAR' para começar.\nUse gritos para atirar." else "Diga 'JOGAR' para tentar de novo\nou 'SAIR' para voltar.",
                score = if (gameState == "GAMEOVER") score else null,
                onRetry = { meteors.clear(); score = 0; gameState = "PLAYING" },
                onExit = onExit
            )
        }

        if (command.isNotEmpty()) {
            Text("Comando: $command", color = Color.White, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp))
        }
    }
}