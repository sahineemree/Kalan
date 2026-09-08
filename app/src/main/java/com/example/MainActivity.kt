package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.MealType
import com.example.data.ThemeMode
import com.example.ui.AddFoodScreen
import com.example.ui.AuthScreen
import com.example.ui.HistoryScreen
import com.example.ui.HomeScreen
import com.example.ui.KalanViewModel
import com.example.ui.ProfileAvatar
import com.example.ui.ProfileScreen
import com.example.ui.Screen
import com.example.ui.SettingsScreen
import com.example.ui.StatsScreen
import com.example.ui.WaterTrackerScreen
import com.example.ui.bottomNavScreens
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.KalanTheme
import com.example.ui.theme.MintAccent
import com.example.ui.theme.MintLight
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: KalanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.notification.NotificationHelper.createNotificationChannels(this)
        enableEdgeToEdge()
        setContent {
            val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
            val isSystemDark = isSystemInDarkTheme()
            val darkTheme = when (userProfile.themeMode) {
                ThemeMode.LIGHT.name -> false
                ThemeMode.DARK.name -> true
                else -> isSystemDark
            }

            MyApplicationTheme(darkTheme = darkTheme) {
                if (!userProfile.isLoggedIn || !userProfile.isRegistered) {
                    AuthScreen(
                        viewModel = viewModel,
                        onAuthSuccess = { /* Automatically navigates due to userProfile state */ }
                    )
                } else {
                    KalanMainApp(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KalanMainApp(viewModel: KalanViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var showHelpDialog by remember { mutableStateOf(false) }

    // Kullanıcı ilk kez giriş yaptığında otomatik rehber paneli açılır
    LaunchedEffect(userProfile.hasSeenGuide, userProfile.isLoggedIn) {
        if (userProfile.isLoggedIn && !userProfile.hasSeenGuide) {
            showHelpDialog = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.testTag("app_header_logo")
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (KalanTheme.isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White,
                            border = if (KalanTheme.isDark) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
                            shadowElevation = if (KalanTheme.isDark) 0.dp else 2.dp,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(3.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_kalan_logo),
                                    contentDescription = "Kalan Logo",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "kalan",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary,
                                    letterSpacing = (-0.5).sp
                                )
                                Text(
                                    text = ".",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MintAccent
                                )
                            }
                            Text(
                                text = "Kalori & Beslenme Takibi",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        // Günlük Seri & Motivasyon Rozeti
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (KalanTheme.isDark) MaterialTheme.colorScheme.surfaceVariant else MintLight,
                            modifier = Modifier.padding(end = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Seri",
                                    tint = if (KalanTheme.isDark) MintAccent else ForestGreenDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Hedefte",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (KalanTheme.isDark) MintAccent else ForestGreenDark
                                )
                            }
                        }

                        // Yardım & Rehber Butonu (?) - Profil fotoğrafının hemen solunda
                        IconButton(
                            onClick = { showHelpDialog = true },
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(36.dp)
                                .testTag("help_guide_button")
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (KalanTheme.isDark) MaterialTheme.colorScheme.surfaceVariant else MintLight,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                        contentDescription = "Nasıl Kullanılır?",
                                        tint = if (KalanTheme.isDark) MintAccent else ForestGreenDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Profil Avatarı (Tıklandığında Profile gider)
                        ProfileAvatar(
                            avatarBase64 = userProfile.avatarBase64,
                            userName = userProfile.userName,
                            size = 36.dp,
                            onClick = {
                                if (currentRoute != Screen.Profile.route) {
                                    navController.navigate(Screen.Profile.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.shadow(1.dp)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = ForestGreenPrimary,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .shadow(8.dp)
                    .testTag("bottom_navigation_bar")
            ) {
                bottomNavScreens.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary,
                            selectedTextColor = if (KalanTheme.isDark) MintAccent else ForestGreenPrimary,
                            indicatorColor = if (KalanTheme.isDark) Color(0xFF1B2F24) else MintLight,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(modifier = Modifier.widthIn(max = 600.dp)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    enterTransition = {
                        fadeIn(animationSpec = tween(220)) + slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(220)
                        )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(180)) + slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(180)
                        )
                    },
                    popEnterTransition = {
                        fadeIn(animationSpec = tween(220)) + slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(220)
                        )
                    },
                    popExitTransition = {
                        fadeOut(animationSpec = tween(180)) + slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(180)
                        )
                    }
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToAddFood = { targetMeal ->
                                viewModel.setSelectedMealTypeForAdd(targetMeal)
                                navController.navigate(Screen.Add.route)
                            },
                            onNavigateToProfile = {
                                navController.navigate(Screen.Profile.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onNavigateToWater = {
                                navController.navigate(Screen.Water.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                    composable(Screen.Water.route) {
                        WaterTrackerScreen(viewModel = viewModel)
                    }
                    composable(Screen.History.route) {
                        HistoryScreen(
                            viewModel = viewModel,
                            onNavigateToHome = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = false }
                                }
                            },
                            onNavigateToAdd = {
                                navController.navigate(Screen.Add.route)
                            }
                        )
                    }
                    composable(Screen.Add.route) {
                        AddFoodScreen(
                            viewModel = viewModel,
                            onFoodAdded = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.Stats.route) {
                        StatsScreen(viewModel = viewModel)
                    }
                    composable(Screen.Profile.route) {
                        ProfileScreen(viewModel = viewModel)
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    // Kullanıcı ilk kez girdiğinde veya ? butonuna tıkladığında gösterilen rehber paneli
    if (showHelpDialog) {
        AppHelpGuideDialog(
            onDismiss = {
                viewModel.markGuideSeen()
                showHelpDialog = false
            }
        )
    }
}

/**
 * Kullanıcıya uygulamanın neyi nasıl yaptığını açıklayan rehber paneli
 */
@Composable
fun AppHelpGuideDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("dismiss_guide_button")
            ) {
                Text("Anladım, Haydi Başlayalım!", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ForestGreenPrimary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Kalan'a Hoş Geldiniz!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Uygulamayı nasıl kullanabilirsiniz?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                item {
                    HelpGuideItem(
                        icon = Icons.Default.AutoAwesome,
                        iconTint = ForestGreenPrimary,
                        title = "1. Gemini AI Metabolizma Hesabı",
                        description = "Boy, kilo, hedef ve yaşam tarzınıza göre günlük almanız gereken kaloriyi ve su miktarını Gemini yapay zekası hesaplar."
                    )
                }
                item {
                    HelpGuideItem(
                        icon = Icons.Default.Restaurant,
                        iconTint = Color(0xFFF59E0B),
                        title = "2. Kolay Yemek Ekleme & AI Analizi",
                        description = "+ butonuna basarak arama yapabilir ya da ne yediğinizi ve miktarını yazarak Gemini'nin kaloriyi ve makroları otomatik hesaplamasını sağlayabilirsiniz."
                    )
                }
                item {
                    HelpGuideItem(
                        icon = Icons.Default.WaterDrop,
                        iconTint = Color(0xFF0284C7),
                        title = "3. Akıllı Su Takibi",
                        description = "Gemini'nin önerdiği veya sizin belirlediğiniz günlük su hedefine göre bardağa tek tıkla su ekleyip hidrasyonunuzu koruyun."
                    )
                }
                item {
                    HelpGuideItem(
                        icon = Icons.Default.TrackChanges,
                        iconTint = Color(0xFF10B981),
                        title = "4. Kalan Kalori & Makro Dengesi",
                        description = "Ana ekrandaki büyük halka göstergeden gün boyu kaç kalori ve protein/karb/yağ hakkınız kaldığını anlık takip edin."
                    )
                }
            }
        },
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun HelpGuideItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = iconTint.copy(alpha = 0.15f),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
