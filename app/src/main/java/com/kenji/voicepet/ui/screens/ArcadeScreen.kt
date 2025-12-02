package com.kenji.voicepet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kenji.voicepet.ui.theme.AccentPurple
import com.kenji.voicepet.ui.theme.TextGray
import com.kenji.voicepet.ui.theme.TextWhite

enum class GameType { NONE, JUMP, BALLOON, NINJA, LASER }

data class GameItem(val type: GameType, val name: String, val desc: String, val icon: ImageVector, val color1: Color, val color2: Color)

val gamesList = listOf(
    GameItem(GameType.JUMP, "Pulo do Grito", "Grite para voar alto!", Icons.Default.Gamepad, Color(0xFF4CAF50), Color(0xFF8BC34A)),
    GameItem(GameType.BALLOON, "Balão Boom", "Controle o sopro.", Icons.Default.Air, Color(0xFF9C27B0), Color(0xFFBA68C8)),
    GameItem(GameType.NINJA, "Ninja Mudo", "Silêncio total!", Icons.Default.DoNotDisturb, Color(0xFFF44336), Color(0xFFE57373)),
    GameItem(GameType.LASER, "Laser Defense", "Destrua meteoros.", Icons.Default.Bolt, Color(0xFF2196F3), Color(0xFF64B5F6))
)

@Composable
fun ArcadeScreen(
    onGameSelected: (GameType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp, start = 24.dp, end = 24.dp)
    ) {
        Text(
            "Arcade",
            style = MaterialTheme.typography.headlineLarge,
            color = TextWhite,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Escolha seu desafio de voz",
            style = MaterialTheme.typography.bodyLarge,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(32.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(gamesList) { game ->
                ModernGameCard(game, onGameSelected)
            }
        }
    }
}

@Composable
fun ModernGameCard(game: GameItem, onClick: (GameType) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick(game.type) },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(game.color1, game.color2)
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(game.icon, contentDescription = null, tint = TextWhite, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(game.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(game.desc, color = TextWhite.copy(alpha = 0.8f), fontSize = 14.sp)
            }

            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TextWhite, modifier = Modifier.size(24.dp))
        }
    }
}