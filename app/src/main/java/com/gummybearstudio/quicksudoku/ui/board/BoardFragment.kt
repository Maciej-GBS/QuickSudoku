package com.gummybearstudio.quicksudoku.ui.board

import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.gridlayout.widget.GridLayout

import com.gummybearstudio.quicksudoku.R
import com.gummybearstudio.quicksudoku.core.Sudoku
import com.gummybearstudio.quicksudoku.ui.board.BoardHelper.flatDecode
import com.gummybearstudio.quicksudoku.ui.board.BoardHelper.flatEncode

class BoardFragment : Fragment(), IGameControls {

    companion object {
        fun newInstance() = BoardFragment()
        const val INNER_PADDING = 4

        private const val PREFS_KEY = "sudokuPrefs"
        private const val SAVE_GAME_KEY = "sudokuSaveGame"
    }

    private val viewModel: BoardViewModel by viewModels()
    private var cellTextViews: MutableList<CellTextView> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.state.observe(this) { state ->
            // TODO handle UI state modes
            Toast.makeText(requireContext(), state.toString(), Toast.LENGTH_SHORT).show()
        }
        viewModel.selectedCell.observe(this) { cell -> callbackCellColorChanged(cell) }
        viewModel.validFlags.observe(this) { flags -> callbackValidColorChanged(flags) }
        viewModel.maskFlags.observe(this) { flags -> callbackMaskColorChanged(flags) }
        viewModel.cellValues.observe(this, ::callbackCellValues)
    }

    override fun onStartNewGame() {
        viewModel.newGame()
    }

    override fun onKeyPressed(id: Int) {
        when (viewModel.state.value) {
            EGameState.NEW_GAME ->
                viewModel.startGame(id, 50) // TODO prompt user for difficulty
            EGameState.ONGOING ->
                viewModel.updateCell(id)
            else -> {}
        }
    }

    override fun onLoad() {
        val prefs = requireActivity()
            .getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        val savedJson = prefs.getString(SAVE_GAME_KEY, null)
        if (!savedJson.isNullOrEmpty()) {
            val deserializedSaveGame = decodeSaveGameFromJson(savedJson)
            viewModel.loadSaveGame(deserializedSaveGame)
        }
    }

    override fun onSave() {
        val jsonSaveGame = viewModel.createSaveGame()?.encodeToJson()
        if (jsonSaveGame != null) {
            val prefs = requireActivity()
                .getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
            with(prefs.edit()) {
                putString(SAVE_GAME_KEY, jsonSaveGame)
                apply()
            }
        }
    }

    private fun resetColor() {
        cellTextViews.forEach { textView ->
            textView.setBackgroundColor(resources.getColor(R.color.white))
        }
        viewModel.maskFlags.value?.apply {
            callbackMaskColorChanged(this)
        }
    }

    private fun callbackValidColorChanged(flags: List<Boolean>) {
        resetColor()
        flags.zip(cellTextViews).forEach {
            if (!it.first) it.second.setBackgroundColor(resources.getColor(R.color.error))
        }
    }

    private fun callbackMaskColorChanged(flags: List<Boolean>) {
        flags.zip(cellTextViews).forEach {
            if (it.first) it.second.setBackgroundColor(resources.getColor(R.color.mask))
        }
    }

    private fun callbackCellColorChanged(cell: Pair<Int, Int>) {
        resetColor()
        cell.apply {
            cellTextViews[this.flatEncode()]
                .setBackgroundColor(resources.getColor(R.color.selection))
        }
    }

    private fun callbackCellValues(values: List<Int>) {
        cellTextViews.zip(values).forEach {
            when (it.second) {
                Sudoku.NO_VALUE -> it.first.text = " "
                else -> it.first.text = it.second.toString()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflatedView = inflater.inflate(R.layout.fragment_board, container, false)

        if (savedInstanceState == null) {
            val mainGrid = inflatedView.findViewById<GridLayout>(R.id.mainGridLayout)
            var nGrid = 0
            for (i in 0 until 3) {
                for (j in 0 until 3) {
                    val subGrid = GridLayout(requireContext())
                    buildGridLayout(subGrid, nGrid++)
                    mainGrid.addView(subGrid, createGridLayoutParams(i, j))
                }
            }
        }

        return inflatedView
    }

    private fun buildGridLayout(grid: GridLayout, gridId: Int) {
        grid.columnCount = 3
        grid.rowCount = 3
        grid.setPadding(INNER_PADDING, INNER_PADDING, INNER_PADDING, INNER_PADDING)
        grid.setBackgroundColor(resources.getColor(R.color.border))
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val textView = CellTextView(requireContext())
                textView.text = " "
                textView.textSize = 26f // TODO should be calculated or use auto-size?
                textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                textView.setBackgroundColor(resources.getColor(R.color.white))
                textView.setOnClickListener {
                    val startCell = (gridId * 9).flatDecode()
                    viewModel.select(startCell.first + i, startCell.second + j)
                }
                grid.addView(textView, createGridLayoutParams(i, j))
                cellTextViews.add(textView)
            }
        }
    }

    private fun createGridLayoutParams(row: Int, col: Int): GridLayout.LayoutParams {
        return GridLayout.LayoutParams().apply {
            rowSpec = GridLayout.spec(row)
            columnSpec = GridLayout.spec(col)
            setGravity(Gravity.CENTER)
        }
    }
}