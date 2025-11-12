package com.gummybearstudio.quicksudoku.ui.board

import android.view.View
import android.widget.Button
import com.gummybearstudio.quicksudoku.R

class ButtonBuilder(
    private val context: android.content.Context) {

    fun build(caption: String, onClick: View.OnClickListener): Button {
        return Button(context).apply {
            minWidth = 10
            minimumWidth = 10
            text = caption
            textSize = 20f
            backgroundTintList = context.resources.getColorStateList(R.color.btn_color_selector)
            setOnClickListener(onClick)
        }
    }

}