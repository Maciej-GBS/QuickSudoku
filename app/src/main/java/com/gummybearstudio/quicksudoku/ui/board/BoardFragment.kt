package com.gummybearstudio.quicksudoku.ui.board

import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.gridlayout.widget.GridLayout

import com.gummybearstudio.quicksudoku.R
import com.gummybearstudio.quicksudoku.core.Sudoku
import com.gummybearstudio.quicksudoku.ui.board.BoardHelper.flatEncode

class BoardFragment : Fragment(), IGameControls {

    companion object {
        fun newInstance() = BoardFragment()

        private const val PREFS_KEY = "sudokuPrefs"
        private const val SAVE_GAME_KEY = "sudokuSaveGame"
    }

    private val viewModel: BoardViewModel by viewModels()
    private var cellTextViews: List<CellTextView> = listOf()
    private var headerTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.state.observe(this, ::callbackStateChanged)
            viewModel.selectedCell.observe(this, ::callbackCellColorChanged)
            viewModel.validFlags.observe(this, ::callbackValidColorChanged)
            viewModel.maskFlags.observe(this, ::callbackMaskColorChanged)
            viewModel.cellValues.observe(this, ::callbackCellValues)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflatedView = inflater.inflate(R.layout.fragment_board, container, false)

        if (savedInstanceState == null) {
            val builder = GridBuilder(requireContext(), viewModel)
            val mainGrid = inflatedView.findViewById<GridLayout>(R.id.mainGridLayout)
            cellTextViews = builder.build(mainGrid)

            val btnBuilder = ButtonBuilder(requireContext())
            val bottomScroll = inflatedView.findViewById<LinearLayout>(R.id.bottomScroll)

            bottomScroll.addView(btnBuilder.build("C") { onKeyPressed(0) })
            (1 until 10).forEach { intValue ->
                bottomScroll.addView(
                    btnBuilder.build(intValue.toString()) { onKeyPressed(intValue) })
            }

            headerTextView = inflatedView.findViewById(R.id.headerTextView)
        }

        return inflatedView
    }

    override fun onStartNewGame() {
        viewModel.newGame()
    }

    override fun onKeyPressed(id: Int) {
        when (viewModel.state.value) {
            EGameState.NEW_GAME -> {
                viewModel.startGame(id, 30) // TODO prompt user for difficulty
                if (viewModel.state.value != EGameState.ONGOING) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.sudoku_failure),
                        Toast.LENGTH_SHORT).show()
                }
            }
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
            textView.setBackgroundColor(resources.getColor(R.color.qs_white))
        }
        viewModel.maskFlags.value?.apply {
            callbackMaskColorChanged(this)
        }
    }

    private fun callbackValidColorChanged(flags: List<Boolean>) {
        resetColor()
        flags.zip(cellTextViews).forEach {
            if (!it.first) it.second.setBackgroundColor(resources.getColor(R.color.qs_error))
        }
    }

    private fun callbackMaskColorChanged(flags: List<Boolean>) {
        flags.zip(cellTextViews).forEach {
            if (it.first) it.second.setBackgroundColor(resources.getColor(R.color.qs_mask))
        }
    }

    private fun callbackCellColorChanged(cell: Pair<Int, Int>) {
        resetColor()
        cell.apply {
            cellTextViews[this.flatEncode()]
                .setBackgroundColor(resources.getColor(R.color.qs_selection))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun callbackCellValues(values: List<Int>) {
        cellTextViews.zip(values).forEach {
            when (it.second) {
                Sudoku.NO_VALUE -> it.first.text = " "
                else -> it.first.text = it.second.toString()
            }
        }
    }

    private fun callbackStateChanged(state: EGameState) {
        when (state) {
            EGameState.ONGOING ->
                headerTextView?.text = resources.getString(R.string.state_ongoing)
            EGameState.FINISHED ->
                headerTextView?.text = resources.getString(R.string.state_finished)
            else ->
                headerTextView?.text = resources.getString(R.string.state_welcome)
        }
    }

}