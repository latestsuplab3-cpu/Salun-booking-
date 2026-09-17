package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SalonLicenseEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringRes
import com.example.ui.localization.tr
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCreatorPanelSheet(
    licenses: List<SalonLicenseEntity>,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onIssueLicense: (
        salonName: String,
        ownerName: String,
        phone: String,
        pass: String,
        address: String,
        customId: String?,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) -> Unit,
    onToggleStatus: (salonId: String, currentActive: Boolean) -> Unit,
    onQuickLoginSalon: (salonId: String, phone: String, pass: String) -> Unit
) {
    var showNewLicenseForm by remember { mutableStateOf(false) }

    var salonNameInput by remember { mutableStateOf("") }
    var ownerNameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var addressInput by remember { mutableStateOf("") }
    var customIdInput by remember { mutableStateOf("") }

    var formError by remember { mutableStateOf<String?>(null) }
    var newlyCreatedId by remember { mutableStateOf<String?>(null) }

    val clipboardManager = LocalClipboardManager.current
    var copiedNotice by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
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
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF7C3AED), Color(0xFF4F46E5))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column {
                        Text(
                            text = StringRes.appCreatorHub.tr(language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = StringRes.appCreatorSubtitle.tr(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Total Salons", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text(
                            text = "${licenses.size}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = AmberPrimary
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Active Licenses", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text(
                            text = "${licenses.count { it.isActive }}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = AccentGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action: Issue New Salon ID Button / Form Toggle
            OutlinedButton(
                onClick = {
                    showNewLicenseForm = !showNewLicenseForm
                    formError = null
                    newlyCreatedId = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_toggle_issue_license"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (showNewLicenseForm) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent
                )
            ) {
                Icon(
                    imageVector = if (showNewLicenseForm) Icons.Default.ExpandLess else Icons.Default.AddBusiness,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = AmberPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (showNewLicenseForm) "Hide Registration Form" else "+ ${StringRes.issueNewSalonId.tr(language)}",
                    fontWeight = FontWeight.Bold,
                    color = AmberPrimary
                )
            }

            // Expandable New License Form
            if (showNewLicenseForm) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "New Salon Partner Registration",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = salonNameInput,
                            onValueChange = { salonNameInput = it },
                            label = { Text("Salon / Shop Name *") },
                            placeholder = { Text("e.g. Royal Hair Studio") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_creator_salon_name")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = ownerNameInput,
                            onValueChange = { ownerNameInput = it },
                            label = { Text("Owner Full Name *") },
                            placeholder = { Text("e.g. Rajesh Kumar") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_creator_owner_name")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
                                label = { Text("Owner Phone *") },
                                placeholder = { Text("017XXXXXXXX") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("input_creator_owner_phone")
                            )

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Password *") },
                                placeholder = { Text("pass123") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("input_creator_owner_pass")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = customIdInput,
                                onValueChange = { customIdInput = it.uppercase() },
                                label = { Text("Custom ID (Optional)") },
                                placeholder = { Text("SALON-XXXX") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = addressInput,
                                onValueChange = { addressInput = it },
                                label = { Text("Address / City") },
                                placeholder = { Text("Market Square") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        formError?.let { err ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = err, color = AccentRed, style = MaterialTheme.typography.bodySmall)
                        }

                        newlyCreatedId?.let { newId ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = AccentGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Success! Generated Salon ID: $newId. Give this ID to the Salon Owner.",
                                        color = AccentGreen,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                onIssueLicense(
                                    salonNameInput,
                                    ownerNameInput,
                                    phoneInput,
                                    passwordInput,
                                    addressInput,
                                    if (customIdInput.isNotBlank()) customIdInput else null,
                                    { createdId ->
                                        newlyCreatedId = createdId
                                        formError = null
                                        salonNameInput = ""
                                        ownerNameInput = ""
                                        phoneInput = ""
                                        passwordInput = ""
                                        addressInput = ""
                                        customIdInput = ""
                                    },
                                    { err ->
                                        formError = err
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                            modifier = Modifier.fillMaxWidth().testTag("btn_submit_issue_license"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Create & Issue Salon ID", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Authorized Salons List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = StringRes.activeLicensesList.tr(language),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                copiedNotice?.let {
                    Text(it, color = AccentGreen, style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // List of Issued Licenses
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(licenses, key = { it.salonId }) { license ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (license.isActive) MaterialTheme.colorScheme.surfaceVariant
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (license.isActive) AmberPrimary.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.2f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Salon ID Badge
                                Surface(
                                    color = AmberPrimary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                            .clickable {
                                                clipboardManager.setText(AnnotatedString(license.salonId))
                                                copiedNotice = "Copied ${license.salonId}!"
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = AmberPrimary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = license.salonId,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = AmberPrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = AmberPrimary, modifier = Modifier.size(12.dp))
                                    }
                                }

                                // Status chip
                                Surface(
                                    color = if (license.isActive) AccentGreen.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (license.isActive) "ACTIVE" else "INACTIVE",
                                        color = if (license.isActive) AccentGreen else Color.Gray,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = license.salonName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "📍 ${license.address}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Divider(color = Color.Gray.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(6.dp))

                            // Credentials summary
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Owner: ${license.ownerName}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Phone: ${license.ownerPhone} | Pass: ${license.password}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }

                                // Quick Login button for immediate testing
                                Button(
                                    onClick = {
                                        onQuickLoginSalon(license.salonId, license.ownerPhone, license.password)
                                        onDismiss()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (license.isActive) AccentBronze else Color.Gray
                                    ),
                                    enabled = license.isActive,
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Test Login", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
