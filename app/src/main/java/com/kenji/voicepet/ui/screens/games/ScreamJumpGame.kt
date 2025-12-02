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

enum class JumpGameState { WAITING, PLAYING, GAME_OVER }

data class JumpObstacle(
    val x: Float,
    val gapY: Float,
    val width: Float = 150f,
    val gapSize: Float = 450f,
    var passed: Boolean = false
)

@Composable
fun ScreamJumpGame(
    voiceLevel: Float,
    command: String = "",
    onClearCommand: () -> Unit = {},
    onExit: () -> Unit
) {
    var gameState by remember { mutableStateOf(JumpGameState.WAITING) }
    var score by remember { mutableIntStateOf(0) }
    var playerY by remember { mutableFloatStateOf(0f) }
    var velocity by remember { mutableFloatStateOf(0f) }
    val jumpForce = -40f
    val obstacles = remember { mutableStateListOf<JumpObstacle>() }
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(command) {
        if (command.isNotEmpty()) {
            if (gameState == JumpGameState.WAITING && command.contains("iniciar")) {
                gameState = JumpGameState.PLAYING
                onClearCommand()
            }
            if (gameState == JumpGameState.GAME_OVER && (command.contains("jogar") || command.contains("novo"))) {
                obstacles.clear(); score = 0; playerY = screenHeight / 2; velocity = 0f; gameState = JumpGameState.PLAYING
                onClearCommand()
            }
            if (command.contains("sair")) {
                onExit()
                onClearCommand()
            }
        }
    }

    LaunchedEffect(gameState, voiceLevel) {
        if (gameState == JumpGameState.PLAYING) {
            val startTime = System.currentTimeMillis()
            var lastObstacleTime = 0L

            while (gameState == JumpGameState.PLAYING) {
                if (voiceLevel > 0.2f) {
                    velocity = jumpForce * (1f + voiceLevel)
                } else {
                    velocity += 1.5f
                }
                playerY += velocity
                if (playerY < 0) playerY = 0f
                if (playerY > screenHeight) gameState = JumpGameState.GAME_OVER

                val iterator = obstacles.listIterator()
                while (iterator.hasNext()) {
                    val obs = iterator.next()
                    val newX = obs.x - 12f

                    if (newX < -obs.width) {
                        iterator.remove()
                    } else {
                        val index = obstacles.indexOf(obs)
                        if (index != -1) obstacles[index] = obs.copy(x = newX)

                        val playerX = screenWidth / 2
                        val playerRadius = 40f
                        if (newX < playerX + playerRadius && newX + obs.width > playerX - playerRadius) {
                            val hitTop = playerY - playerRadius < obs.gapY
                            val hitBottom = playerY + playerRadius > obs.gapY + obs.gapSize
                            if (hitTop || hitBottom) gameState = JumpGameState.GAME_OVER
                        }

                        if (!obs.passed && newX < playerX - playerRadius) {
                            obstacles[obstacles.indexOf(obs)] = obs.copy(passed = true)
                            score++
                        }
                    }
                }

                if (System.currentTimeMillis() - startTime > lastObstacleTime + 2200) {
                    if (screenWidth > 0) {
                        val randomGapY = Random.nextFloat() * (screenHeight - 600f) + 100f
                        obstacles.add(JumpObstacle(x = screenWidth, gapY = randomGapY))
                        lastObstacleTime = System.currentTimeMillis() - startTime
                    }
                }
                delay(16)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MainBackgroundBrush)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            if (gameState == JumpGameState.WAITING) playerY = size.height / 2

            val obstacleBrush = Brush.verticalGradient(listOf(Color(0xFF00E676), Color(0xFF2979FF)))
            obstacles.forEach { obs ->
                drawRect(brush = obstacleBrush, topLeft = Offset(obs.x, 0f), size = Size(obs.width, obs.gapY))
                drawRect(brush = obstacleBrush, topLeft = Offset(obs.x, obs.gapY + obs.gapSize), size = Size(obs.width, size.height - (obs.gapY + obs.gapSize)))
            }

            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0xFFFFD700), Color.Transparent), radius = 80f + (voiceLevel * 20f)),
                radius = 80f + (voiceLevel * 20f),
                center = Offset(size.width / 2, playerY)
            )
            drawCircle(Color.White, radius = 30f, center = Offset(size.width / 2, playerY))
        }

        Box(modifier = Modifier.align(Alignment.TopCenter)) { GameScore(score) }

        if (gameState != JumpGameState.PLAYING) {
            GameOverlay(
                title = if (gameState == JumpGameState.WAITING) "PULO DO GRITO" else "GAME OVER",
                subtitle = if (gameState == JumpGameState.WAITING) "Diga 'INICIAR' para jogar.\nGrite para voar." else "Diga 'JOGAR' para tentar de novo\nou 'SAIR' para voltar.",
                score = if (gameState == JumpGameState.GAME_OVER) score else null,
                onRetry = { obstacles.clear(); score = 0; playerY = screenHeight / 2; velocity = 0f; gameState = JumpGameState.PLAYING },
                onExit = onExit
            )
        }

        if (command.isNotEmpty()) {
            Text("Comando: $command", color = Color.White, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp))
        }
    }
}