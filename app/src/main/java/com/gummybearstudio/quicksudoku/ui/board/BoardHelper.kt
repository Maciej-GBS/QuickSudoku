package com.gummybearstudio.quicksudoku.ui.board

object BoardHelper {
    fun Pair<Int, Int>.flatEncode(): Int {
        val nSquare: Int = (this.first / 3) * 3 + (this.second / 3)
        return nSquare * 9 + (this.first % 3) * 3 + (this.second % 3)
    }

    fun Int.flatDecode(): Pair<Int, Int> {
        val nSquare: Int = this / 9
        val squareOffset = this % 9
        return Pair((nSquare / 3) * 3 + squareOffset / 3, (nSquare % 3) * 3 + squareOffset % 3)
    }
}