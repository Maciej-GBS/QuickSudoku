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
        // Find a complete valid board
        val getEmptyCells =  { testSudoku: Sudoku ->
            CELLS.filter { cell ->
                testSudoku.get(cell.first, cell.second) == NO_VALUE
            }
        }
        possibleSolutions.clear()
        if (!backtraceStep(sudoku, getEmptyCells(sudoku), 1)) {
            return null
        }
        val solution = possibleSolutions.first()
        // Clear cells until difficulty is reached
        val hiddenCells: MutableSet<Pair<Int, Int>> = mutableSetOf()
        var maskedSudoku: Sudoku
        var iterations = 0
        do {
            if (++iterations > 10000) throw UnreachableDifficultyException()
            val cell = CELLS.random()
            if (solution.getMask(cell.first, cell.second)) continue
            maskedSudoku = solution.copy().apply {
                hiddenCells.forEach {
                    set(it.first, it.second, NO_VALUE)
                }
                set(cell.first, cell.second, NO_VALUE)
            }
            possibleSolutions.clear()
            backtraceStep(maskedSudoku, getEmptyCells(maskedSudoku), 2)
            if (possibleSolutions.size == 1) {
                hiddenCells.add(cell)
            }
        } while ((CELLS.size - hiddenCells.size) > difficulty)
        CELLS.subtract(hiddenCells).forEach {
            solution.setMask(it.first, it.second)
        }
        solution.reset()
        return solution
    }

    private fun backtraceStep(sudoku: Sudoku,
                              remCells: List<Pair<Int, Int>>,
                              earlyStop: Int = 0): Boolean {
        if (remCells.isEmpty()) {
            possibleSolutions.add(sudoku.copy())
            return true
        }
        var canBeSolved = false
        val targetCell = getMostConstrainedCell(sudoku, remCells)!!
        for (value in VALID_VALUES) {
            if (earlyStop > 0 && possibleSolutions.size >= earlyStop) {
                return true
            }
            sudoku.set(targetCell.first, targetCell.second, value)
            if (validator.validate(sudoku).isValid()) {
                canBeSolved = canBeSolved || backtraceStep(sudoku.copy(), remCells.filter {
                    cell -> targetCell != cell
                })
            }
        }
        return canBeSolved
    }

    private fun getMostConstrainedCell(sudoku: Sudoku, domain: List<Pair<Int, Int>>): Pair<Int, Int>? {
        return domain.maxByOrNull { cell ->
            val row = cell.first
            val col = cell.second
            val innerConstraints = sudoku.getInnerSquare(row / 3, col / 3).flatten().filter {
                    value -> value != NO_VALUE
            }.size
            val rowConstraints = (0 until 9).map { sudoku.get(row, it) }.filter {
                    value -> value != NO_VALUE
            }.size
            val colConstraints = sudoku.getColumn(col).filter { value -> value != NO_VALUE }.size
            innerConstraints + rowConstraints + colConstraints
        }
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
        const val N_INIT_CELLS = 12
    }

    class UnreachableDifficultyException() : Exception("Unreachable difficulty")
}
