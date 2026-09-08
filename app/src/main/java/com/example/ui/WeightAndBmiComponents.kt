package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WeightEntry
import com.example.ui.theme.AlertExceeded
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.KalanTheme
import com.example.ui.theme.MintAccent
import com.example.ui.theme.MintLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Tıbbi Vücut Kitle İndeksi (VKİ / BMI) Sınıflandırma Modeli
 */
data class MedicalBmiClassification(
    val bmi: Double,
    val categoryTitle: String,
    val shortBadge: String,
    val medicalExplanation: String,
    val categoryColor: Color,
    val idealWeightMinKg: Double,
    val idealWeightMaxKg: Double,
    val weightDifferenceToIdealKg: Double,
    val isObese: Boolean,
    val isUnderweight: Boolean
)

object BmiMedicalAdvisor {
    fun calculate(weightKg: Double, heightCm: Double): MedicalBmiClassification? {
        if (weightKg <= 15.0 || heightCm <= 50.0) return null
        val heightM = heightCm / 100.0
        val bmi = weightKg / (heightM * heightM)
        val idealMin = 18.5 * (heightM * heightM)
        val idealMax = 24.9 * (heightM * heightM)

        val diff = when {
            weightKg < idealMin -> weightKg - idealMin
            weightKg > idealMax -> weightKg - idealMax
            else -> 0.0
        }

        return when {
            bmi < 18.5 -> MedicalBmiClassification(
                bmi = bmi,
                categoryTitle = "Düşük Kilolu / İleri Düzey Zayıflık (Malnütrisyon Riski)",
                shortBadge = "Zayıf (Düşük Kilo)",
                medicalExplanation = "Vücut kitle indeksiniz Dünya Sağlık Örgütü (DSÖ) referans sınırının altındadır (<18.5 kg/m²). Tıbbi literatürde bu durum hiponutrisyon, kemik mineral yoğunluğunda azalma (osteopeni), kas atrofisi ve bağışıklık direncinin düşmesi ile ilişkilendirilir. Yağsız kas dokusunu ve glikojen depolarını destekleyen, besin öğesi yoğunluğu yüksek hiperkalorik bir beslenme protokolü önerilir.",
                categoryColor = Color(0xFFF59E0B), // Warm Amber
                idealWeightMinKg = idealMin,
                idealWeightMaxKg = idealMax,
                weightDifferenceToIdealKg = diff,
                isObese = false,
                isUnderweight = true
            )
            bmi in 18.5..24.99 -> MedicalBmiClassification(
                bmi = bmi,
                categoryTitle = "Normal / Sağlıklı Referans Ağırlık (Ötrofik)",
                shortBadge = "Sağlıklı / İdeal",
                medicalExplanation = "Vücut kitle indeksiniz tıbbi ve fizyolojik açıdan altın standart kabul edilen DSÖ ideal referans aralığındadır (18.5 - 24.9 kg/m²). Kardiyovasküler hastalıklar, insülin direnci ve metabolik sendrom gelişme riski istatistiksel olarak en düşük düzeydedir. Mevcut dengeli makro dağılımınızı ve aktif yaşam tarzınızı korumanız tavsiye edilir.",
                categoryColor = Color(0xFF10B981), // Emerald / Mint Green
                idealWeightMinKg = idealMin,
                idealWeightMaxKg = idealMax,
                weightDifferenceToIdealKg = 0.0,
                isObese = false,
                isUnderweight = false
            )
            bmi in 25.0..29.99 -> MedicalBmiClassification(
                bmi = bmi,
                categoryTitle = "Fazla Kilolu / Pre-Obezite (Artmış Metabolik Yük)",
                shortBadge = "Fazla Kilolu (Pre-Obez)",
                medicalExplanation = "Vücut kitle indeksiniz referans aralığın üzerindedir (25.0 - 29.9 kg/m² - Pre-obezite evresi). Henüz klinik obezite tanısı konulmamış olmakla birlikte; erken evre insülin direnci, hafif tansiyon yüksekliği ve karaciğer yağlanması (hepatosteatoz) riskini önlemek amacıyla kontrollü ve dengeli bir kalori açığı (hipokalorik diyet) önerilir.",
                categoryColor = Color(0xFFF97316), // Orange
                idealWeightMinKg = idealMin,
                idealWeightMaxKg = idealMax,
                weightDifferenceToIdealKg = diff,
                isObese = false,
                isUnderweight = false
            )
            bmi in 30.0..34.99 -> MedicalBmiClassification(
                bmi = bmi,
                categoryTitle = "1. Derece Obezite (Sınıf I / Hafif-Orta Klinik Obezite)",
                shortBadge = "1. Derece Obezite",
                medicalExplanation = "Vücut kitle indeksiniz 30.0 - 34.9 kg/m² olup klinik tıpta 1. Derece Obezite sınıfına girmektedir. Kardiyovasküler sistem, insülin duyarlılığı ve diz/eklem biyomekaniği üzerinde kronik metabolik yük oluşmaya başlamıştır. Rafine şeker ve işlenmiş karbonhidrat kısıtlaması, lif ve protein ağırlıklı sürdürülebilir kalori kısıtlaması esastır.",
                categoryColor = Color(0xFFEF4444), // Red
                idealWeightMinKg = idealMin,
                idealWeightMaxKg = idealMax,
                weightDifferenceToIdealKg = diff,
                isObese = true,
                isUnderweight = false
            )
            bmi in 35.0..39.99 -> MedicalBmiClassification(
                bmi = bmi,
                categoryTitle = "2. Derece Obezite (Sınıf II / Ciddi Klinik Obezite)",
                shortBadge = "2. Derece Obezite",
                medicalExplanation = "Vücut kitle indeksiniz 35.0 - 39.9 kg/m² düzeyinde olup klinik olarak 2. Derece Ciddi Obezite olarak tanımlanır. Tip 2 diyabet, ateroskleroz, dislipidemi ve uyku apnesi gibi komorbiditelerin gelişme riski anlamlı derecede yüksektir. Profesyonel beslenme uzmanı eşliğinde yapılandırılmış klinik kalori kısıtlaması hedeflenmelidir.",
                categoryColor = Color(0xFFDC2626), // Strong Red
                idealWeightMinKg = idealMin,
                idealWeightMaxKg = idealMax,
                weightDifferenceToIdealKg = diff,
                isObese = true,
                isUnderweight = false
            )
            else -> MedicalBmiClassification(
                bmi = bmi,
                categoryTitle = "3. Derece Morbid Obezite (Sınıf III / Ağır Klinik Obezite)",
                shortBadge = "Morbid Obezite",
                medicalExplanation = "Vücut kitle indeksiniz 40.0 kg/m² ve üzerindedir. Tıbbi literatürde Morbid Obezite olarak sınıflandırılır. Yaşam kalitesi ve hayati organ sistemleri üzerinde ileri derecede hemodinamik ve metabolik risk taşır. Endokrinoloji hekimi ve klinik diyetisyen eşliğinde kapsamlı, multidisipliner tıbbi takip programı önerilir.",
                categoryColor = Color(0xFF991B1B), // Deep Maroon
                idealWeightMinKg = idealMin,
                idealWeightMaxKg = idealMax,
                weightDifferenceToIdealKg = diff,
                isObese = true,
                isUnderweight = false
            )
        }
    }
}

