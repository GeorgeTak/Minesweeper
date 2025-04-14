package com.example.minesweeper.ui.theme.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.system.exitProcess

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
                .padding(bottom = 48.dp)
                .padding(end = 24.dp)

        ) {
            Text("Exit", fontWeight = FontWeight.Bold)
        }
    }
}