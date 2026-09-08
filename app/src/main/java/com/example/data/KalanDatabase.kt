package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        FoodItem::class,
        MealEntry::class,
        DailySummary::class,
        WeightEntry::class,
        UserProfile::class,
        WaterLogEntry::class
    ],
    version = 8,
    exportSchema = false
)
abstract class KalanDatabase : RoomDatabase() {
    abstract fun kalanDao(): KalanDao

    companion object {
        @Volatile
        private var INSTANCE: KalanDatabase? = null

        private val MIGRATION_6_7 = object : androidx.room.migration.Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_profile ADD COLUMN notifySnack INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN snackTime TEXT NOT NULL DEFAULT '16:00'")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN notifyWater INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN waterIntervalHours REAL NOT NULL DEFAULT 2.0")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN waterStartTime TEXT NOT NULL DEFAULT '08:30'")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN waterEndTime TEXT NOT NULL DEFAULT '22:00'")
            }
        }

        private val MIGRATION_7_8 = object : androidx.room.migration.Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_profile ADD COLUMN email TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN supabaseUserId TEXT")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN supabaseToken TEXT")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): KalanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KalanDatabase::class.java,
                    "kalan_nutrition.db"
                )
                    .addMigrations(MIGRATION_6_7, MIGRATION_7_8)
                    .fallbackToDestructiveMigration(true)
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.kalanDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: KalanDao) {
            if (dao.getFoodCount() == 0) {
                dao.insertFoods(TurkishFoodDatabase.initialFoods)
            }
            // If no profile exists yet, create an empty unauthenticated profile for the new user
            if (dao.getUserProfileOnce() == null) {
                dao.insertOrUpdateProfile(
                    UserProfile(
                        id = 1,
                        isRegistered = false,
                        isLoggedIn = false
                    )
                )
            }
        }
    }
}
