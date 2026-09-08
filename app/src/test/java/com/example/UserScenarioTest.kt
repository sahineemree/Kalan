package com.example

import com.example.auth.SupabaseAuthService
import com.example.calculator.NutritionCalculator
import com.example.data.ActivityLevel
import com.example.data.Gender
import com.example.data.GoalType
import com.example.data.MacroPreset
import com.example.data.MealEntry
import com.example.data.MealType
import com.example.data.UserProfile
import com.example.data.WaterLogEntry
import com.example.data.WeightEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class UserScenarioTest {

    /**
     * Senaryo 1: Kullanıcı Giriş/Kayıt ve E-posta Normalizasyon Testi
     */
    @Test
    fun testUserAuthAndEmailNormalizationScenario() {
        val usernameInput = "emresahin"
        val normalized = SupabaseAuthService.normalizeEmail(usernameInput)
        assertEquals("emresahin@kalan.app", normalized)

        val directEmail = "emre@example.com"
        val normalizedDirect = SupabaseAuthService.normalizeEmail(directEmail)
        assertEquals("emre@example.com", normalizedDirect)

        val spacedUsername = "  Emre Sahin  "
        val normalizedSpaced = SupabaseAuthService.normalizeEmail(spacedUsername)
        assertEquals("emre_sahin@kalan.app", normalizedSpaced)
    }

    /**
     * Senaryo 2: Kullanıcı Profili Oluşturma ve Kalori/Makro Hesaplama CUJ
     */
    @Test
    fun testUserProfileAndMetabolismCalculationScenario() {
        val profile = UserProfile(
            id = 1,
            userName = "Emre Şahin",
            username = "emresahin",
            email = "emresahin@kalan.app",
            gender = Gender.ERKEK.name,
            age = 26,
            heightCm = 180.0,
            currentWeightKg = 80.0,
            targetWeightKg = 75.0,
            goalType = GoalType.LOSE.name,
            targetPace = "DENGELI",
            activityLevel = ActivityLevel.MODERATE.name,
            exerciseFrequency = "Haftada 3-4 gün",
            dietPreference = "Dengeli Beslenme (Akdeniz)",
            macroPreset = MacroPreset.BALANCED.name,
            waterTargetMl = 2800,
            isRegistered = true,
            isLoggedIn = true
        )

        val nutrition = NutritionCalculator.calculateFromProfile(profile)
        assertTrue("Hedef kalori pozitif ve mantıklı olmalı", nutrition.targetCalories in 1800.0..2600.0)
        assertTrue("Karbonhidrat gramı 0'dan büyük olmalı", nutrition.targetCarbsGrams > 100.0)
        assertTrue("Protein gramı 0'dan büyük olmalı", nutrition.targetProteinGrams > 80.0)
        assertTrue("Yağ gramı 0'dan büyük olmalı", nutrition.targetFatGrams > 40.0)
    }

    /**
     * Senaryo 3: Günlük Öğün Kaydı ve Kalan Kalori Hesaplama CUJ
     */
    @Test
    fun testMealLoggingAndRemainingCalorieScenario() {
        val targetCalories = 2200.0
        val breakfast = MealEntry(
            id = 1,
            date = "2026-09-05",
            mealType = MealType.KAHVALTI.name,
            foodName = "Yulaf Ezmesi ve Muz",
            amountGrams = 150.0,
            servingDescription = "1 Kase",
            calories = 380.0,
            carbs = 62.0,
            protein = 14.0,
            fat = 7.0
        )
        val lunch = MealEntry(
            id = 2,
            date = "2026-09-05",
            mealType = MealType.OGLE.name,
            foodName = "Izgara Tavuk & Bulgur Pilavı",
            amountGrams = 250.0,
            servingDescription = "1 Porsiyon",
            calories = 580.0,
            carbs = 48.0,
            protein = 46.0,
            fat = 12.0
        )
        val dinner = MealEntry(
            id = 3,
            date = "2026-09-05",
            mealType = MealType.AKSAM.name,
            foodName = "Somon ve Yeşillik Salatası",
            amountGrams = 300.0,
            servingDescription = "1 Porsiyon",
            calories = 520.0,
            carbs = 10.0,
            protein = 38.0,
            fat = 26.0
        )

        val totalConsumed = breakfast.calories + lunch.calories + dinner.calories // 1480 kcal
        val burnedExercise = 300.0 // 300 kcal yakıldı

        val remaining = NutritionCalculator.calculateRemainingCalories(
            targetCalories = targetCalories,
            consumedCalories = totalConsumed,
            burnedExercise = burnedExercise
        )

        // 2200 - 1480 + 300 = 1020 kcal kalan
        assertEquals(1020.0, remaining, 0.01)
    }

    /**
     * Senaryo 4: Su Takibi ve İlerleme Hesaplama CUJ
     */
    @Test
    fun testWaterTrackingScenario() {
        val targetWaterMl = 2500
        val logs = listOf(
            WaterLogEntry(id = 1, date = "2026-09-05", amountMl = 250),
            WaterLogEntry(id = 2, date = "2026-09-05", amountMl = 500),
            WaterLogEntry(id = 3, date = "2026-09-05", amountMl = 750)
        )

        val totalWaterDrunk = logs.sumOf { it.amountMl } // 1500 ml
        val remainingWater = (targetWaterMl - totalWaterDrunk).coerceAtLeast(0) // 1000 ml
        val progressFraction = totalWaterDrunk.toFloat() / targetWaterMl.toFloat() // 0.6f

        assertEquals(1500, totalWaterDrunk)
        assertEquals(1000, remainingWater)
        assertEquals(0.60f, progressFraction, 0.001f)
    }

    /**
     * Senaryo 5: Kilo ve BMI Değişim Takibi CUJ
     */
    @Test
    fun testWeightAndBmiTrackingScenario() {
        val heightM = 1.80
        val weightEntry1 = WeightEntry(id = 1, date = "2026-08-01", weightKg = 82.0)
        val weightEntry2 = WeightEntry(id = 2, date = "2026-09-05", weightKg = 78.5)

        val bmi1 = weightEntry1.weightKg / (heightM * heightM) // 82 / 3.24 = 25.308
        val bmi2 = weightEntry2.weightKg / (heightM * heightM) // 78.5 / 3.24 = 24.228

        val weightDiff = weightEntry2.weightKg - weightEntry1.weightKg // -3.5 kg

        assertEquals(25.31, bmi1, 0.05)
        assertEquals(24.23, bmi2, 0.05)
        assertEquals(-3.5, weightDiff, 0.01)
        assertTrue("Kullanıcı sağlıklı BMI aralığına girdi (< 25)", bmi2 < 25.0)
    }

    /**
     * Senaryo 6: Gemini AI Besin & Kalori Doğrulama ve Gerçekçi Gramaj Testi
     */
    @Test
    fun testGeminiFoodNutritionAccuracyScenario() = kotlinx.coroutines.test.runTest {
        // 200g cips testi (100g ~536 kcal -> 200g ~1072 kcal)
        val chipsResult = com.example.calculator.GeminiNutritionService.estimateFoodNutrition("Cips", "200g")
        assertEquals(1072.0, chipsResult.calories, 1.0)
        assertEquals(106.0, chipsResult.carbsGrams, 1.0)
        assertEquals(14.0, chipsResult.proteinGrams, 1.0)
        assertEquals(68.0, chipsResult.fatGrams, 1.0)
        assertEquals(200.0, chipsResult.servingGrams, 0.1)
        assertTrue("Cips için sağlık kalite skoru düşük olmalı", chipsResult.healthScore <= 4)
        assertTrue("Diyetisyen tavsiyesi boş olmamalı", chipsResult.dietaryAdvice.isNotBlank())

        // 1 adet yumurta testi
        val eggResult = com.example.calculator.GeminiNutritionService.estimateFoodNutrition("Yumurta", "1 adet")
        assertTrue("Yumurta kalorisi 70-85 kcal aralığında olmalı", eggResult.calories in 70.0..85.0)
        assertTrue("Yumurta yüksek protein ve kalite skoru vermeli", eggResult.healthScore >= 8)
    }
}
