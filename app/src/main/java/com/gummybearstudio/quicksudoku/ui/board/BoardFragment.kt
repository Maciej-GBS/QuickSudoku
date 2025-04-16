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
    private var cellTextViews: MutableList<TextView> = mutableListOf()

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
                buildGridLayout(subGrid, mainGrid.width / 9)
                val params = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(i)
                    columnSpec = GridLayout.spec(j)
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                mainGrid.addView(subGrid, params)
            }
        }

        return inflatedView
    }

    private fun buildGridLayout(grid: GridLayout, w: Int) {
        grid.columnCount = 3
        grid.rowCount = 3
        grid.setPadding(5, 5, 5, 5)
        grid.setBackgroundColor(resources.getColor(R.color.black))
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val textView = TextView(requireContext())
                textView.text = " "
                textView.gravity = Gravity.CENTER
                textView.textSize = w.toFloat()
                textView.setBackgroundColor(resources.getColor(R.color.white))
                textView.width = w
                textView.height = w

                val params = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(i)
                    columnSpec = GridLayout.spec(j)
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                grid.addView(textView, params)

                cellTextViews.add(textView)
            }
        }
    }

}