package com.gummybearstudio.quicksudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gummybearstudio.quicksudoku.ui.board.BoardFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BoardFragment.newInstance())
                .commitNow()
        }
    }
}