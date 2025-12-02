package com.kenji.voicepet.ui.screens.games

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kenji.voicepet.ui.components.GameOverlay
import com.kenji.voicepet.ui.theme.MainBackgroundBrush
import com.kenji.voicepet.ui.theme.TextWhite
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SilentNinjaGame(
    voiceLevel: Float,
    command: String = "",
    onClearCommand: () -> Unit = {},
    onExit: () -> Unit
) {
    var distance by remember { mutableFloatStateOf(0f) }
    var isGreenLight by remember { mutableStateOf(true) }
    var gameState by remember { mutableStateOf("WAITING") }
    val finishLine = 1000f

    LaunchedEffect(command) {
        if (command.isNotEmpty()) {
            if (gameState == "WAITING" && command.contains("iniciar")) {
                gameState = "PLAYING"
                onClearCommand()
            }
            if ((gameState == "WON" || gameState == "CAUGHT") && (command.contains("jogar") || command.contains("novo"))) {
                distance = 0f; gameState = "PLAYING"
                onClearCommand()
            }
            if (command.contains("sair")) {
                onExit()
                onClearCommand()
            }
        }
    }

    LaunchedEffect(gameState) {
        if (gameState == "PLAYING") {
            while (gameState == "PLAYING") {
                isGreenLight = true
                delay(Random.nextLong(2000, 4000))
                isGreenLight = false
                delay(Random.nextLong(2000, 4000))
            }
        }
    }

    LaunchedEffect(voiceLevel, isGreenLight, gameState) {
        if (gameState == "PLAYING") {
            if (isGreenLight) {
                if (voiceLevel > 0.1f) {
                    distance += voiceLevel * 18f
                    if (distance >= finishLine) gameState = "WON"
                }
            } else {
                if (voiceLevel > 0.12f) gameState = "CAUGHT"
            }
        }
    }

    val ambientColor by animateColorAsState(
        targetValue = if (isGreenLight) Color(0xFF00C853).copy(alpha = 0.2f) else Color(0xFFD50000).copy(alpha = 0.2f),
        animationSpec = tween(500), label = "color"
    )

    Box(modifier = Modifier.fillMaxSize().background(MainBackgroundBrush)) {
        Box(modifier = Modifier.fillMaxSize().background(ambientColor))

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(160.dp).clip(CircleShape)
                    .background(if (isGreenLight) Color(0xFF00C853) else Color(0xFFD50000))
                    .border(6.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isGreenLight) Icons.Default.DirectionsRun else Icons.Default.DoNotDisturb,
                    contentDescription = null, tint = Color.White, modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = if (isGreenLight) "CORRA!!" else "PARADO!!",
                color = TextWhite, fontSize = 26.sp, fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(50.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Progresso", color = TextWhite.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.width(300.dp).height(24.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))) {
                    Box(modifier = Modifier.fillMaxHeight().width(300.dp * (distance / finishLine).coerceIn(0f, 1f)).background(Brush.horizontalGradient(listOf(Color.Blue, Color.Cyan)), RoundedCornerShape(12.dp)))
                }
            }
        }

        if (gameState == "WAITING" || gameState == "WON" || gameState == "CAUGHT") {
            GameOverlay(
                title = when(gameState) { "WON" -> "INFILTRADO!"; "CAUGHT" -> "DETECTADO!"; else -> "NINJA MUDO" },
                subtitle = if (gameState == "WAITING") "Diga 'INICIAR'.\nGrite no verde, cale-se no vermelho." else "Diga 'JOGAR' ou 'SAIR'.",
                onRetry = { distance = 0f; gameState = "PLAYING" },
                onExit = onExit
            )
        }

        if (command.isNotEmpty()) {
            Text("Comando: $command", color = Color.White, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp))
        }
    }
}