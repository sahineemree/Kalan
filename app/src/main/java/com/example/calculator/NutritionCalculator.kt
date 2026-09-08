package com.example.calculator

import com.example.data.ActivityLevel
import com.example.data.Gender
import com.example.data.GoalType
import com.example.data.MacroPreset
import com.example.data.UserProfile
import kotlin.math.roundToInt

data class CalculationResult(
    val bmr: Double,
    val tdee: Double,
    val targetCalories: Double,
    val targetCarbsGrams: Double,
    val targetProteinGrams: Double,
    val targetFatGrams: Double,
    val isCustomTarget: Boolean
)

object NutritionCalculator {

    fun calculateBmr(
        gender: Gender,
        weightKg: Double,
        heightCm: Double,
        age: Int
    ): Double {
        // Mifflin-St Jeor Equation
        return if (gender == Gender.ERKEK) {
            (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age) + 5.0
        } else {
            (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age) - 161.0
        }
    }

    fun calculateTdee(
        bmr: Double,
        activityLevel: ActivityLevel
    ): Double {
        return bmr * activityLevel.multiplier
    }

    fun calculateFromProfile(profile: UserProfile): CalculationResult {
        val gender = try { Gender.valueOf(profile.gender) } catch (e: Exception) { Gender.ERKEK }
        val activity = try { ActivityLevel.valueOf(profile.activityLevel) } catch (e: Exception) { ActivityLevel.MODERATE }
        val goal = try { GoalType.valueOf(profile.goalType) } catch (e: Exception) { GoalType.LOSE }
        val macroPreset = try { MacroPreset.valueOf(profile.macroPreset) } catch (e: Exception) { MacroPreset.BALANCED }

        val bmr = calculateBmr(gender, profile.currentWeightKg, profile.heightCm, profile.age)
        val tdee = calculateTdee(bmr, activity)

        val autoTargetCalories = (tdee + goal.calorieDelta).coerceAtLeast(1200.0)
        val finalTargetCalories = profile.customTargetCalories ?: autoTargetCalories

        val (carbGrams, proteinGrams, fatGrams) = if (
            profile.customCarbsGrams != null &&
            profile.customProteinGrams != null &&
            profile.customFatGrams != null
        ) {
            Triple(profile.customCarbsGrams, profile.customProteinGrams, profile.customFatGrams)
        } else {
            calculateMacroGrams(finalTargetCalories, macroPreset)
        }

        return CalculationResult(
            bmr = bmr,
            tdee = tdee,
            targetCalories = finalTargetCalories,
            targetCarbsGrams = carbGrams,
            targetProteinGrams = proteinGrams,
            targetFatGrams = fatGrams,
            isCustomTarget = profile.customTargetCalories != null
        )
    }

    fun calculateMacroGrams(targetCalories: Double, preset: MacroPreset): Triple<Double, Double, Double> {
        val (carbPct, proteinPct, fatPct) = when (preset) {
            MacroPreset.BALANCED -> Triple(50, 20, 30)
            MacroPreset.HIGH_PROTEIN -> Triple(40, 35, 25)
            MacroPreset.LOW_CARB -> Triple(20, 40, 40)
            MacroPreset.CUSTOM -> Triple(50, 20, 30)
        }

        // Carbs: 4 kcal/g, Protein: 4 kcal/g, Fat: 9 kcal/g
        val carbGrams = (targetCalories * (carbPct / 100.0)) / 4.0
        val proteinGrams = (targetCalories * (proteinPct / 100.0)) / 4.0
        val fatGrams = (targetCalories * (fatPct / 100.0)) / 9.0

        return Triple(carbGrams, proteinGrams, fatGrams)
    }

    /**
     * Kalan Kalori formülü:
     * Kalan = Hedef Kalori - Alınan Kalori + Yakılan Egzersiz
     */
    fun calculateRemainingCalories(
        targetCalories: Double,
        consumedCalories: Double,
        burnedExercise: Double
    ): Double {
        return targetCalories - consumedCalories + burnedExercise
    }
}
