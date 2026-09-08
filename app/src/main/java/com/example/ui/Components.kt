package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertExceeded
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MacroCarb
import com.example.ui.theme.MacroCarbLight
import com.example.ui.theme.MacroFat
import com.example.ui.theme.MacroFatLight
import com.example.ui.theme.MacroProtein
import com.example.ui.theme.MacroProteinLight
import com.example.ui.theme.MintAccent
import com.example.ui.theme.KalanTheme
import com.example.ui.theme.MintLight
import com.example.ui.theme.MintSoft
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmLightBackground
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterBlueLight
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Büyük Kalan Kalori Özeti Kartı:
 * Hedef - Alınan + Yakılan Egzersiz = KALAN KALORİ
 */
@Composable
fun CalorieSummaryCard(
    targetCalories: Double,
    consumedCalories: Double,
    burnedExercise: Double = 0.0,
    currentCarbs: Double = 0.0,
    targetCarbs: Double = 0.0,
    currentProtein: Double = 0.0,
    targetProtein: Double = 0.0,
    currentFat: Double = 0.0,
    targetFat: Double = 0.0,
    onAddExerciseClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val remaining = targetCalories - consumedCalories
    val isExceeded = remaining < 0
    val progress = if (targetCalories > 0) {
        (consumedCalories.coerceAtLeast(0.0) / targetCalories).toFloat()
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(700),
        label = "CalorieRingProgress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("calorie_summary_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GÜNLÜK KALORİ DENGEN",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary,
                    letterSpacing = 1.sp
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isExceeded) {
                        if (KalanTheme.isDark) AlertExceeded.copy(alpha = 0.2f) else Color(0xFFFEE2E2)
                    } else {
                        if (KalanTheme.isDark) MaterialTheme.colorScheme.surfaceVariant else MintLight
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isExceeded) AlertExceeded else (if (KalanTheme.isDark) MintAccent else ForestGreenPrimary))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isExceeded) "Aşıldı" else if (remaining == 0.0) "Tam Hedefte" else "Hedefte",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isExceeded) AlertExceeded else (if (KalanTheme.isDark) MintAccent else ForestGreenPrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dairesel Gösterge & Merkez Değer
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(180.dp)
            ) {
                val isDark = KalanTheme.isDark
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 14.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val topLeft = Offset(
                        (size.width - diameter) / 2f,
                        (size.height - diameter) / 2f
                    )
                    val arcSize = Size(diameter, diameter)

                    // Track Background Arc
                    drawArc(
                        color = if (isDark) Color(0xFF27272A) else Color(0xFFE5E7EB),
                        startAngle = 140f,
                        sweepAngle = 260f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Filled Progress Arc
                    val activeColor = if (isExceeded) AlertExceeded else ForestGreenPrimary
                    val secondaryColor = if (isExceeded) Color(0xFFF87171) else MintAccent

                    drawArc(
                        brush = Brush.sweepGradient(
                            0.0f to activeColor,
                            0.7f to secondaryColor,
                            1.0f to activeColor
                        ),
                        startAngle = 140f,
                        sweepAngle = 260f * animatedProgress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Center Text Content
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isExceeded) "AŞILAN" else "KALAN",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isExceeded) AlertExceeded else MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "${kotlin.math.abs(remaining).roundToInt()}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = if (isExceeded) AlertExceeded else (if (KalanTheme.isDark) MintAccent else ForestGreenPrimary)
                    )
                    Text(
                        text = "kcal",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Formül & Dağılım Çizgisi: Hedef - Alınan = Kalan
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hedef
                    FormulaItem(
                        title = "Hedef",
                        value = "${targetCalories.roundToInt()}",
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(text = "−", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    // Alınan
                    FormulaItem(
                        title = "Alınan",
                        value = "${consumedCalories.roundToInt()}",
                        color = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary
                    )

                    Text(text = "=", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    // Kalan
                    FormulaItem(
                        title = if (isExceeded) "Aşılan" else "Kalan",
                        value = "${kotlin.math.abs(remaining).roundToInt()}",
                        color = if (isExceeded) AlertExceeded else (if (KalanTheme.isDark) MintAccent else ForestGreenPrimary)
                    )
                }
            }

            // 3 Renkli Mini Makro Barı
            if (targetCarbs > 0 || targetProtein > 0 || targetFat > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniMacroIndicator(
                        title = "Karbonhidrat",
                        current = currentCarbs,
                        target = targetCarbs,
                        color = MacroCarb,
                        modifier = Modifier.weight(1f)
                    )
                    MiniMacroIndicator(
                        title = "Protein",
                        current = currentProtein,
                        target = targetProtein,
                        color = MacroProtein,
                        modifier = Modifier.weight(1f)
                    )
                    MiniMacroIndicator(
                        title = "Yağ",
                        current = currentFat,
                        target = targetFat,
                        color = MacroFat,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniMacroIndicator(
    title: String,
    current: Double,
    target: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (target > 0) (current / target).toFloat().coerceIn(0f, 1f) else 0f
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title.take(5),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
            Text(
                text = "${current.roundToInt()}/${target.roundToInt()}g",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(if (KalanTheme.isDark) Color(0xFF27272A) else Color(0xFFE5E7EB))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}

@Composable
private fun FormulaItem(
    title: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            fontSize = 15.sp
        )
    }
}

/**
 * Makro Dağılım İlerleme Çubuğu Kartı
 */
@Composable
fun MacroDistributionCard(
    currentCarbs: Double,
    targetCarbs: Double,
    currentProtein: Double,
    targetProtein: Double,
    currentFat: Double,
    targetFat: Double,
    modifier: Modifier = Modifier
) {
    val isDark = KalanTheme.isDark
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("macro_distribution_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "GÜNLÜK MAKRO HEDEFLERİ",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Karbonhidrat
            MacroProgressBar(
                title = "Karbonhidrat",
                currentGrams = currentCarbs,
                targetGrams = targetCarbs,
                barColor = MacroCarb,
                backgroundColor = if (isDark) MacroCarb.copy(alpha = 0.2f) else MacroCarbLight
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Protein
            MacroProgressBar(
                title = "Protein",
                currentGrams = currentProtein,
                targetGrams = targetProtein,
                barColor = MacroProtein,
                backgroundColor = if (isDark) MacroProtein.copy(alpha = 0.2f) else MacroProteinLight
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Yağ
            MacroProgressBar(
                title = "Yağ",
                currentGrams = currentFat,
                targetGrams = targetFat,
                barColor = MacroFat,
                backgroundColor = if (isDark) MacroFat.copy(alpha = 0.2f) else MacroFatLight
            )
        }
    }
}

@Composable
fun MacroProgressBar(
    title: String,
    currentGrams: Double,
    targetGrams: Double,
    barColor: Color,
    backgroundColor: Color
) {
    val progress = if (targetGrams > 0) (currentGrams / targetGrams).toFloat().coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500),
        label = "MacroBarProgress"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(barColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            val remainingGrams = (targetGrams - currentGrams).coerceAtLeast(0.0)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${currentGrams.roundToInt()} / ${targetGrams.roundToInt()} g",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = barColor.copy(alpha = if (KalanTheme.isDark) 0.2f else 0.12f)
                ) {
                    Text(
                        text = "${remainingGrams.roundToInt()}g kaldı",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = barColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(backgroundColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
    }
}

/**
 * Su Takip Widget'ı (250ml & 500ml Hızlı Butonlar + Doluluk Animasyonu)
 */
@Composable
fun WaterTrackerWidget(
    currentMl: Int,
    targetMl: Int,
    onAddWater: (Int) -> Unit,
    onSubtractWater: (Int) -> Unit,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = KalanTheme.isDark
    val progress = if (targetMl > 0) (currentMl.toFloat() / targetMl).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600),
        label = "WaterProgress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("water_tracker_widget"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onClick() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (isDark) WaterBlue.copy(alpha = 0.2f) else WaterBlueLight,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.LocalDrink,
                                contentDescription = "Su Takibi",
                                tint = WaterBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Su Tüketimi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = WaterBlue.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "Detay ↗",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = WaterBlue,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Hedef: $targetMl ml",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Current ml
                Text(
                    text = "$currentMl ml",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = WaterBlue
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Su İlerleme Çubuğu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isDark) Color(0xFF27272A) else WaterBlueLight)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF60A5FA), WaterBlue)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hızlı Bardak Butonları
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // -250ml
                IconButton(
                    onClick = { onSubtractWater(250) },
                    enabled = currentMl > 0,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (currentMl > 0) MaterialTheme.colorScheme.surfaceVariant
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                        .testTag("water_minus_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Su Azalt",
                        tint = if (currentMl > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline
                    )
                }

                // +250ml (Bardak)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) WaterBlue.copy(alpha = 0.2f) else WaterBlueLight,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clickable { onAddWater(250) }
                        .testTag("water_add_250_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = WaterBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+250 ml",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = WaterBlue
                        )
                    }
                }

                // +500ml (Şişe)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WaterBlue,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clickable { onAddWater(500) }
                        .testTag("water_add_500_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+500 ml",
                            style = MaterialTheme.typography.labelMedium,
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
 * 7 Günlük ve 30 Günlük Kalori Tüketim Sütun Grafiği (Hedef Çizgisi ile birlikte)
 */
data class DayCalorieStat(
    val dayLabel: String,
    val dateStr: String,
    val calories: Double,
    val isToday: Boolean = false
)

@Composable
fun CalorieBarChart(
    stats: List<DayCalorieStat>,
    targetCalories: Double,
    modifier: Modifier = Modifier
) {
    if (stats.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Henüz kalori verisi bulunmuyor", color = TextMuted)
        }
        return
    }

    val maxCalorie = (stats.maxOfOrNull { it.calories } ?: targetCalories)
        .coerceAtLeast(targetCalories)
        .coerceAtLeast(1000.0) * 1.15

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val bottomPadding = 30.dp.toPx()
            val topPadding = 20.dp.toPx()
            val chartHeight = canvasHeight - bottomPadding - topPadding

            // Hedef Kalori Kesikli Çizgisi
            val targetY = topPadding + chartHeight * (1f - (targetCalories / maxCalorie).toFloat().coerceIn(0f, 1f))
            drawLine(
                color = WarningAmber,
                start = Offset(0f, targetY),
                end = Offset(canvasWidth, targetY),
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
            )

            // Barlar
            val barCount = stats.size
            val barSpacing = canvasWidth / barCount
            val barWidth = (barSpacing * 0.55f).coerceAtMost(36.dp.toPx())

            stats.forEachIndexed { index, item ->
                val centerX = (index * barSpacing) + (barSpacing / 2f)
                val barFraction = (item.calories / maxCalorie).toFloat().coerceIn(0f, 1f)
                val barPixelHeight = (chartHeight * barFraction).coerceAtLeast(6f)
                val barTopY = topPadding + (chartHeight - barPixelHeight)
                val barLeft = centerX - (barWidth / 2f)

                val barColor = if (item.calories > targetCalories) {
                    AlertExceeded
                } else if (item.isToday) {
                    ForestGreenPrimary
                } else {
                    MintAccent
                }

                // Sütun gövdesi
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(barLeft, barTopY),
                    size = Size(barWidth, barPixelHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )

                // Gün etiketi
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = if (item.isToday) android.graphics.Color.BLACK else android.graphics.Color.GRAY
                        textSize = 11.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = item.isToday
                    }
                    drawText(
                        item.dayLabel,
                        centerX,
                        canvasHeight - 6.dp.toPx(),
                        paint
                    )
                }
            }
        }

        // Açıklama / Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(ForestGreenPrimary)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Normal Alım", style = MaterialTheme.typography.bodySmall, color = TextSecondary)

            Spacer(modifier = Modifier.width(14.dp))
            Box(
                modifier = Modifier
                    .width(14.dp)
                    .height(2.dp)
                    .background(WarningAmber)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Hedef (${targetCalories.toInt()} kcal)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)

            Spacer(modifier = Modifier.width(14.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(AlertExceeded)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Aşılan", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

/**
 * Makro Dağılım Donut / Halka Grafiği
 */
@Composable
fun MacroDonutChart(
    carbsGrams: Double,
    proteinGrams: Double,
    fatGrams: Double,
    modifier: Modifier = Modifier
) {
    val carbKcal = carbsGrams * 4.0
    val proteinKcal = proteinGrams * 4.0
    val fatKcal = fatGrams * 9.0
    val totalKcal = (carbKcal + proteinKcal + fatKcal).coerceAtLeast(1.0)

    val carbPct = ((carbKcal / totalKcal) * 100).roundToInt()
    val proteinPct = ((proteinKcal / totalKcal) * 100).roundToInt()
    val fatPct = (100 - carbPct - proteinPct).coerceAtLeast(0)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Donut Canvas
        Box(
            modifier = Modifier.size(130.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 16.dp.toPx()
                val diameter = size.minDimension - strokeWidth
                val topLeft = Offset(
                    (size.width - diameter) / 2f,
                    (size.height - diameter) / 2f
                )
                val arcSize = Size(diameter, diameter)

                val carbSweep = (carbPct / 100f) * 360f
                val proteinSweep = (proteinPct / 100f) * 360f
                val fatSweep = 360f - carbSweep - proteinSweep

                // Carbs Arc
                drawArc(
                    color = MacroCarb,
                    startAngle = -90f,
                    sweepAngle = carbSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )

                // Protein Arc
                drawArc(
                    color = MacroProtein,
                    startAngle = -90f + carbSweep,
                    sweepAngle = proteinSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )

                // Fat Arc
                drawArc(
                    color = MacroFat,
                    startAngle = -90f + carbSweep + proteinSweep,
                    sweepAngle = fatSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${totalKcal.roundToInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "kcal",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }

        // Açıklamalar
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            MacroLegendItem(
                label = "Karbonhidrat",
                grams = "${carbsGrams.roundToInt()}g",
                pct = "%$carbPct",
                color = MacroCarb
            )
            MacroLegendItem(
                label = "Protein",
                grams = "${proteinGrams.roundToInt()}g",
                pct = "%$proteinPct",
                color = MacroProtein
            )
            MacroLegendItem(
                label = "Yağ",
                grams = "${fatGrams.roundToInt()}g",
                pct = "%$fatPct",
                color = MacroFat
            )
        }
    }
}

@Composable
private fun MacroLegendItem(
    label: String,
    grams: String,
    pct: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = "$label ($pct)",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
            Text(
                text = grams,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

/**
 * Kilo Değişim Çizgi Grafiği
 */
data class WeightPoint(
    val dateLabel: String,
    val weightKg: Double
)

@Composable
fun WeightTrendChart(
    points: List<WeightPoint>,
    targetWeightKg: Double,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Henüz kilo kaydı bulunmuyor", color = TextMuted)
        }
        return
    }

    val minWeight = (points.minOfOrNull { it.weightKg } ?: targetWeightKg)
        .coerceAtMost(targetWeightKg) - 1.5
    val maxWeight = (points.maxOfOrNull { it.weightKg } ?: targetWeightKg)
        .coerceAtLeast(targetWeightKg) + 1.5
    val weightRange = (maxWeight - minWeight).coerceAtLeast(1.0)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val bottomPadding = 25.dp.toPx()
            val topPadding = 15.dp.toPx()
            val chartHeight = canvasHeight - bottomPadding - topPadding

            // Hedef Kilo Kesikli Çizgisi
            val targetY = topPadding + chartHeight * (1f - ((targetWeightKg - minWeight) / weightRange).toFloat())
            drawLine(
                color = MintAccent,
                start = Offset(0f, targetY),
                end = Offset(canvasWidth, targetY),
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
            )

            // Noktalar ve Çizgi
            val stepX = if (points.size > 1) canvasWidth / (points.size - 1) else canvasWidth / 2f
            val path = Path()

            points.forEachIndexed { index, pt ->
                val x = if (points.size > 1) index * stepX else canvasWidth / 2f
                val y = topPadding + chartHeight * (1f - ((pt.weightKg - minWeight) / weightRange).toFloat())

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = ForestGreenPrimary,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Nokta çemberleri & Değerler
            points.forEachIndexed { index, pt ->
                val x = if (points.size > 1) index * stepX else canvasWidth / 2f
                val y = topPadding + chartHeight * (1f - ((pt.weightKg - minWeight) / weightRange).toFloat())

                drawCircle(
                    color = Color.White,
                    radius = 5.dp.toPx(),
                    center = Offset(x, y)
                )
                drawCircle(
                    color = ForestGreenPrimary,
                    radius = 3.dp.toPx(),
                    center = Offset(x, y)
                )

                // Tarih etiketi
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 10.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    drawText(
                        pt.dateLabel,
                        x,
                        canvasHeight - 4.dp.toPx(),
                        paint
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Son: ${points.lastOrNull()?.weightKg ?: "-"} kg",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary
            )
            Text(
                text = "Hedef Kilo: $targetWeightKg kg",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MintAccent
            )
        }
    }
}
