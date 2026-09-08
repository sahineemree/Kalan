package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.CalculationResult
import com.example.calculator.GeminiCalculationResult
import com.example.calculator.GeminiFoodEstimateResult
import com.example.calculator.GeminiNutritionService
import com.example.calculator.NutritionCalculator
import com.example.data.ActivityLevel
import com.example.data.DailySummary
import com.example.data.FoodItem
import com.example.data.Gender
import com.example.data.GoalType
import com.example.data.KalanDatabase
import com.example.data.KalanRepository
import com.example.data.MealEntry
import com.example.data.MealType
import com.example.data.ThemeMode
import com.example.data.UserProfile
import com.example.data.WaterLogEntry
import com.example.data.WeightEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class WeeklyDayCalorieStat(
    val date: String,
    val dayShortLabel: String,
    val calories: Double,
    val targetCalories: Double,
    val isToday: Boolean
)

data class WeeklyTrendData(
    val days: List<WeeklyDayCalorieStat> = emptyList(),
    val weeklyTotal: Double = 0.0,
    val weeklyAverage: Double = 0.0,
    val daysReachedTarget: Int = 0,
    val totalRecordedDays: Int = 7,
    val todayCalories: Double = 0.0,
    val targetCalories: Double = 2150.0
)

class KalanViewModel(application: Application) : AndroidViewModel(application) {

