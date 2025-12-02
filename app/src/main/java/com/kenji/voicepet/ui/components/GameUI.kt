package com.kenji.voicepet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kenji.voicepet.ui.theme.AccentPurple
import com.kenji.voicepet.ui.theme.SurfaceDark
import com.kenji.voicepet.ui.theme.TextWhite

@Composable
fun GameOverlay(
    title: String,
    subtitle: String,
    score: Int? = null,
    onRetry: () -> Unit,
    onExit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .border(2.dp, Brush.horizontalGradient(listOf(AccentPurple, Color.Cyan)), RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (score != null) {
                    Text(
                        text = "Score: $score",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Cyan
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextWhite.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("JOGAR / REPETIR", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TextWhite.copy(alpha = 0.3f))
                ) {
                    Text("SAIR", color = TextWhite)
                }
            }
        }
    }
}

@Composable
fun GameScore(score: Int) {
    Box(
        modifier = Modifier
            .padding(top = 40.dp)
            .clip(RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp))
            .background(SurfaceDark.copy(alpha = 0.8f))
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = "$score",
            style = MaterialTheme.typography.headlineLarge,
            color = TextWhite,
            fontWeight = FontWeight.Bold
        )
    }
}