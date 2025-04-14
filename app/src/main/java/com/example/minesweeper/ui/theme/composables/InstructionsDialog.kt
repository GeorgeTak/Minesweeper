package com.example.minesweeper.ui.theme.composables



import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun InstructionsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("How to Play") },
        text = {
            Text(
                "- Minesweeper is a game where you must clear the grid without triggering mines (\uD83D\uDCA3).\n" +
                        "- Use logic to determine which tiles are safe.\n" +
                        "- Tap a tile to reveal it. Long press to place a flag (\uD83D\uDEA9) where you suspect a mine.\n" +
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
