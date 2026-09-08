package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MealEntry
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MacroCarb
import com.example.ui.theme.MacroFat
import com.example.ui.theme.MacroProtein
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmLightBackground
import kotlin.math.roundToInt

@Composable
fun EditMealEntryDialog(
    entry: MealEntry,
    onDismiss: () -> Unit,
    onConfirm: (MealEntry) -> Unit
) {
    var grams by remember { mutableDoubleStateOf(entry.amountGrams) }

    // Ratios based on original entry
    val originalGrams = if (entry.amountGrams > 0) entry.amountGrams else 100.0
    val ratio = grams / originalGrams

    val newCalories = entry.calories * ratio
    val newCarbs = entry.carbs * ratio
    val newProtein = entry.protein * ratio
    val newFat = entry.fat * ratio

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Miktar Düzenle",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenPrimary
                )
                Text(
                    text = entry.foodName,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Yeni Miktar:", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Text(
                        text = "${grams.roundToInt()} g",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = grams.toFloat(),
                    onValueChange = { grams = it.toDouble() },
                    valueRange = 10f..600f,
                    steps = 59,
                    colors = SliderDefaults.colors(
                        thumbColor = ForestGreenPrimary,
                        activeTrackColor = ForestGreenPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(50.0, 100.0, 150.0, 200.0).forEach { presetGrams ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (grams.roundToInt() == presetGrams.toInt()) ForestGreenPrimary else WarmLightBackground,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { grams = presetGrams }
                        ) {
                            Text(
                                text = "${presetGrams.toInt()}g",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (grams.roundToInt() == presetGrams.toInt()) Color.White else TextPrimary,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dinamik Yeni Değerler
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
                            Text("${newCalories.roundToInt()} kcal", fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Karb", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("${newCarbs.roundToInt()}g", fontWeight = FontWeight.Bold, color = MacroCarb)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Protein", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("${newProtein.roundToInt()}g", fontWeight = FontWeight.Bold, color = MacroProtein)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Yağ", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("${newFat.roundToInt()}g", fontWeight = FontWeight.Bold, color = MacroFat)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = entry.copy(
                        amountGrams = grams,
                        servingDescription = "${grams.roundToInt()}g",
                        calories = newCalories,
                        carbs = newCarbs,
                        protein = newProtein,
                        fat = newFat
                    )
                    onConfirm(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Güncelle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = TextSecondary)
            }
        },
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(20.dp)
    )
}
