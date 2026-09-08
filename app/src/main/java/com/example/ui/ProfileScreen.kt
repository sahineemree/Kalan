package com.example.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calculator.NutritionCalculator
import com.example.data.ActivityLevel
import com.example.data.Gender
import com.example.data.GoalType
import com.example.data.MacroPreset
import com.example.data.UserProfile
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
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(
    viewModel: KalanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val calcResult by viewModel.calculationResult.collectAsStateWithLifecycle()

    var userNameText by remember(userProfile) { mutableStateOf(userProfile.userName) }

    // Photo picker for profile avatar
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (originalBitmap != null) {
                    val maxDim = 400
                    val ratio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
                    val targetW = if (ratio > 1) maxDim else (maxDim * ratio).toInt()
                    val targetH = if (ratio > 1) (maxDim / ratio).toInt() else maxDim
                    val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetW, targetH, true)
                    val outputStream = ByteArrayOutputStream()
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                    val byteArray = outputStream.toByteArray()
                    val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                    viewModel.updateAvatar(base64String)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    var gender by remember(userProfile) {
        mutableStateOf(try { Gender.valueOf(userProfile.gender) } catch (e: Exception) { Gender.ERKEK })
    }
    var ageText by remember(userProfile) { mutableStateOf(userProfile.age.toString()) }
    var heightText by remember(userProfile) { mutableStateOf(userProfile.heightCm.roundToInt().toString()) }
    var weightText by remember(userProfile) { mutableStateOf(userProfile.currentWeightKg.toString()) }
    var targetWeightText by remember(userProfile) { mutableStateOf(userProfile.targetWeightKg.toString()) }
    var activityLevel by remember(userProfile) {
        mutableStateOf(try { ActivityLevel.valueOf(userProfile.activityLevel) } catch (e: Exception) { ActivityLevel.MODERATE })
    }
    var goalType by remember(userProfile) {
        mutableStateOf(try { GoalType.valueOf(userProfile.goalType) } catch (e: Exception) { GoalType.LOSE })
    }
    var macroPreset by remember(userProfile) {
        mutableStateOf(try { MacroPreset.valueOf(userProfile.macroPreset) } catch (e: Exception) { MacroPreset.BALANCED })
    }

    var showSettingsModal by remember { mutableStateOf(false) }
    var saveSuccessMessage by remember { mutableStateOf<String?>(null) }
    var isRecalculatingWithGemini by remember { mutableStateOf(false) }

    fun saveCurrentProfile(
        overrideGoalType: GoalType? = null,
        overrideMacroPreset: MacroPreset? = null
    ) {
        val effectiveGoal = overrideGoalType ?: goalType
        val effectiveMacroPreset = overrideMacroPreset ?: macroPreset

        val weight = weightText.toDoubleOrNull() ?: userProfile.currentWeightKg
        val height = heightText.toDoubleOrNull() ?: userProfile.heightCm
        val age = ageText.toIntOrNull() ?: userProfile.age

        // Recalculate target calories and macros matching the user's goal
        val bmr = NutritionCalculator.calculateBmr(gender, weight, height, age)
        val tdee = NutritionCalculator.calculateTdee(bmr, activityLevel)
        val calculatedTargetCalories = (tdee + effectiveGoal.calorieDelta).coerceAtLeast(1200.0)
        val (carbGrams, protGrams, fatGrams) = NutritionCalculator.calculateMacroGrams(
            calculatedTargetCalories,
            effectiveMacroPreset
        )

        val updated = userProfile.copy(
            userName = userNameText.trim().ifBlank { "Kullanıcı" },
            gender = gender.name,
            age = age,
            heightCm = height,
            currentWeightKg = weight,
            targetWeightKg = targetWeightText.toDoubleOrNull() ?: userProfile.targetWeightKg,
            activityLevel = activityLevel.name,
            goalType = effectiveGoal.name,
            macroPreset = effectiveMacroPreset.name,
            customTargetCalories = calculatedTargetCalories,
            customCarbsGrams = carbGrams,
            customProteinGrams = protGrams,
            customFatGrams = fatGrams
        )
        viewModel.updateProfile(updated)
        saveSuccessMessage = "Profil ve hedefler başarıyla güncellendi!"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Üst Başlık & Sağ Üstte ⚙️ Ayarlar Dişli Butonu
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Profil & Hedefler",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Kişisel veriler ve beslenme ayarları",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(44.dp)
                ) {
                    IconButton(
                        onClick = { showSettingsModal = true },
                        modifier = Modifier.testTag("profile_settings_gear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ayarlar",
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // 1. Profil Fotoğrafı & Avatar Alanı
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_avatar_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Profil Fotoğrafı & Kimlik",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Large Avatar with Camera badge
                    ProfileAvatar(
                        avatarBase64 = userProfile.avatarBase64,
                        userName = userNameText,
                        size = 96.dp,
                        showCameraBadge = true,
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Ad Soyad Input
                    OutlinedTextField(
                        value = userNameText,
                        onValueChange = {
                            userNameText = it
                            viewModel.updateUserName(it)
                        },
                        label = { Text("Ad Soyad") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreenPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_username_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buttons row: Fotoğraf Seç / Kaldır
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary,
                                contentColor = if (KalanTheme.isDark) ForestGreenDark else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (userProfile.avatarBase64 != null) "Değiştir" else "Fotoğraf Yükle",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (userProfile.avatarBase64 != null) {
                            OutlinedButton(
                                onClick = { viewModel.updateAvatar(null) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertExceeded),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Fotoğrafı Kaldır", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Avatar helper text removed as requested
                }
            }
        }

        // Gemini'nin Sizin İçin Hesapladığı Kalori Kartı
        item {
            val targetCal = (userProfile.customTargetCalories ?: calcResult.targetCalories).roundToInt()
            val isDark = KalanTheme.isDark

            // Kullanıcının hedefine göre başlık/açıklama metni
            val goalText = when (goalType) {
                GoalType.LOSE -> "Kilo vermek için"
                GoalType.GAIN -> "Kilo almanız için"
                GoalType.MAINTAIN -> "Kilonuzu korumak için"
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (isDark) ForestGreenPrimary.copy(alpha = 0.35f) else MintLight,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = if (isDark) MintAccent else ForestGreenPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Gemini Kalori Hesabı",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Yapay zeka destekli hedefiniz",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Gemini AI ile Yeniden Hesapla Butonu
                        IconButton(
                            onClick = {
                                isRecalculatingWithGemini = true
                                viewModel.recalculateCaloriesWithGemini {
                                    isRecalculatingWithGemini = false
                                    saveSuccessMessage = "Kaloriniz Gemini AI tarafından güncellendi!"
                                }
                            },
                            enabled = !isRecalculatingWithGemini
                        ) {
                            if (isRecalculatingWithGemini) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = ForestGreenPrimary)
                            } else {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = "Yeniden Hesapla",
                                    tint = ForestGreenPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isDark) ForestGreenPrimary.copy(alpha = 0.25f) else MintLight.copy(alpha = 0.7f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDark) MintAccent.copy(alpha = 0.5f) else ForestGreenPrimary.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$goalText:",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) MintAccent else ForestGreenDark,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "$targetCal",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Black,
                                    color = if (isDark) Color.White else ForestGreenPrimary,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "kalori",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) MintAccent else ForestGreenDark,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Kişisel Bilgiler Düzenleme Formu
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "VÜCUT ÖLÇÜLERİ VE BİLGİLER",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Cinsiyet Seçimi
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(Gender.ERKEK, Gender.KADIN).forEach { g ->
                            val isSelected = gender == g
                            val isDark = KalanTheme.isDark
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) {
                                    if (isDark) ForestGreenPrimary.copy(alpha = 0.35f) else MintLight
                                } else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MintAccent) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        gender = g
                                        saveCurrentProfile()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = g.titleTr,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) {
                                            if (isDark) MintAccent else ForestGreenDark
                                        } else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Yaş ve Boy
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = ageText,
                            onValueChange = {
                                ageText = it.filter { ch -> ch.isDigit() }
                                saveCurrentProfile()
                            },
                            label = { Text("Yaş") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = heightText,
                            onValueChange = {
                                heightText = it.filter { ch -> ch.isDigit() }
                                saveCurrentProfile()
                            },
                            label = { Text("Boy (cm)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Kilo ve Hedef Kilo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = weightText,
                            onValueChange = {
                                weightText = it
                                val parsed = it.toDoubleOrNull()
                                if (parsed != null && parsed in 20.0..350.0) {
                                    viewModel.logWeight(parsed)
                                }
                                saveCurrentProfile()
                            },
                            label = { Text("Mevcut Kilo (kg)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = targetWeightText,
                            onValueChange = {
                                targetWeightText = it
                                saveCurrentProfile()
                            },
                            label = { Text("Hedef Kilo (kg)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Vücut Kitle İndeksi (VKİ) & Tıbbi Değerlendirme Kartı
        item {
            val currentWeight = weightText.toDoubleOrNull() ?: userProfile.currentWeightKg
            val currentHeight = heightText.toDoubleOrNull() ?: userProfile.heightCm
            MedicalBmiCard(
                currentWeightKg = currentWeight,
                heightCm = currentHeight
            )
        }

        // Kilo Takip & Zaman Çizelgesi Dinamik Grafik Kartı
        item {
            val allWeights by viewModel.allWeights.collectAsStateWithLifecycle()
            val currentWeight = weightText.toDoubleOrNull() ?: userProfile.currentWeightKg
            val targetWeight = targetWeightText.toDoubleOrNull() ?: userProfile.targetWeightKg

            DynamicWeightChartCard(
                weightEntries = allWeights,
                currentProfileWeight = currentWeight,
                targetWeightKg = targetWeight,
                onLogWeight = { weight, date ->
                    viewModel.logWeight(weight, date)
                    weightText = weight.toString()
                },
                onDeleteWeight = { id ->
                    viewModel.deleteWeight(id)
                }
            )
        }

        // Günlük Aktivite Seviyesi Seçimi
        item {
            val isDark = KalanTheme.isDark
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "GÜNLÜK HAREKET VE AKTİVİTE SEVİYESİ",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) MintAccent else ForestGreenPrimary,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    ActivityLevel.entries.forEach { level ->
                        val isSelected = activityLevel == level
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) {
                                if (isDark) ForestGreenPrimary.copy(alpha = 0.35f) else MintLight
                            } else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MintAccent) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    activityLevel = level
                                    saveCurrentProfile()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = level.titleTr,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (isSelected) {
                                            if (isDark) MintAccent else ForestGreenDark
                                        } else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = level.descriptionTr,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (isDark) MintAccent else ForestGreenDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Beslenme Hedefi (Kilo Verme / Kilo Koruma / Kilo Alma)
        item {
            val isDark = KalanTheme.isDark
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "BESLENME HEDEFİ",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) MintAccent else ForestGreenPrimary,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GoalType.entries.forEach { g ->
                            val isSelected = goalType == g
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) {
                                    if (isDark) ForestGreenPrimary.copy(alpha = 0.35f) else MintLight
                                } else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MintAccent) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        goalType = g
                                        saveCurrentProfile(overrideGoalType = g)
                                    }
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
                                ) {
                                    Text(
                                        text = g.titleTr,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (isSelected) {
                                            if (isDark) MintAccent else ForestGreenDark
                                        } else MaterialTheme.colorScheme.onSurface,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = when (g) {
                                            GoalType.LOSE -> "-500 kcal"
                                            GoalType.MAINTAIN -> "Sabit"
                                            GoalType.GAIN -> "+400 kcal"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Makro Dağılım Tercihi
        item {
            val isDark = KalanTheme.isDark
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "MAKRO BESİN DAĞILIM DİYETİ",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) MintAccent else ForestGreenPrimary,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    MacroPreset.entries.forEach { preset ->
                        val isSelected = macroPreset == preset
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) {
                                if (isDark) ForestGreenPrimary.copy(alpha = 0.35f) else MintLight
                            } else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MintAccent) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    macroPreset = preset
                                    saveCurrentProfile(overrideMacroPreset = preset)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = preset.titleTr,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) {
                                        if (isDark) MintAccent else ForestGreenDark
                                    } else MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (isDark) MintAccent else ForestGreenDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Hakkında & Sürüm Bilgisi
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Kalan • Günlük Kalori & Beslenme Takip",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary
                )
                Text(
                    text = "Sürüm 1.0.0 • Çevrimdışı & Yerel Veri Tabanı",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Tam Ekran Ayarlar Modalı
    if (showSettingsModal) {
        Dialog(
            onDismissRequest = { showSettingsModal = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                SettingsScreen(
                    viewModel = viewModel,
                    onBack = { showSettingsModal = false }
                )
            }
        }
    }
}

@Composable
private fun BmrMetricBox(
    title: String,
    subTitle: String,
    value: String,
    unit: String,
    isHighlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDark = KalanTheme.isDark
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isHighlight) {
            if (isDark) ForestGreenPrimary.copy(alpha = 0.35f) else MintLight
        } else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isHighlight) androidx.compose.foundation.BorderStroke(1.dp, MintAccent) else null,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
        ) {
            Text(
                text = subTitle,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isHighlight) {
                    if (isDark) MintAccent else ForestGreenDark
                } else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = if (isHighlight) {
                    if (isDark) MintAccent else ForestGreenPrimary
                } else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
        }
    }
}
