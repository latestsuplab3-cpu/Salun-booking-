package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringRes
import com.example.ui.localization.tr
import com.example.ui.components.FirebaseOtpDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.SalonViewModel

enum class SelectedPortal {
    NONE,
    CUSTOMER,
    SALON_OWNER,
    MASTER
}

/**
 * Centralized LoginSelector Screen.
 * Serves as the primary landing page for the application with 3 distinct login options:
 * 1. Customer Panel Login (কাস্টমার প্যানেল লগইন)
 * 2. Salon Owner Login (সেলুন ওনার লগইন)
 * 3. Master / Creator Login (মাস্টার লগইন)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginSelectorScreen(
    viewModel: SalonViewModel,
    onCustomerLoggedIn: () -> Unit,
    onSalonOwnerLoggedIn: () -> Unit,
    onMasterLoggedIn: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var activePortalSelection by remember { mutableStateOf(SelectedPortal.NONE) }

    var showOtpDialogForRole by remember { mutableStateOf<String?>(null) }
    var otpInitialPhone by remember { mutableStateOf("") }
    var otpSalonId by remember { mutableStateOf<String?>(null) }

    // Customer Form State
    var customerPhone by remember { mutableStateOf("") }
    var customerPassword by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var isNewCustomerMode by remember { mutableStateOf(false) }
    var customerPassVisible by remember { mutableStateOf(false) }
    var customerError by remember { mutableStateOf<String?>(null) }
    var customerLoading by remember { mutableStateOf(false) }

    // Salon Owner Form State
    var ownerSalonId by remember { mutableStateOf("") }
    var ownerPhone by remember { mutableStateOf("") }
    var ownerPassword by remember { mutableStateOf("") }
    var ownerPassVisible by remember { mutableStateOf(false) }
    var ownerError by remember { mutableStateOf<String?>(null) }
    var ownerLoading by remember { mutableStateOf(false) }

    // Master Form State
    var masterAdminId by remember { mutableStateOf("") }
    var masterPassword by remember { mutableStateOf("") }
    var masterPassVisible by remember { mutableStateOf(false) }
    var masterError by remember { mutableStateOf<String?>(null) }
    var masterLoading by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_login_selector"),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Language Selector & Branding Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AmberPrimary.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Style Master v2.4",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AmberPrimary
                        )
                    }
                }

                // Language quick toggle chips
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AppLanguage.values().forEach { lang ->
                        val isSelected = lang == language
                        val labelText = when (lang) {
                            AppLanguage.ENGLISH -> "EN"
                            AppLanguage.HINDI -> "हिं"
                            AppLanguage.BENGALI -> "বাং"
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setLanguage(lang) },
                            label = {
                                Text(
                                    text = labelText,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AmberPrimary,
                                selectedLabelColor = Color(0xFF0F172A)
                            ),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("chip_lang_${lang.name.lowercase()}")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Hero Branding Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E293B),
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(AmberPrimary.copy(alpha = 0.5f), AccentBronze.copy(alpha = 0.3f))),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(AmberPrimary, AmberDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = "Salon Logo",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Text(
                        text = StringRes.loginSelectorTitle.tr(language),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = StringRes.loginSelectorSubtitle.tr(language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Animated Switch: Showing either the 3 Portal Cards or the Selected Active Login Form
            AnimatedContent(
                targetState = activePortalSelection,
                transitionSpec = {
                    fadeIn() + slideInVertically { height -> height / 4 } togetherWith
                            fadeOut() + slideOutVertically { height -> -height / 4 }
                },
                label = "PortalContentTransition"
            ) { currentSelection ->
                when (currentSelection) {
                    SelectedPortal.NONE -> {
                        // 3 DISTINCT ENTRY OPTIONS OVERVIEW
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = StringRes.chooseLoginPortal.tr(language),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(start = 4.dp)
                            )

                            // Option 1: Customer Login
                            PortalSelectionCard(
                                title = StringRes.customerPortalOptionTitle.tr(language),
                                description = StringRes.customerPortalOptionDesc.tr(language),
                                badgeText = "CLIENT BOOKING",
                                icon = Icons.Default.Person,
                                primaryColor = AmberPrimary,
                                accentGradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                                testTag = "selector_customer_login",
                                onClick = {
                                    customerError = null
                                    activePortalSelection = SelectedPortal.CUSTOMER
                                }
                            )

                            // Option 2: Salon Owner Login
                            PortalSelectionCard(
                                title = StringRes.salonOwnerPortalOptionTitle.tr(language),
                                description = StringRes.salonOwnerPortalOptionDesc.tr(language),
                                badgeText = "SALON MERCHANT",
                                icon = Icons.Default.Storefront,
                                primaryColor = AccentBronze,
                                accentGradient = listOf(Color(0xFFD97706), Color(0xFFB45309)),
                                testTag = "selector_owner_login",
                                onClick = {
                                    ownerError = null
                                    activePortalSelection = SelectedPortal.SALON_OWNER
                                }
                            )

                            // Option 3: Master / App Creator Login
                            PortalSelectionCard(
                                title = StringRes.masterPortalOptionTitle.tr(language),
                                description = StringRes.masterPortalOptionDesc.tr(language),
                                badgeText = "MASTER ADMIN",
                                icon = Icons.Default.AdminPanelSettings,
                                primaryColor = Color(0xFFA855F7),
                                accentGradient = listOf(Color(0xFF9333EA), Color(0xFF6366F1)),
                                testTag = "selector_master_login",
                                onClick = {
                                    masterError = null
                                    activePortalSelection = SelectedPortal.MASTER
                                }
                            )

                            // Dual Cloud Integration Card: Firebase & Supabase
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth().testTag("card_dual_cloud_info")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudSync,
                                            contentDescription = null,
                                            tint = AccentGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Dual Cloud Engine Active",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = AmberPrimary.copy(alpha = 0.15f),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = AmberPrimary, modifier = Modifier.size(14.dp))
                                                Text("Firebase: SMS OTP", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AmberPrimary)
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = AccentGreen.copy(alpha = 0.15f),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(14.dp))
                                                Text("Supabase: Live Sync", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AccentGreen)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    SelectedPortal.CUSTOMER -> {
                        // 1. CUSTOMER LOGIN FORM
                        CustomerLoginForm(
                            language = language,
                            phone = customerPhone,
                            onPhoneChange = { customerPhone = it; customerError = null },
                            password = customerPassword,
                            onPasswordChange = { customerPassword = it; customerError = null },
                            name = customerName,
                            onNameChange = { customerName = it; customerError = null },
                            isNewAccount = isNewCustomerMode,
                            onToggleNewAccount = { isNewCustomerMode = it; customerError = null },
                            passwordVisible = customerPassVisible,
                            onTogglePasswordVisibility = { customerPassVisible = !customerPassVisible },
                            errorMessage = customerError,
                            isLoading = customerLoading,
                            onBackToSelection = { activePortalSelection = SelectedPortal.NONE },
                            onQuickFillDemo = {
                                customerPhone = "01800000000"
                                customerPassword = "123"
                                customerName = "সুমন ইসলাম (Customer)"
                                isNewCustomerMode = false
                                customerError = null
                            },
                            onSubmit = {
                                val cleanPhone = customerPhone.trim()
                                val cleanPass = customerPassword.trim()
                                val cleanName = customerName.trim()

                                if (cleanPhone.isBlank()) {
                                    customerError = "Please enter mobile phone number."
                                    return@CustomerLoginForm
                                }
                                if (cleanPass.isBlank()) {
                                    customerError = "Please enter password."
                                    return@CustomerLoginForm
                                }
                                if (isNewCustomerMode && cleanName.isBlank()) {
                                    customerError = "Please enter your full name."
                                    return@CustomerLoginForm
                                }

                                customerLoading = true
                                customerError = null
                                focusManager.clearFocus()

                                viewModel.loginWithPhonePassword(
                                    phone = cleanPhone,
                                    pass = cleanPass,
                                    expectedRole = "CUSTOMER",
                                    name = cleanName,
                                    onSuccess = {
                                        customerLoading = false
                                        onCustomerLoggedIn()
                                    },
                                    onError = { err ->
                                        customerLoading = false
                                        customerError = err
                                    }
                                )
                            },
                            onOpenOtpLogin = {
                                otpInitialPhone = customerPhone
                                otpSalonId = null
                                showOtpDialogForRole = "CUSTOMER"
                            }
                        )
                    }

                    SelectedPortal.SALON_OWNER -> {
                        // 2. SALON OWNER LOGIN FORM
                        SalonOwnerLoginForm(
                            language = language,
                            salonId = ownerSalonId,
                            onSalonIdChange = { ownerSalonId = it; ownerError = null },
                            phone = ownerPhone,
                            onPhoneChange = { ownerPhone = it; ownerError = null },
                            password = ownerPassword,
                            onPasswordChange = { ownerPassword = it; ownerError = null },
                            passwordVisible = ownerPassVisible,
                            onTogglePasswordVisibility = { ownerPassVisible = !ownerPassVisible },
                            errorMessage = ownerError,
                            isLoading = ownerLoading,
                            onBackToSelection = { activePortalSelection = SelectedPortal.NONE },
                            onQuickFillDemo = {
                                ownerSalonId = "SALON-101"
                                ownerPhone = "01700000000"
                                ownerPassword = "123"
                                ownerError = null
                            },
                            onSubmit = {
                                val cleanId = ownerSalonId.trim().uppercase()
                                val cleanPhone = ownerPhone.trim()
                                val cleanPass = ownerPassword.trim()

                                if (cleanId.isBlank()) {
                                    ownerError = "Please enter Salon ID (e.g. SALON-101)."
                                    return@SalonOwnerLoginForm
                                }
                                if (cleanPhone.isBlank()) {
                                    ownerError = "Please enter registered owner phone number."
                                    return@SalonOwnerLoginForm
                                }
                                if (cleanPass.isBlank()) {
                                    ownerError = "Please enter password."
                                    return@SalonOwnerLoginForm
                                }

                                ownerLoading = true
                                ownerError = null
                                focusManager.clearFocus()

                                viewModel.loginSalonOwner(
                                    salonId = cleanId,
                                    phone = cleanPhone,
                                    pass = cleanPass,
                                    onSuccess = {
                                        ownerLoading = false
                                        onSalonOwnerLoggedIn()
                                    },
                                    onError = { err ->
                                        ownerLoading = false
                                        ownerError = err
                                    }
                                )
                            },
                            onOpenOtpLogin = {
                                otpInitialPhone = ownerPhone
                                otpSalonId = ownerSalonId
                                showOtpDialogForRole = "BARBER"
                            }
                        )
                    }

                    SelectedPortal.MASTER -> {
                        // 3. MASTER / APP CREATOR LOGIN FORM
                        MasterLoginForm(
                            language = language,
                            adminId = masterAdminId,
                            onAdminIdChange = { masterAdminId = it; masterError = null },
                            password = masterPassword,
                            onPasswordChange = { masterPassword = it; masterError = null },
                            passwordVisible = masterPassVisible,
                            onTogglePasswordVisibility = { masterPassVisible = !masterPassVisible },
                            errorMessage = masterError,
                            isLoading = masterLoading,
                            onBackToSelection = { activePortalSelection = SelectedPortal.NONE },
                            onQuickFillDemo = {
                                masterAdminId = "MASTER-ADMIN"
                                masterPassword = "admin123"
                                masterError = null
                            },
                            onSubmit = {
                                val cleanId = masterAdminId.trim().uppercase()
                                val cleanPass = masterPassword.trim()

                                if (cleanId.isBlank()) {
                                    masterError = "Please enter Master Admin ID."
                                    return@MasterLoginForm
                                }
                                if (cleanPass.isBlank()) {
                                    masterError = "Please enter master password."
                                    return@MasterLoginForm
                                }

                                masterLoading = true
                                masterError = null
                                focusManager.clearFocus()

                                viewModel.loginMasterAdmin(
                                    adminId = cleanId,
                                    pass = cleanPass,
                                    onSuccess = {
                                        masterLoading = false
                                        onMasterLoggedIn()
                                    },
                                    onError = { err ->
                                        masterLoading = false
                                        masterError = err
                                    }
                                )
                            },
                            onOpenOtpLogin = {
                                otpInitialPhone = "01999999999"
                                otpSalonId = null
                                showOtpDialogForRole = "MASTER"
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer note
            Text(
                text = "Style Master Multi-Role Architecture • Safe & Secure",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                textAlign = TextAlign.Center
            )
        }
    }

    if (showOtpDialogForRole != null) {
        FirebaseOtpDialog(
            targetRole = showOtpDialogForRole!!,
            initialPhone = otpInitialPhone,
            salonId = otpSalonId,
            language = language,
            viewModel = viewModel,
            onDismiss = { showOtpDialogForRole = null },
            onLoginSuccess = {
                val role = showOtpDialogForRole
                showOtpDialogForRole = null
                when (role) {
                    "BARBER" -> onSalonOwnerLoggedIn()
                    "MASTER" -> onMasterLoggedIn()
                    else -> onCustomerLoggedIn()
                }
            }
        )
    }
}

/**
 * Visual card representing each distinct entry option.
 */
