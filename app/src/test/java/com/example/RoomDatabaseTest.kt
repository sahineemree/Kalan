package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.DailySummary
import com.example.data.KalanDao
import com.example.data.KalanDatabase
import com.example.data.MealEntry
import com.example.data.MealType
import com.example.data.UserProfile
import com.example.data.WaterLogEntry
import com.example.data.WeightEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomDatabaseTest {

    private lateinit var db: KalanDatabase
    private lateinit var dao: KalanDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KalanDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.kalanDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testUserProfileInsertAndRead() = runTest {
        val initialProfile = dao.getUserProfileOnce()
        assertEquals(null, initialProfile)

        val profile = UserProfile(
            id = 1,
            userName = "Emre",
            username = "emresahin",
            waterTargetMl = 2500,
            notifyBreakfast = true
        )
        dao.insertOrUpdateProfile(profile)

        val retrieved = dao.getUserProfileOnce()
        assertNotNull(retrieved)
        assertEquals("Emre", retrieved?.userName)
        assertEquals(2500, retrieved?.waterTargetMl)
        assertEquals(true, retrieved?.notifyBreakfast)
    }

    @Test
    fun testMealEntriesFlow() = runTest {
        val today = "2026-09-06"
        val meal1 = MealEntry(
            id = 0,
            date = today,
            mealType = MealType.KAHVALTI.name,
            foodName = "Yumurta & Peynir",
            amountGrams = 120.0,
            servingDescription = "2 Adet",
            calories = 320.0,
            carbs = 4.0,
            protein = 22.0,
            fat = 24.0
        )
        val meal2 = MealEntry(
            id = 0,
            date = today,
            mealType = MealType.OGLE.name,
            foodName = "Mercimek Çorbası",
            amountGrams = 250.0,
            servingDescription = "1 Kase",
            calories = 240.0,
            carbs = 35.0,
            protein = 12.0,
            fat = 5.0
        )

        dao.insertMealEntry(meal1)
        dao.insertMealEntry(meal2)

        val mealsForToday = dao.getEntriesForDate(today).first()
        assertEquals(2, mealsForToday.size)
        assertEquals(560.0, mealsForToday.sumOf { it.calories }, 0.01)
    }

    @Test
    fun testWaterLogsAndTotal() = runTest {
        val today = "2026-09-06"
        dao.insertWaterLog(WaterLogEntry(id = 0, date = today, amountMl = 250))
        dao.insertWaterLog(WaterLogEntry(id = 0, date = today, amountMl = 500))

        val waterLogs = dao.getWaterLogsForDate(today).first()
        assertEquals(2, waterLogs.size)
        assertEquals(750, waterLogs.sumOf { it.amountMl })
    }

    @Test
    fun testWeightTrackingDao() = runTest {
        val entry1 = WeightEntry(id = 0, date = "2026-09-01", weightKg = 81.5)
        val entry2 = WeightEntry(id = 0, date = "2026-09-06", weightKg = 79.8)

        dao.insertWeight(entry1)
        dao.insertWeight(entry2)

        val weights = dao.getAllWeights().first()
        assertEquals(2, weights.size)
        assertTrue(weights.any { it.weightKg == 79.8 })
    }

    @Test
    fun testDailySummaryDao() = runTest {
        val today = "2026-09-06"
        val summary = DailySummary(
            date = today,
            waterMl = 1250,
            burnedExerciseKcal = 300.0,
            notes = "Bugün enerjim harikaydı, antrenman tamam!",
            moodEmoji = "⚡"
        )
        dao.insertOrUpdateSummary(summary)

        val retrievedSummary = dao.getSummaryForDate(today).first()
        assertNotNull(retrievedSummary)
        assertEquals("Bugün enerjim harikaydı, antrenman tamam!", retrievedSummary?.notes)
        assertEquals("⚡", retrievedSummary?.moodEmoji)
        assertEquals(1250, retrievedSummary?.waterMl)
    }
}
