package com.gummybearstudio.quicksudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gummybearstudio.quicksudoku.ui.board.BoardFragment

class MainActivity : AppCompatActivity() {
    private lateinit var board: BoardFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        board = BoardFragment.newInstance()
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, board)
                .commitNow()
        }
    }
}