package com.example.minesweeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minesweeper.ui.theme.Game
import com.example.minesweeper.ui.theme.MinesweeperTheme
import com.example.minesweeper.ui.theme.composables.Difficulty
import com.example.minesweeper.ui.theme.composables.DifficultySelection
import com.example.minesweeper.ui.theme.composables.ExitButtonWithConfirmation
import com.example.minesweeper.ui.theme.composables.InstructionsDialog
import com.example.minesweeper.ui.theme.composables.PauseResumeButton
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkTheme = remember { mutableStateOf(true) }
            val selectedDifficulty = remember { mutableStateOf(Difficulty.EASY) }

            MinesweeperTheme(darkTheme = darkTheme.value) {
                var showGame by remember { mutableStateOf(false) }

                if (showGame) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        MinesweeperGame(
                            modifier = Modifier.padding(innerPadding),
                            darkTheme = darkTheme.value,
                            selectedDifficulty = selectedDifficulty.value
                        )
                    }
                    ExitButtonWithConfirmation()
                } else {
                    StartMenu(
                        onStartGame = { showGame = true },
                        darkTheme = darkTheme.value,
                        onThemeChange = { darkTheme.value = it },
                        selectedDifficulty = selectedDifficulty.value,
                        onDifficultyChange = { selectedDifficulty.value = it }
                    )
                }
            }
        }
    }
}
@Composable
fun StartMenu(
    onStartGame: () -> Unit,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    selectedDifficulty: Difficulty,
    onDifficultyChange: (Difficulty) -> Unit
) {
    var showInstructions by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (darkTheme) Color(112, 128, 144) else Color(210, 255, 230))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Minesweeper",
                fontSize = 32.sp,
                color = if (darkTheme) Color.Blue else Color(0, 0, 180),
                modifier = Modifier
                    .background(if (darkTheme) Color(173, 216, 230) else Color(200, 230, 255))
                    .border(2.dp, if (darkTheme) Color(50, 82, 123) else Color(100, 130, 180))
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(80.dp))

            Button(
                onClick = onStartGame,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (darkTheme) Color(0, 0, 139) else Color(30, 144, 255),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Start Game", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(80.dp))

            Button(
                onClick = { showInstructions = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (darkTheme) Color(0, 0, 139) else Color(30, 144, 255),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("How to Play", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Select Difficulty",
                fontSize = 24.sp,
                color = if (darkTheme) Color.White else Color.Blue
            )

            Spacer(modifier = Modifier.height(16.dp))

            DifficultySelection(selectedDifficulty, onDifficultyChange, darkTheme)

            Spacer(modifier = Modifier.height(80.dp))

            ExitButtonWithConfirmation()
        }


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(top = 16.dp, start = 16.dp, bottom = 48.dp)
        ) {
            Text(
                text = if (darkTheme) "Dark Mode" else "Light Mode",
                fontSize = 20.sp,
                color = if (darkTheme) Color.White else Color.Blue
            )
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = darkTheme,
                onCheckedChange = { onThemeChange(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0, 0, 139),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(30, 144, 255)
                )
            )
        }


        if (showInstructions) {
            InstructionsDialog(onDismiss = { showInstructions = false })
        }
    }
}






