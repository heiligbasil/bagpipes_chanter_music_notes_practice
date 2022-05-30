package com.heiligbasil.bagpipeschantermusicnotespractice

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.heiligbasil.bagpipeschantermusicnotespractice.Constants.CHAR_BAR_START
import com.heiligbasil.bagpipeschantermusicnotespractice.Constants.CHAR_QUARTER_NOTE
import com.heiligbasil.bagpipeschantermusicnotespractice.Constants.CHAR_STAFF_1
import com.heiligbasil.bagpipeschantermusicnotespractice.Constants.CHAR_STAFF_5
import com.heiligbasil.bagpipeschantermusicnotespractice.Constants.CHAR_TREBLE_CLEF

class StaffView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    var my_x: Float = 0f
    var my_y: Float = 0f
    val bk =
        Paint().apply {
            color = Color.BLACK;strokeWidth = 10f;textSize = 425f;textScaleX = 2.75f;isAntiAlias =
            true
        }
    val bk2 = Paint().apply {
        color = Color.BLACK;strokeWidth = 10f;textSize = 400f;textScaleX = 1f;isAntiAlias = true
    }
    val bk3 = Paint().apply {
        color = Color.BLACK;strokeWidth = 5f;textSize = 420f;textScaleX = 0.5f;isAntiAlias = true
    }
    val bk4 =
        Paint().apply { color = Color.BLACK;strokeWidth = 5f;textSize = 420f;isAntiAlias = true; }
    val bk5 =
        Paint().apply {
            color = Color.BLACK;strokeWidth = 5f;textSize = 420f;textScaleX = 0.5f;isAntiAlias =
            true;
        }
    val y1 = Paint().apply { color = Color.RED;strokeWidth = 5f;textSize = 30f;isAntiAlias = true }
    var showingNote: Any = Notes.LOW_G
    var labelNoteName = false
    var labelLedgerLines = false

    fun anim(noteToShow: Any, noteInterval: Long) {
        showingNote = noteToShow
        var startingValue = 850f
        var endingValue = 300f
        if (noteToShow is Notes) {
            my_y = 550f - (50 * noteToShow.ordinal).toFloat()
            if (noteToShow.flip) {
                startingValue = 50f
                endingValue = 600f
                my_y = 100f + (50 * noteToShow.ordinal).toFloat()
            }
        } else {
            my_y = 230f.dp2px()
        }
        val animator = ValueAnimator.ofFloat(startingValue, endingValue)
        animator.interpolator = LinearInterpolator()
        animator.duration = noteInterval
        animator.addUpdateListener {
            my_x = animator.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawStaffScale(canvas)
        drawNoteOrSymbol(canvas)
    }

    private fun drawStaffScale(canvas: Canvas?) {
        canvas?.drawText(CHAR_STAFF_5, (-25f).dp2px(), 230f.dp2px(), bk)
        canvas?.drawText(CHAR_TREBLE_CLEF, (-5f).dp2px(), 230f.dp2px(), bk2)
        canvas?.drawText(CHAR_BAR_START, 100f.dp2px(), 230f.dp2px(), bk3)
        if (labelLedgerLines) {
            Notes.values().reversedArray().forEachIndexed { index, note ->
                canvas?.drawText(
                    note.notation,
                    106f.dp2px(),
                    (19.5f * (index + 1) + 16.5f).dp2px(),
                    y1
                )
            }
        }
    }

    private fun drawNoteOrSymbol(canvas: Canvas?) {
        if ((showingNote is Notes) && (showingNote as Notes).flip) {
            canvas?.save()
            canvas?.rotate(180f, 205f.dp2px(), 100f.dp2px())
            drawQuarterNo(canvas)
            if (showingNote == Notes.HIGH_A) {
                canvas?.drawText(CHAR_STAFF_1, my_x - 30f, my_y + 150f, bk5)
            }
            canvas?.restore()
            if (labelNoteName) {
                val yValue = when (showingNote) {
                    Notes.HIGH_A -> 100f
                    Notes.HIGH_G -> 150f
                    Notes.F -> 200f
                    Notes.E -> 250f
                    Notes.D -> 300f
                    Notes.C -> 350f
                    Notes.B -> 400f
                    else -> 0f
                }
                canvas?.drawText((showingNote as Notes).visual, my_x * -1 + 960f, yValue, y1)
            }
        } else {
            if (showingNote is Notes) {
                drawQuarterNo(canvas)
            } else {
                drawSymbolNo(canvas)
            }
        }
    }

    private fun drawQuarterNo(canvas: Canvas?) {
        canvas?.drawText(CHAR_QUARTER_NOTE, my_x, my_y, bk4)
        if (labelNoteName && !(showingNote as Notes).flip) {
            canvas?.drawText((showingNote as Notes).visual, my_x + 30f, my_y - 40f, y1)
        }
        if (showingNote == Notes.HIGH_A) {
            canvas?.drawText(CHAR_QUARTER_NOTE, my_x, my_y, bk4)
        }
    }

    private fun drawSymbolNo(canvas: Canvas?) {
        canvas?.drawText((showingNote as Symbols).visual, my_x, my_y, bk3)
    }

    private fun Float.dp2px(): Float {
        val scale = context.resources.displayMetrics.density
        return (this * scale + 0.5f)
    }

    fun Float.px2dp(): Float {
        val scale = context.resources.displayMetrics.density
        return (this / scale + 0.5f)
    }
}
