package com.example.minesweeper.ui.theme.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class Difficulty(val rows: Int, val cols: Int, val mines: Int) {
    EASY(8, 8, 10),
    MEDIUM(9, 9, 12),
    HARD(10, 9, 15)
}

@Composable
fun DifficultySelection(selectedDifficulty: Difficulty, onDifficultySelected: (Difficulty) -> Unit, darkTheme: Boolean) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        Difficulty.entries.forEach { difficulty ->

            val containerColors = when {
                (difficulty == selectedDifficulty) && darkTheme -> Color(173, 216, 230)
                (difficulty == selectedDifficulty) && !darkTheme -> Color(25, 25, 112)
                (selectedDifficulty != difficulty) && darkTheme -> Color(0, 0, 139)
                else -> Color(30, 144, 255)
            }
            Button(
                onClick = { onDifficultySelected(difficulty) },
                colors = ButtonDefaults.buttonColors(containerColor = containerColors,contentColor = Color.White),
                shape = RoundedCornerShape(4.dp)
            )
            {
                Text(text = difficulty.name)
            }
        }
    }
}