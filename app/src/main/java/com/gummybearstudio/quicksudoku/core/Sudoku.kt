package com.gummybearstudio.quicksudoku.core

class Sudoku {
    private val mask = Array(9) { BooleanArray(9) }
    private val board = Array(9) { IntArray(9) }

    fun validate(): Boolean {
        return validateRows() && validateCols()
    }

    private fun validateRows(): Boolean {
        for (row in 0 until 9) {
            val nonZeroRow = board[row].filter { it != 0 }
            val distinctRow = nonZeroRow.distinct()
            if (distinctRow.count() != nonZeroRow.count())
                throw DuplicateRowException(row)
            if (!distinctRow.containsAll(VALID_VALUES))
                return false
        }
        return true
    }

    private fun validateCols(): Boolean {
        for (col in 0 until 9) {
            val nonZeroCol = board.getColumn(col).filter { it != 0 }
            val distinctCol = nonZeroCol.distinct()
            if (distinctCol.count() != nonZeroCol.count())
                throw DuplicateColException(col)
            if (!distinctCol.containsAll(VALID_VALUES))
                return false
        }
        return true
    }

    private fun Array<IntArray>.getColumn(c: Int): IntArray {
        return this.map { it[c] }.toIntArray()
    }

    class DuplicateRowException(row: Int)
        : Exception("Duplicate value in row $row") {
        val row = row
    }

    class DuplicateColException(col: Int)
        : Exception("Duplicate value in col $col") {
        val col = col
    }

    companion object {
        val VALID_VALUES: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    }
}
