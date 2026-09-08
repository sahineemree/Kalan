package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MealType(val titleTr: String, val emoji: String) {
    KAHVALTI("Kahvaltı", "🌅"),
    OGLE("Öğle Yemeği", "☀️"),
    AKSAM("Akşam Yemeği", "🌙"),
    ARA_OGUN("Ara Öğün / Atıştırmalık", "🍎")
}

enum class Gender(val titleTr: String) {
    ERKEK("Erkek"),
    KADIN("Kadın")
}

enum class ActivityLevel(val titleTr: String, val descriptionTr: String, val multiplier: Double) {
    SEDENTARY("Hareketsiz", "Masa başı iş, az veya hiç egzersiz", 1.2),
    LIGHT("Az Hareketli", "Haftada 1-3 gün hafif egzersiz", 1.375),
    MODERATE("Orta Hareketli", "Haftada 3-5 gün orta egzersiz", 1.55),
    VERY_ACTIVE("Çok Aktif", "Haftada 6-7 gün yoğun egzersiz", 1.725)
}

enum class GoalType(val titleTr: String, val calorieDelta: Double) {
    LOSE("Kilo Verme", -500.0),
    MAINTAIN("Kiloyu Koruma", 0.0),
    GAIN("Kilo Alma / Kas Kazanımı", 400.0)
}

enum class MacroPreset(val titleTr: String, val carbPct: Int, val proteinPct: Int, val fatPct: Int) {
    BALANCED("Dengeli (%50 K / %20 P / %30 Y)", 50, 20, 30),
    HIGH_PROTEIN("Yüksek Protein (%40 K / %35 P / %25 Y)", 40, 35, 25),
    LOW_CARB("Düşük Karb (%20 K / %40 P / %40 Y)", 20, 40, 40),
    CUSTOM("Özel Makro Dağılımı", 0, 0, 0)
}

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val servingName: String = "Porsiyon",
    val servingGrams: Double = 100.0,
    val caloriesPer100g: Double,
    val carbsPer100g: Double,
    val proteinPer100g: Double,
    val fatPer100g: Double,
    val fiberPer100g: Double = 0.0,
    val isCustom: Boolean = false
) {
    fun caloriesForGrams(grams: Double): Double = (caloriesPer100g * grams) / 100.0
    fun carbsForGrams(grams: Double): Double = (carbsPer100g * grams) / 100.0
    fun proteinForGrams(grams: Double): Double = (proteinPer100g * grams) / 100.0
    fun fatForGrams(grams: Double): Double = (fatPer100g * grams) / 100.0
    fun fiberForGrams(grams: Double): Double = (fiberPer100g * grams) / 100.0
}

@Entity(tableName = "meal_entries")
data class MealEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val mealType: String, // KAHVALTI, OGLE, AKSAM, ARA_OGUN
    val foodName: String,
    val amountGrams: Double,
    val servingDescription: String,
    val calories: Double,
    val carbs: Double,
    val protein: Double,
    val fat: Double,
    val fiber: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_summaries")
data class DailySummary(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val waterMl: Int = 0,
    val burnedExerciseKcal: Double = 0.0,
    val notes: String = "",
    val moodEmoji: String = "😊"
)

@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val weightKg: Double,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ThemeMode(val titleTr: String, val emoji: String) {
    SYSTEM("Sistem Teması", "📱"),
    LIGHT("Açık Tema", "☀️"),
    DARK("Koyu Tema", "🌙")
}

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "full_name") val userName: String = "",
    @ColumnInfo(name = "login_username") val username: String = "",
    val email: String = "",
    val password: String = "",
    val supabaseUserId: String? = null,
    val supabaseToken: String? = null,
    val avatarBase64: String? = null,
    val gender: String = Gender.ERKEK.name,
    val age: Int = 25,
    val heightCm: Double = 175.0,
    val currentWeightKg: Double = 70.0,
    val targetWeightKg: Double = 68.0,
    val targetPace: String = "DENGELI", // HAFIF, DENGELI, HIZLI
    val activityLevel: String = ActivityLevel.MODERATE.name,
    val exerciseFrequency: String = "Haftada 3-4 gün",
    val dietPreference: String = "Dengeli Beslenme (Akdeniz)",
    val goalType: String = GoalType.LOSE.name,
    val macroPreset: String = MacroPreset.BALANCED.name,
    val customTargetCalories: Double? = null,
    val customCarbsGrams: Double? = null,
    val customProteinGrams: Double? = null,
    val customFatGrams: Double? = null,
    val waterTargetMl: Int = 2500,
    val themeMode: String = ThemeMode.SYSTEM.name,
    val language: String = "TR", // "TR", "EN"
    val notifyBreakfast: Boolean = true,
    val notifyLunch: Boolean = true,
    val notifyDinner: Boolean = true,
    val notifySnack: Boolean = false,
    val breakfastTime: String = "08:30",
    val lunchTime: String = "12:30",
    val dinnerTime: String = "19:30",
    val snackTime: String = "16:00",
    val notifyWater: Boolean = true,
    val waterIntervalHours: Double = 2.0,
    val waterStartTime: String = "08:30",
    val waterEndTime: String = "22:00",
    val isRegistered: Boolean = false,
    val isLoggedIn: Boolean = false,
    val hasSeenGuide: Boolean = false,
    val geminiCalorieAdvice: String? = null
)

@Entity(tableName = "water_log_entries")
data class WaterLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis()
)
