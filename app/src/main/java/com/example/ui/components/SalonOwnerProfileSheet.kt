package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingEntity
import com.example.data.model.SalonLicenseEntity
import com.example.data.model.UserEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringRes
import com.example.ui.localization.tr
import com.example.ui.theme.*

/**
 * Dedicated Salon Owner Profile Section.
 * Displays official merchant credentials, salon license status, owner info,
 * performance metrics, interactive profile editing, and merchant account actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalonOwnerProfileSheet(
    currentUser: UserEntity?,
    currentSalonLicense: SalonLicenseEntity?,
    allBookings: List<BookingEntity>,
    language: AppLanguage,
    unreadNotifications: Int = 0,
    onDismiss: () -> Unit,
    onRequireLogin: () -> Unit,
    onLogout: () -> Unit,
    onOpenCreatorHub: () -> Unit,
    onOpenNotifications: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onSwitchToCustomerPanel: () -> Unit,
    onUpdateProfile: (ownerName: String, salonName: String, phone: String, address: String) -> Unit = { _, _, _, _ -> },
    onQuickLoginOwner: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    val isOwnerLoggedIn = currentUser != null && currentUser.role == "BARBER"

    // Primary owner and store information
    val effectiveOwnerName = currentUser?.name ?: currentSalonLicense?.ownerName ?: "Mohammad Ali (Owner)"
    val effectiveSalonName = currentSalonLicense?.salonName ?: (if (isOwnerLoggedIn) currentUser?.name ?: "Style Master Salon" else "Style Master Salon")
    val salonId = currentSalonLicense?.salonId ?: "SALON-101"
    val ownerPhone = currentSalonLicense?.ownerPhone ?: currentUser?.phone ?: "01700000000"
    val salonAddress = currentSalonLicense?.address ?: "Shop #12, City Center Mall, MG Road, Mumbai"

    // Form states for editing profile
    var editOwnerNameInput by remember(effectiveOwnerName) { mutableStateOf(effectiveOwnerName) }
    var editSalonNameInput by remember(effectiveSalonName) { mutableStateOf(effectiveSalonName) }
    var editPhoneInput by remember(ownerPhone) { mutableStateOf(ownerPhone) }
    var editAddressInput by remember(salonAddress) { mutableStateOf(salonAddress) }

    // Calculate salon performance metrics
    val totalBookings = allBookings.size
    val completedBookings = allBookings.count { it.status == "COMPLETED" }
    val confirmedBookings = allBookings.count { it.status == "CONFIRMED" }
    val totalAdvanceCollected = allBookings.filter { it.status != "CANCELLED" }.sumOf { it.advancePaid }
    val totalVolume = allBookings.filter { it.status != "CANCELLED" }.sumOf { it.totalPrice }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("sheet_salon_owner_profile")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(AccentBronze, Color(0xFF632B00))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = StringRes.salonOwnerProfileTitle.tr(language),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = StringRes.salonOwnerProfileSubtitle.tr(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_close_salon_profile")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Comprehensive Salon Owner Details Card (Always Displayed)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                ),
                border = BorderStroke(1.dp, AccentBronze.copy(alpha = 0.45f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_salon_merchant_info")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Owner Avatar & Identity Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(AccentBronze.copy(alpha = 0.2f))
                                    .testTag("avatar_salon_owner"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Salon Owner",
                                    tint = AccentBronze,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = effectiveOwnerName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Salon Owner & Master Stylist",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = AccentBronze
                                )
                                Text(
                                    text = effectiveSalonName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Verified Partner Badge
                        Surface(
                            color = AccentGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, AccentGreen.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "VERIFIED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = AccentGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Salon License ID & Registered Contact
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = StringRes.salonIdLabel.tr(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Text(
                                text = salonId,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = AccentBronze
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = StringRes.registeredPhoneLabel.tr(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Text(
                                text = ownerPhone,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Physical Store Address
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = AccentBronze,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = StringRes.salonAddressLabel.tr(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Text(
                                text = salonAddress,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Operating Hours & Policy
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = AmberPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = StringRes.operatingHoursValue.tr(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )
                        }

                        Text(
                            text = "4 Chairs",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Advance Pre-order Policy Banner
                    Surface(
                        color = AmberPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = AmberPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pre-orders: 50% - 55% Advance Online Deposit Enabled",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Edit Profile Details Button
                    OutlinedButton(
                        onClick = { showEditProfileDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AccentBronze),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("btn_edit_salon_owner_profile")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = AccentBronze,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = StringRes.editOwnerProfileBtn.tr(language),
                            color = AccentBronze,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Salon Business & Revenue Summary
            Text(
                text = StringRes.performanceSummary.tr(language),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Bookings",
                    value = "$totalBookings",
                    subtitle = "$confirmedBookings confirmed",
                    icon = Icons.Default.CalendarMonth,
                    iconTint = AccentBronze,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Completed",
                    value = "$completedBookings",
                    subtitle = "Successful visits",
                    icon = Icons.Default.CheckCircle,
                    iconTint = AccentGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Advance UPI",
                    value = "₹$totalAdvanceCollected",
                    subtitle = "50-55% Deposit",
                    icon = Icons.Default.AccountBalanceWallet,
                    iconTint = AmberPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Gross Total",
                    value = "₹$totalVolume",
                    subtitle = "Gross revenue",
                    icon = Icons.Default.TrendingUp,
                    iconTint = AccentCyan,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Authentication / Session Status Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOwnerLoggedIn) AccentGreen.copy(alpha = 0.08f) else AmberPrimary.copy(alpha = 0.08f)
                ),
                border = BorderStroke(
                    1.dp,
                    if (isOwnerLoggedIn) AccentGreen.copy(alpha = 0.3f) else AmberPrimary.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isOwnerLoggedIn) Icons.Default.CheckCircle else Icons.Default.LockClock,
                            contentDescription = null,
                            tint = if (isOwnerLoggedIn) AccentGreen else AmberPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isOwnerLoggedIn) "Session: Active Authenticated Owner" else "Session: Owner Profile Preview",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isOwnerLoggedIn) "Logged in as $effectiveOwnerName" else "Tap below to quickly authenticate",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    if (!isOwnerLoggedIn) {
                        Button(
                            onClick = {
                                onDismiss()
                                onQuickLoginOwner()
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBronze),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("btn_quick_login_owner")
                        ) {
                            Text(
                                text = "Quick Login",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Quick Navigation & Operations
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Notifications
                    ProfileSheetItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications & Alerts",
                        badge = if (unreadNotifications > 0) "$unreadNotifications" else null,
                        onClick = {
                            onDismiss()
                            onOpenNotifications()
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                    // App Creator Hub
                    ProfileSheetItem(
                        icon = Icons.Default.AdminPanelSettings,
                        title = "App Creator & License Hub",
                        iconTint = AmberPrimary,
                        onClick = {
                            onDismiss()
                            onOpenCreatorHub()
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                    // Switch to Customer Panel
                    ProfileSheetItem(
                        icon = Icons.Default.Storefront,
                        title = "Switch to Customer Booking Screen",
                        onClick = {
                            onDismiss()
                            onSwitchToCustomerPanel()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Language Selection Row
            Text(
                text = StringRes.languageLabel.tr(language),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppLanguage.values().forEach { lang ->
                    val isSelected = lang == language
                    OutlinedButton(
                        onClick = { onLanguageChange(lang) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) AccentBronze.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) AccentBronze else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = lang.displayName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) AccentBronze else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout or Switch Account Button
            if (isOwnerLoggedIn) {
                Button(
                    onClick = { showLogoutConfirm = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed.copy(alpha = 0.12f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_salon_owner_logout")
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        tint = AccentRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = StringRes.logoutSalonOwnerBtn.tr(language),
                        color = AccentRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = {
                        onDismiss()
                        onRequireLogin()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBronze),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_salon_profile_open_login")
                ) {
                    Icon(
                        imageVector = Icons.Default.Login,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = StringRes.loginButton.tr(language) + " with Salon ID",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Interactive Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = StringRes.editOwnerProfileTitle.tr(language),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = editOwnerNameInput,
                        onValueChange = { editOwnerNameInput = it },
                        label = { Text(StringRes.ownerNameLabel.tr(language)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_owner_name")
                    )

                    OutlinedTextField(
                        value = editSalonNameInput,
                        onValueChange = { editSalonNameInput = it },
                        label = { Text(StringRes.salonNameLabel.tr(language)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_salon_name")
                    )

                    OutlinedTextField(
                        value = editPhoneInput,
                        onValueChange = { editPhoneInput = it },
                        label = { Text(StringRes.registeredPhoneLabel.tr(language)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_owner_phone")
                    )

                    OutlinedTextField(
                        value = editAddressInput,
                        onValueChange = { editAddressInput = it },
                        label = { Text(StringRes.salonAddressLabel.tr(language)) },
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_owner_address")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editOwnerNameInput.isNotBlank()) {
                            onUpdateProfile(
                                editOwnerNameInput,
                                editSalonNameInput,
                                editPhoneInput,
                                editAddressInput
                            )
                            showEditProfileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBronze),
                    modifier = Modifier.testTag("btn_save_owner_profile")
                ) {
                    Text(
                        text = StringRes.saveProfileChangesBtn.tr(language),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = {
                Text(
                    text = StringRes.logoutSalonOwnerBtn.tr(language),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Are you sure you want to log out of the Salon Owner account? You will need your Salon ID, Phone, and Password to log back in.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        onDismiss()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun ProfileSheetItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    badge: String? = null,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badge != null) {
                Surface(
                    color = AccentRed,
                    shape = CircleShape
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
