package com.example.lotteryprediction.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.lotteryprediction.R

class LotteryBallView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val ballPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.ball_text_size)
    }
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.ball_text_size)
    }
    
    private var number = "01"
    private var isRed = true
    private val rect = RectF()

    fun setNumber(num: Int, isRedBall: Boolean = true) {
        number = "%02d".format(num)
        isRed = isRedBall
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val radius = width.coerceAtMost(height) / 2f
        rect.set(
            width / 2f - radius,
            height / 2f - radius,
            width / 2f + radius,
            height / 2f + radius
        )

        ballPaint.color = if (isRed) {
            ContextCompat.getColor(context, R.color.red_ball)
        } else {
            ContextCompat.getColor(context, R.color.blue_ball)
        }

        canvas.drawCircle(width / 2f, height / 2f, radius, ballPaint)
        
        val yPos = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
        canvas.drawText(number, width / 2f, yPos, textPaint)
    }
}