@Composable
fun MinesweeperGame(modifier: Modifier = Modifier,darkTheme: Boolean,selectedDifficulty: Difficulty) {
    val rows = selectedDifficulty.rows
    val cols = selectedDifficulty.cols
    val mineCount = selectedDifficulty.mines
    val gameState = remember { mutableStateOf(Game(rows, cols, mineCount)) }
    val timeElapsed = remember { mutableStateOf(0) }
    val message = remember { mutableStateOf("") }
    val isPaused = remember { mutableStateOf(false) }


    val gameOverAlpha by animateFloatAsState(
        targetValue = if (gameState.value.isGameOver.value) 1f else 0f,
        animationSpec = tween(durationMillis = 2000), label = ""
    )

    val gameWonAlpha by animateFloatAsState(
        targetValue = if (gameState.value.isGameWon.value) 1f else 0f,
        animationSpec = tween(durationMillis = 2000), label = ""
    )



    LaunchedEffect(gameState.value.isGameOver.value, gameState.value.isGameWon.value)
    {
        if (!gameState.value.isGameOver.value && !gameState.value.isGameWon.value)
        {
            timeElapsed.value = 0
            while (!gameState.value.isGameOver.value && !gameState.value.isGameWon.value)
            {
                delay(1000L)
                if (!isPaused.value)
                {
                    timeElapsed.value++
                }
            }
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(if (darkTheme) Color(119, 136, 153) else Color(210, 255, 230)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Minesweeper",
            fontSize = 24.sp,
            color = Color.Blue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Difficulty: ${selectedDifficulty.name}",
            fontSize = 18.sp,
            color = Color.DarkGray,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Flags remaining: ${gameState.value.flagsRemaining.value} ",
            fontSize = 18.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (gameState.value.isGameOver.value) {
            Text(
                text = "That mine got you good!",
                fontSize = 20.sp,
                color = Color.Red.copy(alpha = gameOverAlpha),
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(8.dp)
                    .animateContentSize()
            )
        } else if (gameState.value.isGameWon.value) {
            Text(
                text = "Victory! 🏆 You cleared them all!",
                fontSize = 20.sp,
                color = Color.Green.copy(alpha = gameWonAlpha),
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(8.dp)
                    .animateContentSize()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        MinesweeperGrid(gameState.value, message,isPaused.value ,darkTheme)


        if (!gameState.value.isGameOver.value && !gameState.value.isGameWon.value)
        {

            Text(text = "Time: ${timeElapsed.value}s", fontSize = 18.sp, color = Color.DarkGray, modifier = Modifier.padding(8.dp))

            Button(onClick = { gameState.value.undoMove() }, enabled = gameState.value.lastMove != null && !gameState.value.undoUsed,
                colors = ButtonDefaults.buttonColors(containerColor = if (gameState.value.undoUsed) Color.LightGray else if (darkTheme) Color(0,0,139) else Color(30, 144, 255),contentColor = Color.White),
                modifier = Modifier.padding(8.dp))
            {
                Text("Undo")
            }

            if (message.value.isNotEmpty()) {
                var visible by remember { mutableStateOf(true) }

                AnimatedVisibility(
                    visible = visible,
                    exit = fadeOut(animationSpec = tween(durationMillis = 1000))
                ) {
                    Text(
                        text = message.value,
                        fontSize = 16.sp,
                        color = Color(0, 0, 139),
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                LaunchedEffect(message.value) {
                    delay(1000L)
                    visible = false
                    delay(1000L)
                    message.value = ""
                }
            }

            
            PauseResumeButton(isPaused)


        }
        if (gameState.value.isGameOver.value || gameState.value.isGameWon.value)
        {
            val minutes = timeElapsed.value / 60
            val seconds = timeElapsed.value % 60

            Button(onClick = { gameState.value.restartGame() },modifier = Modifier.padding(top = 16.dp),colors = ButtonDefaults.buttonColors(containerColor = if (darkTheme) Color(0,0,139) else Color(30, 144, 255),contentColor = Color.White))
            {
                Text("Restart Game")

            }

            if(minutes > 0)
            {
                Text(
                    text = "Your time was: ${minutes}m ${seconds}s",
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            else
            {
                Text(
                    text = "Your time was: ${seconds}s",
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 8.dp)
                )

            }
        }

    }
}

@Composable
fun MinesweeperGrid(game: Game, message: MutableState<String>,isPaused: Boolean,darkTheme: Boolean) {
    val revealCounter = remember { mutableStateOf(0) }
    Column(modifier = Modifier.border(2.dp, Color.LightGray).padding(2.dp))
    {
        for (row in 0 until game.rows) {
            Row {
                for (col in 0 until game.cols) {
                    MineCell(game, row, col,message,isPaused,darkTheme, revealCounter)
                }
            }
        }
    }
}

@Composable
fun MineCell(game: Game, row: Int, col: Int, message: MutableState<String>,isPaused: Boolean, darkTheme: Boolean,revealCounter: MutableState<Int>) {
    val cellState by game.grid[row][col].state
    val isRevealed = cellState.isRevealed

    val backgroundColor = when {
        cellState.isFlagged -> Color.Yellow
        !cellState.isRevealed -> if (darkTheme) Color(110, 110, 110) else Color(170, 170, 170)
        cellState.isMine -> Color.Red
        else -> if (darkTheme) Color(192, 192, 192) else Color(220, 220, 220)
    }

    val numberColors = mapOf(
        1 to Color.Blue,
        2 to Color.Green,
        3 to Color.Yellow,
        4 to Color.Cyan,
        5 to Color.Magenta,
        6 to Color.Black,
        7 to Color.Red,
        8 to Color(255,0,127)
    )

    val textColor = when {
        cellState.isMine -> Color.White
        cellState.surroundingMines in numberColors -> numberColors[cellState.surroundingMines]!!
        else -> Color.White
    }

    val alpha by animateFloatAsState(
        targetValue = if (isRevealed) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (isRevealed) 1f else 1f,
        animationSpec = if (isRevealed) tween(durationMillis = 300, easing = FastOutSlowInEasing) else tween(0),
        label = "scale"
    )
    val encouragements = listOf(
        "Nice move!",
        "You're doing great!",
        "Keep going!",
        "Looking sharp!",
        "Boom-free zone!",
        "Strategy on point!",
        "You're a pro!"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(2.dp)
            .graphicsLayer {
                this.alpha = if (isRevealed) alpha else 1f
                this.scaleX = scale
                this.scaleY = scale
            }
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .then(
                if (!isPaused) Modifier.pointerInput(Unit)
                {
                    if (!game.isGameOver.value && !game.isGameWon.value) {
                        detectTapGestures(
                            onTap = {
                                val preState = game.grid[row][col].state.value
                                val wasRevealed = preState.isRevealed
                                val wasFlagged = preState.isFlagged
                                val wasMine = preState.isMine

                                val alreadyRevealed = game.revealCell(row, col)

                                if (alreadyRevealed) {
                                    message.value = "This tile is already revealed!"
                                } else {
                                    if (!wasRevealed && !wasFlagged && !wasMine) {
                                        revealCounter.value++
                                        if (revealCounter.value % 3 == 0) {
                                            message.value = encouragements.random()
                                        } else {
                                            message.value = ""
                                        }
                                    } else {
                                        message.value = ""
                                    }
                                }
                            },
                            onLongPress = { game.toggleFlag(row, col) }
                        )
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (cellState.isFlagged) {
            Text(text = "🚩", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
        else if (cellState.isRevealed) {
            Text(
                text = when {
                    cellState.isMine -> "\uD83D\uDCA3"
                    else -> cellState.surroundingMines.toString()
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMinesweeperGame() {
    Column {

        MinesweeperTheme(darkTheme = true) {
            MinesweeperGame(darkTheme = true, selectedDifficulty = Difficulty.EASY)
        }

        Spacer(modifier = Modifier.height(16.dp))

        MinesweeperTheme(darkTheme = false) {
            MinesweeperGame(darkTheme = false, selectedDifficulty = Difficulty.EASY)
        }
    }
}

