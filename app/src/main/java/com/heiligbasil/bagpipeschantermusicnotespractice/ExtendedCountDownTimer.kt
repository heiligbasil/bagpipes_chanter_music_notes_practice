package com.heiligbasil.bagpipeschantermusicnotespractice

import android.os.CountDownTimer

open class ExtendedCountDownTimer(private val duration: Long, private val interval: Long) :
    CountDownTimer(duration, interval) {

    constructor() : this(10_000L, 2_000L)

    private var _remainingTime = 0L
    val remainingTime: Long
        get() = _remainingTime

    private var _isRunning = false
    val isRunning: Boolean
        get() = _isRunning

    override fun onTick(duration: Long) {
        _isRunning = true
        _remainingTime = duration
    }

    override fun onFinish() {
        _isRunning = false
        _remainingTime = 0L
    }

    fun stop() {
        this.cancel()
        _isRunning = false
    }

    fun pause() {
        _remainingTime = duration
        stop()
    }

    fun rewindByInterval() {
        _remainingTime += interval
        pause()
    }

}
