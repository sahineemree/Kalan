package com.example.notification

enum class ReminderType(
    val id: Int,
    val defaultTitleTr: String,
    val defaultTitleEn: String,
    val defaultTime: String = "08:30"
) {
    BREAKFAST(101, "Kahvaltı Zamanı 🌅", "Breakfast Reminder 🌅", "08:30"),
    LUNCH(102, "Öğle Yemeği Vakti ☀️", "Lunch Reminder ☀️", "12:30"),
    DINNER(103, "Akşam Yemeği Vakti 🌙", "Dinner Reminder 🌙", "19:30"),
    SNACK(104, "Ara Öğün Vakti 🍎", "Healthy Snack Time 🍎", "16:00"),
    WATER(201, "Su İçme Zamanı 💧", "Time to Drink Water 💧", "08:30"),
    TEST(999, "Kalan Bildirim Testi 🔔", "Kalan Notification Test 🔔", "12:00");

    companion object {
        fun fromString(value: String?): ReminderType {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: WATER
        }
    }
}
