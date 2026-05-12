package com.example.swapsense.ui.DrawingImage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import kotlin.math.min

@SuppressLint("ClickableViewAccessibility")
class DrawingImageView(context: Context, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    private val path = Path()
    private val paint = Paint().apply {
        // TODO: Initialize paint with red color, 10f strokeWidth, STROKE style, anti-alias enabled
        color = Color.RED
        strokeWidth = 10f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    // TODO: Initialize with appropriate values
    private var drawCanvas: Canvas? = null
    private var bitmap: Bitmap? = null
    private var bitmapScale = 1f
    private var offsetX = 0f
    private var offsetY = 0f

    init {
        setOnTouchListener { _, event ->
            // Convert screen touch to bitmap coordinates
            // TODO: Convert event.x and event.y to bitmap coordinates using offset and scale
            val x = (event.x - offsetX) / bitmapScale
            val y = (event.y - offsetY) / bitmapScale

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // TODO: Start new path at (x, y)
                    path.moveTo(x, y)
                }
                MotionEvent.ACTION_MOVE -> {
                    // TODO: Extend path to (x, y) and draw on canvas
                    path.lineTo(x, y)
                    drawCanvas?.drawPath(path, paint)
                    // TODO: Invalidate the view to trigger redraw
                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    // TODO: Finalize drawing, draw path and reset path
                    drawCanvas?.drawPath(path, paint)
                    path.reset()
                    invalidate()
                }
            }
            true
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let { bmp ->
            // TODO: Compute bitmapScale and offsets to center image in view
            bitmapScale = min(
                width.toFloat() / bmp.width.toFloat(),
                height.toFloat() / bmp.height.toFloat()
            )
            offsetX = (width - bmp.width * bitmapScale) / 2f
            offsetY = (height - bmp.height * bitmapScale) / 2f

            canvas.save()
            // TODO: Translate and scale canvas based on computed values
            canvas.translate(offsetX, offsetY)
            canvas.scale(bitmapScale, bitmapScale)

            // TODO: Draw the base bitmap
            canvas.drawBitmap(bmp, 0f, 0f, null)

            // TODO: Draw the modified bitmap with paths
            canvas.drawPath(path, paint)

            canvas.restore()
        }
    }

    override fun setImageBitmap(bmp: Bitmap?) {
        super.setImageBitmap(bmp)
        bmp?.let {
            // TODO: Make a mutable copy of the bitmap and create a canvas for drawing
            bitmap = it.copy(Bitmap.Config.ARGB_8888, true)
            drawCanvas = Canvas(bitmap!!)
        }
        // TODO: Handle invalidating
        invalidate()
    }

    fun getBitmapWithDrawing(): Bitmap? {
        // TODO: Return bitmap with drawing
        return bitmap
    }

    fun setBrushColor(color: Int) {
        // TODO: Set paint color and refresh view
        paint.color = color
        invalidate()
    }
}