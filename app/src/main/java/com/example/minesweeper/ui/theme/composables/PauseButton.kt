package com.example.minesweeper.ui.theme.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PauseResumeButton(isPaused: MutableState<Boolean>) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPaused.value) Color.Green else Color.Red, contentColor = Color.White),
            modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 16.dp).padding(start = 16.dp)
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