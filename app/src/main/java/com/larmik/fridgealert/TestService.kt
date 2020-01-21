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
import java.util.*

class TestService : Service() {

    var context: Context? = null

    private val serviceBinder: IBinder = RunServiceBinder()
    var isTimerRunning = false
    private var mCountDownTimer: CountDownTimer? = null
    private var mTimeLeftInMillis: Long = 0

    override fun onCreate() {
        isTimerRunning = false
    }

    fun startTimer(isFirstLaunch : Boolean) {
        if (isFirstLaunch)
            mTimeLeftInMillis = getRemaingMillis()
        isTimerRunning = true
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
            }

            override fun onFinish() {
                val currentMillis = System.currentTimeMillis()
                mTimeLeftInMillis = (currentMillis + TEST) - currentMillis
                sendNotification()
                startTimer(false)
            }
        }.start()
        if (mTimeLeftInMillis == 0L) {
            resetTimer()
            startTimer(false)
        }

    }

    private fun resetTimer() {
        mCountDownTimer!!.cancel()
        isTimerRunning = false
        mTimeLeftInMillis = getRemaingMillis()
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

    private fun getRemaingMillis() : Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 22)
        calendar.set(Calendar.MINUTE, 14)
        return calendar.timeInMillis - System.currentTimeMillis()
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
        private const val DAY_IN_MILLIS: Long = 12*60*60*1000
        private const val TEST: Long = 5000

    }
}