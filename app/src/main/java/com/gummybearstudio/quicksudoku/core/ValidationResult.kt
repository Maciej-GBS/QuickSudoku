package com.gummybearstudio.quicksudoku.core

class ValidationResult(val result: EResult, val conflictingCells: List<Pair<Int, Int>>) {
    enum class EResult {
        VALID,
        VALID_INCOMPLETE,
        INVALID,
        INVALID_ROW_DUPLICATE,
        INVALID_COL_DUPLICATE,
        INVALID_INNER_SQUARE_DUPLICATE
    }

    fun isCompleted(): Boolean {
        return result == EResult.VALID
    }

    fun isValid(): Boolean {
        return result == EResult.VALID || result == EResult.VALID_INCOMPLETE
    }

    infix fun and(other: ValidationResult): ValidationResult {
        val combined = ValidationResult(
            if (!this.isValid() && !other.isValid()) EResult.INVALID else EResult.entries[maxOf(this.result.ordinal, other.result.ordinal)],
            this.conflictingCells.plus(other.conflictingCells).distinct()
        )
        return combined
    }
}
