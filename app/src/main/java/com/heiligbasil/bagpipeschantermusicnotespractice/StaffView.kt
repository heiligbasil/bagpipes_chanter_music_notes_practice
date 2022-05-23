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
    var needsLedgerLine = false
    val bk =
        Paint().apply {
            color = Color.BLACK;strokeWidth = 10f;textSize = 425f;textScaleX = 2.0f;isAntiAlias =
            true
        }
    val bk2 = Paint().apply {
        color = Color.BLACK;strokeWidth = 10f;textSize = 400f;textScaleX = 0.75f;isAntiAlias = true
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
    val y1 =
        Paint().apply { color = Color.YELLOW;strokeWidth = 5f;textSize = 420f;isAntiAlias = true; }
    var showingNote: Notes = Notes.LOW_G

    fun anim(noteToShow: Notes, noteInterval: Long) {
        showingNote = noteToShow
        if (noteToShow == Notes.HIGH_A) needsLedgerLine = true
        var startingValue = 850f
        var endingValue = 300f
        my_y = 550f - (50 * noteToShow.ordinal).toFloat()
        if (noteToShow.flipped) {
            startingValue = 50f
            endingValue = 600f
            my_y = 100f + (50 * noteToShow.ordinal).toFloat()
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
        drawQuarterNote(canvas)
    }

    private fun drawStaffScale(canvas: Canvas?) {
        canvas?.drawText(CHAR_STAFF_5, 20f.dp2px(), 230f.dp2px(), bk)
        canvas?.drawText(CHAR_TREBLE_CLEF, 25f.dp2px(), 230f.dp2px(), bk2)
        canvas?.drawText(CHAR_BAR_START, 100f.dp2px(), 230f.dp2px(), bk3)
    }

    private fun drawQuarterNote(canvas: Canvas?) {
        if (showingNote.flipped) {
            canvas?.save()
            canvas?.rotate(-180f, 205f.dp2px(), 100f.dp2px())
            drawQuarterNo(canvas)
            if (showingNote == Notes.HIGH_A)
                canvas?.drawText(CHAR_STAFF_1, my_x - 30f, my_y + 150f, bk5)
            canvas?.restore()
        } else {
            drawQuarterNo(canvas)
        }
    }

    private fun drawQuarterNo(canvas: Canvas?) {
        canvas?.drawText(CHAR_QUARTER_NOTE, my_x, my_y, bk4)
        if (needsLedgerLine) canvas?.drawText(CHAR_QUARTER_NOTE, my_x, my_y, bk4)
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
