package com.gummybearstudio.quicksudoku

import com.gummybearstudio.quicksudoku.core.Sudoku
import com.gummybearstudio.quicksudoku.core.SudokuCreator
import com.gummybearstudio.quicksudoku.core.SudokuValidator
import com.gummybearstudio.quicksudoku.core.ValidationResult
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SudokuUnitTest {
    @Test
    fun validationResultAndOpTest() {
        val result1 = ValidationResult(ValidationResult.EResult.VALID, listOf())
        val result2 = ValidationResult(ValidationResult.EResult.VALID_INCOMPLETE, listOf())
        val result3 = ValidationResult(ValidationResult.EResult.INVALID_ROW_DUPLICATE, listOf(
            Pair(1, 1),
            Pair(1, 7)
        ))
        val result4 = ValidationResult(ValidationResult.EResult.INVALID_INNER_SQUARE_DUPLICATE, listOf(
            Pair(1, 1),
            Pair(2, 2)
        ))

        assertTrue(result1.equals(result1))
        assertFalse(result1.equals(result2))
        assertFalse(result1.equals(result3))

        assertTrue(result2.equals(result1 and result2))
        assertTrue(result3.equals(result1 and result3))
        assertTrue(result4.equals(result2 and result4))

        val expectedResult = ValidationResult(ValidationResult.EResult.INVALID, listOf(
            Pair(1, 1),
            Pair(1, 7),
            Pair(2, 2)
        ))
        assertTrue(expectedResult.equals(result3 and result4))
    }

    @Test
    fun copySudokuTest() {
        val sudoku = Sudoku().apply {
            set(1, 1, 3)
            set(4, 2, 2)
            set(7, 5, 1)
            setMask(4, 2, true)
        }
        val copySudoku = sudoku.copy()

        Sudoku.Companion.CELLS.forEach { cell ->
            assertEquals(sudoku.getMask(cell.first, cell.second), copySudoku.getMask(cell.first, cell.second))
            assertEquals(sudoku.get(cell.first, cell.second), copySudoku.get(cell.first, cell.second))
        }
    }

    @Test
    fun resetSudokuTest() {
        val sudoku = Sudoku().apply {
            set(1, 1, 3)
            set(4, 2, 2)
            set(7, 5, 1)
            setMask(4, 2, true)
            reset()
        }
        assertEquals(sudoku.get(1, 1), Sudoku.Companion.NO_VALUE)
        assertEquals(sudoku.get(4, 2), 2)
    }

    @Test
    fun successfulSudokuValidationTest() {
        val sudoku = Sudoku().apply {
            for (row in 0 until 9) {
                val offset = (row % 3) * 3 + (row / 3)
                val rowValues = Sudoku.Companion.VALID_VALUES.drop(offset).plus(Sudoku.Companion.VALID_VALUES.take(offset))
                for (col in 0 until 9) {
                    set(row, col, rowValues[col])
                }
            }
        }
        val result = SudokuValidator().validate(sudoku)
        printSudoku(sudoku)
        System.out.println("Validation result: ${result.result}")
        assertTrue(result.isValid())
        assertTrue(result.isCompleted())
    }

    @Test
    fun successfulCreateSudokuTest() {
        val validator = SudokuValidator()
        val creator = SudokuCreator(0)
        val sudoku = creator.create(3, 3, 9)!!
        printSudoku(sudoku)
        //assertTrue(validator.validate(sudoku).isCompleted())
    }

    private fun printSudoku(sudoku: Sudoku) {
        Sudoku.Companion.CELLS.forEach { cell ->
            if (cell.second == 0) System.out.println()
            System.out.print("${sudoku.get(cell.first, cell.second)} ")
        }
        System.out.println()
    }
}