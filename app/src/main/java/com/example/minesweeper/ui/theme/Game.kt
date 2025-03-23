package com.example.minesweeper.ui.theme

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlin.random.Random

data class Cell(
    var isMine: Boolean = false,
    var surroundingMines: Int = 0,
    var isRevealed: Boolean = false,
    var isFlagged: Boolean = false
) {
    val state: MutableState<Cell> = mutableStateOf(this)
}

class Game(val rows: Int, val cols: Int, private val mineCount: Int) {
    var grid = MutableList(rows) { _ -> MutableList(cols) { _ -> Cell() } }
    var isGameOver = mutableStateOf(false)
    var isGameWon =  mutableStateOf(false)
    var flagsRemaining = mutableStateOf(mineCount)

    var lastMove: Triple<Int, Int, Cell?>? = null
    var undoUsed = false

    init {
        placeMines()
        calculateSurroundingMines()
    }

    private fun placeMines() {
        var placed = 0
        while (placed < mineCount) {
            val row = Random.nextInt(rows)
            val col = Random.nextInt(cols)
            if (!grid[row][col].isMine) {
                grid[row][col].isMine = true
                placed++
            }
        }
    }

    private fun calculateSurroundingMines() {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (!grid[r][c].isMine) {
                    grid[r][c].surroundingMines = countAdjacentMines(r, c)
                }
            }
        }
    }

    private fun countAdjacentMines(row: Int, col: Int): Int {
        var count = 0
        for (r in row - 1..row + 1) {
            for (c in col - 1..col + 1) {
                if (r in 0 until rows && c in 0 until cols && grid[r][c].isMine) {
                    count++
                }
            }
        }
        return count
    }

    fun revealCell(row: Int, col: Int): Boolean {
        if (isGameOver.value || grid[row][col].isFlagged) return false
        if (grid[row][col].isRevealed) return true

        lastMove = Triple(row, col, grid[row][col].copy())
        val updatedCell = grid[row][col].copy(isRevealed = true)
        grid[row][col].state.value = updatedCell
        grid[row][col] = updatedCell

        if (grid[row][col].isMine) {
            isGameOver.value = true
            revealAllMines()
        } else if (grid[row][col].surroundingMines == 0) {
            revealAdjacentCells(row, col)
        } else if (isWin()) {
            isGameWon.value = true
        }

        return false
    }

    private fun revealAdjacentCells(row: Int, col: Int) {
        for (r in row - 1..row + 1) {
            for (c in col - 1..col + 1) {
                if (r in 0 until rows && c in 0 until cols) {
                    if (!grid[r][c].isRevealed && !grid[r][c].isMine) {
                        revealCell(r, c)
                    }
                }
            }
        }
    }

    private fun revealAllMines() {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (grid[r][c].isMine) {
                    val updatedCell = grid[r][c].copy(isRevealed = true)
                    grid[r][c].state.value = updatedCell
                    grid[r][c] = updatedCell
                }
            }
        }
    }

    private fun isWin(): Boolean {
        var allNonMinesRevealed = true
        var allMinesFlagged = true

        for (row in grid)
        {
            for (cell in row)
            {
                if (!cell.isMine && !cell.isRevealed)
                {
                    allNonMinesRevealed = false
                }
                if (cell.isMine && !cell.isFlagged)
                {
                    allMinesFlagged = false
                }
            }
        }

        return allNonMinesRevealed || allMinesFlagged
    }

    fun restartGame() {
        isGameOver.value = false
        isGameWon.value = false
        undoUsed = false
        lastMove = null
        flagsRemaining = mutableStateOf(mineCount)
        grid = MutableList(rows) { _ -> MutableList(cols) { _ -> Cell() } }
        placeMines()
        calculateSurroundingMines()
    }

    fun toggleFlag(row: Int, col: Int) {
        if (isGameOver.value || grid[row][col].isRevealed) return

        lastMove = Triple(row, col, grid[row][col].copy())
        val updatedCell = grid[row][col].copy(isFlagged = !grid[row][col].isFlagged)
        grid[row][col].state.value = updatedCell
        grid[row][col] = updatedCell

        if (updatedCell.isFlagged) {
            flagsRemaining.value--
        }
        else {
            flagsRemaining.value++
        }
    }

    fun undoMove() {
        if (undoUsed || lastMove == null) return

        val (row, col, previousState) = lastMove!!
        if (previousState != null)
        {
            val currentState = grid[row][col]


            grid[row][col].state.value = previousState
            grid[row][col] = previousState


            if (previousState.isFlagged && !currentState.isFlagged)
            {
                flagsRemaining.value += 1
            }
            else if (!previousState.isFlagged && currentState.isFlagged)
            {
                flagsRemaining.value += 1
            }
        }

        lastMove = null
        undoUsed = true
    }
}

