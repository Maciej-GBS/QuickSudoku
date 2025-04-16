package com.gummybearstudio.quicksudoku.ui.board

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CellTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private var originalWidth: Int? = null
    private var originalHeight: Int? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        originalWidth = this.measuredWidth
        originalHeight = this.measuredHeight
        val size = listOf(this.measuredWidth, this.measuredHeight).max()
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        val dx = (originalWidth?.let { (measuredWidth - it) / 2f } ?: 0f)
        val dy = (originalHeight?.let { (measuredHeight - it) / 2f } ?: 0f)
        canvas.translate(dx, dy)
        super.onDraw(canvas)
    }
}