/**
 * Vücut Kitle İndeksi & Tıbbi Açıklama Kartı
 */
@Composable
fun MedicalBmiCard(
    currentWeightKg: Double,
    heightCm: Double,
    modifier: Modifier = Modifier
) {
    val isDark = KalanTheme.isDark
    val classification = remember(currentWeightKg, heightCm) {
        BmiMedicalAdvisor.calculate(currentWeightKg, heightCm)
    }

    if (classification == null) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("medical_bmi_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = classification.categoryColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (classification.isObese) Icons.Default.Warning else if (classification.isUnderweight) Icons.Default.Info else Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = classification.categoryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Vücut Kitle İndeksi (VKİ)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Klinik & Tıbbi Değerlendirme",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Category Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = classification.categoryColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = classification.shortBadge,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = classification.categoryColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Metric and Ideal Range Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BMI Big Number
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "VKİ DEĞERİNİZ",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format(Locale.US, "%.1f", classification.bmi),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = classification.categoryColor
                        )
                        Text(
                            text = "kg/m²",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Ideal Weight Range Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1.3f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "İDEAL REFERANS KİLO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${String.format(Locale.US, "%.1f", classification.idealWeightMinKg)} - ${String.format(Locale.US, "%.1f", classification.idealWeightMaxKg)} kg",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = when {
                                classification.weightDifferenceToIdealKg > 0 -> "İdeal üst sınıra: -${String.format(Locale.US, "%.1f", classification.weightDifferenceToIdealKg)} kg"
                                classification.weightDifferenceToIdealKg < 0 -> "İdeal alt sınıra: +${String.format(Locale.US, "%.1f", abs(classification.weightDifferenceToIdealKg))} kg"
                                else -> "Tebrikler, ideal aralıktasınız ✨"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (classification.weightDifferenceToIdealKg == 0.0) ForestGreenPrimary else classification.categoryColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visual Multi-segment BMI Gauge Bar
            BmiSpectrumGauge(bmi = classification.bmi)

            Spacer(modifier = Modifier.height(14.dp))

            // Medical Diagnosis & Clinical Analysis Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isDark) classification.categoryColor.copy(alpha = 0.12f) else classification.categoryColor.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, classification.categoryColor.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = classification.categoryColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = classification.categoryTitle,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = classification.categoryColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = classification.medicalExplanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}

/**
 * Renkli VKİ Spektrum Skalası ve İbre Göstergesi
 */
@Composable
private fun BmiSpectrumGauge(bmi: Double) {
    val minScale = 15.0
    val maxScale = 42.0
    val normalizedPos = ((bmi - minScale) / (maxScale - minScale)).coerceIn(0.0, 1.0).toFloat()
    val animatedPos by animateFloatAsState(
        targetValue = normalizedPos,
        animationSpec = tween(800),
        label = "BmiGaugePosition"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // Multi-segment colored bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // <18.5 (Zayıf) ~ %13
                Box(modifier = Modifier.weight(3.5f).fillMaxSize().background(Color(0xFFF59E0B)))
                // 18.5 - 25.0 (Normal) ~ %24
                Box(modifier = Modifier.weight(6.5f).fillMaxSize().background(Color(0xFF10B981)))
                // 25.0 - 30.0 (Fazla) ~ %18.5
                Box(modifier = Modifier.weight(5f).fillMaxSize().background(Color(0xFFF97316)))
                // 30.0 - 35.0 (1. Obez) ~ %18.5
                Box(modifier = Modifier.weight(5f).fillMaxSize().background(Color(0xFFEF4444)))
                // >35.0 (2-3. Morbid Obez) ~ %26
                Box(modifier = Modifier.weight(7f).fillMaxSize().background(Color(0xFF991B1B)))
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Pointer indicator Canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
        ) {
            val indicatorX = size.width * animatedPos
            val path = Path().apply {
                moveTo(indicatorX, 0f)
                lineTo(indicatorX - 6.dp.toPx(), 10.dp.toPx())
                lineTo(indicatorX + 6.dp.toPx(), 10.dp.toPx())
                close()
            }
            drawPath(path, color = Color(0xFF10B981))
        }

        // Labels under spectrum
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("<18.5\nZayıf", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, lineHeight = 11.sp)
            Text("18.5-25\nNormal", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, lineHeight = 11.sp)
            Text("25-30\nFazla", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, lineHeight = 11.sp)
            Text("30-35\n1. Obez", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, lineHeight = 11.sp)
            Text(">35\nMorbid", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, lineHeight = 11.sp)
        }
    }
}

/**
 * Dinamik Kilo ve Tarih Grafiği Kartı
 * Kullanıcı yeni kilo girdikçe veya güncelledikçe grafik otomatik olarak yeniden çizilir.
 */
@Composable
fun DynamicWeightChartCard(
    weightEntries: List<WeightEntry>,
    currentProfileWeight: Double,
    targetWeightKg: Double,
    onLogWeight: (weightKg: Double, date: String) -> Unit,
    onDeleteWeight: (id: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = KalanTheme.isDark
    val todaySdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val todayStr = remember { todaySdf.format(Date()) }

    var showAddDialog by remember { mutableStateOf(false) }

    // Group entries by date taking the latest one for each day, then sort chronologically
    val processedEntries = remember(weightEntries, currentProfileWeight) {
        val list = if (weightEntries.isEmpty()) {
            listOf(WeightEntry(id = -1, date = todayStr, weightKg = currentProfileWeight))
        } else {
            weightEntries.groupBy { it.date }.map { (_, entries) ->
                entries.maxByOrNull { it.timestamp } ?: entries.first()
            }.sortedBy { it.date }
        }
        list
    }

    val latestWeight = processedEntries.lastOrNull()?.weightKg ?: currentProfileWeight
    val firstWeight = processedEntries.firstOrNull()?.weightKg ?: currentProfileWeight
    val totalChange = latestWeight - firstWeight
    val remainingToTarget = latestWeight - targetWeightKg

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dynamic_weight_chart_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isDark) MintAccent.copy(alpha = 0.2f) else MintLight,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MonitorWeight,
                                contentDescription = null,
                                tint = if (isDark) MintAccent else ForestGreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Kilo Takip & Zaman Çizelgesi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${processedEntries.size} Ölçüm Kaydedildi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // + Yeni Kilo Ekle Butonu
                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) MintAccent else ForestGreenPrimary,
                        contentColor = if (isDark) ForestGreenDark else Color.White
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("add_weight_entry_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kilo Ekle", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Metric Summary Boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WeightMiniStatBox(
                    label = "Başlangıç",
                    value = "${String.format(Locale.US, "%.1f", firstWeight)} kg",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                WeightMiniStatBox(
                    label = "Güncel Kilo",
                    value = "${String.format(Locale.US, "%.1f", latestWeight)} kg",
                    color = if (isDark) MintAccent else ForestGreenPrimary,
                    isHighlight = true,
                    modifier = Modifier.weight(1f)
                )
                WeightMiniStatBox(
                    label = "Hedef Kilo",
                    value = "${String.format(Locale.US, "%.1f", targetWeightKg)} kg",
                    color = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Canvas Chart with subtle background styling
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else Color(0xFFF9FAFB),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                WeightLineChartCanvas(
                    entries = processedEntries,
                    targetWeightKg = targetWeightKg,
                    isDark = isDark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress & Trend Indicator Row
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when {
                                totalChange < -0.1 -> Icons.AutoMirrored.Filled.TrendingDown
                                totalChange > 0.1 -> Icons.AutoMirrored.Filled.TrendingUp
                                else -> Icons.AutoMirrored.Filled.TrendingFlat
                            },
                            contentDescription = null,
                            tint = when {
                                totalChange < -0.1 -> ForestGreenPrimary
                                totalChange > 0.1 -> Color(0xFFF97316)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Değişim: ${if (totalChange > 0) "+" else ""}${String.format(Locale.US, "%.1f", totalChange)} kg",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrackChanges,
                            contentDescription = null,
                            tint = if (abs(remainingToTarget) <= 0.3) ForestGreenPrimary else Color(0xFF0284C7),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when {
                                abs(remainingToTarget) <= 0.3 -> "Hedefe Ulaşıldı! 🏆"
                                remainingToTarget > 0 -> "Hedefe ${String.format(Locale.US, "%.1f", remainingToTarget)} kg kaldı"
                                else -> "Hedefin ${String.format(Locale.US, "%.1f", abs(remainingToTarget))} kg altındasınız"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (abs(remainingToTarget) <= 0.3) ForestGreenPrimary else Color(0xFF0284C7)
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddOrUpdateWeightDialog(
            currentWeight = latestWeight,
            todayDate = todayStr,
            onDismiss = { showAddDialog = false },
            onConfirm = { weight, date ->
                onLogWeight(weight, date)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun WeightMiniStatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    isHighlight: Boolean = false
) {
    val isDark = KalanTheme.isDark
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isHighlight) {
            if (isDark) MintAccent.copy(alpha = 0.18f) else MintLight
        } else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isHighlight) BorderStroke(1.dp, MintAccent.copy(alpha = 0.5f)) else null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
    }
}

/**
 * Dinamik Canvas Çizim Grafiği (Tarih ve Kilo Eksenli)
 */
@Composable
private fun WeightLineChartCanvas(
    entries: List<WeightEntry>,
    targetWeightKg: Double,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val displaySdf = remember { SimpleDateFormat("dd MMM", Locale.forLanguageTag("tr-TR")) }
    val parseSdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val weights = entries.map { it.weightKg }
    val allValues = weights + (if (targetWeightKg > 0) listOf(targetWeightKg) else emptyList())

    val minWeight = (allValues.minOrNull() ?: 60.0) - 2.0
    val maxWeight = (allValues.maxOrNull() ?: 90.0) + 2.0
    val weightRange = (maxWeight - minWeight).coerceAtLeast(1.0)

    val gridColor = if (isDark) Color(0xFF3F3F46) else Color(0xFFE5E7EB)
    val textPaint = remember(isDark) {
        android.graphics.Paint().apply {
            color = if (isDark) android.graphics.Color.LTGRAY else android.graphics.Color.DKGRAY
            textSize = 28f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
        }
    }
    val targetPaint = remember(isDark) {
        android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#0284C7")
            textSize = 26f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.RIGHT
        }
    }

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val paddingLeft = 50f
        val paddingRight = 40f
        val paddingTop = 30f
        val paddingBottom = 45f

        val plotWidth = canvasWidth - paddingLeft - paddingRight
        val plotHeight = canvasHeight - paddingTop - paddingBottom

        // 1. Draw 3 Horizontal Grid Lines (Min, Mid, Max)
        val gridSteps = 3
        for (i in 0..gridSteps) {
            val y = paddingTop + plotHeight * (i.toFloat() / gridSteps)
            val valAtStep = maxWeight - (weightRange * (i.toFloat() / gridSteps))

            drawLine(
                color = gridColor,
                start = Offset(paddingLeft, y),
                end = Offset(canvasWidth - paddingRight, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            // Y Axis Label
            drawContext.canvas.nativeCanvas.drawText(
                "${valAtStep.roundToInt()}",
                paddingLeft - 18f,
                y + 10f,
                textPaint.apply { textAlign = android.graphics.Paint.Align.RIGHT }
            )
        }

        // 2. Draw Target Weight Dashed Reference Line
        if (targetWeightKg in minWeight..maxWeight) {
            val targetY = paddingTop + (plotHeight * (1f - ((targetWeightKg - minWeight) / weightRange).toFloat()))
            drawLine(
                color = Color(0xFF0284C7),
                start = Offset(paddingLeft, targetY),
                end = Offset(canvasWidth - paddingRight, targetY),
                strokeWidth = 1.8.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
            )

            drawContext.canvas.nativeCanvas.drawText(
                "🎯 Hedef ${String.format(Locale.US, "%.1f", targetWeightKg)} kg",
                canvasWidth - paddingRight - 8f,
                targetY - 8f,
                targetPaint
            )
        }

        // 3. Calculate points for entries
        val points = mutableListOf<Offset>()
        val count = entries.size

        for (i in 0 until count) {
            val x = if (count == 1) {
                paddingLeft + plotWidth / 2f
            } else {
                paddingLeft + (plotWidth * (i.toFloat() / (count - 1)))
            }
            val normWeight = ((entries[i].weightKg - minWeight) / weightRange).toFloat().coerceIn(0f, 1f)
            val y = paddingTop + (plotHeight * (1f - normWeight))
            points.add(Offset(x, y))
        }

        // 4. Fill Area Under Curve (Gradient)
        if (points.size >= 2) {
            val fillPath = Path().apply {
                moveTo(points.first().x, paddingTop + plotHeight)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, paddingTop + plotHeight)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ForestGreenPrimary.copy(alpha = 0.35f),
                        MintAccent.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    startY = paddingTop,
                    endY = paddingTop + plotHeight
                )
            )

            // 5. Draw Stroke Line
            val strokePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val curr = points[i]
                    val midX = (prev.x + curr.x) / 2f
                    cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                }
            }

            drawPath(
                path = strokePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(ForestGreenPrimary, MintAccent)
                ),
                style = Stroke(width = 3.5.dp.toPx())
            )
        }

        // 6. Draw Dots, Value Callouts, and Dates
        points.forEachIndexed { index, point ->
            val entry = entries[index]

            // Glow Circle
            drawCircle(
                color = ForestGreenPrimary.copy(alpha = 0.25f),
                radius = 7.dp.toPx(),
                center = point
            )

            // Inner Dot
            drawCircle(
                color = if (isDark) MintAccent else ForestGreenPrimary,
                radius = 4.dp.toPx(),
                center = point
            )

            // Weight Value Label above dot
            val weightText = String.format(Locale.US, "%.1f", entry.weightKg)
            drawContext.canvas.nativeCanvas.drawText(
                weightText,
                point.x,
                point.y - 14f,
                textPaint.apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 28f
                    isFakeBoldText = true
                    color = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.parseColor("#1B4332")
                }
            )

            // Date Label on X Axis below point
            val parsedDate = try { parseSdf.parse(entry.date) ?: Date() } catch (e: Exception) { Date() }
            val dateLabel = displaySdf.format(parsedDate)

            // Draw date label if points aren't too crowded
            if (count <= 7 || index % (count / 5).coerceAtLeast(1) == 0 || index == count - 1) {
                drawContext.canvas.nativeCanvas.drawText(
                    dateLabel,
                    point.x,
                    canvasHeight - 8f,
                    textPaint.apply {
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 26f
                        isFakeBoldText = false
                        color = if (isDark) android.graphics.Color.LTGRAY else android.graphics.Color.GRAY
                    }
                )
            }
        }
    }
}

