package com.heiligbasil.bagpipeschantermusicnotespractice

import android.annotation.SuppressLint
import android.os.*
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
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
                val randomInt = Random(System.currentTimeMillis()).nextInt(0, 9)
                binding.staffView.anim(Notes.values().get(randomInt), noteInterval)
                if (binding.checkboxNoteNames.isChecked) binding.textviewWrittenNote.text =
                    Notes.values().get(randomInt).note
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