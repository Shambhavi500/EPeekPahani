package io.sc.eppCordova.ui.lossclaim

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class HorizontalStatusTracker @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    
    var currentStep = 0
        set(value) {
            field = value
            invalidate()
        }

    private val stepCount = 5

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val width = width.toFloat()
        val height = height.toFloat()
        val cy = height / 2f
        
        val radius = 16f
        val padding = 32f
        val stepWidth = (width - 2 * padding) / (stepCount - 1)
        
        // Draw lines
        for (i in 0 until stepCount - 1) {
            val startX = padding + i * stepWidth
            val endX = padding + (i + 1) * stepWidth
            
            linePaint.color = if (i < currentStep) Color.parseColor("#1a6b3a") else Color.parseColor("#E5E7EB")
            canvas.drawLine(startX, cy, endX, cy, linePaint)
        }
        
        // Draw circles
        for (i in 0 until stepCount) {
            val cx = padding + i * stepWidth
            paint.color = if (i <= currentStep) Color.parseColor("#1a6b3a") else Color.parseColor("#E5E7EB")
            canvas.drawCircle(cx, cy, radius, paint)
        }
    }
}
