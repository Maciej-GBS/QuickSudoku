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
                if (it.second[i]) it.first[i] = 0
            }
        }
    }

    companion object {
        val VALID_VALUES: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val CELLS: List<Pair<Int, Int>> = (0 until 9).flatMap {
            row -> (0 until 9).map { col -> Pair(row, col) }
        }
    }
}
