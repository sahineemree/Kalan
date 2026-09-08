package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MealEntry
import com.example.data.MealType
import com.example.ui.theme.AlertExceeded
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.KalanTheme
import com.example.ui.theme.MacroCarb
import com.example.ui.theme.MacroFat
import com.example.ui.theme.MacroProtein
import com.example.ui.theme.MintAccent
import com.example.ui.theme.MintLight
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmLightBackground
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: KalanViewModel,
    onNavigateToAddFood: (MealType) -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToWater: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val calcResult by viewModel.calculationResult.collectAsStateWithLifecycle()
    val currentEntries by viewModel.currentDateEntries.collectAsStateWithLifecycle()
    val currentSummary by viewModel.currentDateSummary.collectAsStateWithLifecycle()
    val weeklyTrendData by viewModel.weeklyTrendData.collectAsStateWithLifecycle()

    var showWaterDetailSheet by remember { mutableStateOf(false) }

    // Bottom Sheet for adding food right inside HomeScreen
    var bottomSheetMealType by remember { mutableStateOf<MealType?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Dialog for editing food entry grams
    var entryToEdit by remember { mutableStateOf<MealEntry?>(null) }

    // Calculate Totals for Current Date with remember to prevent recomposition recalculation
    val consumedCalories = remember(currentEntries) { currentEntries.sumOf { it.calories } }
    val consumedCarbs = remember(currentEntries) { currentEntries.sumOf { it.carbs } }
    val consumedProtein = remember(currentEntries) { currentEntries.sumOf { it.protein } }
    val consumedFat = remember(currentEntries) { currentEntries.sumOf { it.fat } }

    // Date formatting in Turkish
    val dateDisplay = remember(selectedDate) {
        formatTurkishDate(selectedDate)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Space
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Kart 1: Üst Bar (Karşılama, Tarih, Profil Avatarı)
        item {
            HomeHeaderCard(
                userName = userProfile.userName,
                avatarBase64 = userProfile.avatarBase64,
                dateDisplay = dateDisplay,
                onAvatarClick = onNavigateToProfile
            )
        }

        // Kart 2: Kalan Kalori Özeti Kartı (Büyük Rakam + Hedef - Alınan = Kalan Formülü)
        item {
            CalorieSummaryCard(
                targetCalories = calcResult.targetCalories,
                consumedCalories = consumedCalories,
                currentCarbs = consumedCarbs,
                targetCarbs = calcResult.targetCarbsGrams,
                currentProtein = consumedProtein,
                targetProtein = calcResult.targetProteinGrams,
                currentFat = consumedFat,
                targetFat = calcResult.targetFatGrams
            )
        }

        // Kart 3: Makro Dağılım Çubukları (Karb Turuncu, Protein Kırmızı/Gül, Yağ Mavi + Kalan Gramajlar)
        item {
            MacroDistributionCard(
                currentCarbs = consumedCarbs,
                targetCarbs = calcResult.targetCarbsGrams,
                currentProtein = consumedProtein,
                targetProtein = calcResult.targetProteinGrams,
                currentFat = consumedFat,
                targetFat = calcResult.targetFatGrams
            )
        }

        // Kart 4: Su Tüketimi Kartı (Kahvaltının üstüne, makro hedeflerin altına alındı; tıklandığında Su Takibi sekmesine geçer)
        item {
            WaterTrackerWidget(
                currentMl = currentSummary.waterMl,
                targetMl = userProfile.waterTargetMl,
                onAddWater = { ml -> viewModel.addWater(ml) },
                onSubtractWater = { ml -> viewModel.subtractWater(ml) },
                onClick = { onNavigateToWater() }
            )
        }

        // Kart 5: Haftalık Kalori Eğilimi (Dinamik Tablosu - image.png ile birebir)
        item {
            WeeklyCalorieTrendCard(
                weeklyTrendData = weeklyTrendData
            )
        }

        // Kart 6: 4 Öğün Blokları (🌅 Kahvaltı, ☀️ Öğle Yemeği, 🌙 Akşam Yemeği, 🍎 Ara Öğün)
        MealType.entries.forEach { mealType ->
            val mealEntries = currentEntries.filter { it.mealType == mealType.name }
            item(key = mealType.name) {
                MealBlockCard(
                    mealType = mealType,
                    entries = mealEntries,
                    onAddFoodClick = { bottomSheetMealType = mealType },
                    onDeleteEntry = { entryId -> viewModel.deleteMealEntry(entryId) },
                    onEditEntry = { entry -> entryToEdit = entry }
                )
            }
        }

        // Bottom Space to clear bottom navigation
        item { Spacer(modifier = Modifier.height(30.dp)) }
    }

    // Modal Bottom Sheet for Food Add
    bottomSheetMealType?.let { meal ->
        AddFoodBottomSheet(
            targetMeal = meal,
            viewModel = viewModel,
            sheetState = sheetState,
            onDismiss = { bottomSheetMealType = null }
        )
    }

    // Edit Meal Entry Grams Dialog
    entryToEdit?.let { entry ->
        EditMealEntryDialog(
            entry = entry,
            onDismiss = { entryToEdit = null },
            onConfirm = { updated ->
                viewModel.updateMealEntry(updated)
                entryToEdit = null
            }
        )
    }

    // Gelişmiş Su Takibi Detay Ekranı / Modalı
    if (showWaterDetailSheet) {
        WaterTrackingDetailSheet(
            viewModel = viewModel,
            onDismiss = { showWaterDetailSheet = false }
        )
    }

}

