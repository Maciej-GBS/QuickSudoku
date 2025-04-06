package com.gummybearstudio.quicksudoku.core

import com.gummybearstudio.quicksudoku.core.Sudoku.Companion.CELLS
import com.gummybearstudio.quicksudoku.core.Sudoku.Companion.VALID_VALUES

class SudokuCreator(val difficulty: Int) {
    private val validator = SudokuValidator()
    private val possibleSolutions: MutableList<Sudoku> = mutableListOf()
    private var earlyStop = false

    fun create(row: Int, col: Int, value: Int): Sudoku {
        val sudoku = Sudoku().apply {
            set(row, col, value)
            setMask(row, col)
        }
        initialize(sudoku)
        backtrace(sudoku)
        return sudoku
    }

    private fun backtrace(sudoku: Sudoku) {
        possibleSolutions.clear()
        backtraceStep(sudoku, CELLS)
    }

    private fun backtraceStep(sudoku: Sudoku, remCells: List<Pair<Int, Int>>): Boolean {
        if (remCells.isEmpty()) {
            possibleSolutions.add(sudoku.copy())
            return true
        }
        else if (sudoku.getMask(remCells[0].first, remCells[0].second)) {
            return backtraceStep(sudoku, remCells.drop(1))
        }
        var canBeSolved = true
        for (value in VALID_VALUES) {
            sudoku.set(remCells[0].first, remCells[0].second, value)
            if (validator.validate(sudoku).isValid()) {
                canBeSolved = canBeSolved && backtraceStep(sudoku, remCells.drop(1))
                if (earlyStop && !canBeSolved) {
                    return false
                }
            }
        }
        return canBeSolved
    }

    private fun initialize(sudoku: Sudoku) {
    }

    private companion object {
        const val N_INIT_CELLS = 10
    }
}