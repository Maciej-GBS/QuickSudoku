package com.gummybearstudio.quicksudoku.ui.board

import com.gummybearstudio.quicksudoku.core.SudokuData
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SaveGame(
    val state: EGameState,
    val sudoku: SudokuData
)

fun SaveGame.encodeToJson(): String {
    return Json.encodeToString(this)
}

fun decodeSaveGameFromJson(jsonStr: String): SaveGame {
    return Json.decodeFromString<SaveGame>(jsonStr)
}