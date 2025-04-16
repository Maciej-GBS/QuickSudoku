package com.gummybearstudio.quicksudoku

import com.gummybearstudio.quicksudoku.ui.board.BoardHelper.flatEncode
import com.gummybearstudio.quicksudoku.ui.board.BoardHelper.flatDecode

import org.junit.Test
import org.junit.Assert.*

class BoardUnitTest {
    @Test
    fun flatEncodeDecodeTest() {
        val cell0 = Pair(0, 0)
        val cell1 = Pair(1, 1)
        val cell2 = Pair(7, 5)
        assertEquals(cell0, cell0.flatEncode().flatDecode())
        assertEquals(cell1, cell1.flatEncode().flatDecode())
        println(cell2.flatEncode())
        assertEquals(cell2, cell2.flatEncode().flatDecode())
        assertEquals(0, cell0.flatEncode())
        assertEquals(4, cell1.flatEncode())
        assertEquals(9 * 7 + 3 + 2, cell2.flatEncode())
        /*
        0 1 2 3 4 5
        1
        2
        3 - -
        4 - -
        5 - -
        6 x x 0 1 2
        7 1 2 3 4 5
         */
    }
}