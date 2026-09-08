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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import com.example.calculator.GeminiFoodEstimateResult
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.FoodItem
import com.example.data.MealType
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.KalanTheme
import com.example.ui.theme.MacroCarb
import com.example.ui.theme.MacroFat
import com.example.ui.theme.MacroProtein
import com.example.ui.theme.MintAccent
import com.example.ui.theme.MintLight
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.SurfaceVariant
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmLightBackground
import kotlin.math.roundToInt

@Composable
fun AddFoodScreen(
    viewModel: KalanViewModel,
    onFoodAdded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allFoods by viewModel.allFoods.collectAsStateWithLifecycle()
    val customFoods by viewModel.customFoods.collectAsStateWithLifecycle()
    val selectedMealType by viewModel.selectedMealTypeForAdd.collectAsStateWithLifecycle()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Besin Arama, 1: Hızlı Kalori, 2: Özel Yemek Ekle
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Tümü") }

    var foodToPortionDialog by remember { mutableStateOf<FoodItem?>(null) }

    val categories = listOf(
        "Tümü",
        "Özel Besinler",
        "Kahvaltılık",
        "Çorbalar",
        "Ana Yemekler",
        "Sebze & Bakliyat",
        "Pilav & Makarna",
        "Sokak Lezzeti",
        "Salata & Meze",
        "Tatlılar",
        "Meyve & Kuruyemiş",
        "İçecekler",
        "Ekmek & Hamur İşi"
    )

    // Filter Foods
    val filteredFoods = remember(allFoods, searchQuery, selectedCategory) {
        allFoods.filter { food ->
            val matchesCategory = when (selectedCategory) {
                "Tümü" -> true
                "Özel Besinler" -> food.isCustom
                else -> food.category.equals(selectedCategory, ignoreCase = true)
            }
            val matchesQuery = if (searchQuery.isBlank()) {
                true
            } else {
                food.name.contains(searchQuery, ignoreCase = true) ||
                        food.category.contains(searchQuery, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Meal Type Selector Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)) {
                // Meal Type Chips
                Text(
                    text = "EKLENECEK ÖĞÜN",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                    letterSpacing = 0.8.sp
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(MealType.entries) { mealType ->
                        val isSelected = mealType == selectedMealType
                        val isDark = KalanTheme.isDark
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSelectedMealTypeForAdd(mealType) },
                            label = {
                                Text(
                                    text = "${mealType.emoji} ${mealType.titleTr}",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (isDark) ForestGreenPrimary.copy(alpha = 0.35f) else MintLight,
                                selectedLabelColor = if (isDark) MintAccent else ForestGreenDark,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = MintAccent,
                                borderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }

                // Sub Navigation Tabs
                val tabColor = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary
                ScrollableTabRow(
                    selectedTabIndex = activeTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = tabColor,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                            color = tabColor
                        )
                    }
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Besin Veritabanı")
                            }
                        }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Hızlı Kalori")
                            }
                        }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Özel Yemek Oluştur")
                            }
                        }
                    )
                }
            }
        }

        // Tab Content
        when (activeTab) {
            0 -> {
                // Besin Arama & Listeleme
                Column(modifier = Modifier.fillMaxSize()) {
                    // Arama Kutusu
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .testTag("food_search_input"),
                        placeholder = { Text("Menemen, Mercimek, Lahmacun ara...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Ara", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Temizle", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = ForestGreenPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    // Kategori Çipleri
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = cat == selectedCategory
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) ForestGreenPrimary else MaterialTheme.colorScheme.surface,
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier
                                    .clickable { selectedCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Besin Listesi
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            Text(
                                text = "${filteredFoods.size} besin bulundu",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        items(filteredFoods, key = { it.id }) { food ->
                            FoodItemCard(
                                food = food,
                                onClick = { foodToPortionDialog = food }
                            )
                        }
                    }
                }
            }

            1 -> {
                // Hızlı / AI Destekli Kalori Girişi
                QuickCalorieInputSection(
                    selectedMealType = MealType.ARA_OGUN,
                    viewModel = viewModel,
                    onAddQuickCalorie = { targetMeal, title, cal, carbs, protein, fat ->
                        viewModel.addQuickCalorieEntry(targetMeal, title, cal, carbs, protein, fat)
                        onFoodAdded()
                    }
                )
            }

            2 -> {
                // Özel Yemek / Tarif Oluşturma (Gemini AI Destekli)
                CreateCustomFoodSection(
                    viewModel = viewModel,
                    selectedMealType = selectedMealType,
                    onSave = { newFood ->
                        viewModel.saveCustomFood(newFood)
                        activeTab = 0
                        selectedCategory = "Özel Besinler"
                    },
                    onAddDirectlyToMeal = { mealType, title, cal, carbs, protein, fat ->
                        viewModel.addQuickCalorieEntry(mealType, title, cal, carbs, protein, fat)
                        onFoodAdded()
                    }
                )
            }
        }
    }

    // Porsiyon Seçici & Onay Dialogu
    foodToPortionDialog?.let { food ->
        PortionSelectDialog(
            food = food,
            initialMealType = selectedMealType,
            onDismiss = { foodToPortionDialog = null },
            onConfirm = { targetMeal, grams, servingDesc ->
                viewModel.addFoodToMeal(targetMeal, food, grams, servingDesc)
                foodToPortionDialog = null
                onFoodAdded()
            }
        )
    }
}

