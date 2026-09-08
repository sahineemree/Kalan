package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.KalanTheme
import com.example.ui.theme.MintAccent
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Haftalık Kalori Eğilimi (Dinamik Tablosu)
 * Kullanıcının son 7 günlük beslenme ve kalori dengesini gösteren dinamik grafik kartı
 */
@Composable
fun WeeklyCalorieTrendCard(
    weeklyTrendData: WeeklyTrendData,
    modifier: Modifier = Modifier
) {
    val isDark = KalanTheme.isDark
    val targetCalories = weeklyTrendData.targetCalories.coerceAtLeast(1200.0)

    // Calculate Y-axis max scale (multiples of 600 like in image: 2400, 1800, 1200, 600, 0)
    val maxRecorded = weeklyTrendData.days.maxOfOrNull { it.calories } ?: 0.0
    val dynamicMax = max(targetCalories * 1.15, maxRecorded)
    val maxScale = (kotlin.math.ceil(dynamicMax / 600.0) * 600.0).coerceAtLeast(2400.0)

    val yLabels = listOf(
        maxScale.roundToInt(),
        (maxScale * 0.75).roundToInt(),
        (maxScale * 0.50).roundToInt(),
        (maxScale * 0.25).roundToInt(),
        0
    )

    var selectedDayIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_calorie_trend_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF141C18) else MaterialTheme.colorScheme.surface
        ),
        border = if (isDark) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2F26)) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: İkon, Başlık, 7 Günlük Ortalama
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) MintAccent.copy(alpha = 0.15f) else Color(0xFFE8F5E9),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = MintAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Haftalık Kalori Eğilimi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Son 7 günün genel beslenme ve kalori dengesi",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "7 GÜNLÜK ORT.",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${weeklyTrendData.weeklyAverage.roundToInt()} kcal",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MintAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart Area with Y-axis and Day Bars
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Dashed target guideline in Canvas
                val targetFraction = (targetCalories / maxScale).toFloat().coerceIn(0f, 1f)
                val lineColor = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val chartHeight = size.height - 30.dp.toPx()
                    val targetY = chartHeight * (1f - targetFraction)

                    // Draw dashed guideline across chart width
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                    drawLine(
                        color = lineColor,
                        start = Offset(45.dp.toPx(), targetY),
                        end = Offset(size.width, targetY),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = pathEffect
                    )
                }

                // Chart Grid Content: Y-axis Labels on Left + Bars on Right
                Row(modifier = Modifier.fillMaxSize()) {
                    // Y-axis Labels
                    Column(
                        modifier = Modifier
                            .width(42.dp)
                            .height(170.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        yLabels.forEach { label ->
                            Text(
                                text = "$label",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // 7 Bars and Day Labels
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        // "Hedef X" text placed along dashed line
                        Text(
                            text = "Hedef ${targetCalories.roundToInt()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(
                                    top = (170.dp * (1f - targetFraction) + 4.dp).coerceAtLeast(0.dp)
                                )
                        )

                        // Bars Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp)
                                .align(Alignment.TopStart),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            weeklyTrendData.days.forEachIndexed { index, dayStat ->
                                val isSelected = selectedDayIndex == index
                                val ratio = (dayStat.calories / maxScale).toFloat().coerceIn(0f, 1f)
                                val animatedHeightRatio by animateFloatAsState(
                                    targetValue = ratio,
                                    animationSpec = tween(durationMillis = 600),
                                    label = "bar_height"
                                )

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable {
                                            selectedDayIndex = if (selectedDayIndex == index) null else index
                                        },
                                    verticalArrangement = Arrangement.Bottom
                                ) {
                                    // Tooltip or selected day value
                                    if (isSelected && dayStat.calories > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MintAccent,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        ) {
                                            Text(
                                                text = "${dayStat.calories.roundToInt()}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ForestGreenDark,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    // Bar Container
                                    Box(
                                        modifier = Modifier
                                            .width(if (dayStat.isToday) 20.dp else 16.dp)
                                            .fillMaxHeight(fraction = animatedHeightRatio.coerceAtLeast(0.04f))
                                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                            .background(
                                                brush = Brush.verticalGradient(
                                                    colors = when {
                                                        dayStat.calories == 0.0 -> listOf(
                                                            MaterialTheme.colorScheme.surfaceVariant,
                                                            MaterialTheme.colorScheme.surfaceVariant
                                                        )
                                                        dayStat.isToday -> listOf(
                                                            MintAccent,
                                                            ForestGreenPrimary
                                                        )
                                                        else -> listOf(
                                                            MintAccent.copy(alpha = 0.85f),
                                                            ForestGreenPrimary.copy(alpha = 0.85f)
                                                        )
                                                    }
                                                )
                                            )
                                    )
                                }
                            }
                        }

                        // Day Labels Row at Bottom
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .height(26.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            weeklyTrendData.days.forEach { dayStat ->
                                Text(
                                    text = dayStat.dayShortLabel,
                                    fontSize = if (dayStat.isToday) 11.sp else 10.sp,
                                    fontWeight = if (dayStat.isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = if (dayStat.isToday) (if (isDark) MintAccent else ForestGreenDark) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = if (isDark) Color(0xFF1E2F26) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))

            // Bottom 3 Summary Boxes in a Row (exact match to image.png!)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Box 1: Haftalık Toplam
                SummaryMetricBox(
                    label = "Haftalık Toplam",
                    value = "${weeklyTrendData.weeklyTotal.roundToInt()} kcal",
                    valueColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    isDark = isDark
                )

                // Box 2: Hedefe Ulaşma
                SummaryMetricBox(
                    label = "Hedefe Ulaşma",
                    value = "${weeklyTrendData.daysReachedTarget} / ${weeklyTrendData.totalRecordedDays} gün",
                    valueColor = MintAccent,
                    modifier = Modifier.weight(1f),
                    isDark = isDark
                )

                // Box 3: Bugün Durumu
                SummaryMetricBox(
                    label = "Bugün Durumu",
                    value = "${weeklyTrendData.todayCalories.roundToInt()} kcal",
                    valueColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    isDark = isDark
                )
            }
        }
    }
}

@Composable
private fun SummaryMetricBox(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
    isDark: Boolean
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) Color(0xFF1A241F) else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isDark) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22352B)) else null,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