    private val db = KalanDatabase.getDatabase(application, viewModelScope)
    private val repository = KalanRepository(db.kalanDao())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _selectedDate = MutableStateFlow(dateFormat.format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMealTypeForAdd = MutableStateFlow(MealType.KAHVALTI)
    val selectedMealTypeForAdd: StateFlow<MealType> = _selectedMealTypeForAdd.asStateFlow()

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .map { it ?: UserProfile() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile()
        )

    val calculationResult: StateFlow<CalculationResult> = userProfile
        .map { NutritionCalculator.calculateFromProfile(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NutritionCalculator.calculateFromProfile(UserProfile())
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentDateEntries: StateFlow<List<MealEntry>> = _selectedDate
        .flatMapLatest { date -> repository.getEntriesForDate(date) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentDateSummary: StateFlow<DailySummary> = _selectedDate
        .flatMapLatest { date ->
            repository.getSummaryForDate(date).map { it ?: DailySummary(date = date) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailySummary(date = dateFormat.format(Date()))
        )

    val allFoods: StateFlow<List<FoodItem>> = repository.allFoods
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val customFoods: StateFlow<List<FoodItem>> = repository.customFoods
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allWeights: StateFlow<List<WeightEntry>> = repository.allWeights
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allEntries: StateFlow<List<MealEntry>> = repository.allEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allSummaries: StateFlow<List<DailySummary>> = repository.allSummaries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentDateWaterLogs: StateFlow<List<WaterLogEntry>> = _selectedDate
        .flatMapLatest { date -> repository.getWaterLogsForDate(date) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val weeklyAverageWaterMl: StateFlow<Int> = allSummaries.map { summaries ->
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val last7DateStrings = (0..6).map { daysAgo ->
            val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }
            sdf.format(c.time)
        }.toSet()

        val relevant = summaries.filter { it.date in last7DateStrings && it.waterMl > 0 }
        if (relevant.isNotEmpty()) {
            (relevant.sumOf { it.waterMl } / relevant.size)
        } else 0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val weeklyTrendData: StateFlow<WeeklyTrendData> = combine(
        allEntries,
        calculationResult
    ) { entries, calcResult ->
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.forLanguageTag("tr-TR"))
        val todayStr = sdf.format(Date())
        val targetCal = calcResult.targetCalories

        val last7Days = (6 downTo 0).map { daysAgo ->
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }
            val dateStr = sdf.format(cal.time)
            val isToday = dateStr == todayStr
            val dayLabel = if (isToday) "Bugün" else dayFormat.format(cal.time).replace(".", "").replaceFirstChar { it.uppercase() }
            val dayKcal = entries.filter { it.date == dateStr }.sumOf { it.calories }
            WeeklyDayCalorieStat(
                date = dateStr,
                dayShortLabel = dayLabel,
                calories = dayKcal,
                targetCalories = targetCal,
                isToday = isToday
            )
        }

        val totalKcal = last7Days.sumOf { it.calories }
        val daysWithFood = last7Days.filter { it.calories > 0 }
        val avgKcal = if (daysWithFood.isNotEmpty()) totalKcal / daysWithFood.size else 0.0
        val reachedCount = last7Days.count { it.calories in (targetCal * 0.75)..(targetCal * 1.05) }
        val todayKcal = last7Days.find { it.isToday }?.calories ?: 0.0

        WeeklyTrendData(
            days = last7Days,
            weeklyTotal = totalKcal,
            weeklyAverage = avgKcal,
            daysReachedTarget = reachedCount,
            totalRecordedDays = if (daysWithFood.isNotEmpty()) daysWithFood.size else 1,
            todayCalories = todayKcal,
            targetCalories = targetCal
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WeeklyTrendData()
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            KalanDatabase.populateInitialData(db.kalanDao())
        }
        // Initialize Notification Channels
        com.example.notification.NotificationHelper.createNotificationChannels(application)

        // Reschedule alarms when user profile reminder settings change
        viewModelScope.launch {
            userProfile.collect { profile ->
                com.example.notification.ReminderScheduler.rescheduleAll(application, profile)
            }
        }
    }

    // Date Navigation
    fun goToPreviousDay() {
        changeDayBy(-1)
    }

    fun goToNextDay() {
        changeDayBy(1)
    }

    fun selectDate(newDate: String) {
        _selectedDate.value = newDate
    }

    fun goToToday() {
        _selectedDate.value = dateFormat.format(Date())
    }

    private fun changeDayBy(days: Int) {
        try {
            val date = dateFormat.parse(_selectedDate.value) ?: Date()
            val cal = Calendar.getInstance().apply {
                time = date
                add(Calendar.DAY_OF_YEAR, days)
            }
            _selectedDate.value = dateFormat.format(cal.time)
        } catch (e: Exception) {
            _selectedDate.value = dateFormat.format(Date())
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedMealTypeForAdd(mealType: MealType) {
        _selectedMealTypeForAdd.value = mealType
    }

    // Meal Entry Operations
    fun addFoodToMeal(
        mealType: MealType,
        food: FoodItem,
        grams: Double,
        servingDescription: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val entry = MealEntry(
                date = _selectedDate.value,
                mealType = mealType.name,
                foodName = food.name,
                amountGrams = grams,
                servingDescription = servingDescription,
                calories = food.caloriesForGrams(grams),
                carbs = food.carbsForGrams(grams),
                protein = food.proteinForGrams(grams),
                fat = food.fatForGrams(grams),
                fiber = food.fiberForGrams(grams)
            )
            repository.insertMealEntry(entry)
        }
    }

    fun addQuickCalorieEntry(
        mealType: MealType,
        title: String,
        calories: Double,
        carbs: Double,
        protein: Double,
        fat: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val entry = MealEntry(
                date = _selectedDate.value,
                mealType = mealType.name,
                foodName = if (title.isBlank()) "Hızlı Kalori Girişi" else title,
                amountGrams = 100.0,
                servingDescription = "${calories.toInt()} kcal",
                calories = calories,
                carbs = carbs,
                protein = protein,
                fat = fat,
                fiber = 0.0
            )
            repository.insertMealEntry(entry)
        }
    }

    fun deleteMealEntry(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMealEntry(id)
        }
    }

    fun updateMealEntry(entry: MealEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertMealEntry(entry)
        }
    }

    // Water Operations
    fun addWater(amountMl: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSummary = currentDateSummary.value
            repository.addWaterLog(_selectedDate.value, amountMl, currentSummary)
        }
    }

    fun subtractWater(amountMl: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSummary = currentDateSummary.value
            val currentAmount = currentSummary.waterMl
            repository.updateWater(_selectedDate.value, (currentAmount - amountMl).coerceAtLeast(0), currentSummary)
        }
    }