@Composable
fun PortalSelectionCard(
    title: String,
    description: String,
    badgeText: String,
    icon: ImageVector,
    primaryColor: Color,
    accentGradient: List<Color>,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Role Icon with gradient
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(accentGradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = primaryColor.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = primaryColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Proceed Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "Login →",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (primaryColor == AmberPrimary) Color(0xFF0F172A) else Color.White
                    )
                }
            }
        }
    }
}

/**
 * 1. Customer Login Form
 */
@Composable
fun CustomerLoginForm(
    language: AppLanguage,
    phone: String,
    onPhoneChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    isNewAccount: Boolean,
    onToggleNewAccount: (Boolean) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    onBackToSelection: () -> Unit,
    onQuickFillDemo: () -> Unit,
    onSubmit: () -> Unit,
    onOpenOtpLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("panel_customer_login")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AmberPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = AmberPrimary, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text(
                            text = StringRes.customerPortalOptionTitle.tr(language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Customer Mobile Login",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                TextButton(
                    onClick = onBackToSelection,
                    modifier = Modifier.testTag("btn_customer_back_to_portals")
                ) {
                    Text(
                        text = "← Back",
                        color = AmberPrimary,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Phone Field
            OutlinedTextField(
                value = phone,
                onValueChange = { onPhoneChange(it.filter { c -> c.isDigit() }.take(10)) },
                label = { Text(StringRes.phoneNumber.tr(language)) },
                placeholder = { Text("98765 43210") },
                prefix = {
                    Text("🇮🇳 +91 ", fontWeight = FontWeight.Bold, color = AmberPrimary)
                },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = AmberPrimary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_customer_phone")
            )

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text(StringRes.password.tr(language)) },
                placeholder = { Text("••••••") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AmberPrimary) },
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Password",
                            tint = TextSecondary
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_customer_password")
            )

            // Toggle New Account / Existing Account
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { onToggleNewAccount(!isNewAccount) }) {
                    Text(
                        text = if (isNewAccount) "Already registered? Login" else "New customer? Register here",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberPrimary
                    )
                }
            }

            // Submit Button
            Button(
                onClick = onSubmit,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_customer_submit_login")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF0F172A), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Login, contentDescription = null, tint = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isNewAccount) "Register & Enter Customer Panel" else "Enter Customer Panel",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                Text(
                    text = "  OR / বা  ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
            }

            OutlinedButton(
                onClick = onOpenOtpLogin,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AmberPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_customer_otp_login")
            ) {
                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = AmberPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (language) {
                        AppLanguage.ENGLISH -> "Log in with Firebase Phone OTP"
                        AppLanguage.HINDI -> "Firebase Phone OTP से लॉगिन करें"
                        else -> "Firebase Phone OTP দিয়ে লগইন করুন"
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/**
 * 2. Salon Owner Login Form
 */
@Composable
fun SalonOwnerLoginForm(
    language: AppLanguage,
    salonId: String,
    onSalonIdChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    onBackToSelection: () -> Unit,
    onQuickFillDemo: () -> Unit,
    onSubmit: () -> Unit,
    onOpenOtpLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, AccentBronze.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("panel_salon_owner_login")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AccentBronze.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = AccentBronze, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text(
                            text = StringRes.salonOwnerPortalOptionTitle.tr(language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Merchant ID & Password",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                TextButton(
                    onClick = onBackToSelection,
                    modifier = Modifier.testTag("btn_owner_back_to_portals")
                ) {
                    Text(
                        text = "← Back",
                        color = AccentBronze,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Salon ID Field
            OutlinedTextField(
                value = salonId,
                onValueChange = onSalonIdChange,
                label = { Text(StringRes.salonIdLabel.tr(language)) },
                placeholder = { Text("e.g. SALON-101") },
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = AccentBronze) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_owner_salon_id")
            )

            // Phone Field
            OutlinedTextField(
                value = phone,
                onValueChange = { onPhoneChange(it.filter { c -> c.isDigit() }.take(10)) },
                label = { Text(StringRes.phoneNumber.tr(language)) },
                placeholder = { Text("98765 43210") },
                prefix = {
                    Text("🇮🇳 +91 ", fontWeight = FontWeight.Bold, color = AccentBronze)
                },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = AccentBronze) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_owner_phone")
            )

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text(StringRes.password.tr(language)) },
                placeholder = { Text("••••••") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AccentBronze) },
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Password",
                            tint = TextSecondary
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_owner_password")
            )

            // Submit Button
            Button(
                onClick = onSubmit,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBronze),
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_salon_owner_submit_login")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enter Salon Owner Panel",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                Text(
                    text = "  OR / বা  ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
            }

            OutlinedButton(
                onClick = onOpenOtpLogin,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AccentBronze),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_owner_otp_login")
            ) {
                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = AccentBronze, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (language) {
                        AppLanguage.ENGLISH -> "Log in with Firebase Phone OTP"
                        AppLanguage.HINDI -> "Firebase Phone OTP से लॉगिन करें"
                        else -> "Firebase Phone OTP দিয়ে লগইন করুন"
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/**
 * 3. Master / Creator Login Form
 */
@Composable
fun MasterLoginForm(
    language: AppLanguage,
    adminId: String,
    onAdminIdChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    onBackToSelection: () -> Unit,
    onQuickFillDemo: () -> Unit,
    onSubmit: () -> Unit,
    onOpenOtpLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val masterPurple = Color(0xFF8B5CF6)

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, masterPurple.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("panel_master_login")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(masterPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = masterPurple, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text(
                            text = StringRes.masterPortalOptionTitle.tr(language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "App Creator & Root Administration",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                TextButton(
                    onClick = onBackToSelection,
                    modifier = Modifier.testTag("btn_master_back_to_portals")
                ) {
                    Text(
                        text = "← Back",
                        color = masterPurple,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Master Admin ID Field
            OutlinedTextField(
                value = adminId,
                onValueChange = onAdminIdChange,
                label = { Text(StringRes.masterAdminIdLabel.tr(language)) },
                placeholder = { Text("e.g. MASTER-ADMIN or admin") },
                leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = masterPurple) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_master_admin_id")
            )

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text(StringRes.masterPasswordLabel.tr(language)) },
                placeholder = { Text("••••••••") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = masterPurple) },
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Password",
                            tint = TextSecondary
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_master_password")
            )

            // Submit Button
            Button(
                onClick = onSubmit,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = masterPurple),
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_master_submit_login")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = StringRes.masterLoginBtn.tr(language),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                Text(
                    text = "  OR / বা  ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
            }

            OutlinedButton(
                onClick = onOpenOtpLogin,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, masterPurple),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_master_otp_login")
            ) {
                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = masterPurple, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (language) {
                        AppLanguage.ENGLISH -> "Log in with Firebase Phone OTP"
                        AppLanguage.HINDI -> "Firebase Phone OTP से लॉगिन करें"
                        else -> "Firebase Phone OTP দিয়ে লগইন করুন"
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
