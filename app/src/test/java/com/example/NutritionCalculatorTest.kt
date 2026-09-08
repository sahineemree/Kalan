package com.example

import com.example.calculator.NutritionCalculator
import com.example.data.ActivityLevel
import com.example.data.Gender
import com.example.data.GoalType
import com.example.data.MacroPreset
import com.example.data.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.roundToInt

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NutritionCalculatorTest {

    @Test
    fun testBmrCalculationForMale() {
        // Erkek: BMR = (10 * 76) + (6.25 * 178) - (5 * 28) + 5
        // 760 + 1112.5 - 140 + 5 = 1737.5 kcal
        val bmr = NutritionCalculator.calculateBmr(
            gender = Gender.ERKEK,
            weightKg = 76.0,
            heightCm = 178.0,
            age = 28
        )
        assertEquals(1737.5, bmr, 0.1)
    }

    @Test
    fun testBmrCalculationForFemale() {
        // Kadın: BMR = (10 * 60) + (6.25 * 165) - (5 * 30) - 161
        // 600 + 1031.25 - 150 - 161 = 1320.25 kcal
        val bmr = NutritionCalculator.calculateBmr(
            gender = Gender.KADIN,
            weightKg = 60.0,
            heightCm = 165.0,
            age = 30
        )
        assertEquals(1320.25, bmr, 0.1)
    }

    @Test
    fun testTdeeCalculation() {
        val bmr = 1737.5
        val tdeeModerate = NutritionCalculator.calculateTdee(bmr, ActivityLevel.MODERATE)
        // 1737.5 * 1.55 = 2693.125
        assertEquals(2693.125, tdeeModerate, 0.1)
    }

    @Test
    fun testRemainingCaloriesFormula() {
        // Kalan = Hedef (2000) - Alınan (1450) + Egzersiz (250) = 800 kcal
        val remaining = NutritionCalculator.calculateRemainingCalories(
            targetCalories = 2000.0,
            consumedCalories = 1450.0,
            burnedExercise = 250.0
        )
        assertEquals(800.0, remaining, 0.01)

        // Exceeded scenario: Hedef (2000) - Alınan (2300) + Egzersiz (100) = -200 kcal
        val exceeded = NutritionCalculator.calculateRemainingCalories(
            targetCalories = 2000.0,
            consumedCalories = 2300.0,
            burnedExercise = 100.0
        )
        assertEquals(-200.0, exceeded, 0.01)
    }

    @Test
    fun testMacroGramsDistribution() {
        val targetCalories = 2000.0
        val (carbs, protein, fat) = NutritionCalculator.calculateMacroGrams(targetCalories, MacroPreset.BALANCED)
        // 50% Carbs = 1000 kcal / 4 = 250g
        // 20% Protein = 400 kcal / 4 = 100g
        // 30% Fat = 600 kcal / 9 = 66.67g
        assertEquals(250.0, carbs, 0.1)
        assertEquals(100.0, protein, 0.1)
        assertEquals(66.67, fat, 0.1)
    }

    @Test
    fun testProfileCalculationIntegration() {
        val profile = UserProfile(
            id = 1,
            gender = Gender.ERKEK.name,
            age = 28,
            heightCm = 178.0,
            currentWeightKg = 76.0,
            targetWeightKg = 72.0,
            activityLevel = ActivityLevel.MODERATE.name,
            goalType = GoalType.LOSE.name,
            macroPreset = MacroPreset.BALANCED.name,
            waterTargetMl = 2500
        )
        val result = NutritionCalculator.calculateFromProfile(profile)
        // Target should be TDEE (approx 2693) - 500 = 2193 kcal
        assertTrue(result.targetCalories > 2100 && result.targetCalories < 2250)
        assertTrue(result.targetCarbsGrams > 200)
        assertTrue(result.targetProteinGrams > 80)
        assertTrue(result.targetFatGrams > 50)
    }

    @Test
    fun testRealisticFoodCalorieEstimationForChips() = kotlinx.coroutines.test.runTest {
        // 200g cips gerçekçi olarak ~1072 kcal olmalıdır (100g = ~536 kcal)
        val result = com.example.calculator.GeminiNutritionService.estimateFoodNutrition("Patates Cipsi", "200 gr")
        assertTrue("200g cips 1000 ile 1150 kcal arasında olmalıdır fakat: ${result.calories}", result.calories in 1000.0..1150.0)
        assertTrue("200g cips yağı 60g ile 75g arasında olmalıdır", result.fatGrams in 60.0..75.0)
    }
}
