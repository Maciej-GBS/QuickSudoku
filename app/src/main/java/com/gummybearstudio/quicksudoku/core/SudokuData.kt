package com.gummybearstudio.quicksudoku.core

import kotlinx.serialization.Serializable

@Serializable
data class SudokuData(
    val mask: List<List<Boolean>>,
    val board: List<List<Int>>
)
