package com.gummybearstudio.quicksudoku.ui.board

object BoardHelper {
    fun Pair<Int, Int>.flatEncode(): Int {
        return this.first * 9 + this.second
    }

    fun Int.flatDecode(): Pair<Int, Int> {
        return Pair(this / 9, this % 9)
    }
}