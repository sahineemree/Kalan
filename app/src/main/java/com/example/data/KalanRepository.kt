package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class KalanRepository(private val dao: KalanDao) {

    // Foods
    val allFoods: Flow<List<FoodItem>> = dao.getAllFoods()
    val customFoods: Flow<List<FoodItem>> = dao.getCustomFoods()

    fun searchFoods(query: String): Flow<List<FoodItem>> = dao.searchFoods(query)

    suspend fun insertFood(food: FoodItem): Long = dao.insertFood(food)
    suspend fun deleteFood(id: Long) = dao.deleteFood(id)

    // Meals
    fun getEntriesForDate(date: String): Flow<List<MealEntry>> = dao.getEntriesForDate(date)
    fun getEntriesBetweenDates(startDate: String, endDate: String): Flow<List<MealEntry>> =
        dao.getEntriesBetweenDates(startDate, endDate)
    val allEntries: Flow<List<MealEntry>> = dao.getAllEntries()

    suspend fun insertMealEntry(entry: MealEntry): Long = dao.insertMealEntry(entry)
    suspend fun deleteMealEntry(id: Long) = dao.deleteMealEntry(id)

    // Daily Summary (Water, Burned calories, Daily notes)
    fun getSummaryForDate(date: String): Flow<DailySummary?> = dao.getSummaryForDate(date)
    val allSummaries: Flow<List<DailySummary>> = dao.getAllSummaries()

    // Water Logs
    fun getWaterLogsForDate(date: String): Flow<List<WaterLogEntry>> = dao.getWaterLogsForDate(date)
    fun getWaterLogsBetweenDates(startDate: String, endDate: String): Flow<List<WaterLogEntry>> =
        dao.getWaterLogsBetweenDates(startDate, endDate)

    suspend fun addWaterLog(date: String, amountMl: Int, currentSummary: DailySummary?): Long {
        val entryId = dao.insertWaterLog(
            WaterLogEntry(
                date = date,
                amountMl = amountMl,
                timestamp = System.currentTimeMillis()
            )
        )
        val currentWater = currentSummary?.waterMl ?: 0
        updateWater(date, currentWater + amountMl, currentSummary)
        return entryId
    }

    suspend fun deleteWaterLog(id: Long, date: String, amountMl: Int, currentSummary: DailySummary?) {
        dao.deleteWaterLog(id)
        val currentWater = currentSummary?.waterMl ?: 0
        updateWater(date, maxOf(0, currentWater - amountMl), currentSummary)
    }

    suspend fun updateWater(date: String, newAmount: Int, currentSummary: DailySummary?) {
        val updated = currentSummary?.copy(waterMl = newAmount.coerceAtLeast(0))
            ?: DailySummary(date = date, waterMl = newAmount.coerceAtLeast(0))
        dao.insertOrUpdateSummary(updated)
    }

    suspend fun updateBurnedExercise(date: String, burnedKcal: Double, currentSummary: DailySummary?) {
        val updated = currentSummary?.copy(burnedExerciseKcal = burnedKcal.coerceAtLeast(0.0))
            ?: DailySummary(date = date, burnedExerciseKcal = burnedKcal.coerceAtLeast(0.0))
        dao.insertOrUpdateSummary(updated)
    }

    suspend fun updateNotes(date: String, notes: String, currentSummary: DailySummary?) {
        val updated = currentSummary?.copy(notes = notes)
            ?: DailySummary(date = date, notes = notes)
        dao.insertOrUpdateSummary(updated)
    }

    suspend fun updateMoodAndNotes(date: String, moodEmoji: String, notes: String, currentSummary: DailySummary?) {
        val updated = currentSummary?.copy(moodEmoji = moodEmoji, notes = notes)
            ?: DailySummary(date = date, moodEmoji = moodEmoji, notes = notes)
        dao.insertOrUpdateSummary(updated)
    }

    // Weights
    val allWeights: Flow<List<WeightEntry>> = dao.getAllWeights()

    suspend fun logWeight(date: String, weightKg: Double): Long =
        dao.insertWeight(WeightEntry(date = date, weightKg = weightKg))

    suspend fun deleteWeight(id: Long) = dao.deleteWeight(id)

    // Profile
    val userProfile: Flow<UserProfile?> = dao.getUserProfile()

    suspend fun saveProfile(profile: UserProfile) = dao.insertOrUpdateProfile(profile)

    suspend fun getFoodCount(): Int = dao.getFoodCount()

    // Export complete application data to JSON string
    suspend fun exportBackupJson(): String {
        val root = org.json.JSONObject()
        root.put("app", "Kalan")
        root.put("version", "1.0.0 Pro PWA")
        root.put("exportDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
        root.put("timestamp", System.currentTimeMillis())

        // Profile
        val profile = dao.getUserProfileOnce()
        if (profile != null) {
            val pObj = org.json.JSONObject()
            pObj.put("userName", profile.userName)
            profile.avatarBase64?.let { pObj.put("avatarBase64", it) }
            pObj.put("gender", profile.gender)
            pObj.put("age", profile.age)
            pObj.put("heightCm", profile.heightCm)
            pObj.put("currentWeightKg", profile.currentWeightKg)
            pObj.put("targetWeightKg", profile.targetWeightKg)
            pObj.put("activityLevel", profile.activityLevel)
            pObj.put("goalType", profile.goalType)
            pObj.put("macroPreset", profile.macroPreset)
            profile.customTargetCalories?.let { pObj.put("customTargetCalories", it) }
            profile.customCarbsGrams?.let { pObj.put("customCarbsGrams", it) }
            profile.customProteinGrams?.let { pObj.put("customProteinGrams", it) }
            profile.customFatGrams?.let { pObj.put("customFatGrams", it) }
            pObj.put("waterTargetMl", profile.waterTargetMl)
            pObj.put("themeMode", profile.themeMode)
            root.put("profile", pObj)
        }

        // Custom Foods
        val allFoods = dao.getAllFoodsList()
        val customFoods = allFoods.filter { it.isCustom }
        val foodsArray = org.json.JSONArray()
        for (f in customFoods) {
            val fObj = org.json.JSONObject()
            fObj.put("name", f.name)
            fObj.put("category", f.category)
            fObj.put("caloriesPer100g", f.caloriesPer100g)
            fObj.put("carbsPer100g", f.carbsPer100g)
            fObj.put("proteinPer100g", f.proteinPer100g)
            fObj.put("fatPer100g", f.fatPer100g)
            fObj.put("fiberPer100g", f.fiberPer100g)
            fObj.put("servingGrams", f.servingGrams)
            fObj.put("servingName", f.servingName)
            foodsArray.put(fObj)
        }
        root.put("customFoods", foodsArray)

        // Meal Entries
        val entries = dao.getAllEntriesList()
        val entriesArray = org.json.JSONArray()
        for (e in entries) {
            val eObj = org.json.JSONObject()
            eObj.put("date", e.date)
            eObj.put("mealType", e.mealType)
            eObj.put("foodName", e.foodName)
            eObj.put("amountGrams", e.amountGrams)
            eObj.put("servingDescription", e.servingDescription)
            eObj.put("calories", e.calories)
            eObj.put("carbs", e.carbs)
            eObj.put("protein", e.protein)
            eObj.put("fat", e.fat)
            eObj.put("fiber", e.fiber)
            eObj.put("timestamp", e.timestamp)
            entriesArray.put(eObj)
        }
        root.put("mealEntries", entriesArray)

        // Daily Summaries
        val summaries = dao.getAllSummariesList()
        val summariesArray = org.json.JSONArray()
        for (s in summaries) {
            val sObj = org.json.JSONObject()
            sObj.put("date", s.date)
            sObj.put("waterMl", s.waterMl)
            sObj.put("burnedExerciseKcal", s.burnedExerciseKcal)
            sObj.put("notes", s.notes)
            sObj.put("moodEmoji", s.moodEmoji)
            summariesArray.put(sObj)
        }
        root.put("dailySummaries", summariesArray)

        // Weight Entries
        val weights = dao.getAllWeightsList()
        val weightsArray = org.json.JSONArray()
        for (w in weights) {
            val wObj = org.json.JSONObject()
            wObj.put("date", w.date)
            wObj.put("weightKg", w.weightKg)
            wObj.put("timestamp", w.timestamp)
            weightsArray.put(wObj)
        }
        root.put("weightEntries", weightsArray)

        return root.toString(2)
    }

    // Import application data from JSON string
    suspend fun importBackupJson(jsonString: String): Result<Unit> {
        return try {
            val root = org.json.JSONObject(jsonString)

            // Import Profile
            if (root.has("profile")) {
                val pObj = root.getJSONObject("profile")
                val current = dao.getUserProfileOnce() ?: UserProfile()
                val updatedProfile = current.copy(
                    userName = pObj.optString("userName", current.userName),
                    avatarBase64 = if (pObj.has("avatarBase64")) pObj.getString("avatarBase64") else current.avatarBase64,
                    gender = pObj.optString("gender", current.gender),
                    age = pObj.optInt("age", current.age),
                    heightCm = pObj.optDouble("heightCm", current.heightCm),
                    currentWeightKg = pObj.optDouble("currentWeightKg", current.currentWeightKg),
                    targetWeightKg = pObj.optDouble("targetWeightKg", current.targetWeightKg),
                    activityLevel = pObj.optString("activityLevel", current.activityLevel),
                    goalType = pObj.optString("goalType", current.goalType),
                    macroPreset = pObj.optString("macroPreset", current.macroPreset),
                    customTargetCalories = if (pObj.has("customTargetCalories")) pObj.optDouble("customTargetCalories") else current.customTargetCalories,
                    customCarbsGrams = if (pObj.has("customCarbsGrams")) pObj.optDouble("customCarbsGrams") else current.customCarbsGrams,
                    customProteinGrams = if (pObj.has("customProteinGrams")) pObj.optDouble("customProteinGrams") else current.customProteinGrams,
                    customFatGrams = if (pObj.has("customFatGrams")) pObj.optDouble("customFatGrams") else current.customFatGrams,
                    waterTargetMl = pObj.optInt("waterTargetMl", current.waterTargetMl),
                    themeMode = pObj.optString("themeMode", current.themeMode)
                )
                dao.insertOrUpdateProfile(updatedProfile)
            }

            // Import Custom Foods
            if (root.has("customFoods")) {
                val foodsArray = root.getJSONArray("customFoods")
                val newFoods = mutableListOf<FoodItem>()
                for (i in 0 until foodsArray.length()) {
                    val f = foodsArray.getJSONObject(i)
                    newFoods.add(
                        FoodItem(
                            name = f.getString("name"),
                            category = f.optString("category", "Özel Yemek"),
                            caloriesPer100g = f.getDouble("caloriesPer100g"),
                            carbsPer100g = f.getDouble("carbsPer100g"),
                            proteinPer100g = f.getDouble("proteinPer100g"),
                            fatPer100g = f.getDouble("fatPer100g"),
                            fiberPer100g = f.optDouble("fiberPer100g", 0.0),
                            servingGrams = f.optDouble("servingGrams", f.optDouble("defaultServingGrams", 100.0)),
                            servingName = f.optString("servingName", f.optString("servingUnitName", "Porsiyon")),
                            isCustom = true
                        )
                    )
                }
                if (newFoods.isNotEmpty()) {
                    dao.insertFoods(newFoods)
                }
            }

            // Import Meal Entries
            if (root.has("mealEntries")) {
                val entriesArray = root.getJSONArray("mealEntries")
                val newEntries = mutableListOf<MealEntry>()
                for (i in 0 until entriesArray.length()) {
                    val e = entriesArray.getJSONObject(i)
                    newEntries.add(
                        MealEntry(
                            date = e.getString("date"),
                            mealType = e.getString("mealType"),
                            foodName = e.getString("foodName"),
                            amountGrams = e.getDouble("amountGrams"),
                            servingDescription = e.optString("servingDescription", ""),
                            calories = e.getDouble("calories"),
                            carbs = e.getDouble("carbs"),
                            protein = e.getDouble("protein"),
                            fat = e.getDouble("fat"),
                            fiber = e.optDouble("fiber", 0.0),
                            timestamp = e.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
                if (newEntries.isNotEmpty()) {
                    dao.insertMealEntries(newEntries)
                }
            }

            // Import Daily Summaries
            if (root.has("dailySummaries")) {
                val summariesArray = root.getJSONArray("dailySummaries")
                val newSummaries = mutableListOf<DailySummary>()
                for (i in 0 until summariesArray.length()) {
                    val s = summariesArray.getJSONObject(i)
                    newSummaries.add(
                        DailySummary(
                            date = s.getString("date"),
                            waterMl = s.optInt("waterMl", 0),
                            burnedExerciseKcal = s.optDouble("burnedExerciseKcal", 0.0),
                            notes = s.optString("notes", ""),
                            moodEmoji = s.optString("moodEmoji", "😊")
                        )
                    )
                }
                if (newSummaries.isNotEmpty()) {
                    dao.insertSummaries(newSummaries)
                }
            }

            // Import Weight Entries
            if (root.has("weightEntries")) {
                val weightsArray = root.getJSONArray("weightEntries")
                val newWeights = mutableListOf<WeightEntry>()
                for (i in 0 until weightsArray.length()) {
                    val w = weightsArray.getJSONObject(i)
                    newWeights.add(
                        WeightEntry(
                            date = w.getString("date"),
                            weightKg = w.getDouble("weightKg"),
                            timestamp = w.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
                if (newWeights.isNotEmpty()) {
                    dao.insertWeights(newWeights)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Reset All Data (keep default food library)
    suspend fun resetAllData() {
        dao.deleteAllMealEntries()
        dao.deleteAllSummaries()
        dao.deleteAllWeights()
        dao.deleteAllWaterLogs()
        // Reset custom foods back to just defaults if needed
        val count = dao.getFoodCount()
        if (count == 0) {
            dao.insertFoods(TurkishFoodDatabase.initialFoods)
        }
        // Reset profile to clean new user defaults
        dao.insertOrUpdateProfile(
            UserProfile(
                id = 1,
                isRegistered = false,
                isLoggedIn = false
            )
        )
    }

    // Load Sample Data (Populates realistic 7-day diary with Turkish meals and weight progression)
    suspend fun loadSampleData() {
        // Clear previous user meals, summaries, weights
        dao.deleteAllMealEntries()
        dao.deleteAllSummaries()
        dao.deleteAllWeights()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()

        val sampleDays = listOf(
            // Day 0: Today
            SampleDay(
                daysAgo = 0,
                waterMl = 2250,
                burnedKcal = 320.0,
                note = "Bugün 45 dk tempolu yürüyüş yapıldı, harika geçti.",
                weightKg = 76.0,
                meals = listOf(
                    SampleMeal(MealType.KAHVALTI, "Menemen", 200.0, "1 Porsiyon (200g)", 190.0, 7.0, 9.6, 14.0, 2.4),
                    SampleMeal(MealType.KAHVALTI, "Tam Buğday Ekmeği", 60.0, "2 Dilim (60g)", 147.0, 27.0, 5.7, 1.5, 4.1),
                    SampleMeal(MealType.KAHVALTI, "Beyaz Peynir (Tam Yağlı)", 30.0, "1 Dilim (30g)", 78.0, 0.6, 4.8, 6.3, 0.0),
                    SampleMeal(MealType.OGLE, "Tavuklu Pirinç Pilavı", 250.0, "1 Porsiyon (250g)", 437.5, 60.0, 23.75, 11.25, 2.0),
                    SampleMeal(MealType.OGLE, "Geleneksel Yayık Ayran", 200.0, "1 Su Bardağı (200ml)", 76.0, 5.6, 4.0, 4.0, 0.0),
                    SampleMeal(MealType.ARA_OGUN, "Kırmızı Elma", 150.0, "1 Adet Orta (150g)", 78.0, 20.7, 0.45, 0.3, 3.6),
                    SampleMeal(MealType.ARA_OGUN, "Çiğ Badem", 25.0, "1 Avuç (25g)", 144.7, 5.4, 5.3, 12.5, 3.1),
                    SampleMeal(MealType.AKSAM, "Kırmızı Mercimek Çorbası", 250.0, "1 Kase (250ml)", 137.5, 21.25, 8.0, 3.0, 4.5),
                    SampleMeal(MealType.AKSAM, "Izgara Dana Köfte", 160.0, "4-5 Köfte (160g)", 352.0, 8.0, 28.8, 22.4, 0.8),
                    SampleMeal(MealType.AKSAM, "Geleneksel Çoban Salata", 150.0, "1 Porsiyon (150g)", 67.5, 6.75, 1.8, 3.75, 2.25)
                )
            ),
            // Day 1: Yesterday
            SampleDay(
                daysAgo = 1,
                waterMl = 2500,
                burnedKcal = 410.0,
                note = "Spor salonunda bacak ve kardiyo antrenmanı.",
                weightKg = 76.3,
                meals = listOf(
                    SampleMeal(MealType.KAHVALTI, "Yulaf Ezmesi", 50.0, "5 Kaşık (50g)", 185.0, 30.0, 6.25, 3.5, 5.0),
                    SampleMeal(MealType.KAHVALTI, "Haşlanmış Yumurta", 110.0, "2 Adet (110g)", 170.5, 1.2, 13.86, 11.66, 0.0),
                    SampleMeal(MealType.OGLE, "Kıymalı Biber Dolması", 220.0, "2 Adet (220g)", 264.0, 24.2, 12.1, 13.2, 4.6),
                    SampleMeal(MealType.OGLE, "Süzme Yoğurt", 150.0, "1 Kase (150g)", 172.5, 6.75, 12.75, 10.5, 0.0),
                    SampleMeal(MealType.ARA_OGUN, "Yerli Muz", 120.0, "1 Adet (120g)", 106.8, 27.36, 1.32, 0.36, 3.12),
                    SampleMeal(MealType.AKSAM, "Izgara Tavuk Göğsü", 200.0, "1 Porsiyon (200g)", 260.0, 0.0, 54.0, 5.0, 0.0),
                    SampleMeal(MealType.AKSAM, "Meyhane Bulgur Pilavı", 180.0, "1 Porsiyon (180g)", 252.0, 41.4, 8.1, 6.84, 8.1)
                )
            ),
            // Day 2
            SampleDay(
                daysAgo = 2,
                waterMl = 2000,
                burnedKcal = 200.0,
                note = "Yoğun çalışma günü, hafif akşam yemeği.",
                weightKg = 76.6,
                meals = listOf(
                    SampleMeal(MealType.KAHVALTI, "Susamlı Simit", 100.0, "1 Adet Simit", 320.0, 58.0, 10.0, 5.5, 3.5),
                    SampleMeal(MealType.KAHVALTI, "Beyaz Peynir (Tam Yağlı)", 50.0, "Orta Dilim (50g)", 130.0, 1.0, 8.0, 10.5, 0.0),
                    SampleMeal(MealType.OGLE, "Etli Kuru Fasulye", 250.0, "1 Porsiyon (250g)", 337.5, 37.5, 21.25, 12.0, 13.0),
                    SampleMeal(MealType.OGLE, "Tereyağlı Şehriyeli Pirinç Pilavı", 150.0, "Porsiyon (150g)", 277.5, 45.0, 4.8, 9.0, 1.2),
                    SampleMeal(MealType.AKSAM, "Ezogelin Çorbası", 250.0, "1 Kase (250ml)", 150.0, 23.0, 7.0, 3.75, 3.75),
                    SampleMeal(MealType.AKSAM, "Zeytinyağlı Taze Fasulye", 200.0, "1 Porsiyon (200g)", 140.0, 12.0, 4.0, 8.4, 5.6)
                )
            ),
            // Day 3
            SampleDay(
                daysAgo = 3,
                waterMl = 2750,
                burnedKcal = 500.0,
                note = "10 km açık hava bisiklet sürüşü yapıldı.",
                weightKg = 76.8,
                meals = listOf(
                    SampleMeal(MealType.KAHVALTI, "Sucuklu Yumurta", 150.0, "Porsiyon (150g)", 360.0, 2.25, 21.0, 30.0, 0.15),
                    SampleMeal(MealType.KAHVALTI, "Çavdar Ekmeği", 60.0, "2 Dilim (60g)", 138.0, 26.4, 5.1, 1.08, 3.48),
                    SampleMeal(MealType.OGLE, "Lahmacun (Çıtır)", 280.0, "2 Adet Lahmacun (280g)", 490.0, 70.0, 21.84, 14.56, 5.04),
                    SampleMeal(MealType.OGLE, "Geleneksel Yayık Ayran", 300.0, "Büyük Bardak (300ml)", 114.0, 8.4, 6.0, 6.0, 0.0),
                    SampleMeal(MealType.AKSAM, "Fırında Somon Izgara", 180.0, "Fileto (180g)", 324.0, 0.0, 39.6, 18.0, 0.0),
                    SampleMeal(MealType.AKSAM, "Mevsim Yeşillikleri Salatası", 200.0, "Büyük Salata (200g)", 76.0, 6.4, 2.8, 4.4, 3.6)
                )
            ),
            // Day 4
            SampleDay(
                daysAgo = 4,
                waterMl = 2250,
                burnedKcal = 350.0,
                note = "Akşam yürüyüşü ve esneme hareketleri.",
                weightKg = 77.2,
                meals = listOf(
                    SampleMeal(MealType.KAHVALTI, "Haşlanmış Yumurta", 110.0, "2 Adet (110g)", 170.5, 1.2, 13.86, 11.66, 0.0),
                    SampleMeal(MealType.KAHVALTI, "Lor Peyniri (Yağsız)", 60.0, "2 Kaşık (60g)", 51.0, 1.8, 10.2, 0.6, 0.0),
                    SampleMeal(MealType.OGLE, "Karnıyarık (Kıymalı Patlıcan)", 200.0, "1 Adet (200g)", 250.0, 11.0, 13.0, 18.0, 5.0),
                    SampleMeal(MealType.OGLE, "Cacık (Salatalıklı & Naneli)", 200.0, "1 Kase (200g)", 110.0, 8.0, 7.0, 5.6, 1.0),
                    SampleMeal(MealType.AKSAM, "Kırmızı Mercimek Çorbası", 250.0, "1 Kase (250ml)", 137.5, 21.25, 8.0, 3.0, 4.5),
                    SampleMeal(MealType.AKSAM, "Klasik Kısır", 150.0, "1 Porsiyon (150g)", 262.5, 40.5, 6.3, 9.0, 6.0)
                )
            ),
            // Day 5
            SampleDay(
                daysAgo = 5,
                waterMl = 2500,
                burnedKcal = 280.0,
                note = "Düzenli beslenmeye devam, hafif kardiyo.",
                weightKg = 77.5,
                meals = listOf(
                    SampleMeal(MealType.KAHVALTI, "Menemen", 200.0, "1 Porsiyon (200g)", 190.0, 7.0, 9.6, 14.0, 2.4),
                    SampleMeal(MealType.KAHVALTI, "Tam Buğday Ekmeği", 60.0, "2 Dilim (60g)", 147.0, 27.0, 5.7, 1.5, 4.1),
                    SampleMeal(MealType.OGLE, "Etli Nohut Yemeği", 250.0, "1 Porsiyon (250g)", 362.5, 41.25, 22.5, 12.5, 12.0),
                    SampleMeal(MealType.OGLE, "Geleneksel Yayık Ayran", 200.0, "1 Su Bardağı (200ml)", 76.0, 5.6, 4.0, 4.0, 0.0),
                    SampleMeal(MealType.AKSAM, "Sebzeli Tavuk Sote", 200.0, "1 Porsiyon (200g)", 230.0, 8.0, 30.0, 9.0, 2.4),
                    SampleMeal(MealType.AKSAM, "Meyhane Bulgur Pilavı", 150.0, "Porsiyon (150g)", 210.0, 34.5, 6.75, 5.7, 6.75)
                )
            ),
            // Day 6
            SampleDay(
                daysAgo = 6,
                waterMl = 2100,
                burnedKcal = 150.0,
                note = "Haftalık planlama yapıldı.",
                weightKg = 78.0,
                meals = listOf(
                    SampleMeal(MealType.KAHVALTI, "Peynirli Poğaça", 80.0, "1 Adet Poğaça (80g)", 272.0, 32.0, 6.8, 13.2, 1.44),
                    SampleMeal(MealType.KAHVALTI, "Sade Türk Kahvesi", 70.0, "1 Fincan (70ml)", 1.4, 0.14, 0.07, 0.0, 0.0),
                    SampleMeal(MealType.OGLE, "Kayseri Mantısı (Yoğurtlu & Tereyağlı)", 250.0, "1 Porsiyon (250g)", 450.0, 60.0, 19.5, 15.5, 3.0),
                    SampleMeal(MealType.AKSAM, "Izgara Levrek", 200.0, "1 Porsiyon (200g)", 220.0, 0.0, 42.0, 5.6, 0.0),
                    SampleMeal(MealType.AKSAM, "Geleneksel Çoban Salata", 150.0, "1 Porsiyon (150g)", 67.5, 6.75, 1.8, 3.75, 2.25)
                )
            )
        )

        sampleDays.forEach { sample ->
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -sample.daysAgo)
            val dateStr = sdf.format(cal.time)

            dao.insertOrUpdateSummary(
                DailySummary(
                    date = dateStr,
                    waterMl = sample.waterMl,
                    burnedExerciseKcal = sample.burnedKcal,
                    notes = sample.note
                )
            )

            dao.insertWeight(
                WeightEntry(
                    date = dateStr,
                    weightKg = sample.weightKg,
                    timestamp = cal.timeInMillis
                )
            )

            sample.meals.forEach { meal ->
                dao.insertMealEntry(
                    MealEntry(
                        date = dateStr,
                        mealType = meal.type.name,
                        foodName = meal.name,
                        amountGrams = meal.grams,
                        servingDescription = meal.serving,
                        calories = meal.calories,
                        carbs = meal.carbs,
                        protein = meal.protein,
                        fat = meal.fat,
                        fiber = meal.fiber,
                        timestamp = cal.timeInMillis + (when (meal.type) {
                            MealType.KAHVALTI -> 8 * 3600 * 1000
                            MealType.OGLE -> 13 * 3600 * 1000
                            MealType.ARA_OGUN -> 16 * 3600 * 1000
                            MealType.AKSAM -> 19 * 3600 * 1000
                        })
                    )
                )
            }
        }
    }

    private data class SampleDay(
        val daysAgo: Int,
        val waterMl: Int,
        val burnedKcal: Double,
        val note: String,
        val weightKg: Double,
        val meals: List<SampleMeal>
    )

    private data class SampleMeal(
        val type: MealType,
        val name: String,
        val grams: Double,
        val serving: String,
        val calories: Double,
        val carbs: Double,
        val protein: Double,
        val fat: Double,
        val fiber: Double
    )
}
