package com.kenji.voicepet.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Cores Sólidas
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// --- NOVAS CORES DO DESIGN ---
val DarkBackground = Color(0xFF1A1625) // Fundo quase preto
val SurfaceDark = Color(0xFF2D283E) // Para cards e barras
val AccentPurple = Color(0xFF7F5AF0) // Roxo vibrante para destaques
val TextWhite = Color(0xFFFFFFFE)
val TextGray = Color(0xFF94A1B2)

// Gradiente Principal do Fundo (Roxo -> Azulado escuro)
val MainBackgroundBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF2A2144), // Roxo topo
        Color(0xFF121212)  // Preto fundo
    )
)

// Gradiente para botões/cards ativos
val ActiveGradientBrush = Brush.horizontalGradient(
    colors = listOf(Color(0xFF7F5AF0), Color(0xFF2196F3))
)