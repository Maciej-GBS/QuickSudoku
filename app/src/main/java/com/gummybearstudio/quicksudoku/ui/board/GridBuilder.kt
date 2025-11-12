package com.gummybearstudio.quicksudoku.ui.board

import android.view.View
import androidx.gridlayout.widget.GridLayout
import com.gummybearstudio.quicksudoku.R
import com.gummybearstudio.quicksudoku.ui.board.BoardHelper.flatDecode

class GridBuilder(
    private val context: android.content.Context,
    private val viewModel: BoardViewModel) {

    private val cellTextViews: MutableList<CellTextView> = mutableListOf()

    companion object {
        const val INNER_PADDING = 4
    }

    fun build(root: GridLayout): List<CellTextView> {
        var nGrid = 0
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val subGrid = GridLayout(context)
                buildLayout(subGrid, nGrid++)
                root.addView(subGrid, createGridLayoutParams(i, j))
            }
        }
        return cellTextViews
    }

    private fun buildLayout(grid: GridLayout, gridId: Int) {
        grid.columnCount = 3
        grid.rowCount = 3
        grid.setPadding(
            INNER_PADDING,
            INNER_PADDING,
            INNER_PADDING,
            INNER_PADDING
        )
        grid.setBackgroundColor(context.resources.getColor(R.color.qs_border))
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val textView = CellTextView(context)
                textView.apply {
                    text = " "
                    textSize = 26f
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    setBackgroundColor(context.resources.getColor(R.color.qs_white))
                    setOnClickListener {
                        val startCell = (gridId * 9).flatDecode()
                        viewModel.select(startCell.first + i, startCell.second + j)
                    }
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
            setGravity(_root_ide_package_.android.view.Gravity.CENTER)
        }
    }

}