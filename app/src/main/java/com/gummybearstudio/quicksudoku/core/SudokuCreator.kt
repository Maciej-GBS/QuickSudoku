package com.gummybearstudio.quicksudoku.core

import android.util.Log
import com.gummybearstudio.quicksudoku.core.Sudoku.Companion.VALID_VALUES

class SudokuCreator {
    private val validator = SudokuValidator()

    fun randomCompletedSudoku(): Sudoku? {
        for (i in 0 until 1000) {
            for (row in 0 until 9) {
                for (col in 0 until 9) {
                    if (board[row][col] != 0) continue
                    VALID_VALUES.shuffled().forEach { newValue ->
                        board[row][col] = newValue
                        if (validate().isValid()) {
                            return@forEach
                        }
                    }
                }
            }
            if (sudoku.validate().isCompleted()) {
                return sudoku
            }
        }
        Log.e("Sudoku", "No solution found")
        return null
    }
}