package com.gummybearstudio.quicksudoku.ui.board

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.gridlayout.widget.GridLayout
import android.widget.TextView

import com.gummybearstudio.quicksudoku.R

class BoardFragment : Fragment() {
    companion object {
        fun newInstance() = BoardFragment()
    }

    private val viewModel: BoardViewModel by viewModels()
    private var cellTextViews: MutableList<CellTextView> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflatedView = inflater.inflate(R.layout.fragment_board, container, false)

        val mainGrid = inflatedView.findViewById<GridLayout>(R.id.mainGridLayout)
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val subGrid = GridLayout(requireContext())
                buildGridLayout(subGrid)
                val params = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(i)
                    columnSpec = GridLayout.spec(j)
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                    setGravity(Gravity.CENTER)
                }
                mainGrid.addView(subGrid, params)
            }
        }

        return inflatedView
    }

    private fun buildGridLayout(grid: GridLayout) {
        grid.columnCount = 3
        grid.rowCount = 3
        grid.setPadding(5, 5, 5, 5)
        grid.setBackgroundColor(resources.getColor(R.color.black))
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val textView = CellTextView(requireContext())
                textView.text = "X"
                textView.textSize = 24f
                textView.setBackgroundColor(resources.getColor(R.color.purple_100))

                val params = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(i)
                    columnSpec = GridLayout.spec(j)
                    setGravity(Gravity.CENTER)
                    //width = ViewGroup.LayoutParams.WRAP_CONTENT
                    //height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                grid.addView(textView, params)

                cellTextViews.add(textView)
            }
        }
    }

}