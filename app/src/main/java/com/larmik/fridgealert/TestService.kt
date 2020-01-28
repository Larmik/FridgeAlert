package com.larmik.fridgealert

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.larmik.fridgealert.common.ui.MainActivity
import com.larmik.fridgealert.utils.loadProducts
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
                mTimeLeftInMillis = (currentMillis + DAY_IN_MILLIS) - currentMillis
                sendNotification()
                //startTimer(false)
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
        val context = context ?: applicationContext
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val products = context.loadProducts()
        for (product in products) {
            if (product.getRemainingDays().toInt() == 0) {
                notificationManager.notify(System.currentTimeMillis().toInt(), createNotification(context, true))
            }
        }
    }

    private fun getRemaingMillis() : Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 15)
        calendar.set(Calendar.MINUTE, 23)
        calendar.set(Calendar.SECOND, 0)
        if (System.currentTimeMillis() > calendar.timeInMillis)
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) +1)
        return calendar.timeInMillis - System.currentTimeMillis()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return serviceBinder
    }

    private fun getChannelId(isMainNotif: Boolean) : String {
       return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(context ?: applicationContext, isMainNotif)
            } else {
                ""
            }
    }

    fun foreground() {
        startForeground(SERVICE_NOTIFICATION_ID, createNotification(context ?: applicationContext, false))
    }

    fun background() {
       stopForeground(true)
    }

    private fun createNotification(context: Context, isMainNotif : Boolean): Notification {
        val contextIntent = if (isMainNotif) MainActivity::class.java else NotificationReceiver::class.java
        val intent =  Intent(context, contextIntent)
        val builder = if (isMainNotif) NotificationCompat.Builder(context, getChannelId(isMainNotif)) else NotificationCompat.Builder(this, getChannelId(isMainNotif))
        val title = if (isMainNotif) "title" else "App en arrière plan"
        val text = if (isMainNotif) "text" else "Pour masquer cette notification, cliquez dessus et décochez \\\"Notification permanente\\\""
        val resultPendingIntent = if (isMainNotif)
            PendingIntent.getActivity(context,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            ) else PendingIntent.getBroadcast(this,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        return builder
            .setSmallIcon(R.drawable. ic_launcher_foreground )
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setChannelId(getChannelId(isMainNotif))
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(resultPendingIntent)
            .build()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context, isMainNotif: Boolean): String {
        val channelId = if (isMainNotif) "MAIN_CHANNEL_ID" else "SERVICE_CHANNEL_ID"
        val channelName = if (isMainNotif) "Notifications principales" else "Notification permanente"
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    companion object {
        internal const val SERVICE_NOTIFICATION_ID = 2
        private const val DAY_IN_MILLIS: Long = 12*60*60*1000
    }

    inner class RunServiceBinder : Binder() {
        val service: TestService
            get() = this@TestService
    }
}