package com.larmik.fridgealert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.larmik.fridgealert.common.ui.MainActivity

private const val TAG = "MyBroadcastReceiver"

open class NotificationReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, p1: Intent?) {
        val i =  Intent(context, MainActivity::class.java)
            .putExtra(Settings.EXTRA_CHANNEL_ID, TestService.SERVICE_NOTIFICATION_ID)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context!!.startActivity(i)
    }


}