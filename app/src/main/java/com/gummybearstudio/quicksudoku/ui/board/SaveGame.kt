package com.gummybearstudio.quicksudoku.ui.board

import com.gummybearstudio.quicksudoku.core.SudokuData
import com.gummybearstudio.quicksudoku.ui.board.BoardViewModel.EGameState
import kotlinx.serialization.Serializable

@Serializable
data class SaveGame(
    val state: EGameState,
    val sudoku: SudokuData
)