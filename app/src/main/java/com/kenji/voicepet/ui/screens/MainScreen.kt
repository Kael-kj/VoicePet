package com.kenji.voicepet.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.kenji.voicepet.data.SpeechManager
import com.kenji.voicepet.data.VoiceSensor
import com.kenji.voicepet.ui.screens.games.*
import com.kenji.voicepet.ui.theme.*

enum class MainTab { HOME, ARCADE }

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(MainTab.HOME) }
    var activeGame by remember { mutableStateOf(GameType.NONE) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasPermission = it }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true
        } else {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    if (!hasPermission) {
        Box(Modifier.fillMaxSize().background(MainBackgroundBrush), contentAlignment = Alignment.Center) {
            Button(onClick = { launcher.launch(Manifest.permission.RECORD_AUDIO) }, colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)) {
                Text("Permitir Microfone", color = TextWhite)
            }
        }
        return
    }

    val speechManager = remember { SpeechManager(context) }
    val spokenText by speechManager.spokenText.collectAsState()

    val voiceSensor = remember { VoiceSensor() }
    val voiceLevel by voiceSensor.observeVoiceLevel().collectAsState(initial = 0f)

    LaunchedEffect(spokenText) {
        if (spokenText.isNotEmpty()) {
            val cmd = spokenText

            if (cmd.contains("sair") || cmd.contains("voltar") || cmd.contains("menu")) {
                if (activeGame != GameType.NONE) {
                    activeGame = GameType.NONE
                    speechManager.clearText()
                }
            }
            else if (activeGame == GameType.NONE) {
                if (cmd.contains("pet") || cmd.contains("casa")) {
                    currentTab = MainTab.HOME
                    speechManager.clearText()
                }
                else if (cmd.contains("arcade") || cmd.contains("jogos")) {
                    currentTab = MainTab.ARCADE
                    speechManager.clearText()
                }
                else {
                    when {
                        cmd.contains("laser") -> activeGame = GameType.LASER
                        cmd.contains("ninja") -> activeGame = GameType.NINJA
                        cmd.contains("pulo") || cmd.contains("grito") -> activeGame = GameType.JUMP
                        cmd.contains("balão") -> activeGame = GameType.BALLOON
                    }
                    if (activeGame != GameType.NONE) speechManager.clearText()
                }
            }
        }
    }

    LaunchedEffect(activeGame) {
        if (activeGame == GameType.NONE) {
            speechManager.startListening()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MainBackgroundBrush)) {
        if (activeGame != GameType.NONE) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (activeGame) {
                    GameType.LASER -> LaserGame(
                        voiceLevel = voiceLevel,
                        command = spokenText,
                        onClearCommand = { speechManager.clearText() },
                        onExit = { activeGame = GameType.NONE }
                    )
                    GameType.JUMP -> ScreamJumpGame(
                        voiceLevel = voiceLevel,
                        command = spokenText,
                        onClearCommand = { speechManager.clearText() },
                        onExit = { activeGame = GameType.NONE }
                    )
                    GameType.BALLOON -> BalloonGame(
                        voiceLevel = voiceLevel,
                        command = spokenText,
                        onClearCommand = { speechManager.clearText() },
                        onExit = { activeGame = GameType.NONE }
                    )
                    GameType.NINJA -> SilentNinjaGame(
                        voiceLevel = voiceLevel,
                        command = spokenText,
                        onClearCommand = { speechManager.clearText() },
                        onExit = { activeGame = GameType.NONE }
                    )
                    else -> activeGame = GameType.NONE
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)) {
                    when (currentTab) {
                        MainTab.HOME -> HomeContent(voiceLevel)
                        MainTab.ARCADE -> ArcadeScreen(onGameSelected = { game -> activeGame = game })
                    }
                }
                FloatingNavBar(currentTab, { currentTab = it }, Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp))

                if (spokenText.isNotEmpty()) {
                    Text(
                        text = "Ouvindo: \"$spokenText\"",
                        color = TextWhite.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 40.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingNavBar(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(64.dp)
            .clip(CircleShape)
            .background(SurfaceDark)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavBarItem(
            icon = Icons.Default.Home,
            label = "Home",
            isSelected = currentTab == MainTab.HOME,
            onClick = { onTabSelected(MainTab.HOME) }
        )

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(AccentPurple),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Mic, contentDescription = null, tint = TextWhite)
        }

        NavBarItem(
            icon = Icons.Default.Gamepad,
            label = "Arcade",
            isSelected = currentTab == MainTab.ARCADE,
            onClick = { onTabSelected(MainTab.ARCADE) }
        )
    }
}

@Composable
fun NavBarItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    val iconColor by animateColorAsState(if (isSelected) TextWhite else TextGray, label = "color")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
        if (isSelected) {
            Text(label, fontSize = 12.sp, color = iconColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HomeContent(voiceLevel: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            Text(
                text = "Olá, sou Tep seu VoicePet.",
                style = MaterialTheme.typography.headlineLarge,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(AccentPurple.copy(alpha = 0.2f))
            )
            com.kenji.voicepet.ui.components.VoicePet(
                voiceLevel = voiceLevel,
                modifier = Modifier.size(250.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Mic,
                contentDescription = null,
                tint = if (voiceLevel > 0.05f) AccentPurple else TextGray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (voiceLevel > 0.05f) "Ouvindo comandos..." else "Aguardando voz...",
                color = if (voiceLevel > 0.05f) TextWhite else TextGray,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
    }
}