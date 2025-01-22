package com.example.guru2_team_9

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {

    private const val CHANNEL_ID = "expiration_channel_id"
    private const val CHANNEL_NAME = "유통기한 알림"
    private const val NOTIFICATION_ID = 1001

    fun showNotification(context: Context, title: String, content: String) {
        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            // 리소스가 없으면 ic_launcher 등으로 대체
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel(context: Context) {
        // 오레오(API 26) 이상에서 NotificationChannel 이 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "유통기한 임박 식재료를 알려주는 채널"
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