/**
 * Kart 1: Üst Bar
 * Karşılama mesajı ("Günaydın 👋"), güncel tarih ve sağda tıklanabilir Profil Avatarı.
 */
@Composable
fun HomeHeaderCard(
    userName: String,
    avatarBase64: String?,
    dateDisplay: String,
    onAvatarClick: () -> Unit
) {
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Günaydın 👋"
            in 12..17 -> "İyi günler 👋"
            in 18..21 -> "İyi akşamlar 👋"
            else -> "İyi geceler 🌙"
        }
    }

    val firstName = remember(userName) {
        userName.trim().split("\\s+".toRegex()).firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Kullanıcı"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_header_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$greeting, $firstName",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Tarih",
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = dateDisplay,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = ForestGreenPrimary
                    )
                }
            }

            ProfileAvatar(
                avatarBase64 = avatarBase64,
                userName = userName,
                size = 46.dp,
                onClick = onAvatarClick
            )
        }
    }
}

/**
 * Öğün Bloğu Kartı (Kahvaltı, Öğle, Akşam, Ara Öğün)
 */
@Composable
fun MealBlockCard(
    mealType: MealType,
    entries: List<MealEntry>,
    onAddFoodClick: () -> Unit,
    onDeleteEntry: (Long) -> Unit,
    onEditEntry: (MealEntry) -> Unit = {}
) {
    val totalCalories = remember(entries) { entries.sumOf { it.calories } }
    val totalCarbs = remember(entries) { entries.sumOf { it.carbs } }
    val totalProtein = remember(entries) { entries.sumOf { it.protein } }
    val totalFat = remember(entries) { entries.sumOf { it.fat } }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("meal_block_${mealType.name}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Meal Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = mealType.emoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = mealType.titleTr,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (entries.isNotEmpty()) {
                            Text(
                                text = "K: ${totalCarbs.roundToInt()}g • P: ${totalProtein.roundToInt()}g • Y: ${totalFat.roundToInt()}g",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${totalCalories.roundToInt()} kcal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (totalCalories > 0) ForestGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Food Items List
            if (entries.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                entries.forEach { entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = entry.foodName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${entry.servingDescription} (${entry.amountGrams.roundToInt()}g)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${entry.calories.roundToInt()} kcal",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            IconButton(
                                onClick = { onEditEntry(entry) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Miktar Düzenle",
                                    tint = ForestGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = { onDeleteEntry(entry.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Sil",
                                    tint = AlertExceeded.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // "+ Besin Ekle" Butonu
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAddFoodClick() }
                    .testTag("add_food_button_${mealType.name}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Besin Ekle",
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+ Besin Ekle",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenPrimary
                    )
                }
            }
        }
    }
}

/**
 * Kart 6: Günlük Notu & Ruh Hali
 * Günün ruh hali emojisi seçimi ve kısa not kutusu
 */
@Composable
fun DailyNoteCard(
    notes: String,
    moodEmoji: String,
    onMoodSelect: (String) -> Unit,
    onEditClick: () -> Unit
) {
    val isDark = KalanTheme.isDark
    val moodList = remember {
        listOf(
            "🤩" to "Harika",
            "😊" to "İyi",
            "😐" to "Normal",
            "😴" to "Yorgun",
            "🤕" to "Zor"
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_note_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (isDark) MaterialTheme.colorScheme.surfaceVariant else MintLight,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "Günlük Notu",
                                tint = if (isDark) MintAccent else ForestGreenDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Günün Ruh Hali & Notu",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Günün nasıl geçiyor?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Notu Düzenle",
                        tint = if (isDark) MintAccent else ForestGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Ruh Hali Emojileri Seçim Satırı
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                moodList.forEach { (emoji, label) ->
                    val isSelected = moodEmoji == emoji
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            isSelected -> if (isDark) MintAccent.copy(alpha = 0.25f) else MintLight
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        border = if (isSelected) {
                            androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isDark) MintAccent else ForestGreenPrimary
                            )
                        } else null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onMoodSelect(emoji) }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = emoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) (if (isDark) MintAccent else ForestGreenDark) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Kısa Not Kutusu
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onEditClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (notes.isNotBlank()) notes else "Bugüne dair his veya beslenme notu ekle...",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (notes.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}



/**
 * Günlük Not Dialogu
 */
@Composable
fun DailyNotesDialog(
    initialNotes: String,
    initialMood: String,
    onDismiss: () -> Unit,
    onConfirm: (mood: String, notes: String) -> Unit
) {
    var notesText by remember { mutableStateOf(initialNotes) }
    var selectedMood by remember { mutableStateOf(initialMood) }
    val moodList = remember {
        listOf(
            "🤩" to "Harika",
            "😊" to "İyi",
            "😐" to "Normal",
            "😴" to "Yorgun",
            "🤕" to "Zor"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Günün Notu & Ruh Hali", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        },
        text = {
            Column {
                Text(
                    text = "Günün ruh halini seçin:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    moodList.forEach { (emoji, label) ->
                        val isSelected = selectedMood == emoji
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MintLight else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, ForestGreenPrimary) else null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedMood = emoji }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(text = emoji, fontSize = 20.sp)
                                Text(
                                    text = label,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) ForestGreenDark else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Bugün nasıldı? Nasıl hissettin?") },
                    maxLines = 4,
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreenPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedMood, notesText) },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text("Kaydet", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

private val turkishLocale = Locale.forLanguageTag("tr-TR")
private val defaultSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
private val todayDateSdf = SimpleDateFormat("d MMMM", turkishLocale)
private val fullDateSdf = SimpleDateFormat("EEEE, d MMMM", turkishLocale)

private fun formatTurkishDate(dateStr: String): String {
    return try {
        val date = synchronized(defaultSdf) { defaultSdf.parse(dateStr) } ?: return dateStr

        val todayCal = Calendar.getInstance()
        val targetCal = Calendar.getInstance().apply { time = date }

        if (todayCal.get(Calendar.YEAR) == targetCal.get(Calendar.YEAR) &&
            todayCal.get(Calendar.DAY_OF_YEAR) == targetCal.get(Calendar.DAY_OF_YEAR)
        ) {
            val formatted = synchronized(todayDateSdf) { todayDateSdf.format(date) }
            "Bugün, $formatted"
        } else {
            synchronized(fullDateSdf) { fullDateSdf.format(date) }
        }
    } catch (e: Exception) {
        dateStr
    }
}
