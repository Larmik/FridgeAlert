package com.larmik.fridgealert

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
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
    private fun startForeground() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(context ?: applicationContext)
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }

    fun foreground() {
        startForeground()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context): String{
        val chan = NotificationChannel("CHANNEL_ID",
            "channelName", NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return "CHANNEL_ID"
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val START_TIME_IN_MILLIS: Long = 5000
    }
}