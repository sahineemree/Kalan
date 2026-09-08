package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.DailySummary
import com.example.data.KalanDatabase
import com.example.data.WaterLogEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReminderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ReminderReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return
        val action = intent.action ?: return
        Log.d(TAG, "onReceive action=$action")

        when (action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON" -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = KalanDatabase.getDatabase(context, this)
                        val profile = db.kalanDao().getUserProfileOnce()
                        NotificationHelper.createNotificationChannels(context)
                        if (profile != null) {
                            ReminderScheduler.rescheduleAll(context, profile)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error handling boot event: ${e.message}", e)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }

            NotificationHelper.ACTION_REMINDER -> {
                val reminderTypeName = intent.getStringExtra(NotificationHelper.EXTRA_REMINDER_TYPE)
                val type = ReminderType.fromString(reminderTypeName)

                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = KalanDatabase.getDatabase(context, this)
                        val profile = db.kalanDao().getUserProfileOnce()
                        val isEnglish = profile?.language == "EN"

                        // Check if still enabled
                        val isEnabled = when (type) {
                            ReminderType.BREAKFAST -> profile?.notifyBreakfast ?: true
                            ReminderType.LUNCH -> profile?.notifyLunch ?: true
                            ReminderType.DINNER -> profile?.notifyDinner ?: true
                            ReminderType.SNACK -> profile?.notifySnack ?: false
                            ReminderType.WATER -> profile?.notifyWater ?: true
                            ReminderType.TEST -> true
                        }

                        if (isEnabled) {
                            NotificationHelper.showReminderNotification(
                                context = context,
                                type = type,
                                isEnglish = isEnglish
                            )
                        }

                        // Schedule the next recurrence
                        if (profile != null) {
                            when (type) {
                                ReminderType.BREAKFAST -> {
                                    if (profile.notifyBreakfast) {
                                        ReminderScheduler.scheduleMealReminder(
                                            context,
                                            ReminderType.BREAKFAST,
                                            profile.breakfastTime
                                        )
                                    }
                                }
                                ReminderType.LUNCH -> {
                                    if (profile.notifyLunch) {
                                        ReminderScheduler.scheduleMealReminder(
                                            context,
                                            ReminderType.LUNCH,
                                            profile.lunchTime
                                        )
                                    }
                                }
                                ReminderType.DINNER -> {
                                    if (profile.notifyDinner) {
                                        ReminderScheduler.scheduleMealReminder(
                                            context,
                                            ReminderType.DINNER,
                                            profile.dinnerTime
                                        )
                                    }
                                }
                                ReminderType.SNACK -> {
                                    if (profile.notifySnack) {
                                        ReminderScheduler.scheduleMealReminder(
                                            context,
                                            ReminderType.SNACK,
                                            profile.snackTime
                                        )
                                    }
                                }
                                ReminderType.WATER -> {
                                    if (profile.notifyWater) {
                                        ReminderScheduler.scheduleWaterReminder(
                                            context = context,
                                            intervalHours = profile.waterIntervalHours,
                                            startTimeStr = profile.waterStartTime,
                                            endTimeStr = profile.waterEndTime
                                        )
                                    }
                                }
                                ReminderType.TEST -> { /* One-shot */ }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error handling reminder: ${e.message}", e)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }

            NotificationHelper.ACTION_QUICK_ADD_WATER -> {
                val amountMl = intent.getIntExtra(NotificationHelper.EXTRA_AMOUNT_ML, 250)
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = KalanDatabase.getDatabase(context, this)
                        val dao = db.kalanDao()
                        val profile = dao.getUserProfileOnce()
                        val isEnglish = profile?.language == "EN"

                        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                        // Log water entry
                        dao.insertWaterLog(
                            WaterLogEntry(
                                date = today,
                                amountMl = amountMl,
                                timestamp = System.currentTimeMillis()
                            )
                        )

                        // Update summary
                        val existingSummaries = dao.getAllSummariesList()
                        val currentSummary = existingSummaries.find { it.date == today }
                        val currentWater = currentSummary?.waterMl ?: 0
                        val updated = currentSummary?.copy(waterMl = currentWater + amountMl)
                            ?: DailySummary(date = today, waterMl = amountMl)
                        dao.insertOrUpdateSummary(updated)

                        // Show confirmation notification
                        NotificationHelper.showWaterLoggedNotification(
                            context = context,
                            amountMl = amountMl,
                            isEnglish = isEnglish
                        )

                        // Reschedule next water reminder
                        if (profile != null && profile.notifyWater) {
                            ReminderScheduler.scheduleWaterReminder(
                                context = context,
                                intervalHours = profile.waterIntervalHours,
                                startTimeStr = profile.waterStartTime,
                                endTimeStr = profile.waterEndTime
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error logging quick water: ${e.message}", e)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}
