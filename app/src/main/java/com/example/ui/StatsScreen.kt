package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.WeightEntry
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
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterBlueLight
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun StatsScreen(
    viewModel: KalanViewModel,
    modifier: Modifier = Modifier
) {
    val allEntries by viewModel.allEntries.collectAsStateWithLifecycle()
    val allSummaries by viewModel.allSummaries.collectAsStateWithLifecycle()
    val allWeights by viewModel.allWeights.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val calcResult by viewModel.calculationResult.collectAsStateWithLifecycle()

    var selectedRangeDays by remember { mutableIntStateOf(7) } // 7 veya 30 gün
    var showAddWeightDialog by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dayNameFormat = remember { SimpleDateFormat("EEE", Locale.forLanguageTag("tr-TR")) }
    val shortDateFormat = remember { SimpleDateFormat("d MMM", Locale.forLanguageTag("tr-TR")) }

    // Generate Dates List for Range
    val rangeDates = remember(selectedRangeDays) {
        val list = mutableListOf<Date>()
        for (i in (selectedRangeDays - 1) downTo 0) {
            val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            list.add(c.time)
        }
        list
    }

    // Single pass calculation for Chart & Totals
    val rangeData = remember(rangeDates, allEntries, allSummaries) {
        val todayStr = dateFormat.format(Date())
        val dateSet = HashSet<String>(rangeDates.size)
        val stats = rangeDates.map { date ->
            val dStr = dateFormat.format(date)
            dateSet.add(dStr)
            val calSum = allEntries.filter { it.date == dStr }.sumOf { it.calories }
            val label = if (dStr == todayStr) "Bugün" else dayNameFormat.format(date).replace(".", "")
            DayCalorieStat(
                dayLabel = label,
                dateStr = dStr,
                calories = calSum,
                isToday = dStr == todayStr
            )
        }

        val entriesInRange = allEntries.filter { it.date in dateSet }
        val summariesInRange = allSummaries.filter { it.date in dateSet }

        val totalCalories = entriesInRange.sumOf { it.calories }
        val totalCarbs = entriesInRange.sumOf { it.carbs }
        val totalProtein = entriesInRange.sumOf { it.protein }
        val totalFat = entriesInRange.sumOf { it.fat }
        val totalWater = summariesInRange.sumOf { it.waterMl }
        val totalBurned = summariesInRange.sumOf { it.burnedExerciseKcal }

        object {
            val chartStats = stats
            val totalCalories = totalCalories
            val totalCarbs = totalCarbs
            val totalProtein = totalProtein
            val totalFat = totalFat
            val totalWater = totalWater
            val totalBurned = totalBurned
        }
    }

    val chartStats = rangeData.chartStats
    val totalCaloriesInRange = rangeData.totalCalories
    val totalCarbsInRange = rangeData.totalCarbs
    val totalProteinInRange = rangeData.totalProtein
    val totalFatInRange = rangeData.totalFat
    val totalWaterInRange = rangeData.totalWater
    val totalBurnedInRange = rangeData.totalBurned

    val avgCaloriesPerDay = (totalCaloriesInRange / selectedRangeDays).roundToInt()
    val avgWaterPerDay = totalWaterInRange / selectedRangeDays

    // Hedefe uyum oranı: Kaç gün hedef kalori sınırında veya altında kalındı
    val adherencePercent = remember(chartStats, calcResult.targetCalories) {
        val daysWithData = chartStats.filter { it.calories > 0 }
        if (daysWithData.isEmpty()) 100
        else {
            val adhered = daysWithData.count { it.calories <= calcResult.targetCalories * 1.05 }
            ((adhered.toDouble() / daysWithData.size) * 100).roundToInt()
        }
    }

    // Weight trend points
    val weightPoints = remember(allWeights) {
        allWeights.takeLast(10).map { w ->
            val label = try {
                val d = dateFormat.parse(w.date)
                if (d != null) shortDateFormat.format(d) else w.date
            } catch (e: Exception) {
                w.date
            }
            WeightPoint(label, w.weightKg)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        item { Spacer(modifier = Modifier.height(6.dp)) }

        // Range Selector Segment (Son 7 Gün / Son 30 Gün)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(7 to "Son 7 Gün", 30 to "Son 30 Gün").forEach { (days, title) ->
                        val isSelected = selectedRangeDays == days
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ForestGreenPrimary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedRangeDays = days }
                                .testTag("range_button_$days")
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // 4'lü Özet İstatistik Kartları (Ortalama Kalori, Su, Uyum %, Egzersiz)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Ort. Kalori",
                    value = "$avgCaloriesPerDay",
                    unit = "kcal / gün",
                    icon = Icons.Default.BarChart,
                    iconTint = ForestGreenPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Hedefe Uyum",
                    value = "%$adherencePercent",
                    unit = "başarı oranı",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    iconTint = MintAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Ort. Su",
                    value = "$avgWaterPerDay",
                    unit = "ml / gün",
                    icon = Icons.Default.LocalDrink,
                    iconTint = WaterBlue,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Toplam Egzersiz",
                    value = "${totalBurnedInRange.roundToInt()}",
                    unit = "kcal yakıldı",
                    icon = Icons.Default.FitnessCenter,
                    iconTint = MacroCarb,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Kalori Alım Sütun Grafiği Kartı
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "KALORİ TÜKETİM GRAFİĞİ",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Hedef: ${calcResult.targetCalories.toInt()} kcal",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    CalorieBarChart(
                        stats = chartStats,
                        targetCalories = calcResult.targetCalories
                    )
                }
            }
        }

        // Makro Dağılım Donut Grafiği
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "DÖNEMLİK MAKRO DAĞILIMI",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Son $selectedRangeDays günün ortalama enerji kaynakları",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MacroDonutChart(
                        carbsGrams = totalCarbsInRange / selectedRangeDays,
                        proteinGrams = totalProteinInRange / selectedRangeDays,
                        fatGrams = totalFatInRange / selectedRangeDays
                    )
                }
            }
        }

        // Kilo Takip Çizgi Grafiği & Kilo Kaydı
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Scale,
                                contentDescription = null,
                                tint = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "KİLO DEĞİŞİM GRAFİĞİ",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (KalanTheme.isDark) MaterialTheme.colorScheme.surfaceVariant else MintLight,
                            modifier = Modifier
                                .clickable { showAddWeightDialog = true }
                                .testTag("add_weight_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    tint = if (KalanTheme.isDark) MintAccent else ForestGreenDark,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Kilo Kaydet",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (KalanTheme.isDark) MintAccent else ForestGreenDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    WeightTrendChart(
                        points = weightPoints,
                        targetWeightKg = userProfile.targetWeightKg
                    )
                }
            }
        }

        // Kilo Kayıt Geçmişi Listesi
        if (allWeights.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "KİLO GÜNLÜĞÜ",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        allWeights.reversed().take(5).forEach { weight ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = weight.date,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${weight.weightKg} kg",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary
                                    )
                                    IconButton(
                                        onClick = { viewModel.deleteWeight(weight.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Sil",
                                            tint = AlertExceeded.copy(alpha = 0.7f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Weight Dialog
    if (showAddWeightDialog) {
        AddWeightDialog(
            currentWeight = userProfile.currentWeightKg,
            onDismiss = { showAddWeightDialog = false },
            onConfirm = { weight ->
                viewModel.logWeight(weight)
                showAddWeightDialog = false
            }
        )
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun AddWeightDialog(
    currentWeight: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var weightText by remember { mutableStateOf(currentWeight.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Yeni Kilo Kaydet", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        },
        text = {
            Column {
                Text(
                    text = "Günün tartı sonucunu girerek kilo değişim trendinizi takip edin.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Kilo (kg)*") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("weight_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreenPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = weightText.toDoubleOrNull()
                    if (w != null && w > 0) {
                        onConfirm(w)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                modifier = Modifier.testTag("save_weight_button")
            ) {
                Text("Kaydet", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
