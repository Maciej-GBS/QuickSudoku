package com.gummybearstudio.quicksudoku.core

import android.util.Log

class Sudoku() {
    private var mask = Array(9) { BooleanArray(9) { false } }
    private var board = Array(9) { IntArray(9) { 0 } }

    fun copy(): Sudoku {
        val copySudoku = Sudoku()
        this.mask = Array(9) { mask[it].clone() }
        this.board = Array(9) { board[it].clone() }
        return copySudoku
    }

    fun setMask(row: Int, col: Int) {
        mask[row][col] = true
    }

    fun set(row: Int, col: Int, value: Int) {
        if (mask[row][col]) {
            return
        }
        if (value == 0 || value in VALID_VALUES) {
            board[row][col] = value
        }
    }

    fun get(row: Int, col: Int): Int {
        return board[row][col]
    }

    fun getInnerSquare(squareRow: Int, squareCol: Int): List<List<Int>> {
        val startRow = (squareRow * 3)
        val startCol = (squareCol * 3)
        return board.drop(startRow).take(3).map {
            it.drop(startCol).take(3)
        }
    }

    fun Array<IntArray>.getColumn(c: Int): IntArray {
        return this.map { it[c] }.toIntArray()
    }

    companion object {
        val VALID_VALUES: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val CELLS: List<Pair<Int, Int>> = (0 until 9).flatMap {
                row -> (0 until 9).map { col -> Pair(row, col) }
        }
    }

    // ---

    fun solve(): Sudoku? {
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
            if (validate().isCompleted()) {
                return this
            }
        }
        Log.e("Sudoku", "No solution found")
        return null
    }

    fun validate(): ValidationResult {
        return validateRows() and validateCols() and validateInnerSquares()
    }

    private fun validateInnerSquares(): ValidationResult {
        var result: ValidationResult.EResult = ValidationResult.EResult.VALID
        val conflictingCells: MutableList<Pair<Int, Int>> = mutableListOf()
        for (outerRow in 0 until 3) {
            for (outerCol in 0 until 3) {
                val square = getInnerSquare(outerRow, outerCol).flatten()
                square.forEachIndexed { index, value ->
                    if (value == 0) {
                        if (result < ValidationResult.EResult.INVALID) result = ValidationResult.EResult.VALID_INCOMPLETE
                    }
                    else {
                        for (offset in (index + 1) until square.size) {
                            if (value == square[offset]) {
                                result = ValidationResult.EResult.INVALID_INNER_SQUARE_DUPLICATE
                                conflictingCells.add(Pair(outerRow * 3 + index / 3, outerCol * 3 + index % 3))
                                conflictingCells.add(Pair(outerRow * 3 + offset / 3, outerCol * 3 + offset % 3))
                            }
                        }
                    }
                }
            }
        }
        return ValidationResult(result, conflictingCells)
    }

    private fun validateRows(): ValidationResult {
        var result: ValidationResult.EResult = ValidationResult.EResult.VALID
        val conflictingCells: MutableList<Pair<Int, Int>> = mutableListOf()
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (board[row][col] == 0) {
                    if (result < ValidationResult.EResult.INVALID) result = ValidationResult.EResult.VALID_INCOMPLETE
                    continue
                }
                for (i in (col + 1) until 9) {
                    if (board[row][i] == board[row][col]) {
                        result = ValidationResult.EResult.INVALID_ROW_DUPLICATE
                        conflictingCells.add(Pair(row, col))
                        conflictingCells.add(Pair(row, i))
                    }
                }
            }
        }
        return ValidationResult(result, conflictingCells)
    }

    private fun validateCols(): ValidationResult {
        var result: ValidationResult.EResult = ValidationResult.EResult.VALID
        val conflictingCells: MutableList<Pair<Int, Int>> = mutableListOf()
        for (col in 0 until 9) {
            val column = board.getColumn(col)
            for (row in 0 until 9) {
                if (column[row] == 0) {
                    if (result < ValidationResult.EResult.INVALID) result = ValidationResult.EResult.VALID_INCOMPLETE
                    continue
                }
                for (i in (row + 1) until 9) {
                    if (column[i] == column[row]) {
                        result = ValidationResult.EResult.INVALID_COL_DUPLICATE
                        conflictingCells.add(Pair(row, col))
                        conflictingCells.add(Pair(i, col))
                    }
                }
            }
        }
        return ValidationResult(result, conflictingCells)
    }
}
