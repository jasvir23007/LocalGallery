package com.jasvir.localgalleryapp.ui.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.jasvir.localgalleryapp.R

/**
 * @author Jasvir
 */

// https://stackoverflow.com/a/18781747/5577679
class DragRectView : View {
    private var mRectPaint: Paint? = null
    private var mStartX = 0
    private var mStartY = 0
    private var mEndX = 0
    private var mEndY = 0
    private var mDrawRect = false
    private var mTextPaint: TextPaint? = null
    private var mCallback: OnUpCallback? = null
    private var mCanvas: Canvas? = null

    interface OnUpCallback {
        fun onRectFinished(rect: Rect?, coors: IntArray)
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        init()
    }

    /**
     * Sets callback for up
     *
     * @param callback [OnUpCallback]
     */
    fun setOnUpCallback(callback: OnUpCallback?) {
        mCallback = callback
    }

    /**
     * Inits internal data
     */
    private fun init() {
        mRectPaint = Paint()
        mRectPaint!!.color = ContextCompat.getColor(context, R.color.frame)
        mRectPaint!!.style = Paint.Style.STROKE
        mRectPaint!!.strokeWidth = 5f // TODO: should take from resources
        mTextPaint = TextPaint()
        mTextPaint!!.color = ContextCompat.getColor(context, R.color.frame)
        mTextPaint!!.textSize = 20f
    }

    fun drawEdges(endX: Int, endY: Int) {
        mStartX = 0
        mStartY = 0
        mEndX = endX
        mEndY = endY
        if(!mDrawRect) mDrawRect = true
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawRect = false
                mStartX = event.x.toInt()
                mStartY = event.y.toInt()
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x.toInt()
                val y = event.y.toInt()
                if (!mDrawRect || Math.abs(x - mEndX) > 5 || Math.abs(y - mEndY) > 5) {
                    mEndX = x
                    mEndY = y
                    invalidate()
                }
                mDrawRect = true
            }
            MotionEvent.ACTION_UP -> {
                if (mCallback != null) {
                    mCallback!!.onRectFinished(
                        Rect(
                            Math.min(mStartX, mEndX), Math.min(mStartY, mEndY),
                            Math.max(mEndX, mStartX), Math.max(mStartY, mEndY)
                        ),
                        intArrayOf(mStartX, mEndX, mStartY, mEndY)
                    )
                }
                invalidate()
            }
            else -> {
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mDrawRect) {
            canvas.drawRect(
                Math.min(mStartX, mEndX).toFloat(),
                Math.min(mStartY, mEndY).toFloat(),
                Math.max(mEndX, mStartX).toFloat(),
                Math.max(mEndY, mStartY).toFloat(),
                mRectPaint!!
            )
//            canvas.drawText(
//                "  (" + Math.abs(mStartX - mEndX) + ", " + Math.abs(mStartY - mEndY) + ")",
//                Math.max(mEndX, mStartX).toFloat(),
//                Math.max(mEndY, mStartY).toFloat(),
//                mTextPaint!!
//            )
            mCanvas = canvas
        }
    }
}