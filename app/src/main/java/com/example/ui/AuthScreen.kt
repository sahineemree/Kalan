package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.calculator.GeminiCalculationResult
import com.example.data.ActivityLevel
import com.example.data.Gender
import com.example.data.GoalType
import com.example.ui.theme.AlertExceeded
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.KalanTheme
import com.example.ui.theme.MacroCarb
import com.example.ui.theme.MacroFat
import com.example.ui.theme.MacroProtein
import com.example.ui.theme.MintAccent
import com.example.ui.theme.MintLight
import com.example.ui.theme.WaterBlue
import kotlin.math.roundToInt

@Composable
fun AuthScreen(
    viewModel: KalanViewModel,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Giriş Yap, 1: Kayıt Ol

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Brand Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_kalan_logo),
                            contentDescription = "Kalan Logo",
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "kalan",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = ".",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MintAccent
                    )
                }
            }

            Text(
                text = "Akıllı Beslenme ve Kalori Takibi",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
            )

            // Tabs: Giriş Yap / Kayıt Ol
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = ForestGreenPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ForestGreenPrimary,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Giriş Yap",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Kayıt Ol",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Animated Tab Content
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "auth_tabs"
            ) { tabIndex ->
                if (tabIndex == 0) {
                    LoginTabContent(
                        viewModel = viewModel,
                        onLoginSuccess = onAuthSuccess,
                        onNavigateToRegister = { selectedTab = 1 }
                    )
                } else {
                    RegisterFlowContent(
                        viewModel = viewModel,
                        onRegisterSuccess = onAuthSuccess,
                        onNavigateToLogin = { selectedTab = 0 }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginTabContent(
    viewModel: KalanViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("login_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Kullanıcı Girişi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Kullanıcı adı / e-posta ve şifrenizle güvenle giriş yapın.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = usernameInput,
                onValueChange = {
                    usernameInput = it
                    errorMessage = null
                },
                label = { Text("Kullanıcı Adı veya E-posta") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = ForestGreenPrimary)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_username_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ForestGreenPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            OutlinedTextField(
                value = passwordInput,
                onValueChange = {
                    passwordInput = it
                    errorMessage = null
                },
                label = { Text("Şifre") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = ForestGreenPrimary)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ForestGreenPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = AlertExceeded,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    isSubmitting = true
                    viewModel.loginWithCredentials(usernameInput, passwordInput) { success, msg ->
                        isSubmitting = false
                        if (success) {
                            Toast.makeText(context, "Hoş geldiniz!", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        } else {
                            errorMessage = msg
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("login_submit_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text(
                        text = "Giriş Yap",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hesabınız yok mu?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Hemen Kayıt Olun",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenPrimary,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}

@Composable
private fun RegisterFlowContent(
    viewModel: KalanViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    val totalSteps = 5

    // Adım 1: Kullanıcı Bilgileri
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Adım 2: Fiziksel Özellikler
    var gender by remember { mutableStateOf(Gender.ERKEK) }
    var age by remember { mutableIntStateOf(26) }
    var heightCm by remember { mutableStateOf("175") }
    var weightKg by remember { mutableStateOf("75") }

    // Adım 3: Hedef & Tempo
    var goalType by remember { mutableStateOf(GoalType.LOSE) }
    var targetWeightKg by remember { mutableStateOf("70") }
    var targetPace by remember { mutableStateOf("DENGELI") } // HAFIF, DENGELI, HIZLI

    // Adım 4: Aktivite & Diyet
    var activityLevel by remember { mutableStateOf(ActivityLevel.MODERATE) }
    var exerciseFrequency by remember { mutableStateOf("Haftada 3-4 gün") }
    var dietPreference by remember { mutableStateOf("Dengeli Beslenme") }

    // Adım 5: Gemini AI Sonucu
    var isCalculatingWithGemini by remember { mutableStateOf(false) }
    var geminiResult by remember { mutableStateOf<GeminiCalculationResult?>(null) }
    var stepError by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Progress Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Adım $step / $totalSteps",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenPrimary
                    )
                    Text(
                        text = when (step) {
                            1 -> "Hesap Bilgileri"
                            2 -> "Fiziksel Özellikler"
                            3 -> "Kilo Hedefi & Tempo"
                            4 -> "Aktivite & Yaşam Tarzı"
                            else -> "Gemini AI Analizi"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { step.toFloat() / totalSteps.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ForestGreenPrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        // Step Content Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    when (step) {
                        1 -> {
                            // Adım 1: Kullanıcı Adı ve Şifre
                            Text(
                                text = "Hesabınızı Oluşturun",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "E-posta gerekmez; doğrudan kullanıcı adı ve şifre belirleyin.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it; stepError = null },
                                label = { Text("Adınız ve Soyadınız") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it; stepError = null },
                                label = { Text("Kullanıcı Adı (Girişte Kullanılacak)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it; stepError = null },
                                label = { Text("Şifre") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it; stepError = null },
                                label = { Text("Şifre Tekrar") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        2 -> {
                            // Adım 2: Fiziksel Özellikler
                            Text(
                                text = "Fiziksel Özellikleriniz",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Metabolizma hızınızın doğru hesaplanması için gereklidir.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Cinsiyet
                            Text("Biyolojik Cinsiyet", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                listOf(Gender.ERKEK to "👨 Erkek", Gender.KADIN to "👩 Kadın").forEach { (g, title) ->
                                    val isSel = gender == g
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { gender = g },
                                        color = if (isSel) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 12.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Yaş
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Yaşınız:", style = MaterialTheme.typography.bodyMedium)
                                Text("$age Yaş", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { if (age > 12) age-- },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface),
                                    modifier = Modifier.weight(1f)
                                ) { Text("-1") }

                                Text("$age", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp))

                                Button(
                                    onClick = { if (age < 95) age++ },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface),
                                    modifier = Modifier.weight(1f)
                                ) { Text("+1") }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Boy & Kilo
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = heightCm,
                                    onValueChange = { heightCm = it; stepError = null },
                                    label = { Text("Boy (cm)") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = weightKg,
                                    onValueChange = { weightKg = it; stepError = null },
                                    label = { Text("Mevcut Kilo (kg)") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }

                        3 -> {
                            // Adım 3: Hedef Kilo ve Tempo
                            Text(
                                text = "Kilo Hedefiniz ve Hızınız",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Kilo vermek mi, almak mı yoksa kilonuzu korumak mı istiyorsunuz?",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Hedef Türü
                            listOf(
                                GoalType.LOSE to "📉 Kilo Vermek (Yağ Yakımı)",
                                GoalType.MAINTAIN to "⚖️ Mevcut Kiloyu Korumak",
                                GoalType.GAIN to "📈 Kilo Almak (Kas Kazanımı)"
                            ).forEach { (gType, title) ->
                                val isSel = goalType == gType
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { goalType = gType },
                                    color = if (isSel) {
                                        if (KalanTheme.isDark) MaterialTheme.colorScheme.surfaceVariant else MintLight
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    },
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSel) 2.dp else 1.dp,
                                        color = if (isSel) ForestGreenPrimary else MaterialTheme.colorScheme.outline
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSel) ForestGreenPrimary else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (isSel) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreenPrimary)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Hedef Kilo
                            OutlinedTextField(
                                value = targetWeightKg,
                                onValueChange = { targetWeightKg = it; stepError = null },
                                label = { Text("Hedef Kilo (kg)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Tempo / Hız
                            Text("Hedef Temposu:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "HAFIF" to "Hafif\n(~0.25kg)",
                                    "DENGELI" to "Dengeli\n(~0.5kg)",
                                    "HIZLI" to "Hızlı\n(~0.75kg)"
                                ).forEach { (paceKey, paceTitle) ->
                                    val isSel = targetPace == paceKey
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { targetPace = paceKey },
                                        color = if (isSel) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = paceTitle,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        )
                                    }
                                }
                            }
                        }

                        4 -> {
                            // Adım 4: Aktivite Seviyesi & Yaşam Tarzı
                            Text(
                                text = "Günlük Aktivite & Beslenme Tercihi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Gemini AI, günlük kalori hedefinizi ve makro dağılımını bu bilgilere göre optimize eder.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Günlük Hareket Seviyesi", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))

                            ActivityLevel.entries.forEach { act ->
                                val isSel = activityLevel == act
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { activityLevel = act },
                                    color = if (isSel) {
                                        if (KalanTheme.isDark) MaterialTheme.colorScheme.surfaceVariant else MintLight
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    },
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSel) 2.dp else 1.dp,
                                        color = if (isSel) ForestGreenPrimary else MaterialTheme.colorScheme.outline
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = act.titleTr,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSel) ForestGreenPrimary else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (isSel) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                        Text(
                                            text = act.descriptionTr,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Beslenme Modeli Tercihi
                            Text("Beslenme Modeli Tercihi", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            listOf(
                                "Dengeli Beslenme (Akdeniz)",
                                "Yüksek Protein (Kas Kazanımı)",
                                "Düşük Karbonhidrat (Low-Carb)"
                            ).forEach { diet ->
                                val isSel = dietPreference == diet
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { dietPreference = diet },
                                    color = if (isSel) ForestGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = diet,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }

                        5 -> {
                            // Adım 5: Gemini AI Analizi & Sonuç
                            if (isCalculatingWithGemini) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        color = ForestGreenPrimary,
                                        modifier = Modifier.size(52.dp),
                                        strokeWidth = 4.dp
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text(
                                        text = "Gemini AI Metabolizmanızı Analiz Ediyor...",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreenPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Yaşınız, boyunuz, kilonuz, hedef hızınız ve günlük hareketinize göre kişiselleştirilmiş kalori ve makro planı hesaplanıyor.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else if (geminiResult != null) {
                                val res = geminiResult!!
                                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MintLight,
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.AutoAwesome,
                                                    contentDescription = null,
                                                    tint = ForestGreenPrimary,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        }
                                        Column {
                                            Text(
                                                text = "Gemini AI Beslenme Planınız Hazır!",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (res.isFromAi) "Gemini 2.5 Flash Yapay Zeka Motoru" else "Akıllı Metabolizma Algoritması",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ForestGreenPrimary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    // Büyük Günlük Kalori Kartı
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (KalanTheme.isDark) MaterialTheme.colorScheme.surfaceVariant else MintLight,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(18.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "GÜNLÜK HEDEF KALORİ",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = ForestGreenPrimary
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "${res.targetCalories.toInt()}",
                                                style = MaterialTheme.typography.displayMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = ForestGreenPrimary
                                            )
                                            Text(
                                                text = "kcal / gün",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    // Makro Dağılımı Kartları
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        MacroResultCard(
                                            title = "Karb",
                                            grams = res.targetCarbsGrams.toInt(),
                                            color = MacroCarb,
                                            modifier = Modifier.weight(1f)
                                        )
                                        MacroResultCard(
                                            title = "Protein",
                                            grams = res.targetProteinGrams.toInt(),
                                            color = MacroProtein,
                                            modifier = Modifier.weight(1f)
                                        )
                                        MacroResultCard(
                                            title = "Yağ",
                                            grams = res.targetFatGrams.toInt(),
                                            color = MacroFat,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    // Su Hedefi Kartı
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFDBEAFE),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = WaterBlue, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Önerilen Günlük Su:",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Text(
                                                text = "${res.targetWaterMl} ml",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = WaterBlue
                                            )
                                        }
                                    }

                                    // Gemini Yorumu & Açıklaması
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "💡 Uzman Değerlendirmesi",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = res.summaryReasoning,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                lineHeight = 18.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (stepError != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = stepError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = AlertExceeded,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Step Navigation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (step > 1 && !isCalculatingWithGemini) {
                            OutlinedButton(
                                onClick = {
                                    stepError = null
                                    step--
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Geri")
                            }
                        }

                        if (step < 4) {
                            Button(
                                onClick = {
                                    when (step) {
                                        1 -> {
                                            if (name.isBlank()) {
                                                stepError = "Lütfen adınızı ve soyadınızı girin."
                                                return@Button
                                            }
                                            if (username.trim().length < 3) {
                                                stepError = "Kullanıcı adı en az 3 karakter olmalıdır."
                                                return@Button
                                            }
                                            if (password.length < 4) {
                                                stepError = "Şifre en az 4 karakter olmalıdır."
                                                return@Button
                                            }
                                            if (password != confirmPassword) {
                                                stepError = "Girdiğiniz şifreler eşleşmiyor."
                                                return@Button
                                            }
                                            stepError = null
                                            step = 2
                                        }
                                        2 -> {
                                            val h = heightCm.toDoubleOrNull()
                                            val w = weightKg.toDoubleOrNull()
                                            if (h == null || h < 100 || h > 250) {
                                                stepError = "Lütfen geçerli bir boy değeri girin (100-250 cm)."
                                                return@Button
                                            }
                                            if (w == null || w < 30 || w > 300) {
                                                stepError = "Lütfen geçerli bir kilo değeri girin (30-300 kg)."
                                                return@Button
                                            }
                                            stepError = null
                                            step = 3
                                        }
                                        3 -> {
                                            val tw = targetWeightKg.toDoubleOrNull()
                                            if (tw == null || tw < 30 || tw > 300) {
                                                stepError = "Lütfen geçerli bir hedef kilo girin."
                                                return@Button
                                            }
                                            stepError = null
                                            step = 4
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(if (step == 1) 2f else 1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                            ) {
                                Text("Devam Et", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        } else if (step == 4) {
                            Button(
                                onClick = {
                                    step = 5
                                    isCalculatingWithGemini = true
                                    val h = heightCm.toDoubleOrNull() ?: 175.0
                                    val w = weightKg.toDoubleOrNull() ?: 75.0
                                    val tw = targetWeightKg.toDoubleOrNull() ?: 70.0

                                    viewModel.registerWithGemini(
                                        name = name,
                                        username = username,
                                        pass = password,
                                        gender = gender,
                                        age = age,
                                        heightCm = h,
                                        weightKg = w,
                                        targetWeightKg = tw,
                                        goalType = goalType,
                                        targetPace = targetPace,
                                        activityLevel = activityLevel,
                                        exerciseFrequency = exerciseFrequency,
                                        dietPreference = dietPreference
                                    ) { result ->
                                        geminiResult = result
                                        isCalculatingWithGemini = false
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gemini ile Hesapla", fontWeight = FontWeight.Bold)
                            }
                        } else if (step == 5 && geminiResult != null) {
                            Button(
                                onClick = {
                                    onRegisterSuccess()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Planı Onayla & Uygulamaya Başla", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Already registered note
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Zaten bir hesabınız var mı?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Giriş Yapın",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenPrimary,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

@Composable
private fun MacroResultCard(
    title: String,
    grams: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${grams}g",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
    }
}
