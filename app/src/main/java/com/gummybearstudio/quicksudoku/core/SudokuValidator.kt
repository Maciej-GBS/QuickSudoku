package com.gummybearstudio.quicksudoku.core

class SudokuValidator() {
    fun validate(sudoku: Sudoku): ValidationResult {
        return validateRows(sudoku) and validateCols(sudoku) and validateInnerSquares(sudoku)
    }

    private fun validateInnerSquares(sudoku: Sudoku): ValidationResult {
        var result: ValidationResult.EResult = ValidationResult.EResult.VALID
        val conflictingCells: MutableList<Pair<Int, Int>> = mutableListOf()
        for (outerRow in 0 until 3) {
            for (outerCol in 0 until 3) {
                val square = sudoku.getInnerSquare(outerRow, outerCol).flatten()
                square.forEachIndexed { index, value ->
                    if (value == 0) {
                        if (result < ValidationResult.EResult.INVALID) result = ValidationResult.EResult.VALID_INCOMPLETE
                    }
                    else {
                        for (offset in (index + 1) until square.size) {
                            if (value == square[offset]) {
                                result = ValidationResult.EResult.INVALID_INNER_SQUARE_DUPLICATE
                                conflictingCells.add(Pair(outerRow * 3 + index / 3, outerCol * 3 + index % 3))
                                conflictingCells.add(Pair(outerRow * 3 + offset / 3, outerCol * 3 + offset % 3))
                            }
                        }
                    }
                }
            }
        }
        return ValidationResult(result, conflictingCells)
    }

    private fun validateRows(sudoku: Sudoku): ValidationResult {
        var result: ValidationResult.EResult = ValidationResult.EResult.VALID
        val conflictingCells: MutableList<Pair<Int, Int>> = mutableListOf()
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (sudoku.get(row, col) == 0) {
                    if (result < ValidationResult.EResult.INVALID) result = ValidationResult.EResult.VALID_INCOMPLETE
                    continue
                }
                for (i in (col + 1) until 9) {
                    if (sudoku.get(row, i) == sudoku.get(row, col)) {
                        result = ValidationResult.EResult.INVALID_ROW_DUPLICATE
                        conflictingCells.add(Pair(row, col))
                        conflictingCells.add(Pair(row, i))
                    }
                }
            }
        }
        return ValidationResult(result, conflictingCells)
    }

    private fun validateCols(sudoku: Sudoku): ValidationResult {
        var result: ValidationResult.EResult = ValidationResult.EResult.VALID
        val conflictingCells: MutableList<Pair<Int, Int>> = mutableListOf()
        for (col in 0 until 9) {
            val column = sudoku.getColumn(col)
            for (row in 0 until 9) {
                if (column[row] == 0) {
                    if (result < ValidationResult.EResult.INVALID) result = ValidationResult.EResult.VALID_INCOMPLETE
                    continue
                }
                for (i in (row + 1) until 9) {
                    if (column[i] == column[row]) {
                        result = ValidationResult.EResult.INVALID_COL_DUPLICATE
                        conflictingCells.add(Pair(row, col))
                        conflictingCells.add(Pair(i, col))
                    }
                }
            }
        }
        return ValidationResult(result, conflictingCells)
    }
}