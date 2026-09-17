package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringRes
import com.example.ui.localization.tr
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithLanguage(
    currentLanguage: AppLanguage,
    activePanel: String,
    unreadNotifications: Int,
    userName: String?,
    onLanguageChange: (AppLanguage) -> Unit,
    onSwitchPanel: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onOpenCreatorHub: () -> Unit = {}
) {
    var languageMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand logo & title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(AmberPrimary, AmberDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = "Salon Logo",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = StringRes.appTitle.tr(currentLanguage),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = when (activePanel) {
                                "BARBER" -> "✂ ${StringRes.barberPanel.tr(currentLanguage)}"
                                "MASTER" -> "🛡 ${StringRes.masterPortalLabel.tr(currentLanguage)}"
                                else -> "👤 ${StringRes.customerPanel.tr(currentLanguage)}"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            ),
                            color = when (activePanel) {
                                "BARBER" -> AccentBronze
                                "MASTER" -> Color(0xFFA855F7)
                                else -> AmberPrimary
                            }
                        )
                    }
                }

                // Action buttons: Language, Notifications, Switch Panel / Profile
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Language selection button
                    OutlinedButton(
                        onClick = { languageMenuExpanded = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("btn_language_switch")
                    ) {
                        Text(
                            text = currentLanguage.nativeName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Language",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Notification bell with badge
                    BadgedBox(
                        badge = {
                            if (unreadNotifications > 0) {
                                Badge(
                                    containerColor = AccentRed,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = "$unreadNotifications",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = onNotificationsClick,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("btn_notifications")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (activePanel == "BARBER") {
                        // Switch Panel Quick Toggle Pill for Barber
                        Button(
                            onClick = onSwitchPanel,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentBronze.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("btn_switch_panel")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Switch Panel",
                                tint = AccentBronze,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Customer",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AccentBronze
                            )
                        }

                        // Creator Hub icon button
                        IconButton(
                            onClick = onOpenCreatorHub,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("btn_creator_hub")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "App Creator Hub",
                                tint = AmberPrimary
                            )
                        }

                        // Salon Owner Profile icon button
                        IconButton(
                            onClick = onProfileClick,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("btn_profile")
                                .testTag("btn_salon_profile")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Salon Owner Profile",
                                tint = AccentBronze
                            )
                        }
                    } else {
                        // CUSTOMER PANEL: Prominent Customer Profile Icon Button
                        Surface(
                            onClick = onProfileClick,
                            shape = RoundedCornerShape(12.dp),
                            color = if (userName != null) AmberPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (userName != null) AmberPrimary else DarkBorder
                            ),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("btn_customer_profile")
                                .testTag("btn_profile")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                if (userName != null) {
                                    val initial = userName.trim().take(1).uppercase()
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(AmberPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = initial.ifEmpty { "C" },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                    }
                                    val shortName = if (userName.length > 7) userName.take(7) + "…" else userName
                                    Text(
                                        text = shortName,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = AmberPrimary
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Customer Profile",
                                        tint = AmberPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = StringRes.profile.tr(currentLanguage),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Compact Quick Switch to Salon for Barber staff
                        IconButton(
                            onClick = onSwitchPanel,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("btn_switch_panel")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCut,
                                contentDescription = "Switch to Salon Mode",
                                tint = AccentBronze,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (languageMenuExpanded) {
        ModalBottomSheet(
            onDismissRequest = { languageMenuExpanded = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle(color = AmberPrimary) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .testTag("sheet_language_picker")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AmberPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = AmberPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.ENGLISH -> "Select Language"
                                AppLanguage.HINDI -> "भाषा चुनें"
                                AppLanguage.BENGALI -> "ভাষা নির্বাচন করুন"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = { languageMenuExpanded = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                AppLanguage.values().forEach { lang ->
                    val isSelected = lang == currentLanguage
                    val flag = when (lang) {
                        AppLanguage.ENGLISH -> "🇬🇧"
                        AppLanguage.HINDI -> "🇮🇳"
                        AppLanguage.BENGALI -> "🇮🇳"
                    }

                    Surface(
                        onClick = {
                            languageMenuExpanded = false
                            onLanguageChange(lang)
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) AmberPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) AmberPrimary else DarkBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .testTag("menu_lang_${lang.code}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = flag, fontSize = 22.sp)
                                Column {
                                    Text(
                                        text = lang.nativeName,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) AmberPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = lang.displayName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    languageMenuExpanded = false
                                    onLanguageChange(lang)
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = AmberPrimary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}
