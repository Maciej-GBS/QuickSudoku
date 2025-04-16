package com.gummybearstudio.quicksudoku.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gummybearstudio.quicksudoku.core.Sudoku
import com.gummybearstudio.quicksudoku.core.SudokuCreator
import com.gummybearstudio.quicksudoku.core.SudokuValidator

class BoardViewModel : ViewModel() {
    private val _validFlags: MutableLiveData<List<Boolean>> = MutableLiveData()
    private val _cellValues: MutableLiveData<List<Int>> = MutableLiveData()
    val validFlags: LiveData<List<Boolean>> = _validFlags
    val cellValues: LiveData<List<Int>> = _cellValues
    private var state: EGameState = EGameState.INITIAL
    private val validator = SudokuValidator()
    var sudoku: Sudoku? = null

    fun newGame() {
        sudoku = null
        state = EGameState.NEW_GAME
    }

    fun startGame(initCell: Pair<Int,Int>, initValue: Int, difficulty: Int): Boolean {
        if (state != EGameState.NEW_GAME) return false
        sudoku = SudokuCreator(difficulty).create(initCell.first, initCell.second, initValue)
        if (sudoku == null) return false
        state = EGameState.ONGOING
        return true
    }

    fun updateCell(cell: Pair<Int, Int>, value: Int): Boolean {
        if (sudoku == null) return false
        if (!(sudoku!!.set(cell.first, cell.second, value))) return false
        val valResult = validator.validate(sudoku!!)
        if (valResult.isCompleted()) {
            state = EGameState.FINISHED
            return true
        }
        Sudoku.CELLS.forEach {}
        if (!valResult.isValid()) {
            valResult.conflictingCells.forEach {}
        }
        return false
    }

    fun Pair<Int, Int>.flatEncode(): Int {
        return 0
    }

    fun Int.flatDecode(): Pair<Int, Int> {
        return Pair(0, 0)
    }

    private enum class EGameState {
        INITIAL,
        NEW_GAME,
        ONGOING,
        FINISHED
    }
}