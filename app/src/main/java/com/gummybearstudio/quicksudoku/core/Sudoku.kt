package com.gummybearstudio.quicksudoku.core

class Sudoku {
    private val mask = Array(9) { BooleanArray(9) }
    private val board = Array(9) { IntArray(9) }

    fun set(row: Int, col: Int, value: Int) {
        if (mask[row][col]) {
            return
        }
        if (value in VALID_VALUES) {
            board[row][col] = value
        }
    }

    fun solve() {
        // TODO
    }

    fun validate(): ValidationResult {
        return validateRows() and validateCols() and validateInnerSquares()
    }

    private fun validateInnerSquares(): ValidationResult {
        var result: ValidationResult.EResult = ValidationResult.EResult.VALID
        val conflictingCells: MutableList<Pair<Int, Int>> = mutableListOf()
        // TODO
        return ValidationResult(result, conflictingCells)
    }

    private fun validateRows(): ValidationResult {
        var result: ValidationResult.EResult = ValidationResult.EResult.VALID
        val conflictingCells: MutableList<Pair<Int, Int>> = mutableListOf()
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (board[row][col] == 0) {
                    result = ValidationResult.EResult.VALID_INCOMPLETE
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
                    result = ValidationResult.EResult.VALID_INCOMPLETE
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

    private fun getInnerSquare(squareRow: Int, squareCol: Int): List<List<Int>> {
        val startRow = (squareRow * 3)
        val startCol = (squareCol * 3)
        return board.drop(startRow).take(3).map {
            it.drop(startCol).take(3)
        }
    }

    private fun Array<IntArray>.getColumn(c: Int): IntArray {
        return this.map { it[c] }.toIntArray()
    }

    companion object {
        val VALID_VALUES: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    }
}
