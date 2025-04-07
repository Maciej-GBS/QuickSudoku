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
    fun successfulCompleteSudokuTest() {
        val validator = SudokuValidator()
        val creator = SudokuCreator(0)
        val sudoku = creator.create(3, 3, 9)!!

        Sudoku.Companion.CELLS.forEach { cell ->
            if (cell.second == 0) System.out.println()
            System.out.print("${sudoku.get(cell.first, cell.second)} ")
        }
        System.out.println()

        assertTrue(validator.validate(sudoku).isCompleted())
    }
}