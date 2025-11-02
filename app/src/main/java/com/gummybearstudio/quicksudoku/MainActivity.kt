package com.gummybearstudio.quicksudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.gummybearstudio.quicksudoku.ui.board.BoardFragment
import com.gummybearstudio.quicksudoku.ui.board.IGameControls
import com.gummybearstudio.quicksudoku.ui.menu.IMenuObserver
import com.gummybearstudio.quicksudoku.ui.menu.MenuFragment

class MainActivity : AppCompatActivity(), IMenuObserver {
    private lateinit var board: BoardFragment
    private lateinit var menu: MenuFragment
    private lateinit var controls: IGameControls

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            board = BoardFragment.newInstance()
            menu = MenuFragment.newInstance()
            controls = board
            switchToMenu()

            menu.subscribe(this)

            val bottomScroll = findViewById<LinearLayout>(R.id.bottomScroll)
            bottomScroll.addView(createButton("Clear") { controls.onKeyPressed(0) })
            (1 until 10).forEach { intValue ->
                bottomScroll.addView(
                    createButton(intValue.toString()) { controls.onKeyPressed(intValue) })
            }
        }
    }

    override fun onStop() {
        controls.onSave()
        super.onStop()
    }

    override fun onNewGame() {
        switchToBoard()
        controls.onStartNewGame()
    }

    override fun onResumeGame() {
        switchToBoard()
        controls.onLoad()
    }

    override fun onExit() {
        finishAffinity()
    }

    private fun createButton(caption: String, onClick: View.OnClickListener): Button {
        return Button(this).apply {
            text = caption
            setOnClickListener(onClick)
        }
    }

    private fun switchToBoard() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, board)
            .commitNow()
    }

    private fun switchToMenu() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, menu)
            .commitNow()
    }

}