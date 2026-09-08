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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.KalanTheme
import com.example.ui.theme.MintAccent
import com.example.ui.theme.MintLight
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterBlueLight
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * 💧 Su Takibi Ekranı
 * Alt gezinme barındaki 2. sekme: Kullanıcının günlük ve haftalık hidrasyon takibi
 */
@Composable
fun WaterTrackerScreen(
    viewModel: KalanViewModel,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.currentDateSummary.collectAsStateWithLifecycle()
    val waterLogs by viewModel.currentDateWaterLogs.collectAsStateWithLifecycle()
    val weeklyAverageWater by viewModel.weeklyAverageWaterMl.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    val currentWater = summary.waterMl
    val targetWater = userProfile.waterTargetMl
    val remainingWater = max(0, targetWater - currentWater)
    val isDark = KalanTheme.isDark
    val percentage = if (targetWater > 0) ((currentWater.toFloat() / targetWater.toFloat()) * 100).roundToInt() else 0

    var showCustomWaterDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp)
    ) {
        // Üst Başlık ve Hızlı Durum Rozeti
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (isDark) WaterBlue.copy(alpha = 0.25f) else WaterBlueLight,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = WaterBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Su Takibi",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Günlük Hidrasyon & Su Günlüğü",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Yüzde Rozeti
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (percentage >= 100) {
                        if (isDark) ForestGreenPrimary.copy(alpha = 0.35f) else MintLight
                    } else {
                        if (isDark) WaterBlue.copy(alpha = 0.25f) else WaterBlueLight
                    },
                    modifier = Modifier.testTag("water_tracker_status_badge")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Opacity,
                            contentDescription = null,
                            tint = if (percentage >= 100) (if (isDark) MintAccent else ForestGreenDark) else WaterBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "%$percentage",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (percentage >= 100) (if (isDark) MintAccent else ForestGreenDark) else WaterBlue
                        )
                    }
                }
            }
        }

        // Özet İlerleme Şeridi Kartı
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("water_summary_status_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color(0xFFF3F8FF)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "GÜNLÜK HEDEF",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) WaterBlue else Color(0xFF2563EB),
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$targetWater ml",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (remainingWater > 0) "HEDEFE KALAN" else "DURUM",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (remainingWater > 0) "$remainingWater ml" else "Hedef Tamamlandı! 🎉",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (remainingWater > 0) MaterialTheme.colorScheme.onSurface else if (isDark) MintAccent else ForestGreenDark
                        )
                    }
                }
            }
        }

        // Kart 1: Büyük Animasyonlu Dalga Göstergesi
        item {
            WaterWaveProgressCard(
                currentMl = currentWater,
                targetMl = targetWater,
                isDark = isDark
            )
        }

        // Kart 2: Hızlı Miktar Ekleme Butonları (1 bardak 200ml, büyük bardak 300ml, şişe 500ml, matara 750ml, özel miktar)
        item {
            WaterQuickAddCard(
                onAddWater = { viewModel.addWater(it) },
                onSubtractWater = { viewModel.subtractWater(it) },
                onOpenCustom = { showCustomWaterDialog = true },
                isDark = isDark
            )
        }

        // Kart 2.5: Günlük Su Hedefini Ayarla (Ayarlar sekmesinden taşınan hedef belirleme)
        item {
            var waterSliderValue by remember(targetWater) {
                mutableFloatStateOf(targetWater.toFloat())
            }
            val geminiRecommendedWater = ((userProfile.currentWeightKg * 35.0) / 250).roundToInt() * 250

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("water_target_setting_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) WaterBlue.copy(alpha = 0.25f) else Color(0xFFDBEAFE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = WaterBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Günlük Su Hedefi",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Hedefinizi buradan kolayca belirleyin",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "${waterSliderValue.roundToInt()} ml",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = WaterBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Slider(
                        value = waterSliderValue,
                        onValueChange = { waterSliderValue = it },
                        onValueChangeFinished = {
                            val rounded = ((waterSliderValue / 250).roundToInt() * 250).coerceIn(1000, 5000)
                            viewModel.updateWaterTarget(rounded)
                        },
                        valueRange = 1000f..5000f,
                        steps = 15,
                        colors = SliderDefaults.colors(
                            thumbColor = WaterBlue,
                            activeTrackColor = WaterBlue,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Hızlı Hedef Butonları
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(2000, 2500, 3000, 3500).forEach { preset ->
                            val isSelected = targetWater == preset
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) WaterBlue else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        waterSliderValue = preset.toFloat()
                                        viewModel.updateWaterTarget(preset)
                                    }
                            ) {
                                Text(
                                    text = "$preset ml",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Gemini Tavsiye Rozeti & Butonu
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFEFF6FF),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                waterSliderValue = geminiRecommendedWater.toFloat()
                                viewModel.updateWaterTarget(geminiRecommendedWater)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = WaterBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Gemini Önerisi: Kilonuza göre $geminiRecommendedWater ml",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "Uygula",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = WaterBlue
                            )
                        }
                    }
                }
            }
        }

        // Kart 3: Günlük Su İçme Zaman Çizelgesi
        item {
            WaterTimelineCard(
                waterLogs = waterLogs,
                onDeleteLog = { viewModel.deleteWaterLog(it) },
                isDark = isDark
            )
        }

        // Kart 4: Haftalık Su Ortalaması & 7 Günlük Dağılım
        item {
            WaterWeeklyStatsCard(
                weeklyAverageMl = weeklyAverageWater,
                targetMl = targetWater,
                isDark = isDark
            )
        }

        // Kart 5: Hidrasyon İpucu Kartı
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hydration_tip_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF0FDF4)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = if (isDark) MintAccent else ForestGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Sağlıklı Hidrasyon İpucu",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) MintAccent else ForestGreenDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sabah uyanır uyanmaz içilen 1 bardak ılık su metabolizmayı %25-30 hızlandırır ve gece boyunca kaybedilen sıvıyı yeniler.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }

    // Özel Miktar Giriş Dialogu
    if (showCustomWaterDialog) {
        CustomWaterInputDialog(
            onDismiss = { showCustomWaterDialog = false },
            onConfirm = { ml ->
                viewModel.addCustomWater(ml)
                showCustomWaterDialog = false
            }
        )
    }
}
