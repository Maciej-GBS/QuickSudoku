package com.gummybearstudio.quicksudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.gummybearstudio.quicksudoku.ui.board.BoardFragment
import com.gummybearstudio.quicksudoku.ui.board.IGameControls

class MainActivity : AppCompatActivity() {
    private lateinit var controls: IGameControls

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            val board = BoardFragment.newInstance()
            controls = board
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, board)
                .commitNow()
        }

        val bottomScroll = findViewById<LinearLayout>(R.id.bottomScroll)
        bottomScroll.addView(createButton("New Game") { controls.onStartNewGame() })
        bottomScroll.addView(createButton("Clear") { controls.onKeyPressed(0) })
        (1 until 10).forEach { intValue ->
            bottomScroll.addView(
                createButton(intValue.toString()) { controls.onKeyPressed(intValue) })
        }
    }

    private fun createButton(caption: String, onClick: View.OnClickListener): Button {
        return Button(this).apply {
            text = caption
            setOnClickListener(onClick)
        }
    }
}