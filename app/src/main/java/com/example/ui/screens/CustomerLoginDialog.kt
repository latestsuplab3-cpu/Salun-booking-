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
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringRes
import com.example.ui.localization.tr
import com.example.ui.theme.*

/**
 * Dedicated Customer Login Panel.
 * Completely separate from the Salon Owner Panel.
 * Handles customer authentication (Phone, Password, and optional Name for new accounts).
 */
@Composable
fun CustomerLoginDialog(
    currentUser: UserEntity?,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onLoginSubmit: (name: String, phone: String, pass: String, onError: (String) -> Unit) -> Unit,
    onSwitchToSalonOwnerLogin: () -> Unit,
    onLogout: () -> Unit,
    onOpenOtpLogin: (() -> Unit)? = null
) {
    var isNewAccountMode by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    fun performSubmit() {
        errorMessage = null
        val cleanPhone = phoneInput.trim()
        val cleanPass = passwordInput.trim()
        val cleanName = nameInput.trim()

        if (cleanPhone.isBlank()) {
            errorMessage = "Please enter your mobile phone number."
            return
        }
        if (cleanPass.isBlank()) {
            errorMessage = "Please enter your password."
            return
        }
        if (isNewAccountMode && cleanName.isBlank()) {
            errorMessage = "Please enter your full name."
            return
        }

        isLoading = true
        onLoginSubmit(cleanName, cleanPhone, cleanPass) { err ->
            isLoading = false
            errorMessage = err
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.35f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .testTag("dialog_customer_login")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(AmberPrimary, AmberDark)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = StringRes.customerLoginPanelTitle.tr(language),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Dedicated Customer Portal",
                                style = MaterialTheme.typography.labelSmall,
                                color = AmberPrimary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_customer_login")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (currentUser != null && currentUser.role == "CUSTOMER") {
                    // ALREADY LOGGED IN CUSTOMER VIEW
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(AmberPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentUser.name.trim().take(1).uppercase().ifEmpty { "C" },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = currentUser.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "📱 ${currentUser.phone}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "⭐ Verified Customer Account",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AccentGreen
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onLogout()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_customer_logout")
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = StringRes.logoutBtn.tr(language),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onSwitchToSalonOwnerLogin()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = StringRes.switchToSalonOwnerLogin.tr(language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = AccentBronze
                        )
                    }
                } else {
                    // CUSTOMER AUTH FORM (Separate from Salon Owner)
                    Text(
                        text = StringRes.customerLoginPanelSubtitle.tr(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sign In vs Register Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            onClick = { isNewAccountMode = false },
                            shape = RoundedCornerShape(10.dp),
                            color = if (!isNewAccountMode) AmberPrimary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("tab_customer_signin")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Existing Customer",
                                    fontSize = 12.sp,
                                    fontWeight = if (!isNewAccountMode) FontWeight.Bold else FontWeight.Medium,
                                    color = if (!isNewAccountMode) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Surface(
                            onClick = { isNewAccountMode = true },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isNewAccountMode) AmberPrimary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("tab_customer_register")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "+ New Registration",
                                    fontSize = 12.sp,
                                    fontWeight = if (isNewAccountMode) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isNewAccountMode) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Field: Customer Name (if registering or optional)
                    if (isNewAccountMode) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = {
                                nameInput = it
                                errorMessage = null
                            },
                            label = { Text("Your Full Name") },
                            placeholder = { Text(StringRes.namePlaceholder.tr(language)) },
                            leadingIcon = {
                                Icon(Icons.Default.PersonOutline, contentDescription = null, tint = AmberPrimary)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_customer_name"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Field: Phone Number
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = {
                            phoneInput = it
                            errorMessage = null
                        },
                        label = { Text(StringRes.phoneNumber.tr(language)) },
                        placeholder = { Text(StringRes.phonePlaceholder.tr(language)) },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = AmberPrimary)
                        },
                        trailingIcon = {
                            if (phoneInput.isNotEmpty()) {
                                IconButton(onClick = { phoneInput = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
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
                            .testTag("input_customer_phone"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Field: Password
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            errorMessage = null
                        },
                        label = { Text(StringRes.password.tr(language)) },
                        placeholder = { Text(StringRes.passwordPlaceholder.tr(language)) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AmberPrimary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = TextSecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                performSubmit()
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_customer_password"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Error Message
                    errorMessage?.let { err ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = AccentRed.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, AccentRed.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("banner_customer_login_error")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = AccentRed, modifier = Modifier.size(18.dp))
                                Text(err, style = MaterialTheme.typography.bodySmall, color = AccentRed)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button
                    Button(
                        onClick = { performSubmit() },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = Color(0xFF0F172A)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_customer_login_submit")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verifying...", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        } else {
                            Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFF0F172A))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isNewAccountMode) "Register as Customer" else StringRes.loginButton.tr(language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }

                    if (onOpenOtpLogin != null) {
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
                            onClick = onOpenOtpLogin,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, AmberPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_customer_dialog_otp_login")
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1-Tap Customer Demo Logins
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = StringRes.quickDemoCustomersLabel.tr(language),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        phoneInput = "+91 98765 43210"
                                        passwordInput = "123456"
                                        nameInput = "Rahul Sharma"
                                        isNewAccountMode = false
                                        errorMessage = null
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_quick_demo_rahul")
                                ) {
                                    Text("👤 Rahul (9876543210)", fontSize = 11.sp, maxLines = 1)
                                }

                                OutlinedButton(
                                    onClick = {
                                        phoneInput = "+91 91234 56789"
                                        passwordInput = "123456"
                                        nameInput = "Priya Das"
                                        isNewAccountMode = false
                                        errorMessage = null
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_quick_demo_priya")
                                ) {
                                    Text("👤 Priya (9123456789)", fontSize = 11.sp, maxLines = 1)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Clean separation link to Salon Owner Login
                    TextButton(
                        onClick = {
                            onDismiss()
                            onSwitchToSalonOwnerLogin()
                        },
                        modifier = Modifier.testTag("btn_switch_to_salon_login")
                    ) {
                        Text(
                            text = StringRes.switchToSalonOwnerLogin.tr(language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentBronze
                        )
                    }
                }
            }
        }
    }
}
