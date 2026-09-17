package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SalonLicenseEntity
import com.example.data.model.UserEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringRes
import com.example.ui.localization.tr
import com.example.ui.theme.*

@Composable
fun AuthDialog(
    currentUser: UserEntity?,
    currentSalonLicense: SalonLicenseEntity?,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onCustomerLogin: (name: String, phone: String, pass: String, onError: (String) -> Unit) -> Unit,
    onSalonLogin: (salonId: String, phone: String, pass: String, onError: (String) -> Unit) -> Unit,
    onOpenCreatorHub: () -> Unit,
    onQuickSwitchRole: (String) -> Unit,
    onLogout: () -> Unit
) {
    // Tab selection: 0 for Customer Panel, 1 for Salon Owner Panel
    var selectedTab by remember { mutableStateOf(if (currentUser?.role == "BARBER") 1 else 0) }

    // Customer fields
    var customerNameInput by remember { mutableStateOf("") }
    var customerPhoneInput by remember { mutableStateOf("") }
    var customerPasswordInput by remember { mutableStateOf("") }

    // Salon Owner fields
    var salonIdInput by remember { mutableStateOf("") }
    var salonPhoneInput by remember { mutableStateOf("") }
    var salonPasswordInput by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .testTag("dialog_auth")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentUser != null) "Account & Profile" else "Access Portal",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (currentUser != null) {
                    // Logged-in State Card
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(
                                            if (currentUser.role == "BARBER") AccentBronze else AmberPrimary,
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (currentUser.role == "BARBER") Icons.Default.ContentCut else Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.White
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
                                        text = if (currentUser.role == "BARBER") "✂ Salon Owner / Agent" else "👤 Verified Customer",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (currentUser.role == "BARBER") AccentBronze else AmberPrimary
                                    )
                                    Text(
                                        text = "📱 ${currentUser.phone}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }

                            if (currentSalonLicense != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = currentSalonLicense.salonName,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "ID: ${currentSalonLicense.salonId} • ${currentSalonLicense.address}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                    Surface(
                                        color = AccentGreen.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "VERIFIED",
                                            color = AccentGreen,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Log Out Button
                    Button(
                        onClick = {
                            onLogout()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_logout")
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

                    // Switch to different account
                    OutlinedButton(
                        onClick = {
                            onLogout()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Switch to Another Login Panel", style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    // Two Distinct Login Panels Tabs: Customer vs Salon Owner
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = {
                                selectedTab = 0
                                errorMessage = null
                            },
                            text = {
                                Text(
                                    StringRes.customerLoginTab.tr(language),
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == 0) AmberPrimary else TextSecondary
                                )
                            },
                            icon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = if (selectedTab == 0) AmberPrimary else TextSecondary)
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = {
                                selectedTab = 1
                                errorMessage = null
                            },
                            text = {
                                Text(
                                    StringRes.salonOwnerLoginTab.tr(language),
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == 1) AccentBronze else TextSecondary
                                )
                            },
                            icon = {
                                Icon(Icons.Default.ContentCut, contentDescription = null, tint = if (selectedTab == 1) AccentBronze else TextSecondary)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // PANEL 1: CUSTOMER LOGIN
                    if (selectedTab == 0) {
                        Text(
                            text = "Customer Self-Service Login",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customerNameInput,
                            onValueChange = { customerNameInput = it },
                            label = { Text(StringRes.fullName.tr(language)) },
                            placeholder = { Text(StringRes.namePlaceholder.tr(language)) },
                            leadingIcon = {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = AmberPrimary)
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_customer_name"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = customerPhoneInput,
                            onValueChange = { customerPhoneInput = it },
                            label = { Text(StringRes.phoneNumber.tr(language)) },
                            placeholder = { Text(StringRes.phonePlaceholder.tr(language)) },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = AmberPrimary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_customer_phone"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = customerPasswordInput,
                            onValueChange = { customerPasswordInput = it },
                            label = { Text(StringRes.password.tr(language)) },
                            placeholder = { Text(StringRes.passwordPlaceholder.tr(language)) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = AmberPrimary)
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_customer_pass"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        errorMessage?.let { err ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = err, color = AccentRed, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (customerPhoneInput.isBlank() || customerPasswordInput.isBlank()) {
                                    errorMessage = "Please enter both mobile phone and password"
                                } else {
                                    onCustomerLogin(customerNameInput, customerPhoneInput, customerPasswordInput) { err ->
                                        errorMessage = err
                                    }
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_customer_login_submit")
                        ) {
                            Text(
                                text = StringRes.loginButton.tr(language),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Demo Customer
                        OutlinedButton(
                            onClick = {
                                onQuickSwitchRole("CUSTOMER")
                                onDismiss()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("btn_quick_customer_demo")
                        ) {
                            Text("⚡ Quick Demo Customer Login", color = AmberPrimary, fontSize = 12.sp)
                        }
                    }

                    // PANEL 2: SALON OWNER LOGIN
                    if (selectedTab == 1) {
                        Surface(
                            color = AccentBronze.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = AccentBronze, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = StringRes.salonOwnerLoginNotice.tr(language),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Salon ID Input
                        OutlinedTextField(
                            value = salonIdInput,
                            onValueChange = { salonIdInput = it.uppercase() },
                            label = { Text(StringRes.salonIdLabel.tr(language)) },
                            placeholder = { Text(StringRes.salonIdPlaceholder.tr(language)) },
                            leadingIcon = {
                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = AccentBronze)
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_salon_id"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Registered Phone Input
                        OutlinedTextField(
                            value = salonPhoneInput,
                            onValueChange = { salonPhoneInput = it },
                            label = { Text(StringRes.phoneNumber.tr(language)) },
                            placeholder = { Text("017XXXXXXXX") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = AccentBronze)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_salon_owner_phone"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Password Input
                        OutlinedTextField(
                            value = salonPasswordInput,
                            onValueChange = { salonPasswordInput = it },
                            label = { Text(StringRes.password.tr(language)) },
                            placeholder = { Text(StringRes.passwordPlaceholder.tr(language)) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = AccentBronze)
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_salon_owner_pass"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        errorMessage?.let { err ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = err, color = AccentRed, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (salonIdInput.isBlank() || salonPhoneInput.isBlank() || salonPasswordInput.isBlank()) {
                                    errorMessage = "Please enter Salon ID, Phone and Password"
                                } else {
                                    onSalonLogin(salonIdInput, salonPhoneInput, salonPasswordInput) { err ->
                                        errorMessage = err
                                    }
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBronze),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_salon_login_submit")
                        ) {
                            Icon(Icons.Default.Login, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = StringRes.loginSalonBtn.tr(language),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Demo Credentials Chip / Quick Fill
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SuggestionChip(
                                onClick = {
                                    salonIdInput = "SALON-101"
                                    salonPhoneInput = "01700000000"
                                    salonPasswordInput = "123"
                                    errorMessage = null
                                },
                                label = { Text("Demo: SALON-101 (123)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            SuggestionChip(
                                onClick = {
                                    salonIdInput = "SALON-202"
                                    salonPhoneInput = "9876543210"
                                    salonPasswordInput = "pass"
                                    errorMessage = null
                                },
                                label = { Text("Demo: SALON-202 (pass)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // App Creator Panel Hub button (for the person who creates app and provides IDs)
                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                onOpenCreatorHub()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberPrimary),
                            modifier = Modifier.fillMaxWidth().testTag("btn_open_creator_hub_from_auth")
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("👑 App Creator Hub: Issue New IDs", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
