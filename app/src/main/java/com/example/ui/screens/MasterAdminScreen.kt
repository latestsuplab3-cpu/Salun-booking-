package com.example.ui.screens

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
import com.example.ui.viewmodel.SalonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterAdminScreen(
    viewModel: SalonViewModel,
    onLogoutToSelector: () -> Unit,
    onLoginAsSalon: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsState()
    val licenses by viewModel.salonLicenses.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    var showNewLicenseForm by remember { mutableStateOf(false) }
    var salonNameInput by remember { mutableStateOf("") }
    var ownerNameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var addressInput by remember { mutableStateOf("") }
    var customIdInput by remember { mutableStateOf("") }

    var formError by remember { mutableStateOf<String?>(null) }
    var newlyCreatedId by remember { mutableStateOf<String?>(null) }
    var copiedFeedback by remember { mutableStateOf<String?>(null) }

    val masterPurple = Color(0xFF8B5CF6)
    val isSyncingSupabase by viewModel.isSyncingSupabase.collectAsState()
    val supabaseSyncStatus by viewModel.lastSupabaseSyncStatus.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_master_admin"),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(listOf(masterPurple, Color(0xFF6366F1)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Master Admin Hub",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "App Creator Platform Control",
                                style = MaterialTheme.typography.labelSmall,
                                color = masterPurple
                            )
                        }
                    }

                    // Logout & Back to Login Selector Button
                    Button(
                        onClick = onLogoutToSelector,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_master_logout")
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = AccentRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Exit Portal",
                            color = AccentRed,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stats Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, masterPurple.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${licenses.size}",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = masterPurple
                            )
                            Text(
                                text = "Total Salons",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Divider(
                            modifier = Modifier
                                .height(36.dp)
                                .width(1.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${licenses.count { it.isActive }}",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = AccentGreen
                            )
                            Text(
                                text = "Active Licenses",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // Supabase Cloud Live Sync Control
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, Color(0xFF3ECF8E).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().testTag("card_supabase_cloud_sync")
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF3ECF8E).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudSync,
                                        contentDescription = null,
                                        tint = Color(0xFF3ECF8E),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Supabase Cloud Database",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Project: llhyjuqthwsdmauvucqc",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF3ECF8E)
                                    )
                                }
                            }

                            Button(
                                onClick = { viewModel.syncNowToSupabase() },
                                enabled = !isSyncingSupabase,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3ECF8E)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("btn_supabase_sync_now")
                            ) {
                                if (isSyncingSupabase) {
                                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Syncing...", color = Color.White, style = MaterialTheme.typography.labelSmall)
                                } else {
                                    Icon(Icons.Default.Sync, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sync Now", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }

                        if (supabaseSyncStatus != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = supabaseSyncStatus ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Action: Issue New License Button
            item {
                Button(
                    onClick = {
                        showNewLicenseForm = !showNewLicenseForm
                        formError = null
                        newlyCreatedId = null
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = masterPurple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_creator_toggle_issue_form")
                ) {
                    Icon(
                        imageVector = if (showNewLicenseForm) Icons.Default.ExpandLess else Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showNewLicenseForm) "Hide Registration Form" else "+ Issue New Salon License & ID",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // New License Form
            if (showNewLicenseForm) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, masterPurple.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Generate New Salon License",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = salonNameInput,
                                onValueChange = { salonNameInput = it },
                                label = { Text("Salon / Shop Name *") },
                                placeholder = { Text("e.g. Royal Gents Salon") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_creator_salon_name")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = ownerNameInput,
                                onValueChange = { ownerNameInput = it },
                                label = { Text("Owner Full Name *") },
                                placeholder = { Text("e.g. Kamal Hossain") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_creator_owner_name")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = phoneInput,
                                    onValueChange = { phoneInput = it },
                                    label = { Text("Owner Phone *") },
                                    placeholder = { Text("9876543210") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_creator_owner_phone")
                                )

                                OutlinedTextField(
                                    value = passwordInput,
                                    onValueChange = { passwordInput = it },
                                    label = { Text("Password *") },
                                    placeholder = { Text("123") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_creator_owner_pass")
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = customIdInput,
                                    onValueChange = { customIdInput = it.uppercase() },
                                    label = { Text("Custom ID (Optional)") },
                                    placeholder = { Text("SALON-XXX") },
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
                                Spacer(modifier = Modifier.height(8.dp))
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
                                            text = "Success! Issued Salon ID: $newId",
                                            color = AccentGreen,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val sName = salonNameInput.trim()
                                    val oName = ownerNameInput.trim()
                                    val ph = phoneInput.trim()
                                    val ps = passwordInput.trim()
                                    val addr = addressInput.trim()
                                    val cid = customIdInput.trim().ifEmpty { null }

                                    if (sName.isEmpty() || oName.isEmpty() || ph.isEmpty() || ps.isEmpty()) {
                                        formError = "Please fill in all required (*) fields"
                                        return@Button
                                    }

                                    viewModel.issueSalonLicense(
                                        salonName = sName,
                                        ownerName = oName,
                                        phone = ph,
                                        pass = ps,
                                        address = addr,
                                        customId = cid,
                                        onSuccess = { genId ->
                                            newlyCreatedId = genId
                                            formError = null
                                            salonNameInput = ""
                                            ownerNameInput = ""
                                            phoneInput = ""
                                            passwordInput = ""
                                            addressInput = ""
                                            customIdInput = ""
                                        },
                                        onError = { err -> formError = err }
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = masterPurple),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .testTag("btn_creator_submit_license")
                            ) {
                                Text("Issue License & Save", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            copiedFeedback?.let { msg ->
                item {
                    Surface(
                        color = masterPurple.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg,
                            color = masterPurple,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Authorized Salon Licenses (${licenses.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(licenses, key = { it.salonId }) { lic ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
                        1.dp,
                        if (lic.isActive) masterPurple.copy(alpha = 0.35f) else AccentRed.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = masterPurple.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = lic.salonId,
                                    color = masterPurple,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = lic.isActive,
                                    onCheckedChange = {
                                        viewModel.toggleSalonLicenseStatus(lic.salonId, !lic.isActive)
                                    },
                                    modifier = Modifier.height(24.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (lic.isActive) "ACTIVE" else "SUSPENDED",
                                    color = if (lic.isActive) AccentGreen else AccentRed,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = lic.salonName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Owner: ${lic.ownerName} • 📞 ${lic.ownerPhone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Text(
                            text = "Password: ${lic.password} • Address: ${lic.address}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(
                                        AnnotatedString("Salon ID: ${lic.salonId} | Phone: ${lic.ownerPhone} | Pass: ${lic.password}")
                                    )
                                    copiedFeedback = "Copied credentials for ${lic.salonName}!"
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy Credentials", fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    viewModel.loginSalonOwner(
                                        salonId = lic.salonId,
                                        phone = lic.ownerPhone,
                                        pass = lic.password,
                                        onSuccess = { onLoginAsSalon() },
                                        onError = { }
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentBronze),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Open Salon", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
