package com.example.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import kotlin.random.Random

object NotificationHelper {

    const val CHANNEL_MEALS = "kalan_meals_channel"
    const val CHANNEL_WATER = "kalan_water_channel"

    const val ACTION_QUICK_ADD_WATER = "com.example.ACTION_QUICK_ADD_WATER"
    const val ACTION_REMINDER = "com.example.ACTION_REMINDER"

    const val EXTRA_REMINDER_TYPE = "extra_reminder_type"
    const val EXTRA_AMOUNT_ML = "extra_amount_ml"
    const val EXTRA_DESTINATION = "extra_destination"

    private val waterMessagesTr = listOf(
        "Vücudunun hidrasyonunu sağlamak ve metabolizmanı canlı tutmak için 1 bardak (250 ml) taze su içme vakti! 💧",
        "Enerjini ve odaklanmanı yüksek tutmak için bir bardak ferahlatıcı su içmeyi unutma. Sağlığın için bir yudum al!",
        "Günlük su hedefine bir adım daha yaklaş! Şimdi sağlıklı bir bardak su tüketebilirsin.",
        "Cildinin ışıltısı ve zindeliğin için gün ortası su molası ver. Bir bardak su içtin mi?",
        "Metabolizmanı hızlandırmak ve tokluk hissini desteklemek için harika bir an: Hemen su iç!"
    )

    private val waterMessagesEn = listOf(
        "Time for a refreshing glass of water (250 ml) to stay hydrated and active! 💧",
        "Keep your energy and focus high—drink a glass of fresh water now!",
        "One step closer to your daily hydration goal. Take a sip of water!",
        "Boost your metabolism and stay healthy: Grab a glass of water right now."
    )

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Meal Reminders Channel
            val mealsChannel = NotificationChannel(
                CHANNEL_MEALS,
                "Öğün Hatırlatıcıları",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kahvaltı, öğle yemeği, akşam yemeği ve ara öğün bildirimleri"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            // Water Reminders Channel
            val waterChannel = NotificationChannel(
                CHANNEL_WATER,
                "Su İçme Hatırlatıcıları",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Günlük su tüketim hedefinize ulaşmanız için su içme hatırlatıcıları"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(mealsChannel)
            notificationManager.createNotificationChannel(waterChannel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun showReminderNotification(
        context: Context,
        type: ReminderType,
        isEnglish: Boolean = false,
        customTitle: String? = null,
        customMessage: String? = null
    ) {
        if (!hasNotificationPermission(context)) return

        createNotificationChannels(context)

        val channelId = if (type == ReminderType.WATER) CHANNEL_WATER else CHANNEL_MEALS
        val title = customTitle ?: if (isEnglish) type.defaultTitleEn else type.defaultTitleTr

        val message = customMessage ?: when (type) {
            ReminderType.WATER -> {
                val list = if (isEnglish) waterMessagesEn else waterMessagesTr
                list[Random.nextInt(list.size)]
            }
            ReminderType.BREAKFAST -> {
                if (isEnglish) "Time to fuel your day! Enjoy a balanced breakfast and log it in Kalan."
                else "Güne zinde ve enerjik başlamak için dengeli bir kahvaltı yapma ve öğününü kaydetme zamanı! 🥐🍳"
            }
            ReminderType.LUNCH -> {
                if (isEnglish) "Time for lunch! Keep your macros balanced and recharge your body."
                else "Günün temposunda enerjini tazelemek ve hedefine sadık kalmak için besleyici öğle yemeği zamanı! 🥗"
            }
            ReminderType.DINNER -> {
                if (isEnglish) "Dinner time! Choose a light and nutritious meal to wrap up your day."
                else "Günün son ana öğününde porsiyon kontrolünü koruyarak hafif ve besleyici bir akşam yemeği tüketebilirsin! 🍲"
            }
            ReminderType.SNACK -> {
                if (isEnglish) "Time for a healthy snack to keep your blood sugar balanced."
                else "Kan şekerini dengede tutmak ve enerjini korumak için taze meyve veya sağlıklı bir atıştırmalık vakti! 🍎"
            }
            ReminderType.TEST -> {
                if (isEnglish) "Awesome! Kalan local notification system is working perfectly."
                else "Tebrikler! Kalan yerel bildirim sistemi cihazınızda sorunsuz ve anında çalışıyor. 🔔"
            }
        }

        // Tap action -> Open App
        val destination = if (type == ReminderType.WATER) "water" else "home"
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_DESTINATION, destination)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            type.id,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_stat_kalan)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)

        // For Water notifications, add quick action: +250 ml Water directly from notification!
        if (type == ReminderType.WATER) {
            val quickAddIntent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                action = ACTION_QUICK_ADD_WATER
                putExtra(EXTRA_AMOUNT_ML, 250)
            }
            val quickAddPendingIntent = PendingIntent.getBroadcast(
                context,
                202,
                quickAddIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(
                R.drawable.ic_stat_kalan,
                if (isEnglish) "+250 ml Water" else "+250 ml Su Ekle",
                quickAddPendingIntent
            )
        }

        try {
            NotificationManagerCompat.from(context).notify(type.id, builder.build())
        } catch (_: SecurityException) {
            // Permission revoked or not granted
        }
    }

    fun showWaterLoggedNotification(context: Context, amountMl: Int, isEnglish: Boolean = false) {
        if (!hasNotificationPermission(context)) return

        createNotificationChannels(context)

        val title = if (isEnglish) "💧 +$amountMl ml Water Logged!" else "💧 +$amountMl ml Su Başarıyla Eklendi!"
        val message = if (isEnglish)
            "Great job! Your hydration log has been updated in Kalan."
        else
            "Harika gidiyorsun! Günlük su tüketimin güncellendi. Vücudun sana teşekkür ediyor!"

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_DESTINATION, "water")
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            205,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_WATER)
            .setSmallIcon(R.drawable.ic_stat_kalan)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(201, notification)
        } catch (_: SecurityException) {
        }
    }
}
