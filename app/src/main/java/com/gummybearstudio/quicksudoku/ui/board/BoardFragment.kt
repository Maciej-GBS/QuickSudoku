package com.gummybearstudio.quicksudoku.ui.board

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
import com.gummybearstudio.quicksudoku.ui.board.BoardHelper.flatDecode
import com.gummybearstudio.quicksudoku.ui.board.BoardHelper.flatEncode

class BoardFragment : Fragment() {
    companion object {
        fun newInstance() = BoardFragment()

        const val INNER_PADDING = 4
    }

    private val viewModel: BoardViewModel by viewModels()
    private var cellTextViews: MutableList<CellTextView> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.state.observe(this) { state ->
            Toast.makeText(requireContext(), state.toString(), Toast.LENGTH_SHORT).show()
        }
        viewModel.selectedCell.observe(this) { cell ->
            cellTextViews.forEach { textView ->
                textView.setBackgroundColor(resources.getColor(R.color.purple_100))
            }
            cellTextViews[cell.flatEncode()].setBackgroundColor(resources.getColor(R.color.purple_200))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflatedView = inflater.inflate(R.layout.fragment_board, container, false)

        val mainGrid = inflatedView.findViewById<GridLayout>(R.id.mainGridLayout)
        var nGrid = 0
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val subGrid = GridLayout(requireContext())
                buildGridLayout(subGrid, nGrid++)
                mainGrid.addView(subGrid, createGridLayoutParams(i, j))
            }
        }

        return inflatedView
    }

    private fun buildGridLayout(grid: GridLayout, gridId: Int) {
        grid.columnCount = 3
        grid.rowCount = 3
        grid.setPadding(INNER_PADDING, INNER_PADDING, INNER_PADDING, INNER_PADDING)
        grid.setBackgroundColor(resources.getColor(R.color.purple_900))
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val textView = CellTextView(requireContext())
                textView.text = " "
                textView.textSize = 26f
                textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                textView.setBackgroundColor(resources.getColor(R.color.purple_100))
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