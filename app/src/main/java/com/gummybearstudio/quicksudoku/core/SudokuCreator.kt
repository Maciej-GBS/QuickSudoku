package com.gummybearstudio.quicksudoku.core

import com.gummybearstudio.quicksudoku.core.Sudoku.Companion.CELLS
import com.gummybearstudio.quicksudoku.core.Sudoku.Companion.NO_VALUE
import com.gummybearstudio.quicksudoku.core.Sudoku.Companion.VALID_VALUES

class SudokuCreator(val difficulty: Int) {
    private val validator = SudokuValidator()
    private val possibleSolutions: MutableList<Sudoku> = mutableListOf()

    fun create(row: Int, col: Int, value: Int): Sudoku? {
        val sudoku = Sudoku().apply {
            set(row, col, value)
            setMask(row, col)
        }
        initialize(sudoku)
        return backtrace(sudoku)
    }

    private fun backtrace(sudoku: Sudoku): Sudoku? {
        possibleSolutions.clear()
        if (!backtraceStep(sudoku, CELLS, true)) {
            return null
        }
        return possibleSolutions.first()
    }

    private fun backtraceStep(sudoku: Sudoku,
                              remCells: List<Pair<Int, Int>>,
                              earlyStop: Boolean = false): Boolean {
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
                if (earlyStop && canBeSolved) {
                    return true
                }
            }
        }
        return canBeSolved
    }

    private fun initialize(sudoku: Sudoku) {
        var initCells = N_INIT_CELLS
        while (initCells > 0) {
            val cell = CELLS.random()
            if (sudoku.getMask(cell.first, cell.second)) continue
            sudoku.set(cell.first, cell.second, VALID_VALUES.random())
            if (validator.validate(sudoku).isValid()) {
                --initCells
            }
            else {
                sudoku.set(cell.first, cell.second, NO_VALUE)
            }
        }
    }

    private companion object {
        const val N_INIT_CELLS = 10
    }
}