/**
 * Besin Arama Listesi Kartı
 */
@Composable
fun FoodItemCard(
    food: FoodItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("food_item_${food.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = food.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (food.isCustom) {
                        Spacer(modifier = Modifier.width(6.dp))
                        val isDark = KalanTheme.isDark
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isDark) ForestGreenPrimary.copy(alpha = 0.35f) else MintLight
                        ) {
                            Text(
                                text = "ÖZEL",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) MintAccent else ForestGreenDark,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "1 ${food.servingName} (${food.servingGrams.roundToInt()}g) • 100g: ${food.caloriesPer100g.roundToInt()} kcal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Macro Badges
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "K: ${(food.carbsPer100g * food.servingGrams / 100).roundToInt()}g",
                        style = MaterialTheme.typography.labelSmall,
                        color = MacroCarb,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "P: ${(food.proteinPer100g * food.servingGrams / 100).roundToInt()}g",
                        style = MaterialTheme.typography.labelSmall,
                        color = MacroProtein,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Y: ${(food.fatPer100g * food.servingGrams / 100).roundToInt()}g",
                        style = MaterialTheme.typography.labelSmall,
                        color = MacroFat,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val servingCalories = (food.caloriesPer100g * food.servingGrams / 100).roundToInt()
                Text(
                    text = "$servingCalories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary
                )
                Text(
                    text = "kcal / ${food.servingName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Porsiyon Seçici Dialogu
 */
@Composable
fun PortionSelectDialog(
    food: FoodItem,
    initialMealType: MealType,
    onDismiss: () -> Unit,
    onConfirm: (MealType, Double, String) -> Unit
) {
    var selectedMeal by remember { mutableStateOf(initialMealType) }
    var selectedPortionType by remember { mutableIntStateOf(0) } // 0: 1 Porsiyon, 1: Yarım Porsiyon, 2: 100 Gram, 3: Özel Gramaj
    var customGramsText by remember { mutableStateOf(food.servingGrams.roundToInt().toString()) }

    val effectiveGrams = remember(selectedPortionType, customGramsText) {
        when (selectedPortionType) {
            0 -> food.servingGrams
            1 -> food.servingGrams * 0.5
            2 -> 100.0
            3 -> customGramsText.toDoubleOrNull() ?: food.servingGrams
            else -> food.servingGrams
        }
    }

    val servingDescription = remember(selectedPortionType, effectiveGrams) {
        when (selectedPortionType) {
            0 -> "1 ${food.servingName}"
            1 -> "Yarım ${food.servingName}"
            2 -> "100 Gram"
            3 -> "${effectiveGrams.roundToInt()}g"
            else -> "${effectiveGrams.roundToInt()}g"
        }
    }

    val currentCalories = food.caloriesForGrams(effectiveGrams)
    val currentCarbs = food.carbsForGrams(effectiveGrams)
    val currentProtein = food.proteinForGrams(effectiveGrams)
    val currentFat = food.fatForGrams(effectiveGrams)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${food.category} • 100g = ${food.caloriesPer100g.roundToInt()} kcal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Öğün Seçici
                Text(
                    text = "Eklenecek Öğün",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MealType.entries.forEach { m ->
                        val isSelected = m == selectedMeal
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMeal = m }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(text = m.emoji, fontSize = 16.sp)
                                Text(
                                    text = m.titleTr.take(5),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Porsiyon Seçenekleri
                Text(
                    text = "Porsiyon / Miktar",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val isDark = KalanTheme.isDark
                    listOf(
                        0 to "1 ${food.servingName}",
                        1 to "0.5 Porsiyon",
                        2 to "100g",
                        3 to "Özel Gram"
                    ).forEach { (idx, label) ->
                        val isSelected = selectedPortionType == idx
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) {
                                if (isDark) ForestGreenPrimary.copy(alpha = 0.35f) else MintLight
                            } else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MintAccent) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedPortionType = idx }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) {
                                    if (isDark) MintAccent else ForestGreenDark
                                } else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // Özel Gramaj Girişi (Seçilmişse)
                if (selectedPortionType == 3) {
                    OutlinedTextField(
                        value = customGramsText,
                        onValueChange = { customGramsText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Miktar (Gram)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreenPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }

                // Canlı Kalori & Makro Özeti
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${currentCalories.roundToInt()} kcal",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary
                            )
                            Text(
                                text = "$servingDescription (${effectiveGrams.roundToInt()}g)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Karb", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                                Text("${currentCarbs.roundToInt()}g", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MacroCarb)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Protein", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                                Text("${currentProtein.roundToInt()}g", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MacroProtein)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Yağ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                                Text("${currentFat.roundToInt()}g", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MacroFat)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedMeal, effectiveGrams, servingDescription) },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                modifier = Modifier.testTag("confirm_add_food_button")
            ) {
                Text("Öğüne Ekle", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

/**
 * Hızlı Kalori Girişi Bölümü (Gemini AI Destekli)
 */
@Composable
fun QuickCalorieInputSection(
    selectedMealType: MealType = MealType.ARA_OGUN,
    viewModel: KalanViewModel? = null,
    onAddQuickCalorie: (MealType, String, Double, Double, Double, Double) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var targetMeal by remember { mutableStateOf(selectedMealType) }
    var title by remember { mutableStateOf("") }
    var portionText by remember { mutableStateOf("1 porsiyon") }
    var caloriesText by remember { mutableStateOf("350") }
    var carbsText by remember { mutableStateOf("") }
    var proteinText by remember { mutableStateOf("") }
    var fatText by remember { mutableStateOf("") }
    var isEstimating by remember { mutableStateOf(false) }
    var aiNote by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "HIZLI & AI KALORİ GİRİŞİ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary
                    )
                    Text(
                        text = "Atıştırmalık veya hızlı kaçamaklarınızı kaydedin, Gemini AI kaloriyi standartlara göre hesaplasın.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Öğün Seçim Çipleri (Varsayılan: Atıştırmalık)
                    Text(
                        text = "Eklenecek Öğün:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MealType.entries.forEach { meal ->
                            val isSelected = meal == targetMeal
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { targetMeal = meal }
                            ) {
                                Text(
                                    text = "${meal.emoji} ${meal.titleTr}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Ne Yediniz? (Atıştırmalık / Yemek)") },
                        placeholder = { Text("Örn: Simit, Bitter Çikolata, Fındık") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_food_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = portionText,
                        onValueChange = { portionText = it },
                        label = { Text("Miktar / Porsiyon") },
                        placeholder = { Text("Örn: 1 adet, 200g, 1 avuç") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Gemini ile Otomatik Hesapla Butonu
                    Button(
                        onClick = {
                            if (title.isNotBlank() && viewModel != null) {
                                isEstimating = true
                                coroutineScope.launch {
                                    val result = viewModel.estimateFoodNutrition(title, portionText)
                                    caloriesText = result.calories.roundToInt().toString()
                                    carbsText = result.carbsGrams.roundToInt().toString()
                                    proteinText = result.proteinGrams.roundToInt().toString()
                                    fatText = result.fatGrams.roundToInt().toString()
                                    aiNote = result.explanation
                                    isEstimating = false
                                }
                            }
                        },
                        enabled = title.isNotBlank() && !isEstimating,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (KalanTheme.isDark) ForestGreenPrimary else Color(0xFF15803D)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("quick_gemini_estimate_button")
                    ) {
                        if (isEstimating) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gemini Hesaplarken Bekleyin...", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFEF08A), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gemini ile Otomatik Hesapla", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    aiNote?.let { note ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (KalanTheme.isDark) Color(0xFF1E293B) else Color(0xFFF0FDF4),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = caloriesText,
                        onValueChange = { caloriesText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Hesaplanan / Manuel Kalori (kcal)*") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_calorie_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Hızlı Kalori Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(150, 250, 350, 500).forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { caloriesText = preset.toString() }
                            ) {
                                Text(
                                    text = "$preset",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Makro Değerleri (Gram):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = carbsText,
                            onValueChange = { carbsText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Karb (g)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = proteinText,
                            onValueChange = { proteinText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Protein (g)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = fatText,
                            onValueChange = { fatText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Yağ (g)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    val buttonLabel = if (targetMeal == MealType.ARA_OGUN) {
                        "🍎 Atıştırmalıklara Ekle"
                    } else {
                        "${targetMeal.emoji} ${targetMeal.titleTr}'e Ekle"
                    }

                    Button(
                        onClick = {
                            val cal = caloriesText.toDoubleOrNull() ?: 0.0
                            if (cal > 0) {
                                val carbs = carbsText.toDoubleOrNull() ?: 0.0
                                val protein = proteinText.toDoubleOrNull() ?: 0.0
                                val fat = fatText.toDoubleOrNull() ?: 0.0
                                val entryTitle = title.trim().ifBlank { "Hızlı Kalori Girişi" }
                                onAddQuickCalorie(targetMeal, entryTitle, cal, carbs, protein, fat)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_quick_calorie_button")
                    ) {
                        Text(
                            text = buttonLabel,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Özel Besin / Yemek Oluşturma Bölümü (Gemini AI Destekli)
 * Kullanıcı ne yiyeceğini ve miktarını girer, Gemini kalori ve makroları otomatik hesaplar.
 */
@Composable
fun CreateCustomFoodSection(
    viewModel: KalanViewModel? = null,
    selectedMealType: MealType? = null,
    onSave: (FoodItem) -> Unit,
    onAddDirectlyToMeal: ((MealType, String, Double, Double, Double, Double) -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var portionText by remember { mutableStateOf("1 porsiyon") }
    var category by remember { mutableStateOf("Ana Yemekler") }
    var servingName by remember { mutableStateOf("Porsiyon") }
    var servingGramsText by remember { mutableStateOf("200") }
    var caloriesPer100gText by remember { mutableStateOf("") }
    var carbsPer100gText by remember { mutableStateOf("") }
    var proteinPer100gText by remember { mutableStateOf("") }
    var fatPer100gText by remember { mutableStateOf("") }
    var fiberPer100gText by remember { mutableStateOf("") }

    var isEstimating by remember { mutableStateOf(false) }
    var lastEstimateResult by remember { mutableStateOf<GeminiFoodEstimateResult?>(null) }
    var showManualValues by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "YENİ ÖZEL YEMEK (GEMINI AI DESTEKLİ)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary
                    )
                    Text(
                        text = "Ne yiyeceğinizi ve miktarını girin, Gemini AI kaloriyi ve makroları otomatik hesaplasın.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Ne Yiyeceksiniz? / Yemek Adı*") },
                        placeholder = { Text("Örn: Anne Köftesi, Mercimek Çorbası, Tavuk Sote") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_food_name_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = portionText,
                        onValueChange = {
                            portionText = it
                            servingName = it
                            // Gram sayısını tahmin et
                            val digits = it.filter { ch -> ch.isDigit() }
                            if (digits.isNotBlank() && it.contains("g", ignoreCase = true)) {
                                servingGramsText = digits
                            }
                        },
                        label = { Text("Miktar / Porsiyon*") },
                        placeholder = { Text("Örn: 1 kase, 200 gram, 1 tabak, 2 dilim") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_food_portion_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 🤖 GEMINI AI İLE OTOMATİK HESAPLA BUTONU
                    Button(
                        onClick = {
                            if (name.isNotBlank() && viewModel != null) {
                                isEstimating = true
                                coroutineScope.launch {
                                    val result = viewModel.estimateFoodNutrition(name, portionText)
                                    lastEstimateResult = result
                                    isEstimating = false

                                    if (result.servingGrams > 0) {
                                        servingGramsText = result.servingGrams.roundToInt().toString()
                                    }

                                    // Gram bazlı 100g değerlerine dağıt
                                    val estimatedGrams = result.servingGrams.takeIf { it > 0 } ?: (servingGramsText.toDoubleOrNull() ?: 100.0)
                                    val ratio = if (estimatedGrams > 0) 100.0 / estimatedGrams else 1.0
                                    caloriesPer100gText = (result.calories * ratio).roundToInt().toString()
                                    carbsPer100gText = String.format(java.util.Locale.US, "%.1f", result.carbsGrams * ratio)
                                    proteinPer100gText = String.format(java.util.Locale.US, "%.1f", result.proteinGrams * ratio)
                                    fatPer100gText = String.format(java.util.Locale.US, "%.1f", result.fatGrams * ratio)
                                    if (result.fiberGrams > 0) {
                                        fiberPer100gText = String.format(java.util.Locale.US, "%.1f", result.fiberGrams * ratio)
                                    }
                                }
                            }
                        },
                        enabled = name.isNotBlank() && !isEstimating,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (KalanTheme.isDark) ForestGreenPrimary else Color(0xFF15803D)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("gemini_estimate_custom_food_button")
                    ) {
                        if (isEstimating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Gemini Uzman Diyetisyen Analizi Yapıyor...", fontWeight = FontWeight.Bold, color = Color.White)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFEF08A), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gemini AI ile Akıllı Hesapla", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // GEMINI HESAPLAMA SONUÇ KARTI
                    lastEstimateResult?.let { result ->
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (KalanTheme.isDark) Color(0xFF1E293B) else Color(0xFFF0FDF4),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (KalanTheme.isDark) MintAccent.copy(alpha = 0.4f) else ForestGreenPrimary.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = ForestGreenPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (result.isFromAi) "Gemini AI Uzman Analizi" else "Besin Tablosu Analizi",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (KalanTheme.isDark) MintAccent else ForestGreenDark
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = when {
                                            result.healthScore >= 8 -> Color(0xFF15803D)
                                            result.healthScore >= 5 -> Color(0xFFD97706)
                                            else -> Color(0xFFDC2626)
                                        }
                                    ) {
                                        Text(
                                            text = "${result.healthScore}/10 Kalite Skoru",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${portionText} Toplamı:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${result.calories.roundToInt()} kcal",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MacroCarb.copy(alpha = 0.15f),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Karb", style = MaterialTheme.typography.labelSmall, color = MacroCarb)
                                            Text("${result.carbsGrams.roundToInt()}g", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MacroCarb)
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MacroProtein.copy(alpha = 0.15f),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Protein", style = MaterialTheme.typography.labelSmall, color = MacroProtein)
                                            Text("${result.proteinGrams.roundToInt()}g", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MacroProtein)
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MacroFat.copy(alpha = 0.15f),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Yağ", style = MaterialTheme.typography.labelSmall, color = MacroFat)
                                            Text("${result.fatGrams.roundToInt()}g", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MacroFat)
                                        }
                                    }
                                    if (result.fiberGrams > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF0D9488).copy(alpha = 0.15f),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("Lif", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0D9488))
                                                Text("${result.fiberGrams.roundToInt()}g", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = result.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (result.dietaryAdvice.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (KalanTheme.isDark) Color(0xFF0F172A) else Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = ForestGreenPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = result.dietaryAdvice,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (KalanTheme.isDark) MintAccent else ForestGreenDark,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                // Eğer öğüne direkt ekleme isteniyorsa
                                if (onAddDirectlyToMeal != null && selectedMealType != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            val foodTitle = "$name ($portionText)"
                                            onAddDirectlyToMeal(
                                                selectedMealType,
                                                foodTitle,
                                                result.calories,
                                                result.carbsGrams,
                                                result.proteinGrams,
                                                result.fatGrams
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                    ) {
                                        Text(
                                            text = "Bu Öğüne Ekle (${selectedMealType.titleTr})",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Manuel Düzenleme Alanı Aç/Kapat Butonu
                    TextButton(
                        onClick = { showManualValues = !showManualValues },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (showManualValues) "Değerleri Gizle ▲" else "100g Besin Değerlerini Görüntüle / Düzenle ▼",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (showManualValues) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = { category = it },
                                label = { Text("Kategori") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = servingGramsText,
                                onValueChange = { servingGramsText = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Porsiyon (Gram)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = caloriesPer100gText,
                            onValueChange = { caloriesPer100gText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Kalori (100g için kcal)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = carbsPer100gText,
                                onValueChange = { carbsPer100gText = it },
                                label = { Text("Karb (g)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = proteinPer100gText,
                                onValueChange = { proteinPer100gText = it },
                                label = { Text("Protein (g)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = fatPer100gText,
                                onValueChange = { fatPer100gText = it },
                                label = { Text("Yağ (g)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Özel Besini Veritabanına Kaydet Butonu
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                val newFood = FoodItem(
                                    name = name.trim(),
                                    category = category.trim().ifBlank { "Özel Besinler" },
                                    servingName = portionText.trim().ifBlank { "Porsiyon" },
                                    servingGrams = servingGramsText.toDoubleOrNull() ?: 100.0,
                                    caloriesPer100g = caloriesPer100gText.toDoubleOrNull() ?: 100.0,
                                    carbsPer100g = carbsPer100gText.toDoubleOrNull() ?: 0.0,
                                    proteinPer100g = proteinPer100gText.toDoubleOrNull() ?: 0.0,
                                    fatPer100g = fatPer100gText.toDoubleOrNull() ?: 0.0,
                                    fiberPer100g = fiberPer100gText.toDoubleOrNull() ?: 0.0,
                                    isCustom = true
                                )
                                onSave(newFood)
                            }
                        },
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (KalanTheme.isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFE2E8F0)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_custom_food_button")
                    ) {
                        Text(
                            "Özel Besinlerime Kaydet (Tekrar Kullanım)",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
