package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.data.FoodItem
import com.example.data.MealType
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodBottomSheet(
    targetMeal: MealType,
    viewModel: KalanViewModel,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Besin Ara", "Hızlı Kalori", "Özel Yemek")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp),
                shape = CircleShape,
                color = OutlineBorder
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header: Target Meal Title & Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = targetMeal.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${targetMeal.titleTr}'e Ekle",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Besin ara, hızlı kalori yaz veya tarif oluştur",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Kapat",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = WarmLightBackground,
                contentColor = ForestGreenPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .height(44.dp),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ForestGreenPrimary,
                        height = 3.dp
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (selectedTab == index) ForestGreenPrimary else TextSecondary
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> BottomSheetSearchTab(
                    viewModel = viewModel,
                    targetMeal = targetMeal,
                    onFoodAdded = onDismiss
                )
                1 -> BottomSheetQuickAddTab(
                    viewModel = viewModel,
                    targetMeal = targetMeal,
                    onAdded = onDismiss
                )
                2 -> BottomSheetCustomFoodTab(
                    viewModel = viewModel,
                    targetMeal = targetMeal,
                    onAdded = onDismiss
                )
            }
        }
    }
}

@Composable
private fun BottomSheetSearchTab(
    viewModel: KalanViewModel,
    targetMeal: MealType,
    onFoodAdded: () -> Unit
) {
    val allFoods by viewModel.allFoods.collectAsStateWithLifecycle()
    val customFoods by viewModel.customFoods.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Tümü") }
    var selectedFoodForPortion by remember { mutableStateOf<FoodItem?>(null) }

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
        "İçecekler"
    )

    // Combine standard and custom foods
    val combinedFoods = remember(allFoods, customFoods) {
        val customIds = customFoods.map { it.id }.toSet()
        customFoods + allFoods.filter { it.id !in customIds }
    }

    val filteredList = remember(combinedFoods, searchQuery, selectedCategory) {
        combinedFoods.filter { food ->
            val matchesSearch = searchQuery.isBlank() ||
                    food.name.contains(searchQuery, ignoreCase = true) ||
                    food.category.contains(searchQuery, ignoreCase = true)

            val matchesCategory = when (selectedCategory) {
                "Tümü" -> true
                "Özel Besinler" -> food.isCustom
                else -> food.category.equals(selectedCategory, ignoreCase = true)
            }

            matchesSearch && matchesCategory
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Menemen, çorba, tavuk, simit...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Ara", tint = ForestGreenPrimary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Temizle", tint = TextMuted)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceWhite,
                focusedBorderColor = ForestGreenPrimary,
                unfocusedBorderColor = OutlineBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("bottom_sheet_food_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { categoryName ->
                val isSelected = selectedCategory == categoryName
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) ForestGreenPrimary else SurfaceVariant,
                    modifier = Modifier
                        .clickable { selectedCategory = categoryName }
                ) {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else TextSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Portion Selector Card if a food is selected
        selectedFoodForPortion?.let { food ->
            PortionSelectorInlineCard(
                food = food,
                onDismiss = { selectedFoodForPortion = null },
                onAdd = { grams, desc ->
                    viewModel.addFoodToMeal(
                        mealType = targetMeal,
                        food = food,
                        grams = grams,
                        servingDescription = desc
                    )
                    onFoodAdded()
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Foods List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Besin bulunamadı. Özel yemek sekmesinden ekleyebilirsiniz.",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(filteredList, key = { it.id }) { food ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedFoodForPortion = food }
                            .testTag("food_row_${food.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmLightBackground),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = food.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    if (food.isCustom) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MintLight
                                        ) {
                                            Text(
                                                text = "Özel",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 9.sp,
                                                color = ForestGreenDark,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "100g: ${food.caloriesPer100g.toInt()} kcal • Porsiyon: ${food.servingName} (${food.servingGrams.roundToInt()}g)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "K: ${food.carbsPer100g.toInt()}g  P: ${food.proteinPer100g.toInt()}g  Y: ${food.fatPer100g.toInt()}g",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }

                            Button(
                                onClick = { selectedFoodForPortion = food },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ForestGreenPrimary
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("+ Seç", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PortionSelectorInlineCard(
    food: FoodItem,
    onDismiss: () -> Unit,
    onAdd: (Double, String) -> Unit
) {
    var grams by remember { mutableDoubleStateOf(food.servingGrams) }
    var selectedPreset by remember { mutableIntStateOf(1) } // 1: 1 porsiyon, 2: 0.5 porsiyon, 3: 100g, 4: Özel

    val dynamicCalories = food.caloriesForGrams(grams)
    val dynamicCarbs = food.carbsForGrams(grams)
    val dynamicProtein = food.proteinForGrams(grams)
    val dynamicFat = food.fatForGrams(grams)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ForestGreenPrimary))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = food.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenPrimary
                    )
                    Text(
                        text = "Miktar & Porsiyon Seçimi",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat", tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedPreset == 1) ForestGreenPrimary else SurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedPreset = 1
                            grams = food.servingGrams
                        }
                ) {
                    Text(
                        text = "1 Porsiyon",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedPreset == 1) Color.White else TextPrimary,
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedPreset == 2) ForestGreenPrimary else SurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedPreset = 2
                            grams = (food.servingGrams * 0.5).coerceAtLeast(10.0)
                        }
                ) {
                    Text(
                        text = "0.5 Pors.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedPreset == 2) Color.White else TextPrimary,
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedPreset == 3) ForestGreenPrimary else SurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedPreset = 3
                            grams = 100.0
                        }
                ) {
                    Text(
                        text = "100g",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedPreset == 3) Color.White else TextPrimary,
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Gram Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Gramaj:", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Text(
                    text = "${grams.roundToInt()} g",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ForestGreenPrimary
                )
            }

            Slider(
                value = grams.toFloat(),
                onValueChange = {
                    grams = it.toDouble()
                    selectedPreset = 4
                },
                valueRange = 10f..600f,
                steps = 59,
                colors = SliderDefaults.colors(
                    thumbColor = ForestGreenPrimary,
                    activeTrackColor = ForestGreenPrimary
                )
            )

            // Dinamik Makro Özeti
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = WarmLightBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Kalori", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("${dynamicCalories.roundToInt()} kcal", fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Karb", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("${dynamicCarbs.roundToInt()}g", fontWeight = FontWeight.Bold, color = MacroCarb)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Protein", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("${dynamicProtein.roundToInt()}g", fontWeight = FontWeight.Bold, color = MacroProtein)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Yağ", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("${dynamicFat.roundToInt()}g", fontWeight = FontWeight.Bold, color = MacroFat)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    val desc = when (selectedPreset) {
                        1 -> "${food.servingName} (${grams.roundToInt()}g)"
                        2 -> "Yarım ${food.servingName} (${grams.roundToInt()}g)"
                        3 -> "100g"
                        else -> "${grams.roundToInt()}g"
                    }
                    onAdd(grams, desc)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text(
                    text = "Öğüne Ekle (${dynamicCalories.roundToInt()} kcal)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun BottomSheetQuickAddTab(
    viewModel: KalanViewModel,
    targetMeal: MealType,
    onAdded: () -> Unit
) {
    var selectedMeal by remember { mutableStateOf(if (targetMeal == MealType.KAHVALTI) MealType.ARA_OGUN else targetMeal) }
    var title by remember { mutableStateOf("") }
    var caloriesStr by remember { mutableStateOf("") }
    var carbsStr by remember { mutableStateOf("") }
    var proteinStr by remember { mutableStateOf("") }
    var fatStr by remember { mutableStateOf("") }

    val presets = listOf(150, 250, 350, 500)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Hızlı Kalori Girişi",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ForestGreenPrimary
        )
        Text(
            text = "Atıştırmalık veya hızlı kaçamakları kolayca ekleyin",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        // Öğün Seçim Çipleri
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MealType.entries.forEach { meal ->
                val isSelected = meal == selectedMeal
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) ForestGreenPrimary else WarmLightBackground,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedMeal = meal }
                ) {
                    Text(
                        text = "${meal.emoji} ${meal.titleTr}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextPrimary,
                        modifier = Modifier.padding(vertical = 6.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }

        // Hızlı Preset Butonları
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { cal ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MintLight,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { caloriesStr = cal.toString() }
                ) {
                    Text(
                        text = "+$cal",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenDark,
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Açıklama (Örn: Simit, Kaçamak)") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = caloriesStr,
            onValueChange = { caloriesStr = it },
            label = { Text("Kalori (kcal)*") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = carbsStr,
                onValueChange = { carbsStr = it },
                label = { Text("Karb (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = proteinStr,
                onValueChange = { proteinStr = it },
                label = { Text("Protein (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = fatStr,
                onValueChange = { fatStr = it },
                label = { Text("Yağ (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val buttonLabel = if (selectedMeal == MealType.ARA_OGUN) {
            "🍎 Atıştırmalıklara Ekle"
        } else {
            "${selectedMeal.emoji} ${selectedMeal.titleTr}'e Ekle"
        }

        Button(
            onClick = {
                val cal = caloriesStr.toDoubleOrNull() ?: 0.0
                if (cal > 0) {
                    viewModel.addQuickCalorieEntry(
                        mealType = selectedMeal,
                        title = title.ifBlank { "Hızlı Kalori" },
                        calories = cal,
                        carbs = carbsStr.toDoubleOrNull() ?: 0.0,
                        protein = proteinStr.toDoubleOrNull() ?: 0.0,
                        fat = fatStr.toDoubleOrNull() ?: 0.0
                    )
                    onAdded()
                }
            },
            enabled = caloriesStr.toDoubleOrNull() != null && (caloriesStr.toDoubleOrNull() ?: 0.0) > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
        ) {
            Text(buttonLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun BottomSheetCustomFoodTab(
    viewModel: KalanViewModel,
    targetMeal: MealType,
    onAdded: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var servingName by remember { mutableStateOf("1 Porsiyon") }
    var servingGramsStr by remember { mutableStateOf("200") }
    var cal100gStr by remember { mutableStateOf("") }
    var carbs100gStr by remember { mutableStateOf("") }
    var protein100gStr by remember { mutableStateOf("") }
    var fat100gStr by remember { mutableStateOf("") }

    var isEstimating by remember { mutableStateOf(false) }
    var showManualValues by remember { mutableStateOf(false) }
    var aiExplanation by remember { mutableStateOf<String?>(null) }
    var lastAiResult by remember { mutableStateOf<com.example.calculator.GeminiFoodEstimateResult?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Özel Yemek & AI Analiz",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary
            )
            Text(
                text = "Yemek adı, tarifi veya porsiyonunu girin; Gemini AI kalori, makro, lif ve sağlık skorunu otomatik hesaplasın.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Yemek / Tarif Adı*") },
                placeholder = { Text("Örn: 200g Fırın Somon, 1 Porsiyon Döner, Menemen") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = servingName,
                    onValueChange = {
                        servingName = it
                        val digits = it.filter { ch -> ch.isDigit() }
                        if (digits.isNotBlank() && it.contains("g", ignoreCase = true)) {
                            servingGramsStr = digits
                        }
                    },
                    label = { Text("Porsiyon Tanımı") },
                    placeholder = { Text("Örn: 1 Kase, 1 Tabak, 200g") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1.2f)
                )
                OutlinedTextField(
                    value = servingGramsStr,
                    onValueChange = { servingGramsStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Tahmini Gram") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(0.8f)
                )
            }
        }

        // 🤖 Gemini AI Kalori Hesaplama Butonu
        item {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        isEstimating = true
                        coroutineScope.launch {
                            val explicitPortion = if (servingGramsStr.isNotBlank()) {
                                "$servingName (${servingGramsStr}g)"
                            } else {
                                servingName
                            }
                            val result = viewModel.estimateFoodNutrition(name, explicitPortion)
                            isEstimating = false
                            lastAiResult = result
                            aiExplanation = result.explanation

                            if (result.servingGrams > 0) {
                                servingGramsStr = result.servingGrams.roundToInt().toString()
                            }

                            val grams = result.servingGrams.takeIf { it > 0 } ?: (servingGramsStr.toDoubleOrNull() ?: 100.0)
                            val ratio = if (grams > 0) 100.0 / grams else 1.0
                            cal100gStr = (result.calories * ratio).roundToInt().toString()
                            carbs100gStr = String.format(java.util.Locale.US, "%.1f", result.carbsGrams * ratio)
                            protein100gStr = String.format(java.util.Locale.US, "%.1f", result.proteinGrams * ratio)
                            fat100gStr = String.format(java.util.Locale.US, "%.1f", result.fatGrams * ratio)
                        }
                    }
                },
                enabled = name.isNotBlank() && !isEstimating,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (isEstimating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gemini Uzman Diyetisyen Analizi Yapıyor...", color = Color.White, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFEF08A), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gemini AI ile Akıllı Hesapla", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Hesaplanan Sonuç Gösterimi
        if (cal100gStr.isNotBlank() || lastAiResult != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF0FDF4),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreenPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (lastAiResult?.isFromAi == true) "🤖 Gemini AI Analiz Sonucu" else "✨ Besin Değerleri Özeti",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenDark
                                )
                            }

                            lastAiResult?.let { res ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = when {
                                        res.healthScore >= 8 -> Color(0xFF15803D)
                                        res.healthScore >= 5 -> Color(0xFFD97706)
                                        else -> Color(0xFFDC2626)
                                    }
                                ) {
                                    Text(
                                        text = "${res.healthScore}/10 Kalite Skoru",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Porsiyon Toplamı Vurgusu
                        lastAiResult?.let { res ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${servingGramsStr}g Toplam Porsiyon:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "${res.calories.roundToInt()} kcal",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ForestGreenPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(shape = RoundedCornerShape(6.dp), color = MacroCarb.copy(alpha = 0.15f), modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Karb", style = MaterialTheme.typography.labelSmall, color = MacroCarb)
                                    Text("${lastAiResult?.carbsGrams?.roundToInt() ?: carbs100gStr}g", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MacroCarb)
                                }
                            }
                            Surface(shape = RoundedCornerShape(6.dp), color = MacroProtein.copy(alpha = 0.15f), modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Protein", style = MaterialTheme.typography.labelSmall, color = MacroProtein)
                                    Text("${lastAiResult?.proteinGrams?.roundToInt() ?: protein100gStr}g", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MacroProtein)
                                }
                            }
                            Surface(shape = RoundedCornerShape(6.dp), color = MacroFat.copy(alpha = 0.15f), modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Yağ", style = MaterialTheme.typography.labelSmall, color = MacroFat)
                                    Text("${lastAiResult?.fatGrams?.roundToInt() ?: fat100gStr}g", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MacroFat)
                                }
                            }
                            if ((lastAiResult?.fiberGrams ?: 0.0) > 0) {
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF0D9488).copy(alpha = 0.15f), modifier = Modifier.weight(1f)) {
                                    Column(modifier = Modifier.padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Lif", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0D9488))
                                        Text("${lastAiResult?.fiberGrams?.roundToInt()}g", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
                                    }
                                }
                            }
                        }

                        if (!aiExplanation.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = aiExplanation!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        lastAiResult?.dietaryAdvice?.takeIf { it.isNotBlank() }?.let { advice ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.7f),
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
                                        text = advice,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ForestGreenDark,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Manuel Değerleri Düzenleme İsteğe Bağlı Bölümü
        item {
            TextButton(
                onClick = { showManualValues = !showManualValues },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (showManualValues) "Değerleri Gizle ▲" else "Değerleri Manuel Olarak Değiştir / Düzenle ▼",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (showManualValues) {
            item {
                OutlinedTextField(
                    value = cal100gStr,
                    onValueChange = { cal100gStr = it },
                    label = { Text("100g Kalorisi (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = carbs100gStr,
                        onValueChange = { carbs100gStr = it },
                        label = { Text("100g Karb") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = protein100gStr,
                        onValueChange = { protein100gStr = it },
                        label = { Text("100g Prot") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = fat100gStr,
                        onValueChange = { fat100gStr = it },
                        label = { Text("100g Yağ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    val cal = cal100gStr.toDoubleOrNull() ?: 0.0
                    val sGrams = servingGramsStr.toDoubleOrNull() ?: 200.0
                    if (name.isNotBlank() && cal > 0) {
                        val customFood = FoodItem(
                            name = name.trim(),
                            category = "Özel Yemekler",
                            servingName = servingName.ifBlank { "Porsiyon" },
                            servingGrams = sGrams,
                            caloriesPer100g = cal,
                            carbsPer100g = carbs100gStr.toDoubleOrNull() ?: 0.0,
                            proteinPer100g = protein100gStr.toDoubleOrNull() ?: 0.0,
                            fatPer100g = fat100gStr.toDoubleOrNull() ?: 0.0,
                            fiberPer100g = 0.0,
                            isCustom = true
                        )
                        viewModel.saveCustomFood(customFood)

                        // Add directly to meal
                        viewModel.addFoodToMeal(
                            mealType = targetMeal,
                            food = customFood,
                            grams = sGrams,
                            servingDescription = "$servingName (${sGrams.roundToInt()}g)"
                        )
                        onAdded()
                    }
                },
                enabled = name.isNotBlank() && (cal100gStr.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text("Tarifi Kaydet ve Öğüne Ekle", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
