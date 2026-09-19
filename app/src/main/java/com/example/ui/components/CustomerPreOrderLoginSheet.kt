package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceItemEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringRes
import com.example.ui.localization.tr
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerPreOrderLoginSheet(
    selectedServices: Set<ServiceItemEntity>,
    totalPrice: Double,
    advanceToPay: Double,
    remainingDue: Double,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onLoginSuccess: () -> Unit,
    onLoginSubmit: (name: String, phone: String, pass: String, onError: (String) -> Unit) -> Unit,
    onQuickDemoCustomer: () -> Unit,
    onOpenOtpLogin: (() -> Unit)? = null
) {
    var nameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = AmberPrimary) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
                .testTag("sheet_customer_preorder_login")
        ) {
            // Header
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
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(AmberPrimary, AmberDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column {
                        Text(
                            text = StringRes.preOrderLoginHeader.tr(language),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val subtitleText = when (language) {
                            AppLanguage.ENGLISH -> "Enter your name, phone and password to pre-order"
                            AppLanguage.HINDI -> "प्री-ऑर्डर के लिए नाम, फोन और पासवर्ड दर्ज करें"
                            AppLanguage.BENGALI -> "নাম, ফোন নম্বর ও পাসওয়ার্ড দিয়ে প্রি-অর্ডার করুন"
                        }
                        Text(
                            text = subtitleText,
                            style = MaterialTheme.typography.bodySmall,
                            color = AmberPrimary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_close_login_sheet")
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pre-Order Summary Badge
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val selectedServicesLabel = when (language) {
                            AppLanguage.ENGLISH -> "Selected Services: ${selectedServices.size}"
                            AppLanguage.HINDI -> "चयनित सेवाएं: ${selectedServices.size}"
                            AppLanguage.BENGALI -> "নির্বাচিত সেবা: ${selectedServices.size} টি"
                        }
                        Text(
                            text = selectedServicesLabel,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            color = AmberPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = StringRes.advanceBadge.tr(language),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = selectedServices.joinToString(", ") { svc ->
                            when (language) {
                                AppLanguage.ENGLISH -> svc.nameEn
                                AppLanguage.HINDI -> svc.nameHi
                                AppLanguage.BENGALI -> svc.nameBn
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Bill: ₹${totalPrice.toInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                        Text(
                            text = "Advance (55%): ₹${advanceToPay.toInt()}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = AccentGreen
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = StringRes.preOrderLoginInstruction.tr(language),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Field 1: Customer Name
            OutlinedTextField(
                value = nameInput,
                onValueChange = {
                    nameInput = it
                    errorMessage = null
                },
                label = { Text(StringRes.fullName.tr(language)) },
                placeholder = { Text(StringRes.namePlaceholder.tr(language)) },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = AmberPrimary)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_customer_name"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberPrimary,
                    focusedLabelColor = AmberPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Field 2: Phone Number
            OutlinedTextField(
                value = phoneInput,
                onValueChange = {
                    phoneInput = it.filter { char -> char.isDigit() }.take(10)
                },
                label = { Text(StringRes.phoneNumber.tr(language)) },
                placeholder = { Text("98765 43210") },
                prefix = {
                    Text("🇮🇳 +91 ", fontWeight = FontWeight.Bold, color = AmberPrimary)
                },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = AmberPrimary)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_customer_phone"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberPrimary,
                    focusedLabelColor = AmberPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Field 3: Password
            OutlinedTextField(
                value = passwordInput,
                onValueChange = {
                    passwordInput = it
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
                            contentDescription = "Toggle password visibility",
                            tint = TextSecondary
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_customer_password"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberPrimary,
                    focusedLabelColor = AmberPrimary
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Login / Register & Proceed button
            Button(
                onClick = {
                    val digits = phoneInput.filter { it.isDigit() }.take(10)
                    if (nameInput.isBlank() || digits.length != 10 || passwordInput.isBlank()) {
                        return@Button
                    }

                    isLoading = true
                    onLoginSubmit(nameInput, "+91$digits", passwordInput) { _ ->
                        isLoading = false
                    }
                },
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_customer_login_proceed")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color(0xFF0F172A),
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = StringRes.continuePreOrderBtn.tr(language),
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(18.dp)
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
                        .testTag("btn_preorder_otp_login")
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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
