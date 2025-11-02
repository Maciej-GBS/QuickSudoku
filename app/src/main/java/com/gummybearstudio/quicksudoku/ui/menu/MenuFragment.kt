package com.gummybearstudio.quicksudoku.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.gummybearstudio.quicksudoku.R

class MenuFragment : Fragment() {

    private val observers: MutableList<IMenuObserver> = mutableListOf()

    companion object {
        fun newInstance() = MenuFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflatedView = inflater.inflate(R.layout.fragment_menu, container, false)

        if (savedInstanceState == null) {
            inflatedView.findViewById<Button>(R.id.buttonNew).setOnClickListener {
                observers.forEach { it.onNewGame() }
            }
            inflatedView.findViewById<Button>(R.id.buttonResume).setOnClickListener {
                observers.forEach { it.onResumeGame() }
            }
            inflatedView.findViewById<Button>(R.id.buttonExit).setOnClickListener {
                observers.forEach { it.onExit() }
            }
        }

        return inflatedView
    }

    fun subscribe(observer: IMenuObserver) {
        observers.add(observer)
    }

}