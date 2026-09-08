package com.example.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.mutableFloatStateOf
import kotlin.math.roundToInt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.WaterLogEntry
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.KalanTheme
import com.example.ui.theme.MintAccent
import com.example.ui.theme.MintLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterBlueLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * 💧 GELİŞMİŞ SU TAKİBİ MODÜLÜ / EKRANI
 * Tıklanabilir detay ekranı & BottomSheet modalı
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackingDetailSheet(
    viewModel: KalanViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("water_tracking_detail_sheet")
    ) {
        WaterTrackingContent(
            viewModel = viewModel,
            onClose = onDismiss
        )
    }
}

@Composable
fun WaterTrackingContent(
    viewModel: KalanViewModel,
    onClose: () -> Unit
) {
    val summary by viewModel.currentDateSummary.collectAsStateWithLifecycle()
    val waterLogs by viewModel.currentDateWaterLogs.collectAsStateWithLifecycle()
    val weeklyAverageWater by viewModel.weeklyAverageWaterMl.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

    val currentWater = summary.waterMl
    val targetWater = userProfile.waterTargetMl
    val isDark = KalanTheme.isDark

    var showCustomWaterDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Üst Başlık & Kapat Butonu
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (isDark) WaterBlue.copy(alpha = 0.25f) else WaterBlueLight,
                        modifier = Modifier.size(40.dp)
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
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Günlük Hidrasyon Durumu",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("water_detail_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Kapat",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Kart 1: Büyük Animasyonlu Su Şişesi / Dalga Göstergesi
        item {
            WaterWaveProgressCard(
                currentMl = currentWater,
                targetMl = targetWater,
                isDark = isDark
            )
        }

        // Kart 2: Hızlı Miktar Ekleme Butonları (Özelleştirilebilir)
        item {
            WaterQuickAddCard(
                onAddWater = { viewModel.addWater(it) },
                onSubtractWater = { viewModel.subtractWater(it) },
                onOpenCustom = { showCustomWaterDialog = true },
                isDark = isDark
            )
        }

        // Kart 2.5: Günlük Su Hedefini Ayarla (Profil sekmesinden taşınan hedef belirleme)
        item {
            var waterSliderValue by remember(targetWater) {
                mutableFloatStateOf(targetWater.toFloat())
            }
            val geminiRecommendedWater = ((userProfile.currentWeightKg * 35.0) / 250).roundToInt() * 250

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("water_target_setting_card_sheet"),
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

                    // Hızlı Hedef Seçenek Butonları
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
                                    textAlign = TextAlign.Center
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

        // Kart 3: Günlük Su İçme Zaman Çizelgesi (Zaman Damgalı Kayıtlar)
        item {
            WaterTimelineCard(
                waterLogs = waterLogs,
                onDeleteLog = { viewModel.deleteWaterLog(it) },
                isDark = isDark
            )
        }

        // Kart 4: Haftalık Su Ortalaması ve Hatırlatıcı Durumu
        item {
            WaterWeeklyStatsCard(
                weeklyAverageMl = weeklyAverageWater,
                targetMl = targetWater,
                isDark = isDark
            )
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

/**
 * Kart 1: Büyük Animasyonlu Su Dalga Göstergesi
 */
@Composable
fun WaterWaveProgressCard(
    currentMl: Int,
    targetMl: Int,
    isDark: Boolean
) {
    val progressPct = if (targetMl > 0) ((currentMl.toFloat() / targetMl.toFloat()) * 100).roundToInt() else 0
    val clampedRatio = (currentMl.toFloat() / targetMl.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val isGoalReached = currentMl >= targetMl && targetMl > 0

    // Dalga animasyonu için faz geçişi
    val infiniteTransition = rememberInfiniteTransition(label = "water_wave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("water_wave_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color(0xFFF0F7FF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animasyonlu Dalga Çemberi
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE2EDFB))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val waterLevel = height * (1f - clampedRatio)

                    val path = Path()
                    path.moveTo(0f, height)
                    path.lineTo(0f, waterLevel)

                    val waveHeight = 12.dp.toPx()
                    val waveLength = width

                    var x = 0f
                    while (x <= width) {
                        val y = waterLevel + (sin((x / waveLength) * 2 * Math.PI + wavePhase) * waveHeight).toFloat()
                        path.lineTo(x, y)
                        x += 4f
                    }

                    path.lineTo(width, height)
                    path.close()

                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = if (isDark) {
                                listOf(WaterBlue.copy(alpha = 0.85f), Color(0xFF1D4ED8))
                            } else {
                                listOf(Color(0xFF60A5FA), WaterBlue)
                            }
                        )
                    )
                }

                // Gösterge Metinleri
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "%$progressPct",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = if (clampedRatio > 0.45f) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$currentMl / $targetMl ml",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (clampedRatio > 0.45f) Color.White.copy(alpha = 0.95f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hedefe Ulaşma Tebrik Bildirimi
            if (isGoalReached) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDark) MintAccent.copy(alpha = 0.2f) else MintLight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MintAccent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isDark) MintAccent else ForestGreenDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Günlük hedefine ulaştın! 💧",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) MintAccent else ForestGreenDark,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                val remaining = (targetMl - currentMl).coerceAtLeast(0)
                Text(
                    text = "Hedefe ulaşmak için $remaining ml daha su içmelisin.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Kart 2: Hızlı Miktar Ekleme Butonları (Özelleştirilebilir)
 */
@Composable
fun WaterQuickAddCard(
    onAddWater: (Int) -> Unit,
    onSubtractWater: (Int) -> Unit,
    onOpenCustom: () -> Unit,
    isDark: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("water_quick_add_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hızlı Miktar Ekle",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Butonlar Izgarası
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickWaterButton(
                    amountText = "+200 ml",
                    subtitle = "Küçük Bardak",
                    modifier = Modifier.weight(1f),
                    onClick = { onAddWater(200) }
                )
                QuickWaterButton(
                    amountText = "+250 ml",
                    subtitle = "Su Bardağı",
                    modifier = Modifier.weight(1f),
                    onClick = { onAddWater(250) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickWaterButton(
                    amountText = "+330 ml",
                    subtitle = "Küçük Şişe",
                    modifier = Modifier.weight(1f),
                    onClick = { onAddWater(330) }
                )
                QuickWaterButton(
                    amountText = "+500 ml",
                    subtitle = "Büyük Şişe",
                    modifier = Modifier.weight(1f),
                    onClick = { onAddWater(500) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Alt Satır: -250 ml (Geri Al) ve + Özel Miktar Ekle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSubtractWater(250) }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = "-250 ml",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                        Text(
                            text = "Geri Al",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) ForestGreenPrimary.copy(alpha = 0.35f) else MintLight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreenPrimary),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenCustom() }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = "+ Özel Ekle",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) MintAccent else ForestGreenDark
                        )
                        Text(
                            text = "İstediğin Miktar",
                            fontSize = 10.sp,
                            color = if (isDark) MintAccent.copy(alpha = 0.8f) else ForestGreenPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickWaterButton(
    amountText: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = WaterBlue.copy(alpha = 0.10f),
        border = androidx.compose.foundation.BorderStroke(1.dp, WaterBlue.copy(alpha = 0.35f)),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Text(
                text = amountText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = WaterBlue
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Kart 3: Günlük Su İçme Zaman Çizelgesi (Zaman Damgalı Kayıtlar)
 */
@Composable
fun WaterTimelineCard(
    waterLogs: List<WaterLogEntry>,
    onDeleteLog: (WaterLogEntry) -> Unit,
    isDark: Boolean
) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("water_timeline_card"),
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
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = WaterBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Bugünkü Su Geçmişi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${waterLogs.size} Kayıt",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (waterLogs.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Bugün henüz kaydedilmiş su yok. Yukarıdaki butonlardan hızlıca ekleyebilirsiniz!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(14.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    waterLogs.forEach { log ->
                        val formattedTime = timeFormat.format(Date(log.timestamp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = WaterBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = formattedTime,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "+${log.amountMl} ml",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteLog(log) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Kaydı Sil",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(18.dp)
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

/**
 * Kart 4: Haftalık Su Ortalaması ve Hatırlatıcı Durumu
 */
@Composable
fun WaterWeeklyStatsCard(
    weeklyAverageMl: Int,
    targetMl: Int,
    isDark: Boolean
) {
    val averageRatio = if (targetMl > 0) (weeklyAverageMl.toFloat() / targetMl.toFloat()).coerceIn(0f, 1f) else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("water_weekly_stats_card"),
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
                    Icon(
                        imageVector = Icons.Default.Insights,
                        contentDescription = null,
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Haftalık Su Ortalaması",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Son 7 Gün",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Günlük Ortalama:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$weeklyAverageMl ml / gün",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WaterBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { averageRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = WaterBlue,
                        trackColor = WaterBlue.copy(alpha = 0.15f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "💡 İpucu: Günlük hedefin olan $targetMl ml suya düzenli ulaşmak vücut enerjisini artırır ve tokluk hissini destekler.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

/**
 * Özel Su Miktarı Giriş Dialogu
 */
@Composable
fun CustomWaterInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var textValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Özel Su Miktarı Ekle",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                Text(
                    text = "İçtiğiniz su miktarını mililitre (ml) olarak yazın:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Miktar (ml)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WaterBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = textValue.toIntOrNull() ?: 0
                    if (parsed > 0) {
                        onConfirm(parsed)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = WaterBlue)
            ) {
                Text("Ekle", color = Color.White)
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
