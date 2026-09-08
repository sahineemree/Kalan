package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface KalanDao {
    // Foods
    @Query("SELECT * FROM food_items ORDER BY isCustom DESC, name ASC")
    fun getAllFoods(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY isCustom DESC, name ASC")
    fun searchFoods(query: String): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE isCustom = 1 ORDER BY name ASC")
    fun getCustomFoods(): Flow<List<FoodItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodItem): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFoods(foods: List<FoodItem>)

    @Query("DELETE FROM food_items WHERE id = :id")
    suspend fun deleteFood(id: Long)

    @Query("SELECT COUNT(*) FROM food_items")
    suspend fun getFoodCount(): Int

    // Meal Entries
    @Query("SELECT * FROM meal_entries WHERE date = :date ORDER BY timestamp ASC")
    fun getEntriesForDate(date: String): Flow<List<MealEntry>>

    @Query("SELECT * FROM meal_entries WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC, timestamp ASC")
    fun getEntriesBetweenDates(startDate: String, endDate: String): Flow<List<MealEntry>>

    @Query("SELECT * FROM meal_entries ORDER BY date DESC, timestamp DESC")
    fun getAllEntries(): Flow<List<MealEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealEntry(entry: MealEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealEntries(entries: List<MealEntry>)

    @Query("SELECT * FROM meal_entries ORDER BY date ASC, timestamp ASC")
    suspend fun getAllEntriesList(): List<MealEntry>

    @Query("SELECT * FROM food_items ORDER BY isCustom DESC, name ASC")
    suspend fun getAllFoodsList(): List<FoodItem>

    @Query("DELETE FROM meal_entries WHERE id = :id")
    suspend fun deleteMealEntry(id: Long)

    @Query("DELETE FROM meal_entries")
    suspend fun deleteAllMealEntries()

    // Daily Summary (Water, Burned, Notes)
    @Query("SELECT * FROM daily_summaries WHERE date = :date")
    fun getSummaryForDate(date: String): Flow<DailySummary?>

    @Query("SELECT * FROM daily_summaries ORDER BY date ASC")
    fun getAllSummaries(): Flow<List<DailySummary>>

    @Query("SELECT * FROM daily_summaries ORDER BY date ASC")
    suspend fun getAllSummariesList(): List<DailySummary>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSummary(summary: DailySummary)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummaries(summaries: List<DailySummary>)

    @Query("DELETE FROM daily_summaries")
    suspend fun deleteAllSummaries()

    // Weight Tracking
    @Query("SELECT * FROM weight_entries ORDER BY date ASC, timestamp ASC")
    fun getAllWeights(): Flow<List<WeightEntry>>

    @Query("SELECT * FROM weight_entries ORDER BY date ASC, timestamp ASC")
    suspend fun getAllWeightsList(): List<WeightEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(weight: WeightEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeights(weights: List<WeightEntry>)

    @Query("DELETE FROM weight_entries WHERE id = :id")
    suspend fun deleteWeight(id: Long)

    @Query("DELETE FROM weight_entries")
    suspend fun deleteAllWeights()

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    @Query("DELETE FROM user_profile")
    suspend fun deleteAllProfiles()

    // Water Logs
    @Query("SELECT * FROM water_log_entries WHERE date = :date ORDER BY timestamp DESC")
    fun getWaterLogsForDate(date: String): Flow<List<WaterLogEntry>>

    @Query("SELECT * FROM water_log_entries WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC, timestamp ASC")
    fun getWaterLogsBetweenDates(startDate: String, endDate: String): Flow<List<WaterLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(entry: WaterLogEntry): Long

    @Query("DELETE FROM water_log_entries WHERE id = :id")
    suspend fun deleteWaterLog(id: Long)

    @Query("DELETE FROM water_log_entries")
    suspend fun deleteAllWaterLogs()
}
