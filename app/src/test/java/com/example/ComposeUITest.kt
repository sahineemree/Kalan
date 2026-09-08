package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.notification.NotificationHelper
import com.example.notification.ReminderType
import com.example.ui.WeeklyCalorieTrendCard
import com.example.ui.WeeklyDayCalorieStat
import com.example.ui.WeeklyTrendData
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ComposeUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testWeeklyCalorieTrendCardRendersProperly() {
        val testTrendData = WeeklyTrendData(
            days = listOf(
                WeeklyDayCalorieStat(date = "2026-08-31", dayShortLabel = "Pzt", calories = 1950.0, targetCalories = 2000.0, isToday = false),
                WeeklyDayCalorieStat(date = "2026-09-01", dayShortLabel = "Sal", calories = 2100.0, targetCalories = 2000.0, isToday = false),
                WeeklyDayCalorieStat(date = "2026-09-02", dayShortLabel = "Çar", calories = 1800.0, targetCalories = 2000.0, isToday = false),
                WeeklyDayCalorieStat(date = "2026-09-03", dayShortLabel = "Per", calories = 2050.0, targetCalories = 2000.0, isToday = false),
                WeeklyDayCalorieStat(date = "2026-09-04", dayShortLabel = "Cum", calories = 2200.0, targetCalories = 2000.0, isToday = false),
                WeeklyDayCalorieStat(date = "2026-09-05", dayShortLabel = "Cmt", calories = 1900.0, targetCalories = 2000.0, isToday = false),
                WeeklyDayCalorieStat(date = "2026-09-06", dayShortLabel = "Paz", calories = 1450.0, targetCalories = 2000.0, isToday = true)
            ),
            weeklyAverage = 1921.4,
            weeklyTotal = 13450.0,
            daysReachedTarget = 4,
            totalRecordedDays = 7,
            targetCalories = 2000.0,
            todayCalories = 1450.0
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                WeeklyCalorieTrendCard(weeklyTrendData = testTrendData)
            }
        }

        // Verify key headers and metrics are visible
        composeTestRule.onNodeWithTag("weekly_calorie_trend_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("Haftalık Kalori Eğilimi").assertIsDisplayed()
        composeTestRule.onNodeWithText("1921 kcal").assertIsDisplayed()
        composeTestRule.onNodeWithText("Haftalık Toplam").assertIsDisplayed()
        composeTestRule.onNodeWithText("13450 kcal").assertIsDisplayed()
        composeTestRule.onNodeWithText("4 / 7 gün").assertIsDisplayed()
    }

    @Test
    fun testNotificationChannelsAndTypes() {
        val types = ReminderType.entries
        assertEquals(6, types.size)
        assertNotNull(ReminderType.valueOf("BREAKFAST"))
        assertNotNull(ReminderType.valueOf("LUNCH"))
        assertNotNull(ReminderType.valueOf("DINNER"))
        assertNotNull(ReminderType.valueOf("SNACK"))
        assertNotNull(ReminderType.valueOf("WATER"))
        assertNotNull(ReminderType.valueOf("TEST"))

        assertEquals("kalan_meals_channel", NotificationHelper.CHANNEL_MEALS)
        assertEquals("kalan_water_channel", NotificationHelper.CHANNEL_WATER)
    }
}
