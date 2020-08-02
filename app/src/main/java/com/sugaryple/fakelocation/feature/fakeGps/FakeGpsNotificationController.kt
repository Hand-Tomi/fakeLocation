package com.sugaryple.fakelocation.feature.fakeGps

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.sugaryple.fakelocation.R

class FakeGpsNotificationController(private val appContext: Context) {

    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun create(cancelPendingIntent: PendingIntent): Notification {

        val id = appContext.getString(R.string.gps_service_notification_channel_id)
        val title = appContext.getString(R.string.gps_service_notification_title)
        val cancel = appContext.getString(R.string.gps_service_notification_cancel)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(id)
        }

        return NotificationCompat.Builder(appContext, id)
            .setContentTitle(title)
            .setTicker(title) // 알림이 상태 바에 때 텍스트
            .setContentText("progress")
            .setSmallIcon(R.drawable.ic_baseline_location_searching_24)
            .setOngoing(true) // 알림의 지속석
            .addAction(android.R.drawable.ic_delete, cancel, cancelPendingIntent)
            .build()
    }

    fun cancel() {
        notificationManager.cancel(FAKE_GPS_NOTIFICATION_ID)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(channelId: String) {
        val name = appContext.getString(R.string.gps_service_notification_channel_id)
        val descriptionText = appContext.getString(R.string.gps_service_notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(channelId, name, importance)
        mChannel.description = descriptionText
        notificationManager.createNotificationChannel(mChannel)
    }

    companion object {
        const val FAKE_GPS_NOTIFICATION_ID = 152
    }
}