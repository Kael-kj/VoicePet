# 🎤 VoicePet & Arcade

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple?style=for-the-badge&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-M3-green?style=for-the-badge&logo=android)
![Status](https://img.shields.io/badge/Status-Completed-success?style=for-the-badge)

> **Um bichinho virtual e console de jogos arcade controlados inteiramente pela voz.**

O **VoicePet** é uma aplicação Android nativa inovadora que explora o potencial do microfone para criar interações de jogabilidade. O projeto vai além do reconhecimento de fala tradicional, utilizando processamento de áudio em tempo real (amplitude/decibéis) para controlar físicas de jogos e animações no Canvas.

---

## 📱 Screenshots

| Home (Pet) | Arcade Menu | Gameplay (Laser) | Gameplay (Ninja) |
|:---:|:---:|:---:|:---:|
| ![Pet Screen](https://via.placeholder.com/200x400?text=Pet+Home) | ![Arcade Screen](https://via.placeholder.com/200x400?text=Arcade+Menu) | ![Gameplay Laser](https://via.placeholder.com/200x400?text=Laser+Game) | ![Gameplay Ninja](https://via.placeholder.com/200x400?text=Ninja+Game) |
---

## 🎮 Funcionalidades

### 1. Voice Pet (Home) 👾
Um personagem reativo desenhado em Canvas que responde ao volume da voz do usuário em tempo real.
* **Sincronia Labial:** A boca abre proporcionalmente ao volume da fala.
* **Animação Squash & Stretch:** O corpo reage fisicamente ao som, esticando e encolhendo.
* **Comandos de Voz:** O pet entende comandos como "Arcade", "Sair", "Jogos".

### 2. Voice Arcade (Minigames) 🕹️
Uma coleção de 4 jogos controlados por sopro, grito ou fala, com visual Neon/Cyberpunk:

* **🚀 Pulo do Grito (Scream Jump):** Um endless runner estilo Flappy Bird onde gritar faz o personagem voar e o silêncio o faz cair.
* **🎈 Balão Boom:** Teste de precisão. Sopre para encher o balão até a linha pontilhada sem estourar.
* **🥷 Ninja Mudo:** Mecânica "Batatinha Frita 1,2,3". Grite para correr na luz verde, fique em silêncio absoluto na luz vermelha.
* **🔫 Laser Defense:** Defenda a base de meteoros. O volume da sua voz define a largura do laser destruidor.

### 3. Navegação por Voz Híbrida 🗣️
O aplicativo possui um sistema inteligente de controle de áudio:
* **Navegação:** Use comandos como *"Entrar no Laser"*, *"Voltar"*, *"Iniciar"* para navegar sem tocar na tela.
* **Gameplay:** O sistema muda dinamicamente para sensor de decibéis durante os jogos para garantir performance em tempo real (60fps), ignorando palavras e focando na intensidade do som.

---

## 🛠️ Tecnologias e Arquitetura

Este projeto foi desenvolvido focando nas práticas modernas do desenvolvimento Android nativo:

* **Linguagem:** [Kotlin](https://kotlinlang.org/) (100%)
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Sem XML)
    * Uso extensivo de `Canvas` e `DrawScope` para renderizar jogos sem assets de imagem.
    * Design System customizado com gradientes e tema Dark/Neon.
    * Animações fluidas com `animateFloatAsState`, `animateColorAsState` e `spring`.
* **Audio Engine:**
    * `AudioRecord`: Leitura de buffer de áudio bruto (PCM 16bit) para cálculo de amplitude.
    * `SpeechRecognizer`: API nativa para processamento de linguagem natural (comandos de navegação).
* **Gerenciamento de Estado:**
    * `StateFlow` e `MutableState` para reatividade da UI.
    * Arquitetura unidirecional de dados.
* **Coroutines & Flow:** Processamento de áudio assíncrono fora da Thread Principal para evitar ANR (Application Not Responding).

---

## 💡 Destaques de Código

### Game Loop no Compose
Os jogos não utilizam engines externas (como Unity). Foi implementado um Game Loop nativo usando `LaunchedEffect` do Compose:

```kotlin
LaunchedEffect(gameState) {
    while (gameState == GameState.PLAYING) {
        // Atualiza física (gravidade, colisão)
        playerY += velocity
        // Redesenha o Canvas
        delay(16) // ~60 FPS
    }
}
