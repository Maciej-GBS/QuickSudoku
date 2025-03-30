package com.gummybearstudio.quicksudoku.core

class Coord(row: Int, col: Int) {
    private val _row = row
    private val _col = col

    fun toInt(): Int {
        return _row * 9 + _col
    }
}

fun Int.toCoord(): Coord {
    return Coord(this / 9, this % 9)
}
