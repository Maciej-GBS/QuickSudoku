package com.gummybearstudio.quicksudoku.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gummybearstudio.quicksudoku.core.Sudoku
import com.gummybearstudio.quicksudoku.core.SudokuCreator
import com.gummybearstudio.quicksudoku.core.SudokuValidator
import com.gummybearstudio.quicksudoku.core.ValidationResult
import com.gummybearstudio.quicksudoku.ui.board.BoardHelper.flatDecode
import com.gummybearstudio.quicksudoku.ui.board.BoardHelper.flatEncode

class BoardViewModel : ViewModel() {

    enum class EGameState {
        INITIAL,
        NEW_GAME,
        ONGOING,
        FINISHED
    }

    class InvalidGameStateException(what: String, state: EGameState) :
        Exception("$what not allowed in game state $state")

    private val _state: MutableLiveData<EGameState> = MutableLiveData(EGameState.INITIAL)
    private val _validFlags: MutableLiveData<List<Boolean>> = MutableLiveData()
    private val _cellValues: MutableLiveData<List<Int>> = MutableLiveData()
    private val _selectedCell: MutableLiveData<Pair<Int, Int>> = MutableLiveData(Pair(0, 0))

    val state: LiveData<EGameState> get() = _state
    val validFlags: LiveData<List<Boolean>> get() = _validFlags
    val cellValues: LiveData<List<Int>> get() = _cellValues
    val selectedCell: LiveData<Pair<Int, Int>> get() = _selectedCell

    private val validator = SudokuValidator()
    private var sudoku: Sudoku? = null

    fun newGame() {
        sudoku = null
        _state.value = EGameState.NEW_GAME
    }

    fun startGame(initValue: Int, difficulty: Int) {
        if (_state.value != EGameState.NEW_GAME || _selectedCell.value == null) {
            throw InvalidGameStateException("startGame", _state.value!!)
        }
        val cell = _selectedCell.value!!
        sudoku = SudokuCreator(difficulty).create(cell.first, cell.second, initValue)
        if (sudoku == null) return // failed to generate sudoku
        _state.value = EGameState.ONGOING
    }

    fun updateCell(value: Int) {
        if (sudoku == null) {
            throw InvalidGameStateException("updateCell", _state.value!!)
        }
        val cell = _selectedCell.value!!
        if (sudoku!!.set(cell.first, cell.second, value)) {
            val valResult = validator.validate(sudoku!!)
            markInvalidCells(valResult)
            refreshLiveBoard()
            if (valResult.isCompleted()) {
                _state.value = EGameState.FINISHED
            }
        }
    }

    fun select(row: Int, col: Int) {
        _selectedCell.value = Pair(row, col)
    }

    private fun markInvalidCells(validationResult: ValidationResult) {
        val mutableFlags: MutableList<Boolean> = MutableList(Sudoku.CELLS.size) { true }
        if (!validationResult.isValid()) {
            validationResult.conflictingCells.forEach {
                mutableFlags[it.flatEncode()] = false
            }
        }
        _validFlags.value = mutableFlags.toList()
    }

    private fun refreshLiveBoard() {
        _cellValues.value = (0 until Sudoku.CELLS.size).map {
            val cell = it.flatDecode()
            sudoku!!.get(cell.first, cell.second)
        }
    }
}