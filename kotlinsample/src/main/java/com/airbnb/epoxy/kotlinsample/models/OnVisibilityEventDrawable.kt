package com.airbnb.epoxy.kotlinsample.models

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

/**
 * Drawable for sample app that draw the current visibility state :
 * - circle #1 : visible
 * - circle #2 : focused
 * - circle #3 : full impression
 * - rectangle : visibility percentage
 */
class OnVisibilityEventDrawable(context: Context) : Drawable() {

    private val density = context.resources.displayMetrics.density
    private val diameter = density * 12
    private val padding = density * 2
    private val paint = Paint().apply {
        color = Color.LTGRAY
        isAntiAlias = true
        strokeWidth = density
    }

    init {
        setBounds(0, 0, padding.toInt() * 4 + diameter.toInt() * 3, diameter.toInt())
    }

    var visible = false
        set(value) {
            field = value
            invalidateSelf()
        }

    var focusedVisible = false
        set(value) {
            field = value
            invalidateSelf()
        }

    var fullImpression = false
        set(value) {
            field = value
            invalidateSelf()
        }

    var percent = 0.0f
        set(value) {
            field = value
            invalidateSelf()
        }

    fun reset() {
        visible = false
        focusedVisible = false
        fullImpression = false
        percent = 0.0f
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {

        val y = diameter / 2
        var x = diameter / 2 + padding

        paint.style = if (visible) Paint.Style.FILL_AND_STROKE else Paint.Style.STROKE
        canvas.drawCircle(x, y, diameter / 2, paint)

        x += diameter + padding
        paint.style = if (focusedVisible) Paint.Style.FILL_AND_STROKE else Paint.Style.STROKE
        canvas.drawCircle(x, y, diameter / 2, paint)

        x += diameter + padding
        paint.style = if (fullImpression) Paint.Style.FILL_AND_STROKE else Paint.Style.STROKE
        canvas.drawCircle(x, y, diameter / 2, paint)

        paint.style = Paint.Style.STROKE
        canvas.drawRect(
            0.0f,
            0.0f,
            bounds.width() * percent / 100,
            bounds.height().toFloat(),
            paint
        )
    }

    override fun setAlpha(alpha: Int) = Unit

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit
}