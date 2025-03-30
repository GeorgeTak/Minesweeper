package com.example.minesweeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minesweeper.ui.theme.Game
import com.example.minesweeper.ui.theme.MinesweeperTheme
import kotlinx.coroutines.delay
import kotlin.system.exitProcess


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkTheme = remember { mutableStateOf(true) }

            MinesweeperTheme(darkTheme = darkTheme.value) {
                var showGame by remember { mutableStateOf(false) }

                if (showGame) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        MinesweeperGame(
                            modifier = Modifier.padding(innerPadding),
                            darkTheme = darkTheme.value
                        )
                    }
                    ExitButtonWithConfirmation()
                } else {
                    StartMenu(
                        onStartGame = { showGame = true },
                        darkTheme = darkTheme.value,
                        onThemeChange = { darkTheme.value = it }
                    )
                }
            }
        }
    }
}
@Composable
fun StartMenu(onStartGame: () -> Unit, darkTheme: Boolean, onThemeChange: (Boolean) -> Unit)
{

    var showInstructions by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(if (darkTheme) Color(112, 128, 144) else Color(240, 240, 245)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Minesweeper", fontSize = 32.sp, color = if (darkTheme) Color.Blue else Color(0, 0, 180), modifier = Modifier.background(color = if (darkTheme) Color(173,216,230) else Color(200, 230, 255)).border(2.dp, if (darkTheme) Color(50,82,123) else Color(100, 130, 180)).padding(8.dp))

        Spacer(modifier = Modifier.height(80.dp))


        Button(
            onClick = onStartGame,
            colors = ButtonDefaults.buttonColors(containerColor = if (darkTheme) Color(0, 0, 139) else Color(30, 144, 255), contentColor = Color.White),
            shape = RoundedCornerShape(4.dp)
        )
        {
            Text("Start Game", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(80.dp))

        Button(
            onClick = { showInstructions = true },
            colors = ButtonDefaults.buttonColors(containerColor = if (darkTheme) Color(0,0,139) else Color(30, 144, 255),contentColor = Color.White),
            shape = RoundedCornerShape(4.dp)
        )
        {
            Text("How to Play", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(80.dp))

        Button(
            onClick = { onThemeChange(!darkTheme) },
            colors = ButtonDefaults.buttonColors(containerColor = if (darkTheme) Color(0,0,139) else Color(30, 144, 255),contentColor = Color.White),
            shape = RoundedCornerShape(4.dp)
        )
        {
            Text(if (darkTheme) "Light Mode" else "Dark Mode", fontSize = 20.sp)
        }



        Spacer(modifier = Modifier.height(200.dp))

        ExitButtonWithConfirmation()
    }
    if (showInstructions)
    {
        InstructionsDialog(onDismiss = { showInstructions = false })
    }
}

@Composable
fun InstructionsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("How to Play") },
        text = {
            Text(
                "- Minesweeper is a game where you must clear the grid without triggering mines.\n" +
                        "- Use logic to determine which tiles are safe.\n" +
                        "- Tap a tile to reveal it. Long press to place a flag where you suspect a mine.\n" +
                        "- You can undo your last move once per game.\n" +
                        "- You can pause the game as many times as you want.\n"+
                        "- The goal is to reveal all non-mine tiles without hitting any mines!"
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0, 0, 139), contentColor = Color.White)
            ) {
                Text("Got it!")
            }
        }
    )
}


