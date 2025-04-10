package com.gummybearstudio.quicksudoku.core

class Sudoku() {
    private var mask = Array(9) { BooleanArray(9) { false } }
    private var board = Array(9) { IntArray(9) { NO_VALUE } }

    private constructor(srcMask: Array<BooleanArray>, srcBoard: Array<IntArray>) : this() {
        mask = Array(9) { srcMask[it].clone() }
        board = Array(9) { srcBoard[it].clone() }
    }

    fun copy(): Sudoku {
        return Sudoku(mask, board)
    }

    fun setMask(row: Int, col: Int) {
        mask[row][col] = true
    }

    fun getMask(row: Int, col: Int): Boolean {
        return mask[row][col]
    }

    fun set(row: Int, col: Int, value: Int) {
        if (mask[row][col]) {
            return
        }
        else if (value == NO_VALUE || value in VALID_VALUES) {
            board[row][col] = value
        }
    }

    fun get(row: Int, col: Int): Int {
        return board[row][col]
    }

    fun getColumn(c: Int): IntArray {
        return board.map { it[c] }.toIntArray()
    }

    fun getInnerSquare(squareRow: Int, squareCol: Int): List<List<Int>> {
        val startRow = (squareRow * 3)
        val startCol = (squareCol * 3)
        return board.drop(startRow).take(3).map {
            it.drop(startCol).take(3)
        }
    }

    fun reset() {
        board.zip(mask).forEach {
            for (i in 0 until it.first.size) {
                if (!it.second[i]) it.first[i] = NO_VALUE
            }
        }
    }

    companion object {
        val VALID_VALUES: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val CELLS: List<Pair<Int, Int>> = (0 until 9).flatMap {
            row -> (0 until 9).map { col -> Pair(row, col) }
        }
        const val NO_VALUE = 0
    }
}
