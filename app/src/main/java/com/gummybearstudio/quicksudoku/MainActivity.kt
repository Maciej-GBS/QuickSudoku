package com.gummybearstudio.quicksudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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