/**
 * Hızlı Kilo Kayıt ve Tarih Güncelleme Dialogu
 */
@Composable
fun AddOrUpdateWeightDialog(
    currentWeight: Double,
    todayDate: String,
    onDismiss: () -> Unit,
    onConfirm: (weight: Double, date: String) -> Unit
) {
    var weightInput by remember { mutableStateOf(if (currentWeight > 0) currentWeight.toString() else "") }
    var selectedDate by remember { mutableStateOf(todayDate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MonitorWeight,
                    contentDescription = null,
                    tint = ForestGreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kilo Ölçümü Kaydet", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
        },
        text = {
            Column {
                Text(
                    text = "Ölçtüğünüz güncel kilonuzu girin. Grafik zaman çizelgeniz anında yenilenecektir.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Kilo (kg)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreenPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tarih Bilgisi (Bugün veya serbest tarih girişi YYYY-MM-DD)
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = { selectedDate = it },
                    label = { Text("Tarih (YYYY-AA-GG)") },
                    singleLine = true,
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
                onClick = {
                    val parsed = weightInput.toDoubleOrNull()
                    if (parsed != null && parsed > 0) {
                        onConfirm(parsed, selectedDate.trim().ifBlank { todayDate })
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Kaydet & Grafiğe Ekle", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
