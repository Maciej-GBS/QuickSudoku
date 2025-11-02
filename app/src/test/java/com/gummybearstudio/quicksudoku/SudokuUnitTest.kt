package com.gummybearstudio.quicksudoku

import com.gummybearstudio.quicksudoku.core.Sudoku
import com.gummybearstudio.quicksudoku.core.SudokuCreator
import com.gummybearstudio.quicksudoku.core.SudokuValidator
import com.gummybearstudio.quicksudoku.core.ValidationResult

import org.junit.Test
import org.junit.Assert.*
import kotlin.system.measureTimeMillis

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

        assertEquals(result1, result1)
        assertNotEquals(result1, result2)
        assertNotEquals(result1, result3)

        assertEquals(result2, (result1 and result2))
        assertEquals(result3, (result1 and result3))
        assertEquals(result4, (result2 and result4))

        val expectedResult = ValidationResult(ValidationResult.EResult.INVALID, listOf(
            Pair(1, 1),
            Pair(1, 7),
            Pair(2, 2)
        ))
        assertEquals(expectedResult, (result3 and result4))
    }

    @Test
    fun copySudokuTest() {
        val sudoku = Sudoku().apply {
            set(1, 1, 3)
            set(4, 2, 2)
            set(7, 5, 1)
            setMask(4, 2)
        }
        val copySudoku = sudoku.copy()

        Sudoku.Companion.CELLS.forEach { cell ->
            assertEquals(sudoku.getMask(cell.first, cell.second), copySudoku.getMask(cell.first, cell.second))
            assertEquals(sudoku.get(cell.first, cell.second), copySudoku.get(cell.first, cell.second))
        }

        copySudoku.set(2, 2, 9)
        sudoku.set(3, 3, 9)
        assertNotEquals(sudoku.get(2, 2), copySudoku.get(2, 2))
        assertNotEquals(sudoku.get(3, 3), copySudoku.get(3, 3))
    }

    @Test
    fun resetSudokuTest() {
        val sudoku = Sudoku().apply {
            set(1, 1, 3)
            set(4, 2, 2)
            set(7, 5, 1)
            setMask(4, 2)
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
        println("Validation result: ${result.result}")
        assertTrue(result.isValid())
        assertTrue(result.isCompleted())
    }

    @Test(timeout = 10000)
    fun successfulCreateSudokuTest() {
        val validator = SudokuValidator()
        val creator = SudokuCreator(SudokuCreator.EASY_DIFFICULTY)
        val sudoku = creator.create(3, 3, 9)
        printSudoku(sudoku)
        assertEquals(
            validator.validate(sudoku), ValidationResult(
                ValidationResult.EResult.VALID_INCOMPLETE, listOf()
            )
        )
    }

    @Test(timeout = 20000)
    fun perfCreateSudokuTest() {
        var assertedOnce = false
        val validator = SudokuValidator()
        val creator = SudokuCreator(30)
        try {
            creator.create(6, 6, 1)
        } catch (_: Exception) {}
        val executionTime = measureTimeMillis {
            for (i in 0 until 10) {
                try {
                    val sudoku = creator.create(3, 3, 9)
                    assertEquals(
                        validator.validate(sudoku), ValidationResult(
                            ValidationResult.EResult.VALID_INCOMPLETE, listOf()
                        )
                    )
                    assertedOnce = true
                } catch (_: Exception) {}
            }
        }
        println("(perfCreateSudokuTest) Execution time: $executionTime ms")
        assertTrue(assertedOnce)
    }

    @Test(expected = SudokuCreator.UnreachableDifficultyException::class, timeout = 10000)
    fun failedCreateSudokuTest() {
        SudokuCreator(SudokuCreator.HARD_DIFFICULTY).create(1, 1, 1)
    }

    private fun printSudoku(sudoku: Sudoku) {
        Sudoku.Companion.CELLS.forEach { cell ->
            if (cell.second == 0) println()
            print("${sudoku.get(cell.first, cell.second)} ")
        }
        println()
    }
}