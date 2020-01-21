package com.larmik.fridgealert

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import com.larmik.fridgealert.common.ui.MainActivity

class TestService : Service() {

    var context: Context? = null

    private val serviceBinder: IBinder = RunServiceBinder()
    var isTimerRunning = false
    private var mCountDownTimer: CountDownTimer? = null
    private var mTimeLeftInMillis = START_TIME_IN_MILLIS

    override fun onCreate() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS
        isTimerRunning = false
    }

    fun startTimer() {
        if (mTimeLeftInMillis == 0L) {
            resetTimer()
            startTimer()
        }
        isTimerRunning = true
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
            }

            override fun onFinish() {
                mTimeLeftInMillis = 0
                sendNotification()
                startTimer()
            }
        }.start()

    }

    private fun resetTimer() {
        mCountDownTimer!!.cancel()
        isTimerRunning = false
        mTimeLeftInMillis = START_TIME_IN_MILLIS
    }

    private fun sendNotification() {
        val vibrator = context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
        } else
            vibrator.vibrate(400)
        val notificationManager =
            context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), createNotification(context!!))

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return serviceBinder
    }

    fun foreground() {
        startForeground(NOTIFICATION_ID, createNotification(applicationContext))
    }

    fun background() {
       stopForeground(true)
    }

    private fun createNotification(context: Context): Notification {
        val intent =  Intent(context, MainActivity::class.java);
        val resultPendingIntent =
            PendingIntent. getActivity (
                context,
                0,
                intent,
                PendingIntent. FLAG_CANCEL_CURRENT
            )
        val builder = NotificationCompat.Builder(context, "CHANNEL_ID" )
            .setSmallIcon(R.drawable. ic_launcher_foreground )
            .setContentTitle( "Test" )
            .setContentText( "Hello! This is my first push notification" )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .addAction(R.drawable. ic_launcher_foreground, "Add", resultPendingIntent)
            .setContentIntent(resultPendingIntent)
        return builder.build()
    }

    inner class RunServiceBinder : Binder() {
        val service: TestService
            get() = this@TestService
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val START_TIME_IN_MILLIS: Long = 5000
    }
}