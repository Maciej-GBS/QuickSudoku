package com.gummybearstudio.quicksudoku.core

import com.gummybearstudio.quicksudoku.core.Sudoku.Companion.CELLS
import com.gummybearstudio.quicksudoku.core.Sudoku.Companion.NO_VALUE
import com.gummybearstudio.quicksudoku.core.Sudoku.Companion.VALID_VALUES

typealias CellXY = Pair<Int, Int>

class SudokuCreator(private val difficulty: Int) {
    private val validator = SudokuValidator()
    private val possibleSolutions: MutableList<Sudoku> = mutableListOf()

    fun create(row: Int, col: Int, value: Int): Sudoku {
        val sudoku = Sudoku().apply {
            set(row, col, value)
            setMask(row, col)
        }
        initialize(sudoku)
        return backtrace(sudoku)
    }

    private fun backtrace(sudoku: Sudoku): Sudoku {
        // Find a complete valid board
        val getEmptyCells =  { testSudoku: Sudoku ->
            CELLS.shuffled().filter { cell ->
                testSudoku.get(cell.first, cell.second) == NO_VALUE
            }
        }
        possibleSolutions.clear()
        if (!backtraceStep(sudoku, getEmptyCells(sudoku), 1)) {
            throw BadInitException()
        }
        val solution = possibleSolutions.first()
        // Clear cells until difficulty is reached
        val hiddenCells: MutableSet<CellXY> = mutableSetOf()
        var maskedSudoku: Sudoku? = null
        var iterations = 0
        do {
            if (++iterations > UNREACHABLE_ITERATIONS) throw UnreachableDifficultyException(CELLS.size - hiddenCells.size)
            val cell = if (maskedSudoku != null) getMostConstrainedCell(maskedSudoku, CELLS.subtract(hiddenCells))!! else CELLS.random()
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
                              remCells: Collection<CellXY>,
                              earlyStop: Int = 0): Boolean {
        if (remCells.isEmpty()) {
            possibleSolutions.add(sudoku.copy())
            return true
        }
        var canBeSolved = false
        val targetCell = getMostConstrainedCell(sudoku, remCells)!!
        val reducedRemCells = remCells.filter { cell -> targetCell != cell }
        for (value in VALID_VALUES) {
            if (earlyStop > 0 && possibleSolutions.size >= earlyStop) {
                return canBeSolved
            }
            sudoku.set(targetCell.first, targetCell.second, value)
            if (validator.validate(sudoku).isValid()) {
                canBeSolved = canBeSolved || backtraceStep(sudoku.copy(), reducedRemCells, earlyStop)
            }
        }
        return canBeSolved
    }

    private fun getMostConstrainedCell(sudoku: Sudoku, domain: Collection<CellXY>): CellXY? {
        if (domain.isEmpty()) return null
        return domain.maxByOrNull { cell ->
            val row = cell.first
            val col = cell.second
            val innerConstraints = sudoku.getInnerSquare(row / 3, col / 3)
                .flatten().filter({ value -> value != NO_VALUE }).size
            val rowConstraints = (0 until 9).map { sudoku.get(row, it) }
                .filter({ value -> value != NO_VALUE }).size
            val colConstraints =
                sudoku.getColumn(col).filter({ value -> value != NO_VALUE }).size
            innerConstraints + rowConstraints + colConstraints
        }
    }

    private fun initialize(sudoku: Sudoku) {
        var initCells = INIT_CELLS
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

    companion object {
        private const val INIT_CELLS = 2
        private const val UNREACHABLE_ITERATIONS = 500
        const val EASY_DIFFICULTY = 80
        const val HARD_DIFFICULTY = 1
    }


    class UnreachableDifficultyException(level: Int) : Exception("Unreachable difficulty, reached $level")
    class BadInitException() : Exception("Badly initialized sudoku")
}
