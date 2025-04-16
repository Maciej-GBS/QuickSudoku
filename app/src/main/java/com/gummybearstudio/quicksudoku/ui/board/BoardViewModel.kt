package com.gummybearstudio.quicksudoku.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gummybearstudio.quicksudoku.core.Sudoku
import com.gummybearstudio.quicksudoku.core.SudokuCreator

class BoardViewModel : ViewModel() {
    private var state: EGameState = EGameState.INITIAL
    var sudoku: Sudoku? = null

    fun newGame() {
        if (sudoku != null) {
            sudoku = null
        }
        state = EGameState.NEW_GAME
    }

    private enum class EGameState {
        INITIAL,
        NEW_GAME,
        ONGOING,
        FINISHED
    }
}