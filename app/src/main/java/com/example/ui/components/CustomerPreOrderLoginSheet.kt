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
    onQuickDemoCustomer: () -> Unit
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
                    phoneInput = it
                    errorMessage = null
                },
                label = { Text(StringRes.phoneNumber.tr(language)) },
                placeholder = { Text("017XXXXXXXX") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = AmberPrimary)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

            // Error display
            AnimatedVisibility(visible = errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = AccentRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = AccentRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Login / Register & Proceed button
            Button(
                onClick = {
                    if (nameInput.isBlank()) {
                        errorMessage = when (language) {
                            AppLanguage.ENGLISH -> "Please enter your full name"
                            AppLanguage.HINDI -> "कृपया अपना पूरा नाम दर्ज करें"
                            AppLanguage.BENGALI -> "দয়া করে আপনার সম্পূর্ণ নাম লিখুন"
                        }
                        return@Button
                    }
                    if (phoneInput.isBlank() || phoneInput.length < 8) {
                        errorMessage = when (language) {
                            AppLanguage.ENGLISH -> "Please enter a valid mobile number (e.g., 9876543210)"
                            AppLanguage.HINDI -> "कृपया मान्य मोबाइल नंबर दर्ज करें (उदा. 9876543210)"
                            AppLanguage.BENGALI -> "দয়া করে সঠিক মোবাইল নম্বর লিখুন (যেমন: 9876543210)"
                        }
                        return@Button
                    }
                    if (passwordInput.isBlank() || passwordInput.length < 3) {
                        errorMessage = when (language) {
                            AppLanguage.ENGLISH -> "Password must be at least 3 characters"
                            AppLanguage.HINDI -> "पासवर्ड कम से कम 3 अक्षरों का होना चाहिए"
                            AppLanguage.BENGALI -> "পাসওয়ার্ড অন্তত ৩ অক্ষরের হতে হবে"
                        }
                        return@Button
                    }

                    isLoading = true
                    onLoginSubmit(nameInput, phoneInput, passwordInput) { err ->
                        isLoading = false
                        errorMessage = err
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

            Spacer(modifier = Modifier.height(14.dp))

            // Fast Demo Login option
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (language) {
                                AppLanguage.ENGLISH -> "Want to test?"
                                AppLanguage.HINDI -> "परीक्षण करना चाहते हैं?"
                                AppLanguage.BENGALI -> "টেস্ট করতে চান?"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Text(
                            text = when (language) {
                                AppLanguage.ENGLISH -> "Try 1-click Demo Customer Login"
                                AppLanguage.HINDI -> "1-क्लिक डेमो ग्राहक लॉगिन आज़माएं"
                                AppLanguage.BENGALI -> "১-ক্লিকে ডেমো কাস্টমার দিয়ে ট্রাই করুন"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            onQuickDemoCustomer()
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_quick_demo_customer")
                    ) {
                        Text(
                            text = when (language) {
                                AppLanguage.ENGLISH -> "⚡ Demo Login"
                                AppLanguage.HINDI -> "⚡ डेमो लॉगिन"
                                AppLanguage.BENGALI -> "⚡ ডেমো লগইন"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
