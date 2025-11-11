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

    companion object {
        private const val MAX_START_ATTEMPTS = 3
    }

    class InvalidGameStateException(what: String, state: EGameState) :
        Exception("$what not allowed in game state $state")

    private val _state: MutableLiveData<EGameState> = MutableLiveData(EGameState.INITIAL)
    private val _validFlags: MutableLiveData<List<Boolean>> = MutableLiveData()
    private val _maskFlags: MutableLiveData<List<Boolean>> = MutableLiveData()
    private val _cellValues: MutableLiveData<List<Int>> = MutableLiveData()
    private val _selectedCell: MutableLiveData<Pair<Int, Int>> = MutableLiveData(Pair(0, 0))

    val state: LiveData<EGameState> get() = _state
    val validFlags: LiveData<List<Boolean>> get() = _validFlags
    val maskFlags: LiveData<List<Boolean>> get() = _maskFlags
    val cellValues: LiveData<List<Int>> get() = _cellValues
    val selectedCell: LiveData<Pair<Int, Int>> get() = _selectedCell

    private val validator = SudokuValidator()
    private var sudoku: Sudoku? = null

    fun createSaveGame(): SaveGame? {
        return sudoku?.let { SaveGame(_state.value!!, sudoku!!.toData()) }
    }

    fun loadSaveGame(saveGame: SaveGame) {
        _state.value = saveGame.state
        sudoku = Sudoku(saveGame.sudoku)
        markInvalidCells(validator.validate(sudoku!!))
        markMaskedCells()
        refreshLiveBoard()
    }

    fun newGame() {
        sudoku = null
        markMaskedCells()
        _state.value = EGameState.NEW_GAME
    }

    fun startGame(initValue: Int, difficulty: Int) {
        if (_state.value != EGameState.NEW_GAME || _selectedCell.value == null) {
            throw InvalidGameStateException("startGame", _state.value!!)
        }
        if (initValue == 0) {
            return
        }
        val cell = _selectedCell.value!!
        var d = 0
        while (d < MAX_START_ATTEMPTS && d + difficulty < SudokuCreator.EASY_DIFFICULTY) {
            try {
                sudoku = SudokuCreator(d + difficulty)
                    .create(cell.first, cell.second, initValue)
                markMaskedCells()
                refreshLiveBoard()
                _state.value = EGameState.ONGOING
                return
            }
            catch (_: SudokuCreator.UnreachableDifficultyException) {
                android.util.Log.d(
                    "BoardViewModel",
                    "Difficulty ${d + difficulty} is unreachable")
                d += 1
            }
        }
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

    private fun markMaskedCells() {
        val mutableFlags = MutableList(Sudoku.CELLS.size) { false }
        if (sudoku == null) {
            _maskFlags.value = mutableFlags
            return
        }
        Sudoku.CELLS.forEach {
            mutableFlags[it.flatEncode()] = sudoku!!.getMask(it.first, it.second)
        }
        _maskFlags.value = mutableFlags
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