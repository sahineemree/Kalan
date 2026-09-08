package com.example.ui

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ThemeMode
import com.example.notification.NotificationHelper
import com.example.notification.ReminderType
import com.example.ui.theme.AlertExceeded
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.KalanTheme
import com.example.ui.theme.MintAccent
import com.example.ui.theme.MintLight
import com.example.ui.theme.WaterBlue
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    viewModel: KalanViewModel,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var showPasswordDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    // Account Edit State
    var editName by remember(userProfile.userName) { mutableStateOf(userProfile.userName) }
    var editUsername by remember(userProfile.username) { mutableStateOf(userProfile.username) }

    var hasNotificationPermission by remember {
        mutableStateOf(NotificationHelper.hasNotificationPermission(context))
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, if (userProfile.language == "EN") "Notification permission granted!" else "Bildirim izni verildi!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, if (userProfile.language == "EN") "Permission denied" else "Bildirim izni verilmedi", Toast.LENGTH_SHORT).show()
        }
    }

    // Time Editing Dialog State
    var showTimeEditDialog by remember { mutableStateOf(false) }
    var editingMealType by remember { mutableStateOf("breakfast") }
    var editingMealTitle by remember { mutableStateOf("Kahvaltı") }
    var editingTimeValue by remember { mutableStateOf("08:30") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Top Bar if onBack is provided
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri Dön",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (userProfile.language == "EN") "Settings" else "Ayarlar",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // 1. Profil & Hesap Bilgileri (İsim, Kullanıcı Adı, Şifre Değiştir)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_settings_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MintLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = ForestGreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (userProfile.language == "EN") "Account & Security" else "Hesap & Güvenlik",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (userProfile.language == "EN") "Manage name, username, and password" else "İsim, kullanıcı adı ve şifre yönetimi",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(if (userProfile.language == "EN") "Full Name" else "Ad ve Soyad") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_name_field"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreenPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editUsername,
                        onValueChange = { editUsername = it },
                        label = { Text(if (userProfile.language == "EN") "Username" else "Kullanıcı Adı") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_username_field"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreenPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.updateAccountInfo(editName, editUsername) { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("save_account_info_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (userProfile.language == "EN") "Save" else "Kaydet",
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedButton(
                            onClick = { showPasswordDialog = true },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(46.dp)
                                .testTag("change_password_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (KalanTheme.isDark) MintAccent else ForestGreenPrimary
                            )
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (userProfile.language == "EN") "Change Password" else "Şifre Değiştir",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // 2. Görünüm & Tema Seçimi (Açık, Koyu, Sistem)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("theme_settings_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MintLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = ForestGreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (userProfile.language == "EN") "Theme" else "Görünüm & Tema",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (userProfile.language == "EN") "Select light, dark, or system mode" else "Karanlık veya açık mod tercihinizi seçin",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ThemeOptionCard(
                            title = if (userProfile.language == "EN") "Light" else "Açık",
                            icon = Icons.Default.LightMode,
                            isSelected = userProfile.themeMode == ThemeMode.LIGHT.name,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.updateThemeMode(ThemeMode.LIGHT) }
                        )

                        ThemeOptionCard(
                            title = if (userProfile.language == "EN") "Dark" else "Koyu",
                            icon = Icons.Default.DarkMode,
                            isSelected = userProfile.themeMode == ThemeMode.DARK.name,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.updateThemeMode(ThemeMode.DARK) }
                        )

                        ThemeOptionCard(
                            title = if (userProfile.language == "EN") "System" else "Sistem",
                            icon = Icons.Default.PhoneAndroid,
                            isSelected = userProfile.themeMode == ThemeMode.SYSTEM.name,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.updateThemeMode(ThemeMode.SYSTEM) }
                        )
                    }
                }
            }
        }

        // 3. Uygulama Dili (TR & EN)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("language_settings_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MintLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = ForestGreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (userProfile.language == "EN") "App Language" else "Uygulama Dili",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (userProfile.language == "EN") "Turkish & English language support" else "Türkçe ve İngilizce dil desteği",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LanguageOptionCard(
                            flag = "🇹🇷",
                            title = "Türkçe",
                            subtitle = "Varsayılan Dil",
                            isSelected = userProfile.language == "TR",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.updateLanguage("TR")
                                Toast.makeText(context, "Dil Türkçe olarak ayarlandı", Toast.LENGTH_SHORT).show()
                            }
                        )

                        LanguageOptionCard(
                            flag = "🇬🇧",
                            title = "English",
                            subtitle = "International",
                            isSelected = userProfile.language == "EN",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.updateLanguage("EN")
                                Toast.makeText(context, "Language set to English", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }

        // 4. Öğün ve Su Bildirimleri (Yerel Bildirimler & Hatırlatıcılar)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notifications_settings_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MintLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = ForestGreenPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (userProfile.language == "EN") "Local Notifications & Reminders" else "Yerel Bildirimler & Hatırlatıcılar",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (userProfile.language == "EN") "Daily alerts for meals and water hydration" else "Su içme ve öğün saatleriniz için akıllı hatırlatmalar",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Android 13+ Notification Permission Warning Banner if missing
                    if (!hasNotificationPermission && Build.VERSION.SDK_INT >= 33) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text(
                                        text = if (userProfile.language == "EN") "Notification Permission Needed" else "Bildirim İzni Gerekli",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = if (userProfile.language == "EN") "Enable notifications to receive timely meal and water alerts." else "Hatırlatıcıların zamanında çalabilmesi için bildirim izni verin.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                Button(
                                    onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (userProfile.language == "EN") "Enable" else "İzin Ver",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // BÖLÜM A: SU İÇME HATIRLATICILARI
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
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
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(WaterBlue.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WaterDrop,
                                            contentDescription = null,
                                            tint = WaterBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = if (userProfile.language == "EN") "Water Reminders" else "Su İçme Hatırlatıcısı",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (userProfile.notifyWater) {
                                                if (userProfile.language == "EN") "Active: Every ${userProfile.waterIntervalHours}h (${userProfile.waterStartTime} - ${userProfile.waterEndTime})"
                                                else "Aktif: Her ${userProfile.waterIntervalHours} saatte bir (${userProfile.waterStartTime} - ${userProfile.waterEndTime})"
                                            } else {
                                                if (userProfile.language == "EN") "Disabled" else "Devre Dışı"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (userProfile.notifyWater) ForestGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Switch(
                                    checked = userProfile.notifyWater,
                                    onCheckedChange = { viewModel.updateWaterNotification(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = WaterBlue,
                                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            }

                            if (userProfile.notifyWater) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (userProfile.language == "EN") "Reminder Frequency:" else "Hatırlatma Sıklığı:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val intervals = listOf(
                                        1.0 to (if (userProfile.language == "EN") "1 Hour" else "1 Saat"),
                                        1.5 to (if (userProfile.language == "EN") "1.5 H" else "1.5 Saat"),
                                        2.0 to (if (userProfile.language == "EN") "2 Hours" else "2 Saat"),
                                        3.0 to (if (userProfile.language == "EN") "3 Hours" else "3 Saat")
                                    )
                                    intervals.forEach { (hours, label) ->
                                        val isSelected = kotlin.math.abs(userProfile.waterIntervalHours - hours) < 0.1
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSelected) WaterBlue else MaterialTheme.colorScheme.surface,
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isSelected) WaterBlue else MaterialTheme.colorScheme.outline
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { viewModel.updateWaterInterval(hours) }
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = label,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (userProfile.language == "EN") "💡 Tap notification '+250ml' to quick log" else "💡 Bildirimden '+250 ml Ekle' ile hızlıca kaydedin",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f).padding(end = 6.dp)
                                    )
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.sendTestNotification(ReminderType.WATER)
                                            Toast.makeText(context, if (userProfile.language == "EN") "Water test notification sent!" else "Su hatırlatma bildirimi gönderildi!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text(
                                            text = if (userProfile.language == "EN") "Test Water" else "Test Et",
                                            fontSize = 12.sp,
                                            color = WaterBlue
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // BÖLÜM B: GÜNLÜK ÖĞÜN HATIRLATICILARI
                    Text(
                        text = if (userProfile.language == "EN") "Meal Alerts" else "Öğün Saatleri",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    NotificationRow(
                        emoji = "🌅",
                        title = if (userProfile.language == "EN") "Breakfast Reminder" else "Kahvaltı Bildirimi",
                        time = userProfile.breakfastTime,
                        isChecked = userProfile.notifyBreakfast,
                        onCheckedChange = { viewModel.updateMealNotification("breakfast", it) },
                        onEditTime = {
                            editingMealType = "breakfast"
                            editingMealTitle = if (userProfile.language == "EN") "Breakfast" else "Kahvaltı"
                            editingTimeValue = userProfile.breakfastTime
                            showTimeEditDialog = true
                        }
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    NotificationRow(
                        emoji = "☀️",
                        title = if (userProfile.language == "EN") "Lunch Reminder" else "Öğle Yemeği Bildirimi",
                        time = userProfile.lunchTime,
                        isChecked = userProfile.notifyLunch,
                        onCheckedChange = { viewModel.updateMealNotification("lunch", it) },
                        onEditTime = {
                            editingMealType = "lunch"
                            editingMealTitle = if (userProfile.language == "EN") "Lunch" else "Öğle Yemeği"
                            editingTimeValue = userProfile.lunchTime
                            showTimeEditDialog = true
                        }
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    NotificationRow(
                        emoji = "🌙",
                        title = if (userProfile.language == "EN") "Dinner Reminder" else "Akşam Yemeği Bildirimi",
                        time = userProfile.dinnerTime,
                        isChecked = userProfile.notifyDinner,
                        onCheckedChange = { viewModel.updateMealNotification("dinner", it) },
                        onEditTime = {
                            editingMealType = "dinner"
                            editingMealTitle = if (userProfile.language == "EN") "Dinner" else "Akşam Yemeği"
                            editingTimeValue = userProfile.dinnerTime
                            showTimeEditDialog = true
                        }
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    NotificationRow(
                        emoji = "🍎",
                        title = if (userProfile.language == "EN") "Snack Reminder" else "Ara Öğün Bildirimi",
                        time = userProfile.snackTime,
                        isChecked = userProfile.notifySnack,
                        onCheckedChange = { viewModel.updateMealNotification("snack", it) },
                        onEditTime = {
                            editingMealType = "snack"
                            editingMealTitle = if (userProfile.language == "EN") "Snack" else "Ara Öğün"
                            editingTimeValue = userProfile.snackTime
                            showTimeEditDialog = true
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            viewModel.sendTestNotification(ReminderType.BREAKFAST)
                            Toast.makeText(context, if (userProfile.language == "EN") "Meal test notification sent!" else "Öğün hatırlatma bildirimi gönderildi!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (userProfile.language == "EN") "Test Meal Notification Now" else "Öğün Bildirimini Hemen Test Et",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ForestGreenPrimary
                        )
                    }
                }
            }
        }

        // 5. Oturum İşlemleri & Profil Yönetimi (Çıkış Yap ve Profili Sil)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("session_management_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (userProfile.language == "EN") "Session & Account Actions" else "Oturum & Hesap İşlemleri",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (userProfile.language == "EN") "Sign out or permanently delete profile" else "Oturumu kapatabilir veya hesabınızı silebilirsiniz",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Çıkış Yap Butonu
                    Button(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("logout_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (userProfile.language == "EN") "Sign Out" else "Çıkış Yap",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Profili Tamamen Sil Butonu
                    OutlinedButton(
                        onClick = { showDeleteAccountDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("delete_profile_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertExceeded),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AlertExceeded.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (userProfile.language == "EN") "Permanently Delete Profile" else "Profili Tamamen Sil",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // Şifre Değiştirme Dialogu
    if (showPasswordDialog) {
        var oldPasswordInput by remember { mutableStateOf("") }
        var newPasswordInput by remember { mutableStateOf("") }
        var confirmPasswordInput by remember { mutableStateOf("") }
        var oldPassVisible by remember { mutableStateOf(false) }
        var newPassVisible by remember { mutableStateOf(false) }
        var confirmPassVisible by remember { mutableStateOf(false) }
        var passwordErrorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (userProfile.language == "EN") "Change Password" else "Şifre Değiştir",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (userProfile.language == "EN")
                            "Enter current password, then type new password twice to confirm."
                        else
                            "Lütfen önceki şifrenizi doğru girin ve yeni şifrenizi 2 defa yazın:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Mevcut Şifre
                    OutlinedTextField(
                        value = oldPasswordInput,
                        onValueChange = {
                            oldPasswordInput = it
                            passwordErrorMessage = null
                        },
                        label = { Text(if (userProfile.language == "EN") "Current Password" else "Mevcut Şifre") },
                        singleLine = true,
                        visualTransformation = if (oldPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { oldPassVisible = !oldPassVisible }) {
                                Icon(
                                    imageVector = if (oldPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Yeni Şifre
                    OutlinedTextField(
                        value = newPasswordInput,
                        onValueChange = {
                            newPasswordInput = it
                            passwordErrorMessage = null
                        },
                        label = { Text(if (userProfile.language == "EN") "New Password" else "Yeni Şifre") },
                        singleLine = true,
                        visualTransformation = if (newPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { newPassVisible = !newPassVisible }) {
                                Icon(
                                    imageVector = if (newPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Yeni Şifre (Tekrar)
                    OutlinedTextField(
                        value = confirmPasswordInput,
                        onValueChange = {
                            confirmPasswordInput = it
                            passwordErrorMessage = null
                        },
                        label = { Text(if (userProfile.language == "EN") "Confirm New Password" else "Yeni Şifre (Tekrar)") },
                        singleLine = true,
                        visualTransformation = if (confirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                                Icon(
                                    imageVector = if (confirmPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (passwordErrorMessage != null) {
                        Text(
                            text = passwordErrorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = AlertExceeded,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.changePassword(
                            oldPass = oldPasswordInput,
                            newPass = newPasswordInput,
                            confirmPass = confirmPasswordInput
                        ) { success, message ->
                            if (success) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                showPasswordDialog = false
                            } else {
                                passwordErrorMessage = message
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text(if (userProfile.language == "EN") "Update Password" else "Şifreyi Güncelle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text(if (userProfile.language == "EN") "Cancel" else "İptal")
                }
            }
        )
    }

    // Çıkış Yap Onay Dialogu
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = if (userProfile.language == "EN") "Sign Out?" else "Çıkış Yapılsın mı?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (userProfile.language == "EN")
                        "You will be logged out. You can log back in anytime with your username and password."
                    else
                        "Oturumunuz kapatılacaktır. Kullanıcı adınız ve şifrenizle dilediğiniz zaman tekrar giriş yapabilirsiniz.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logOut()
                        showLogoutDialog = false
                        Toast.makeText(
                            context,
                            if (userProfile.language == "EN") "Signed out successfully" else "Başarıyla çıkış yapıldı",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text(if (userProfile.language == "EN") "Sign Out" else "Çıkış Yap")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(if (userProfile.language == "EN") "Cancel" else "İptal")
                }
            }
        )
    }

    // Profili Tamamen Sil Onay Dialogu
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = {
                Text(
                    text = if (userProfile.language == "EN") "Permanently Delete Profile?" else "Profili Tamamen Sil?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AlertExceeded
                )
            },
            text = {
                Text(
                    text = if (userProfile.language == "EN")
                        "All your records, meal entries, water logs, and profile data will be permanently erased. This cannot be undone."
                    else
                        "Tüm öğün kayıtlarınız, su verileriniz ve profiliniz kalıcı olarak silinecektir. Bu işlem geri alınamaz.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAccount {
                            showDeleteAccountDialog = false
                            Toast.makeText(
                                context,
                                if (userProfile.language == "EN") "Profile deleted" else "Profil ve tüm veriler silindi",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertExceeded)
                ) {
                    Text(if (userProfile.language == "EN") "Yes, Delete" else "Evet, Tamamen Sil")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text(if (userProfile.language == "EN") "Cancel" else "İptal")
                }
            }
        )
    }

    // Öğün Saati Düzenleme Dialogu
    if (showTimeEditDialog) {
        var inputTime by remember(editingTimeValue) { mutableStateOf(editingTimeValue) }
        var isFormatError by remember { mutableStateOf(false) }

        val presetTimes = when (editingMealType) {
            "breakfast" -> listOf("07:30", "08:00", "08:30", "09:00", "09:30")
            "lunch" -> listOf("12:00", "12:30", "13:00", "13:30", "14:00")
            "dinner" -> listOf("18:30", "19:00", "19:30", "20:00", "20:30")
            "snack" -> listOf("15:00", "15:30", "16:00", "16:30", "17:00")
            else -> listOf("08:00", "12:00", "18:00")
        }

        AlertDialog(
            onDismissRequest = { showTimeEditDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = ForestGreenPrimary
                    )
                    Text(
                        text = if (userProfile.language == "EN") "Set $editingMealTitle Time" else "$editingMealTitle Saatini Ayarla",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = if (userProfile.language == "EN")
                            "Choose a preset time or enter in HH:mm format (e.g. 08:30):"
                        else
                            "Hazır saatlerden seçin veya Saat:Dakika (örn: 08:30) formatında yazın:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Preset Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetTimes.forEach { preset ->
                            val isChosen = inputTime == preset
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isChosen) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        inputTime = preset
                                        isFormatError = false
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = preset,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isChosen) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = inputTime,
                        onValueChange = {
                            inputTime = it
                            isFormatError = false
                        },
                        label = { Text(if (userProfile.language == "EN") "Time (HH:mm)" else "Saat (SS:dd)") },
                        placeholder = { Text("08:30") },
                        isError = isFormatError,
                        supportingText = {
                            if (isFormatError) {
                                Text(
                                    text = if (userProfile.language == "EN") "Invalid format. Use HH:mm (e.g. 08:30)" else "Geçersiz format. SS:dd (örn: 08:30) giriniz.",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val regex = Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
                        if (regex.matches(inputTime.trim())) {
                            val formatted = if (inputTime.trim().length == 4) "0${inputTime.trim()}" else inputTime.trim()
                            viewModel.updateMealTime(editingMealType, formatted)
                            showTimeEditDialog = false
                            Toast.makeText(
                                context,
                                if (userProfile.language == "EN") "Reminder time updated" else "Hatırlatma saati güncellendi",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            isFormatError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text(if (userProfile.language == "EN") "Save" else "Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimeEditDialog = false }) {
                    Text(if (userProfile.language == "EN") "Cancel" else "İptal")
                }
            }
        )
    }
}

@Composable
private fun NotificationRow(
    emoji: String,
    title: String,
    time: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onEditTime: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .then(if (onEditTime != null) Modifier.clickable { onEditTime() } else Modifier)
        ) {
            Text(text = emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Saat: $time",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (onEditTime != null) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Düzenle",
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ForestGreenPrimary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
private fun LanguageOptionCard(
    flag: String,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ForestGreenPrimary else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        color = if (isSelected) {
            if (KalanTheme.isDark) MaterialTheme.colorScheme.surfaceVariant else MintLight
        } else {
            MaterialTheme.colorScheme.surface
        },
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = flag, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) ForestGreenPrimary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun ThemeOptionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ForestGreenPrimary else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        color = if (isSelected) {
            if (KalanTheme.isDark) MaterialTheme.colorScheme.surfaceVariant else MintLight
        } else {
            MaterialTheme.colorScheme.surface
        },
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) ForestGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) ForestGreenPrimary else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