@Composable
fun MinesweeperGame(modifier: Modifier = Modifier,darkTheme: Boolean) {
    val rows = 8
    val cols = 8
    val mineCount = 8
    val gameState = remember { mutableStateOf(Game(rows, cols, mineCount)) }
    val timeElapsed = remember { mutableStateOf(0) }
    val message = remember { mutableStateOf("") }
    val isPaused = remember { mutableStateOf(false) }

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


    val gameOverAlpha by animateFloatAsState(
        targetValue = if (gameState.value.isGameOver.value) 1f else 0f,
        animationSpec = tween(durationMillis = 2000), label = ""
    )

    val gameWonAlpha by animateFloatAsState(
        targetValue = if (gameState.value.isGameWon.value) 1f else 0f,
        animationSpec = tween(durationMillis = 2000), label = ""
    )



    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(if (darkTheme) Color(119, 136, 153) else Color(230, 230, 235)),
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
            text = "Flags remaining: ${gameState.value.flagsRemaining.value} ",
            fontSize = 18.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        if (gameState.value.isGameOver.value) {
            Text(
                text = "Game Over!!",
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
                text = "Congrats, you won!!",
                fontSize = 20.sp,
                color = Color.Green.copy(alpha = gameWonAlpha),
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(8.dp)
                    .animateContentSize()
            )
        }


        MinesweeperGrid(gameState.value, message,isPaused.value ,darkTheme)


        if (!gameState.value.isGameOver.value && !gameState.value.isGameWon.value)
        {

            Text(text = "Time: ${timeElapsed.value}s", fontSize = 18.sp, color = Color.DarkGray, modifier = Modifier.padding(8.dp))

            if (message.value.isNotEmpty())
            {
                Text(
                    text = message.value,
                    fontSize = 16.sp,
                    color = Color(0, 0, 139),
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.padding(8.dp)
                )

                LaunchedEffect(message.value) {
                    delay(2000L)
                    message.value = ""
                }
            }

            Button(onClick = { gameState.value.undoMove() }, enabled = gameState.value.lastMove != null && !gameState.value.undoUsed,
                    colors = ButtonDefaults.buttonColors(containerColor = if (gameState.value.undoUsed) Color.LightGray else if (darkTheme) Color(0,0,139) else Color(30, 144, 255),contentColor = Color.White),
                    modifier = Modifier.padding(8.dp))
                {
                    Text("Undo")
                }
            
            PauseResumeButton(isPaused)


        }
        if (gameState.value.isGameOver.value || gameState.value.isGameWon.value) {

            Button(onClick = { gameState.value.restartGame() },modifier = Modifier.padding(top = 16.dp),colors = ButtonDefaults.buttonColors(containerColor = if (darkTheme) Color(0,0,139) else Color(30, 144, 255),contentColor = Color.White))
            {
                Text("Restart Game")

            }

            Text(
                text = "Your time was: ${timeElapsed.value}s",
                fontSize = 18.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 8.dp)
            )

        }

    }
}

@Composable
fun PauseResumeButton(isPaused: MutableState<Boolean>) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 1.dp, bottom = 1.dp, start = 4.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPaused.value) Color.Green else Color.Red,
                contentColor = Color.White
            ),
        ) {
            Text(if (isPaused.value) "Resume" else "Pause")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (isPaused.value) "Game Resumed" else "Game Paused") },
            text = { Text(if (isPaused.value) "The game is now running." else "The game is paused.") },
            confirmButton = {
                Button(
                    onClick = {
                        isPaused.value = !isPaused.value
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0, 0, 139), contentColor = Color.White)
                )
                {
                    Text("Got it!")
                }
            }
        )
    }
}

@Composable
fun ExitButtonWithConfirmation() {
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {

        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Exit") },
            text = { Text("Are you sure you want to exit the game?") },
            confirmButton = {
                Button(
                    onClick = { exitProcess(0) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0, 0, 139),
                        contentColor = Color.White,
                    ),
                )
                {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0, 0, 139),
                        contentColor = Color.White,
                    ),
                ) {
                    Text("No")
                }
            }
        )
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Button(
            onClick = { showDialog.value = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0,0,139), contentColor = Color.White),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp)
                .padding(end = 16.dp)

        ) {
            Text("Exit", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MinesweeperGrid(game: Game, message: MutableState<String>,isPaused: Boolean,darkTheme: Boolean) {
    Column(modifier = Modifier.border(2.dp, Color.LightGray).padding(2.dp))
    {
        for (row in 0 until game.rows) {
            Row {
                for (col in 0 until game.cols) {
                    MineCell(game, row, col,message,isPaused,darkTheme)
                }
            }
        }
    }
}

@Composable
fun MineCell(game: Game, row: Int, col: Int, message: MutableState<String>,isPaused: Boolean, darkTheme: Boolean) {
    val cellState by game.grid[row][col].state

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
    )

    val textColor = when {
        cellState.isMine -> Color.White
        cellState.surroundingMines in numberColors -> numberColors[cellState.surroundingMines]!!
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(2.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .then(
                if (!isPaused) Modifier.pointerInput(Unit)
                {
                    if (!game.isGameOver.value && !game.isGameWon.value) {
                        detectTapGestures(
                            onTap = {
                                if (game.revealCell(row, col)) {
                                    message.value = "This tile is already revealed!"
                                } else {
                                    message.value = ""
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
        // Dark theme preview
        MinesweeperTheme(darkTheme = true) {
            MinesweeperGame(darkTheme = true)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Light theme preview
        MinesweeperTheme(darkTheme = false) {
            MinesweeperGame(darkTheme = false)
        }
    }
}

