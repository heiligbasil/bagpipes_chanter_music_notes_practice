package com.heiligbasil.bagpipeschantermusicnotespractice

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.*
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.allViews
import androidx.core.view.children
import com.google.android.material.slider.LabelFormatter
import com.heiligbasil.bagpipeschantermusicnotespractice.databinding.ActivityFullscreenBinding
import kotlin.random.Random

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var fullscreenContent: StaffView
    private val hideHandler = Handler(Looper.getMainLooper())
    private var countDownTimer: CountDownTimer? = null
    private var sessionType = SessionType.RANDOM
    private var upcomingNotesList = arrayListOf<Any>()

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        binding.constraintLayoutSettings.visibility = View.VISIBLE
        binding.sliderDuration.labelBehavior = LabelFormatter.LABEL_VISIBLE
        binding.sliderPersist.labelBehavior = LabelFormatter.LABEL_VISIBLE
        binding.constraintLayoutPracticing.visibility = View.GONE
    }

    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide()
            }
            MotionEvent.ACTION_UP ->
                view.performClick()
            else -> {
            }
        }
        false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isFullscreen = true

        setupSelectionOfSessionTypes()

        // Set up the user interaction to manually show or hide the system UI.
        fullscreenContent = binding.staffView
        fullscreenContent.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        binding.buttonBegin.setOnTouchListener(delayHideTouchListener)
        binding.sliderDuration.setLabelFormatter {
            resources.getQuantityString(
                R.plurals.plural_minutes,
                it.toInt(),
                it.toInt()
            )
        }
        binding.sliderPersist.setLabelFormatter {
            resources.getQuantityString(
                R.plurals.plural_seconds,
                it.toInt(),
                it.toInt()
            )
        }
    }

    private fun setupSelectionOfSessionTypes() {
        SessionType.values().forEach { sessionTypeEnum ->
            val viewToAdd = TextView(this).apply {
                this.text = sessionTypeEnum.displayText
                this.tag = sessionTypeEnum
                this.setOnClickListener { textView ->
                    binding.linearLayoutSessionType.allViews.forEach {
                        it.setBackgroundColor(getColor(R.color.light_blue_900))
                    }
                    textView.setBackgroundColor(Color.RED)
                    sessionType = textView.tag as SessionType
                }
            }
            binding.linearLayoutSessionType.addView(viewToAdd)
        }
        binding.linearLayoutSessionType.children.first().performClick()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
//        delayedHide(100)
    }

    private fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        binding.sliderDuration.labelBehavior = LabelFormatter.LABEL_GONE
        binding.sliderPersist.labelBehavior = LabelFormatter.LABEL_GONE
        Handler(mainLooper).postDelayed(Runnable {
            binding.constraintLayoutSettings.visibility = View.GONE
        }, 50L)
        binding.constraintLayoutPracticing.visibility = View.VISIBLE
        isFullscreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
        val practiceDuration = binding.sliderDuration.value.toSeconds() * 60L
        val noteInterval = binding.sliderPersist.value.toSeconds()
        countDownTimer = object : CountDownTimer(practiceDuration, noteInterval) {
            override fun onTick(millisUntilFinished: Long) {
                binding.textviewRemainingTime.text = millisUntilFinished.millisToTime()
                val noteToShow = getNextNote()
                binding.staffView.anim(noteToShow, noteInterval)
                if (binding.checkboxNoteNames.isChecked) {
                    if (noteToShow is Notes) {
                        binding.textviewWrittenNote.text = noteToShow.visual
                    } else {
                        binding.textviewWrittenNote.text = ""
                    }
                }
            }

            override fun onFinish() {
                show()
            }
        }.start()
    }

    private fun show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        isFullscreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
        countDownTimer?.cancel()
        upcomingNotesList.clear()
    }

    private fun delayedHide() {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, 250L)
    }

    private fun Float.toSeconds() = this.toLong() * 1000L

    private fun Long.millisToTime(): String {
        val totalSeconds = this / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        return if (hours > 0) {
            "%d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d".format(minutes, seconds)
        }
    }

    private fun getNextNote(): Any {
        return when (sessionType) {
            SessionType.RANDOM, SessionType.RANDOM_NO_DUPES -> {
                if (upcomingNotesList.isEmpty()) {
                    var lastRandomNote = -1
                    repeat(50) {
                        val randomNote = Random(System.nanoTime()).nextInt(0, 9)
                        if (sessionType == SessionType.RANDOM_NO_DUPES && randomNote == lastRandomNote) {
                            return@repeat
                        }
                        upcomingNotesList.add(Notes.values()[randomNote])
                        lastRandomNote = randomNote
                    }
                }
                val noteToReturn = upcomingNotesList[0]
                upcomingNotesList.removeAt(0)
                return noteToReturn
            }
            SessionType.SCALE_UP -> {
                if (upcomingNotesList.isEmpty()) {
                    Notes.values().forEach { note ->
                        upcomingNotesList.add(note)
                    }
                    upcomingNotesList.add(Symbols.END_BAR)
                    upcomingNotesList.add(Symbols.PAUSE)
                }
                val noteToReturn = upcomingNotesList[0]
                upcomingNotesList.removeAt(0)
                noteToReturn
            }
            SessionType.SCALE_DOWN -> {
                if (upcomingNotesList.isEmpty()) {
                    Notes.values().reversedArray().forEach { note ->
                        upcomingNotesList.add(note)
                    }
                    upcomingNotesList.add(Symbols.END_BAR)
                    upcomingNotesList.add(Symbols.PAUSE)
                }
                val noteToReturn = upcomingNotesList[0]
                upcomingNotesList.removeAt(0)
                noteToReturn
            }
            SessionType.SCALE_BOTH -> {
                if (upcomingNotesList.isEmpty()) {
                    Notes.values().forEach { note ->
                        upcomingNotesList.add(note)
                    }
                    Notes.values().reversedArray().forEach { note ->
                        upcomingNotesList.add(note)
                    }
                    upcomingNotesList.add(Symbols.END_BAR)
                    upcomingNotesList.add(Symbols.PAUSE)
                }
                val noteToReturn = upcomingNotesList[0]
                upcomingNotesList.removeAt(0)
                noteToReturn
            }
            SessionType.STAGGERED_UP -> {
                if (upcomingNotesList.isEmpty()) {
                    Notes.values().forEach { baseNote ->
                        Notes.values().forEach { alternatingNote ->
                            if (baseNote != alternatingNote) {
                                upcomingNotesList.add(baseNote)
                                upcomingNotesList.add(alternatingNote)
                            }
                        }
                    }
                    upcomingNotesList.add(Symbols.END_BAR)
                    upcomingNotesList.add(Symbols.PAUSE)
                }
                val noteToReturn = upcomingNotesList[0]
                upcomingNotesList.removeAt(0)
                noteToReturn
            }
            SessionType.STAGGERED_DOWN -> {
                if (upcomingNotesList.isEmpty()) {
                    Notes.values().reversedArray().forEach { baseNote ->
                        Notes.values().reversedArray().forEach { alternatingNote ->
                            if (baseNote != alternatingNote) {
                                upcomingNotesList.add(baseNote)
                                upcomingNotesList.add(alternatingNote)
                            }
                        }
                    }
                    upcomingNotesList.add(Symbols.END_BAR)
                    upcomingNotesList.add(Symbols.PAUSE)
                }
                val noteToReturn = upcomingNotesList[0]
                upcomingNotesList.removeAt(0)
                noteToReturn
            }
            SessionType.STAGGERED_BOTH -> {
                if (upcomingNotesList.isEmpty()) {
                    Notes.values().forEach { baseNote ->
                        Notes.values().forEach { alternatingNote ->
                            if (baseNote != alternatingNote) {
                                upcomingNotesList.add(baseNote)
                                upcomingNotesList.add(alternatingNote)
                            }
                        }
                    }
                    Notes.values().reversedArray().forEach { baseNote ->
                        Notes.values().reversedArray().forEach { alternatingNote ->
                            if (baseNote != alternatingNote) {
                                upcomingNotesList.add(baseNote)
                                upcomingNotesList.add(alternatingNote)
                            }
                        }
                    }
                    upcomingNotesList.add(Symbols.END_BAR)
                    upcomingNotesList.add(Symbols.PAUSE)
                }
                val noteToReturn = upcomingNotesList[0]
                upcomingNotesList.removeAt(0)
                noteToReturn
            }
            SessionType.PRESET_1 -> {
                if (upcomingNotesList.isEmpty()) {
                    "GABGABBAGBAG|_".forEach { noteString ->
                        upcomingNotesList.add(convertNoteStringToEnum(noteString))
                    }
                }
                val noteToReturn = upcomingNotesList[0]
                upcomingNotesList.removeAt(0)
                noteToReturn
            }
            SessionType.PRESET_2 -> {
                if (upcomingNotesList.isEmpty()) {
                    "BCBCBCBCBCBC|_".forEach { noteString ->
                        upcomingNotesList.add(convertNoteStringToEnum(noteString))
                    }
                }
                val noteToReturn = upcomingNotesList[0]
                upcomingNotesList.removeAt(0)
                noteToReturn
            }
            SessionType.PRESET_3 -> {
                if (upcomingNotesList.isEmpty()) {
                    "GABCBAGGABCBAG|_".forEach { noteString ->
                        upcomingNotesList.add(convertNoteStringToEnum(noteString))
                    }
                }
                val noteToReturn = upcomingNotesList[0]
                upcomingNotesList.removeAt(0)
                noteToReturn
            }
            SessionType.PRESET_4 -> {
                if (upcomingNotesList.isEmpty()) {
                    "CDCDCBACDCDACD|_".forEach { noteString ->
                        upcomingNotesList.add(convertNoteStringToEnum(noteString))
                    }
                }
                val noteToReturn = upcomingNotesList[0]
                upcomingNotesList.removeAt(0)
                noteToReturn
            }
            SessionType.PRESET_5 -> {
                if (upcomingNotesList.isEmpty()) {
                    "GABCDCBAGABCDCD|_".forEach { noteString ->
                        upcomingNotesList.add(convertNoteStringToEnum(noteString))
                    }
                }
                val noteToReturn = upcomingNotesList[0]
                upcomingNotesList.removeAt(0)
                noteToReturn
            }
            SessionType.PRESET_6 -> {
                if (upcomingNotesList.isEmpty()) {
                    "ADCDBGAADBDACD|_".forEach { noteString ->
                        upcomingNotesList.add(convertNoteStringToEnum(noteString))
                    }
                }
                val noteToReturn = upcomingNotesList[0]
                upcomingNotesList.removeAt(0)
                noteToReturn
            }
        }
    }

    private fun convertNoteStringToEnum(noteString: Char) =
        Notes.values().find { note -> note.notation == noteString.toString() }
            ?: Symbols.values().find { symbol -> symbol.notation == noteString.toString() }
            ?: Notes.HIGH_A

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }
}