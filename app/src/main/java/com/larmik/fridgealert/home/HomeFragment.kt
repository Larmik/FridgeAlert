package com.larmik.fridgealert.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.larmik.fridgealert.R
import com.larmik.fridgealert.common.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    lateinit var notificationManager : NotificationManager
    val v = longArrayOf(500, 1000)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        view.notif.setOnClickListener {
            val intent =  Intent(requireContext(), MainActivity::class.java);
            val resultPendingIntent =
                PendingIntent. getActivity (
                    requireContext(),
                    0,
                    intent,
                    PendingIntent. FLAG_CANCEL_CURRENT
                )
            val mBuilder = NotificationCompat.Builder(requireContext(), "CHANNEL_ID" )
                .setSmallIcon(R.drawable. ic_launcher_foreground )
                .setContentTitle( "Test" )
                .setContentText( "Hello! This is my first push notification" )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .addAction(R.drawable. ic_launcher_foreground, "Add", resultPendingIntent)
                .setContentIntent(resultPendingIntent)
            val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
            } else
                vibrator.vibrate(400)
            notificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
        }
        return view
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "CHANNEL_NAME"
            val descriptionText = "CHANNEL_DESCRIPTION"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }
            channel.vibrationPattern = v
            channel.enableVibration(true)
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
    }



}