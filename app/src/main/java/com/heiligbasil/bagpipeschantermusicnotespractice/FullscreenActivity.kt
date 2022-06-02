package com.heiligbasil.bagpipeschantermusicnotespractice

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.*
import android.text.Html
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
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
    private var countDownTimer = ExtendedCountDownTimer()
    private var sessionType = SessionType.RANDOM
    private var upcomingNotesList = arrayListOf<Any>()
    private var upcomingNotesListIndex = 0
    private var timerDuration = 10_000L
    private var noteInterval = 1_000L

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
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

    private val hideRunnable = Runnable { goFullscreen() }

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
        binding.buttonRewind.setOnClickListener {
            countDownTimer.rewindByInterval()
            upcomingNotesListIndex = if (binding.staffView.percentageAnimationComplete < 10) {
                upcomingNotesListIndex - 2
            } else {
                upcomingNotesListIndex - 1
            }
            if (upcomingNotesListIndex < 0) {
                upcomingNotesListIndex = 0
            }
            timerDuration = countDownTimer.remainingTime
            startTimer()
            togglePlayPauseButtonText()
            updateHighlightedNoteOnDeck()
        }
        binding.buttonPlayPause.setOnClickListener {
            togglePlayPauseButtonText()
            if (countDownTimer.isRunning) {
                countDownTimer.pause()
            } else {
                timerDuration = countDownTimer.remainingTime
                startTimer()
            }
        }
        binding.buttonStop.setOnClickListener {
            toggle()
        }
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
            goFullscreen()
        } else {
            hideFullscreen()
        }
    }

    private fun goFullscreen() {
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
        timerDuration = binding.sliderDuration.value.toSeconds() * 60L
        noteInterval = binding.sliderPersist.value.toSeconds()
        binding.staffView.labelNoteName = binding.checkboxNoteName.isChecked
        binding.staffView.labelLedgerLines = binding.checkboxLedgerLineNames.isChecked
        togglePlayPauseButtonText()
        startTimer()
    }

    private fun hideFullscreen() {
        // Show the system bar
        supportActionBar?.show()

        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(
                window,
                window.decorView
            ).show(WindowInsetsCompat.Type.systemBars())
        }
        isFullscreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
        countDownTimer.stop()
        upcomingNotesList.clear()
    }

    private fun delayedHide() {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, 250L)
    }

    private fun togglePlayPauseButtonText() {
        val hasPauseText = binding.buttonPlayPause.text.equals(getString(R.string.pause))
        val newText = if (countDownTimer.isRunning && hasPauseText) {
            getString(R.string.play)
        } else {
            getString(R.string.pause)
        }
        binding.buttonPlayPause.text = newText
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

    private fun startTimer() {
        countDownTimer = object : ExtendedCountDownTimer(timerDuration, noteInterval) {
            override fun onTick(duration: Long) {
                super.onTick(duration)
                binding.textviewRemainingTime.text = duration.millisToTime()
                val noteToShow = getNextNote()
                binding.staffView.anim(noteToShow, noteInterval)
            }

            override fun onFinish() {
                super.onFinish()
                hideFullscreen()
            }
        }.start() as ExtendedCountDownTimer
    }

    private fun getNextNote(): Any {
        if (upcomingNotesList.isNotEmpty()) {
            if (upcomingNotesListIndex < upcomingNotesList.size - 1) {
                upcomingNotesListIndex++
            } else {
                upcomingNotesListIndex = 0
            }
        } else {
            when (sessionType) {
                SessionType.RANDOM, SessionType.RANDOM_NO_DUPES -> {
                    var lastRandomNote = -1
                    repeat(150) {
                        val randomNote = Random(System.nanoTime()).nextInt(0, 9)
                        if (sessionType == SessionType.RANDOM_NO_DUPES && randomNote == lastRandomNote) {
                            return@repeat
                        }
                        upcomingNotesList.add(Notes.values()[randomNote])
                        lastRandomNote = randomNote
                    }
                }
                SessionType.SCALE_UP -> {
                    Notes.values().forEach { note ->
                        upcomingNotesList.add(note)
                    }
                }
                SessionType.SCALE_DOWN -> {
                    Notes.values().reversedArray().forEach { note ->
                        upcomingNotesList.add(note)
                    }
                }
                SessionType.SCALE_BOTH -> {
                    Notes.values().forEach { note ->
                        upcomingNotesList.add(note)
                    }
                    Notes.values().reversedArray().forEach { note ->
                        upcomingNotesList.add(note)
                    }
                }
                SessionType.STAGGERED_UP -> {
                    Notes.values().forEach { baseNote ->
                        Notes.values().forEach { alternatingNote ->
                            if (baseNote != alternatingNote) {
                                upcomingNotesList.add(baseNote)
                                upcomingNotesList.add(alternatingNote)
                            }
                        }
                    }
                }
                SessionType.STAGGERED_DOWN -> {
                    Notes.values().reversedArray().forEach { baseNote ->
                        Notes.values().reversedArray().forEach { alternatingNote ->
                            if (baseNote != alternatingNote) {
                                upcomingNotesList.add(baseNote)
                                upcomingNotesList.add(alternatingNote)
                            }
                        }
                    }
                }
                SessionType.STAGGERED_BOTH -> {
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
                }
                else -> {
                    sessionType.notes.forEach { noteString ->
                        upcomingNotesList.add(convertNoteStringToEnum(noteString))
                    }
                }
            }
            upcomingNotesList.add(Symbols.END_BAR)
            upcomingNotesList.add(Symbols.PAUSE)
            upcomingNotesListIndex = 0
        }
        updateHighlightedNoteOnDeck()
        return upcomingNotesList[upcomingNotesListIndex]
    }

    private fun updateHighlightedNoteOnDeck() {
        val notesList = upcomingNotesList.mapNotNull { (it as? Notes)?.notation }.toMutableList()
        var notesIndex = upcomingNotesListIndex
        val currentNote = notesList.getOrNull(notesIndex)
        var encodedNoteLength = 0
        currentNote?.let {
            val noteToEncode = "<font color=#FFD700>$currentNote</font>"
            notesList[notesIndex] = noteToEncode
            encodedNoteLength = noteToEncode.length - 1
        }
        var indexRange = when (notesIndex) {
            in 0..89 -> {
                0..89 + encodedNoteLength
            }
            in 90..179 -> {
                notesIndex -= 90
                90..179 + encodedNoteLength
            }
            in 180..269 -> {
                notesIndex -= 180
                180..269 + encodedNoteLength
            }
            else -> 270..notesList.size
        }
        if (indexRange.last >= notesList.size) {
            indexRange = indexRange.first until notesList.size + encodedNoteLength
        }
        val arrayAsString = notesList.joinToString(separator = "")
        val resizedString = arrayAsString.substring(indexRange)
        val finalString = Html.fromHtml(resizedString, Html.FROM_HTML_MODE_COMPACT)
        binding.textviewNotesOnDeck.text = finalString
    }

    private fun isTextTooWide(newText: String): Boolean {
        val textWidth = binding.textviewNotesOnDeck.paint.measureText(newText)
        return (textWidth / binding.textviewNotesOnDeck.maxLines) >= binding.textviewNotesOnDeck.measuredWidth
    }

    private fun convertNoteStringToEnum(noteString: Char) =
        Notes.values().find { note -> note.notation == noteString.toString() }
            ?: Symbols.values().find { symbol -> symbol.notation == noteString.toString() }
            ?: Notes.HIGH_A

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