package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.UserProfile
import java.util.Calendar
import kotlin.math.roundToInt

object ReminderScheduler {

    private const val TAG = "ReminderScheduler"

    fun scheduleMealReminder(context: Context, type: ReminderType, timeStr: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pendingIntent = getReminderPendingIntent(context, type)

        val triggerMillis = calculateNextOccurrence(timeStr)
        setAlarmCompat(alarmManager, triggerMillis, pendingIntent)
        Log.d(TAG, "Scheduled ${type.name} at triggerMillis=$triggerMillis ($timeStr)")
    }

    fun cancelMealReminder(context: Context, type: ReminderType) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pendingIntent = getReminderPendingIntent(context, type)
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Cancelled ${type.name}")
    }

    fun scheduleWaterReminder(
        context: Context,
        intervalHours: Double = 2.0,
        startTimeStr: String = "08:30",
        endTimeStr: String = "22:00"
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pendingIntent = getReminderPendingIntent(context, ReminderType.WATER)

        val triggerMillis = calculateNextWaterCheckpoint(intervalHours, startTimeStr, endTimeStr)
        setAlarmCompat(alarmManager, triggerMillis, pendingIntent)
        Log.d(TAG, "Scheduled WATER reminder at triggerMillis=$triggerMillis")
    }

    fun cancelWaterReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pendingIntent = getReminderPendingIntent(context, ReminderType.WATER)
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Cancelled WATER reminder")
    }

    fun rescheduleAll(context: Context, profile: UserProfile) {
        // Breakfast
        if (profile.notifyBreakfast) {
            scheduleMealReminder(context, ReminderType.BREAKFAST, profile.breakfastTime)
        } else {
            cancelMealReminder(context, ReminderType.BREAKFAST)
        }

        // Lunch
        if (profile.notifyLunch) {
            scheduleMealReminder(context, ReminderType.LUNCH, profile.lunchTime)
        } else {
            cancelMealReminder(context, ReminderType.LUNCH)
        }

        // Dinner
        if (profile.notifyDinner) {
            scheduleMealReminder(context, ReminderType.DINNER, profile.dinnerTime)
        } else {
            cancelMealReminder(context, ReminderType.DINNER)
        }

        // Snack
        if (profile.notifySnack) {
            scheduleMealReminder(context, ReminderType.SNACK, profile.snackTime)
        } else {
            cancelMealReminder(context, ReminderType.SNACK)
        }

        // Water
        if (profile.notifyWater) {
            scheduleWaterReminder(
                context = context,
                intervalHours = profile.waterIntervalHours,
                startTimeStr = profile.waterStartTime,
                endTimeStr = profile.waterEndTime
            )
        } else {
            cancelWaterReminder(context)
        }
    }

    private fun getReminderPendingIntent(context: Context, type: ReminderType): PendingIntent {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = NotificationHelper.ACTION_REMINDER
            putExtra(NotificationHelper.EXTRA_REMINDER_TYPE, type.name)
        }
        return PendingIntent.getBroadcast(
            context,
            type.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun setAlarmCompat(
        alarmManager: AlarmManager,
        triggerMillis: Long,
        pendingIntent: PendingIntent
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "Exact alarm permission missing, falling back to set(): ${e.message}")
            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            } catch (ignored: Exception) {}
        }
    }

    private fun calculateNextOccurrence(timeStr: String): Long {
        val parts = timeStr.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 8
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 30

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val now = System.currentTimeMillis()
        if (cal.timeInMillis <= now) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    private fun calculateNextWaterCheckpoint(
        intervalHours: Double,
        startTimeStr: String,
        endTimeStr: String
    ): Long {
        val startParts = startTimeStr.split(":")
        val startH = startParts.getOrNull(0)?.toIntOrNull() ?: 8
        val startM = startParts.getOrNull(1)?.toIntOrNull() ?: 30

        val endParts = endTimeStr.split(":")
        val endH = endParts.getOrNull(0)?.toIntOrNull() ?: 22
        val endM = endParts.getOrNull(1)?.toIntOrNull() ?: 0

        val intervalMinutes = (intervalHours * 60).roundToInt().coerceIn(30, 360)
        val startMinutesOfDay = startH * 60 + startM
        val endMinutesOfDay = endH * 60 + endM

        val nowCal = Calendar.getInstance()
        val currentMinutesOfDay = nowCal.get(Calendar.HOUR_OF_DAY) * 60 + nowCal.get(Calendar.MINUTE)

        // Find checkpoints for today
        var checkpoint = startMinutesOfDay
        var nextMinuteToday: Int? = null

        while (checkpoint <= endMinutesOfDay) {
            // Must be at least 1 minute in the future
            if (checkpoint > currentMinutesOfDay + 1) {
                nextMinuteToday = checkpoint
                break
            }
            checkpoint += intervalMinutes
        }

        return if (nextMinuteToday != null) {
            // Schedule for today
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, nextMinuteToday / 60)
                set(Calendar.MINUTE, nextMinuteToday % 60)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        } else {
            // All today checkpoints passed, schedule for tomorrow at start time
            Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, startH)
                set(Calendar.MINUTE, startM)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    }
}
