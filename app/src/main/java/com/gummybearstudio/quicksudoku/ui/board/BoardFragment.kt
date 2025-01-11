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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflatedView = inflater.inflate(R.layout.fragment_board, container, false)

        val mainGrid = inflatedView.findViewById<GridLayout>(R.id.mainGridLayout)

        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val subGrid = GridLayout
            }
        }

        return inflatedView
    }

    private fun buildGridLayout(grid: GridLayout) {
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val textView = TextView(requireContext())
                textView.text = "A"
                textView.gravity = Gravity.CENTER
                textView.textSize = 24f
                val params = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(i)
                    columnSpec = GridLayout.spec(j)
                    width = 0
                    height = 0
                }
                grid.addView(textView, params)
            }
        }
    }

}