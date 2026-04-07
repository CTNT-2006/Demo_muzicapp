package com.example.demo_muzicapp.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

class WaveformView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnProgressChangedListener {
        fun onProgressChanged(progress: Float, fromUser: Boolean)
    }

    private var onProgressChangedListener: OnProgressChangedListener? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#42A5F5") // Light Blue
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
    }

    private var progress = 0f
    private val bars = 40
    private val barHeights = FloatArray(bars)
    private var randomSeed = 0L

    init {
        generateBars()
    }

    fun setOnProgressChangedListener(listener: OnProgressChangedListener) {
        this.onProgressChangedListener = listener
    }

    private fun generateBars() {
        val random = Random(randomSeed)
        for (i in 0 until bars) {
            barHeights[i] = random.nextFloat() * 0.8f + 0.2f
        }
    }

    fun setSeed(seed: Long) {
        if (this.randomSeed != seed) {
            this.randomSeed = seed
            generateBars()
            invalidate()
        }
    }

    fun setProgress(value: Float) {
        progress = value
        invalidate()
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                performClick()
                val newProgress = (event.x / width).coerceIn(0f, 1f)
                progress = newProgress
                onProgressChangedListener?.onProgressChanged(newProgress, true)
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val newProgress = (event.x / width).coerceIn(0f, 1f)
                progress = newProgress
                onProgressChangedListener?.onProgressChanged(newProgress, true)
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()
        if (width == 0f || height == 0f) return

        val spacing = width / bars
        val paintWidth = spacing * 0.6f
        paint.strokeWidth = paintWidth
        progressPaint.strokeWidth = paintWidth

        for (i in 0 until bars) {
            val x = i * spacing + spacing / 2
            val barHeight = barHeights[i] * height
            val startY = (height - barHeight) / 2
            val stopY = startY + barHeight

            val p = i.toFloat() / bars
            if (p < progress) {
                canvas.drawLine(x, startY, x, stopY, progressPaint)
            } else {
                canvas.drawLine(x, startY, x, stopY, paint)
            }
        }
    }
}