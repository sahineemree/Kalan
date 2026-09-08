package com.example.ui

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.KalanTheme
import com.example.ui.theme.MintAccent
import java.util.Locale

/**
 * Generates uppercase 1-2 letter initials from a user's full name.
 * e.g., "Emre Şahin" -> "EŞ", "Selin" -> "SE", "" -> "K"
 */
fun getInitials(name: String): String {
    val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "K"
        parts.size == 1 -> parts[0].take(2).uppercase(Locale.getDefault())
        else -> "${parts.first().first().uppercase(Locale.getDefault())}${parts.last().first().uppercase(Locale.getDefault())}"
    }
}

/**
 * Reusable Profile Avatar component:
 * Displays user's uploaded Base64 photo if available, or a gradient initials avatar.
 */
@Composable
fun ProfileAvatar(
    avatarBase64: String?,
    userName: String,
    size: Dp = 44.dp,
    showCameraBadge: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isDark = KalanTheme.isDark
    val imageBitmap = remember(avatarBase64) {
        if (!avatarBase64.isNullOrBlank()) {
            try {
                val decoded = Base64.decode(avatarBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decoded, 0, decoded.size)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    val initials = remember(userName) { getInitials(userName) }
    val borderColor = if (isDark) MintAccent.copy(alpha = 0.5f) else ForestGreenPrimary.copy(alpha = 0.3f)

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (onClick != null) {
                    Modifier
                        .clip(CircleShape)
                        .clickable { onClick() }
                } else {
                    Modifier.clip(CircleShape)
                }
            )
            .testTag("profile_avatar_component"),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(width = if (size > 60.dp) 2.dp else 1.5.dp, color = borderColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Profil Fotoğrafı ($userName)",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Şık yeşil gradient ve baş harfler (EŞ)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ForestGreenDark, MintAccent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = (size.value * 0.38f).sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Camera edit badge in Profile Screen
        if (showCameraBadge) {
            Surface(
                shape = CircleShape,
                color = ForestGreenPrimary,
                contentColor = Color.White,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .size((size.value * 0.32f).coerceAtLeast(24f).dp)
                    .align(Alignment.BottomEnd)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Fotoğrafı Değiştir",
                        modifier = Modifier.size((size.value * 0.18f).coerceAtLeast(14f).dp)
                    )
                }
            }
        }
    }
}