    fun deleteWaterLog(entry: WaterLogEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSummary = currentDateSummary.value
            repository.deleteWaterLog(entry.id, entry.date, entry.amountMl, currentSummary)
        }
    }

    fun addCustomWater(amountMl: Int) {
        if (amountMl > 0) {
            addWater(amountMl)
        }
    }

    // Burned Exercise Calories
    fun updateBurnedExercise(burnedKcal: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSummary = currentDateSummary.value
            repository.updateBurnedExercise(_selectedDate.value, burnedKcal, currentSummary)
        }
    }

    // Daily Note & Mood
    fun updateDailyNotes(notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSummary = currentDateSummary.value
            repository.updateNotes(_selectedDate.value, notes, currentSummary)
        }
    }

    fun updateDailyMoodAndNotes(moodEmoji: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSummary = currentDateSummary.value
            repository.updateMoodAndNotes(_selectedDate.value, moodEmoji, notes, currentSummary)
        }
    }

    fun updateAvatar(avatarBase64: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            repository.saveProfile(current.copy(avatarBase64 = avatarBase64))
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            repository.saveProfile(current.copy(userName = name.trim()))
        }
    }

    // Weight Logging
    fun logWeight(weightKg: Double, date: String = _selectedDate.value) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.logWeight(date, weightKg)
            // Also update current weight in profile
            val currentProfile = userProfile.value
            repository.saveProfile(currentProfile.copy(currentWeightKg = weightKg))
        }
    }

    fun deleteWeight(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWeight(id)
        }
    }

    // Custom Food Creation
    fun saveCustomFood(food: FoodItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFood(food.copy(isCustom = true))
        }
    }

    fun deleteCustomFood(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFood(id)
        }
    }

    // User Profile Updating
    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveProfile(profile)
        }
    }

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            repository.saveProfile(current.copy(themeMode = themeMode.name))
        }
    }

    fun updateWaterTarget(targetMl: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            repository.saveProfile(current.copy(waterTargetMl = targetMl))
        }
    }

    // Backup & Restore
    fun exportBackupJson(onResult: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val json = repository.exportBackupJson()
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                onResult(json)
            }
        }
    }

    fun importBackupJson(jsonString: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.importBackupJson(jsonString)
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    onResult(true, null)
                } else {
                    onResult(false, result.exceptionOrNull()?.message ?: "Bilinmeyen hata")
                }
            }
        }
    }

    // Sample Data & Reset Data
    fun loadSampleData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadSampleData()
        }
    }

    fun resetAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetAllData()
        }
    }

    // Account & Password Management
    fun updateAccountInfo(name: String, username: String, onResult: (Boolean, String) -> Unit) {
        val trimmedName = name.trim()
        val trimmedUsername = username.trim().lowercase()

        if (trimmedName.isBlank()) {
            onResult(false, "İsim alanı boş bırakılamaz.")
            return
        }
        if (trimmedUsername.isBlank()) {
            onResult(false, "Kullanıcı adı boş bırakılamaz.")
            return
        }
        if (trimmedUsername.length < 3) {
            onResult(false, "Kullanıcı adı en az 3 karakter olmalıdır.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            repository.saveProfile(current.copy(userName = trimmedName, username = trimmedUsername))
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                onResult(true, "Hesap bilgileri başarıyla güncellendi.")
            }
        }
    }

    fun changePassword(
        oldPass: String,
        newPass: String,
        confirmPass: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val current = userProfile.value
        if (oldPass != current.password) {
            onResult(false, "Mevcut şifreniz hatalı!")
            return
        }
        if (newPass.length < 4) {
            onResult(false, "Yeni şifre en az 4 karakter olmalıdır.")
            return
        }
        if (newPass != confirmPass) {
            onResult(false, "Girdiğiniz yeni şifreler birbiriyle eşleşmiyor!")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.saveProfile(current.copy(password = newPass))
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                onResult(true, "Şifreniz başarıyla değiştirildi.")
            }
        }
    }

    fun updateLanguage(lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            repository.saveProfile(current.copy(language = lang))
        }
    }

    fun updateMealNotification(meal: String, isEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            val updated = when (meal.lowercase()) {
                "breakfast", "kahvalti" -> current.copy(notifyBreakfast = isEnabled)
                "lunch", "ogle" -> current.copy(notifyLunch = isEnabled)
                "dinner", "aksam" -> current.copy(notifyDinner = isEnabled)
                "snack", "ara_ogun" -> current.copy(notifySnack = isEnabled)
                else -> current
            }
            repository.saveProfile(updated)
            com.example.notification.ReminderScheduler.rescheduleAll(getApplication(), updated)
        }
    }

    fun updateMealTime(meal: String, newTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            val updated = when (meal.lowercase()) {
                "breakfast", "kahvalti" -> current.copy(breakfastTime = newTime)
                "lunch", "ogle" -> current.copy(lunchTime = newTime)
                "dinner", "aksam" -> current.copy(dinnerTime = newTime)
                "snack", "ara_ogun" -> current.copy(snackTime = newTime)
                else -> current
            }
            repository.saveProfile(updated)
            com.example.notification.ReminderScheduler.rescheduleAll(getApplication(), updated)
        }
    }

    fun updateWaterNotification(isEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            val updated = current.copy(notifyWater = isEnabled)
            repository.saveProfile(updated)
            com.example.notification.ReminderScheduler.rescheduleAll(getApplication(), updated)
        }
    }

    fun updateWaterInterval(intervalHours: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            val updated = current.copy(waterIntervalHours = intervalHours)
            repository.saveProfile(updated)
            com.example.notification.ReminderScheduler.rescheduleAll(getApplication(), updated)
        }
    }

    fun updateWaterTimeRange(startTime: String, endTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            val updated = current.copy(waterStartTime = startTime, waterEndTime = endTime)
            repository.saveProfile(updated)
            com.example.notification.ReminderScheduler.rescheduleAll(getApplication(), updated)
        }
    }

    fun sendTestNotification(type: com.example.notification.ReminderType) {
        val isEn = userProfile.value.language == "EN"
        com.example.notification.NotificationHelper.showReminderNotification(
            context = getApplication(),
            type = type,
            isEnglish = isEn
        )
    }

    fun logOut() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            com.example.auth.SupabaseAuthService.signOut(current.supabaseToken)
            repository.saveProfile(current.copy(isLoggedIn = false))
        }
    }

    fun logout() = logOut()

    fun deleteAccount(onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            com.example.auth.SupabaseAuthService.signOut(current.supabaseToken)
            repository.resetAllData()
            repository.saveProfile(
                UserProfile(
                    id = 1,
                    userName = "Kullanıcı",
                    username = "kullanici",
                    email = "",
                    password = "",
                    supabaseUserId = null,
                    supabaseToken = null,
                    isRegistered = false,
                    isLoggedIn = false
                )
            )
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun loginWithCredentials(
        enteredUsername: String,
        enteredPass: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val current = userProfile.value
        val cleanUser = enteredUsername.trim()
        if (cleanUser.isBlank() || enteredPass.isBlank()) {
            onResult(false, "Lütfen kullanıcı adı / e-posta ve şifrenizi girin.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            // First attempt Supabase remote authentication
            val supabaseResult = com.example.auth.SupabaseAuthService.signIn(cleanUser, enteredPass)
            if (supabaseResult.isSuccess) {
                val updatedProfile = current.copy(
                    username = if (cleanUser.contains("@")) current.username.ifBlank { cleanUser.substringBefore("@") } else cleanUser.lowercase(),
                    email = supabaseResult.email ?: current.email,
                    password = enteredPass,
                    supabaseUserId = supabaseResult.userId,
                    supabaseToken = supabaseResult.accessToken,
                    isLoggedIn = true,
                    isRegistered = true
                )
                repository.saveProfile(updatedProfile)
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    onResult(true, "Giriş başarılı!")
                }
                return@launch
            }

            // Fallback: If Supabase fails due to network or if local credentials match
            if (cleanUser.lowercase() == current.username.lowercase() && enteredPass == current.password) {
                repository.saveProfile(current.copy(isLoggedIn = true, isRegistered = true))
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    onResult(true, "Giriş başarılı (Çevrimdışı Mod)!")
                }
            } else {
                val failureMsg = supabaseResult.errorMessage ?: "Kullanıcı adı veya şifre hatalı!"
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    onResult(false, failureMsg)
                }
            }
        }
    }

    fun sendPasswordReset(
        emailOrUsername: String,
        onResult: (Boolean, String) -> Unit
    ) {
        if (emailOrUsername.isBlank()) {
            onResult(false, "Lütfen e-posta adresinizi veya kullanıcı adınızı girin.")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val res = com.example.auth.SupabaseAuthService.sendPasswordReset(emailOrUsername)
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                onResult(res.first, res.second)
            }
        }
    }

    fun registerWithGemini(
        name: String,
        username: String,
        pass: String,
        gender: Gender,
        age: Int,
        heightCm: Double,
        weightKg: Double,
        targetWeightKg: Double,
        goalType: GoalType,
        targetPace: String,
        activityLevel: ActivityLevel,
        exerciseFrequency: String,
        dietPreference: String,
        onComplete: (GeminiCalculationResult) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // Register on Supabase
            val supabaseResult = com.example.auth.SupabaseAuthService.signUp(
                emailOrUsername = username,
                password = pass,
                displayName = name,
                username = username
            )

            val geminiResult = GeminiNutritionService.calculatePersonalizedNutrition(
                gender = gender,
                age = age,
                heightCm = heightCm,
                currentWeightKg = weightKg,
                targetWeightKg = targetWeightKg,
                goalType = goalType,
                targetPace = targetPace,
                activityLevel = activityLevel,
                exerciseFrequency = exerciseFrequency,
                dietPreference = dietPreference
            )

            val profile = UserProfile(
                id = 1,
                userName = name.trim(),
                username = username.trim().lowercase(),
                email = supabaseResult.email ?: com.example.auth.SupabaseAuthService.normalizeEmail(username),
                password = pass,
                supabaseUserId = supabaseResult.userId,
                supabaseToken = supabaseResult.accessToken,
                gender = gender.name,
                age = age,
                heightCm = heightCm,
                currentWeightKg = weightKg,
                targetWeightKg = targetWeightKg,
                goalType = goalType.name,
                targetPace = targetPace,
                activityLevel = activityLevel.name,
                exerciseFrequency = exerciseFrequency,
                dietPreference = dietPreference,
                customTargetCalories = geminiResult.targetCalories,
                customCarbsGrams = geminiResult.targetCarbsGrams,
                customProteinGrams = geminiResult.targetProteinGrams,
                customFatGrams = geminiResult.targetFatGrams,
                waterTargetMl = geminiResult.targetWaterMl,
                geminiCalorieAdvice = geminiResult.summaryReasoning,
                isRegistered = true,
                isLoggedIn = true
            )
            repository.saveProfile(profile)
            repository.logWeight(_selectedDate.value, weightKg)

            kotlinx.coroutines.withContext(Dispatchers.Main) {
                onComplete(geminiResult)
            }
        }
    }

    fun recalculateCaloriesWithGemini(onComplete: (GeminiCalculationResult) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            val gender = try { Gender.valueOf(current.gender) } catch (e: Exception) { Gender.ERKEK }
            val goal = try { GoalType.valueOf(current.goalType) } catch (e: Exception) { GoalType.LOSE }
            val activity = try { ActivityLevel.valueOf(current.activityLevel) } catch (e: Exception) { ActivityLevel.MODERATE }

            val geminiResult = GeminiNutritionService.calculatePersonalizedNutrition(
                gender = gender,
                age = current.age,
                heightCm = current.heightCm,
                currentWeightKg = current.currentWeightKg,
                targetWeightKg = current.targetWeightKg,
                goalType = goal,
                targetPace = current.targetPace,
                activityLevel = activity,
                exerciseFrequency = current.exerciseFrequency,
                dietPreference = current.dietPreference
            )

            val updatedProfile = current.copy(
                customTargetCalories = geminiResult.targetCalories,
                customCarbsGrams = geminiResult.targetCarbsGrams,
                customProteinGrams = geminiResult.targetProteinGrams,
                customFatGrams = geminiResult.targetFatGrams,
                waterTargetMl = geminiResult.targetWaterMl,
                geminiCalorieAdvice = geminiResult.summaryReasoning
            )
            repository.saveProfile(updatedProfile)

            kotlinx.coroutines.withContext(Dispatchers.Main) {
                onComplete(geminiResult)
            }
        }
    }

    fun markGuideSeen() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            repository.saveProfile(current.copy(hasSeenGuide = true))
        }
    }

    suspend fun estimateFoodNutrition(
        foodName: String,
        portion: String
    ): GeminiFoodEstimateResult {
        return GeminiNutritionService.estimateFoodNutrition(foodName, portion)
    }
}
