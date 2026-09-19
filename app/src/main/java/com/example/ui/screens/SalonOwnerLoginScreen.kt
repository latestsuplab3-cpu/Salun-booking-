package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

/**
 * Dedicated Login Screen for the Salon Owner Panel.
 * Authenticates salon merchants using:
 * 1. Salon License ID (e.g. SALON-101)
 * 2. Registered Owner Phone Number (e.g. 01700000000)
 * 3. Secret Password (e.g. 123)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalonOwnerLoginScreen(
    viewModel: SalonViewModel,
    onBackToCustomer: () -> Unit,
    onOpenCreatorHub: () -> Unit,
    onOpenProfile: () -> Unit = {}
) {
    val language by viewModel.currentLanguage.collectAsState()
    val focusManager = LocalFocusManager.current

    var salonIdInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showFirebaseOtpDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    fun performLogin() {
        errorMessage = null
        val cleanId = salonIdInput.trim()
        val cleanPhone = phoneInput.trim()
        val cleanPass = passwordInput.trim()

        if (cleanId.isBlank()) {
            errorMessage = "Please enter your Salon License ID."
            return
        }
        if (cleanPhone.isBlank()) {
            errorMessage = "Please enter your registered owner phone number."
            return
        }
        if (cleanPass.isBlank()) {
            errorMessage = "Please enter your password."
            return
        }

        isLoading = true
        viewModel.loginSalonOwner(
            salonId = cleanId,
            phone = cleanPhone,
            pass = cleanPass,
            onSuccess = {
                isLoading = false
            },
            onError = { err ->
                isLoading = false
                errorMessage = err
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("screen_salon_owner_login"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation & Language Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBackToCustomer,
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.testTag("btn_back_to_customer")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(16.dp),
                    tint = AmberPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = StringRes.backToCustomerPortal.tr(language),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Quick Language Selector and Profile Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AppLanguage.values().forEach { lang ->
                        val isSelected = lang == language
                        val chipText = when (lang) {
                            AppLanguage.ENGLISH -> "EN"
                            AppLanguage.HINDI -> "हिं"
                            AppLanguage.BENGALI -> "বাং"
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setLanguage(lang) },
                            label = { Text(chipText, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            shape = RoundedCornerShape(14.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentBronze.copy(alpha = 0.25f),
                                selectedLabelColor = AccentBronze
                            ),
                            modifier = Modifier.height(30.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onOpenProfile,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("btn_login_screen_profile")
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Salon Owner Profile",
                        tint = AccentBronze,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hero Logo & Portal Badge
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentBronze, Color(0xFF451A03))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = StringRes.salonOwnerLoginTitle.tr(language),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = StringRes.salonOwnerLoginSubtitle.tr(language),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Security Pill Badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = AccentBronze.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, AccentBronze.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = AccentBronze,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = StringRes.salonIdRequirementHint.tr(language),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = AccentBronze
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main Login Card with the 3 required fields
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, AccentBronze.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_salon_owner_form")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Merchant Authentication",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Verify your 3-Factor Salon Credentials to proceed:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Field 1: Salon ID
                OutlinedTextField(
                    value = salonIdInput,
                    onValueChange = {
                        salonIdInput = it.uppercase()
                        errorMessage = null
                    },
                    label = { Text(StringRes.salonIdLabel.tr(language)) },
                    placeholder = { Text(StringRes.salonIdPlaceholder.tr(language)) },
                    supportingText = {
                        Text(StringRes.salonIdHelperText.tr(language), fontSize = 11.sp, color = TextSecondary)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = "Salon ID",
                            tint = AccentBronze
                        )
                    },
                    trailingIcon = {
                        if (salonIdInput.isNotEmpty()) {
                            IconButton(onClick = { salonIdInput = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_salon_owner_id")
                        .testTag("input_salon_id"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Field 2: Phone Number
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = {
                        phoneInput = it
                        errorMessage = null
                    },
                    label = { Text(StringRes.phoneNumber.tr(language)) },
                    placeholder = { Text(StringRes.ownerPhonePlaceholder.tr(language)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone Number",
                            tint = AccentBronze
                        )
                    },
                    trailingIcon = {
                        if (phoneInput.isNotEmpty()) {
                            IconButton(onClick = { phoneInput = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_salon_owner_phone"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Field 3: Password
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = {
                        passwordInput = it
                        errorMessage = null
                    },
                    label = { Text(StringRes.password.tr(language)) },
                    placeholder = { Text(StringRes.passwordPlaceholder.tr(language)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password",
                            tint = AccentBronze
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                tint = TextSecondary
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            performLogin()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_salon_owner_password")
                        .testTag("input_salon_owner_pass"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Error Message banner
                errorMessage?.let { err ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = AccentRed.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, AccentRed.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("banner_login_error")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Error",
                                tint = AccentRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = err,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = AccentRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Submit Button
                Button(
                    onClick = { performLogin() },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBronze,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_salon_owner_login")
                        .testTag("btn_salon_login_submit")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = StringRes.authenticatingOwner.tr(language),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Login,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = StringRes.loginSalonBtn.tr(language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
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
                    onClick = { showFirebaseOtpDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AccentBronze),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_salon_owner_otp_login")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = AccentBronze,
                        modifier = Modifier.size(18.dp)
                    )
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

        Spacer(modifier = Modifier.height(18.dp))

        // Quick 1-Tap Demo Credentials Section
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = AmberPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = StringRes.quickDemoCredentialsLabel.tr(language),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Demo Option 1: SALON-101
                Surface(
                    onClick = {
                        salonIdInput = "SALON-101"
                        phoneInput = "01700000000"
                        passwordInput = "123"
                        errorMessage = null
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_quick_demo_salon_101")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "💈 Style Master Salon",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "ID: SALON-101  •  Phone: 01700000000  •  Pass: 123",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Surface(
                            color = AmberPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "FILL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Demo Option 2: SALON-202
                Surface(
                    onClick = {
                        salonIdInput = "SALON-202"
                        phoneInput = "9876543210"
                        passwordInput = "pass"
                        errorMessage = null
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, AccentBronze.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_quick_demo_salon_202")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "✂️ Royal Grooming Studio",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "ID: SALON-202  •  Phone: 9876543210  •  Pass: pass",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Surface(
                            color = AccentBronze.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "FILL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentBronze,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // App Creator Hub Entry Point
        OutlinedButton(
            onClick = onOpenCreatorHub,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_open_creator_hub_from_login")
        ) {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = AmberPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "👑 App Creator Hub: Issue New Salon License IDs",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AmberPrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showFirebaseOtpDialog) {
        FirebaseOtpDialog(
            targetRole = "BARBER",
            initialPhone = phoneInput.ifBlank { "01700000000" },
            salonId = salonIdInput.ifBlank { "SALON-101" },
            language = language,
            viewModel = viewModel,
            onDismiss = { showFirebaseOtpDialog = false },
            onLoginSuccess = {
                showFirebaseOtpDialog = false
            }
        )
    }
}
