package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BookingEntity
import com.example.data.model.ServiceItemEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringRes
import com.example.ui.localization.tr
import com.example.ui.theme.*
import com.example.ui.viewmodel.SalonViewModel

@Composable
fun BarberScreen(
    viewModel: SalonViewModel,
    onBackToCustomer: () -> Unit = {},
    onOpenCreatorHub: () -> Unit = {},
    onOpenProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    val language by viewModel.currentLanguage.collectAsState()
    val allBookings by viewModel.allBookings.collectAsState()
    val services by viewModel.services.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val currentSalonLicense by viewModel.currentSalonLicense.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isSyncingSupabase by viewModel.isSyncingSupabase.collectAsState()
    val todayDate = viewModel.todayDateString

    // Gate Salon Owner Dashboard behind the Salon Owner Login Screen
    if (currentSalonLicense == null || currentUser?.role != "BARBER") {
        SalonOwnerLoginScreen(
            viewModel = viewModel,
            onBackToCustomer = onBackToCustomer,
            onOpenCreatorHub = onOpenCreatorHub,
            onOpenProfile = onOpenProfile
        )
        return
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var editingService by remember { mutableStateOf<ServiceItemEntity?>(null) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    // Analytics calculations
    val totalAdvanceCollected = remember(allBookings) {
        allBookings.filter { it.status != "CANCELLED" }.sumOf { it.advancePaid }
    }
    val totalRemainingDue = remember(allBookings) {
        allBookings.filter { it.status != "CANCELLED" && it.status != "COMPLETED" }.sumOf { it.remainingDue }
    }
    val todayAppointmentsCount = remember(allBookings, todayDate) {
        allBookings.count { it.bookingDate == todayDate && it.status != "CANCELLED" }
    }
    val pendingCount = remember(allBookings) {
        allBookings.count { it.status == "PENDING" }
    }

    val filteredBookings = remember(allBookings, selectedFilter, todayDate) {
        when (selectedFilter) {
            "TODAY" -> allBookings.filter { it.bookingDate == todayDate }
            "PENDING" -> allBookings.filter { it.status == "PENDING" }
            "CONFIRMED" -> allBookings.filter { it.status == "CONFIRMED" }
            "IN_PROGRESS" -> allBookings.filter { it.status == "IN_PROGRESS" }
            "COMPLETED" -> allBookings.filter { it.status == "COMPLETED" }
            else -> allBookings
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Salon Verified License Header Banner
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onOpenProfile)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(AccentBronze, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(
                            text = currentSalonLicense?.salonName ?: "Master Barber Portal",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = "ID: ${currentSalonLicense?.salonId ?: "SALON-101"} • ${currentSalonLicense?.ownerName ?: "Owner"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
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

                    // Supabase Cloud Sync Button
                    IconButton(
                        onClick = { viewModel.syncNowToSupabase() },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_barber_sync_supabase")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Sync with Supabase Cloud",
                            tint = Color(0xFF3ECF8E),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Supabase Cloud Sync Quick Button
                    IconButton(
                        onClick = { viewModel.syncNowToSupabase() },
                        enabled = !isSyncingSupabase,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_barber_supabase_sync")
                    ) {
                        if (isSyncingSupabase) {
                            CircularProgressIndicator(
                                color = Color(0xFF3ECF8E),
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Sync with Supabase Cloud",
                                tint = Color(0xFF3ECF8E),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Small Profile Icon in top right corner of banner
                    IconButton(
                        onClick = onOpenProfile,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_barber_top_profile")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Salon Owner Profile Details",
                            tint = AccentBronze,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Salon Owner Logout button
                    IconButton(
                        onClick = { showLogoutConfirmDialog = true },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_barber_logout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout Salon Owner",
                            tint = AccentRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Tab Row
        if (showLogoutConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirmDialog = false },
                title = { Text("Confirm Log Out", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to log out from your Salon Owner account?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutConfirmDialog = false
                            viewModel.logoutSalonOwner()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                    ) {
                        Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showLogoutConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = AccentBronze
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        val ordersLabel = if (language == AppLanguage.ENGLISH) "Orders"
                        else if (language == AppLanguage.HINDI) "आदेश"
                        else "অর্ডার"
                        Text(
                            text = "$ordersLabel (${allBookings.size})",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                modifier = Modifier.testTag("barber_tab_orders")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CurrencyRupee, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        val title = StringRes.managePrices.tr(language)
                        Text(
                            text = if (title.length > 12) title.take(12) + "..." else title,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                modifier = Modifier.testTag("barber_tab_pricing")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.StarRate, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${StringRes.reviewsTab.tr(language)} (${reviews.size})",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                modifier = Modifier.testTag("barber_tab_reviews")
            )
        }

        when (selectedTab) {
            0 -> BarberOrdersContent(
                bookings = filteredBookings,
                selectedFilter = selectedFilter,
                totalAdvance = totalAdvanceCollected,
                totalDue = totalRemainingDue,
                todayCount = todayAppointmentsCount,
                pendingCount = pendingCount,
                language = language,
                onFilterChange = { selectedFilter = it },
                onConfirmClick = { booking ->
                    viewModel.updateBookingStatus(booking.id, "CONFIRMED", booking.customerPhone)
                },
                onStartClick = { booking ->
                    viewModel.updateBookingStatus(booking.id, "IN_PROGRESS", booking.customerPhone)
                },
                onCompleteClick = { booking ->
                    viewModel.updateBookingStatus(booking.id, "COMPLETED", booking.customerPhone)
                },
                onCancelClick = { booking ->
                    viewModel.updateBookingStatus(booking.id, "CANCELLED", booking.customerPhone)
                },
                onCallCustomer = { phone ->
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                    context.startActivity(intent)
                }
            )

            1 -> BarberPricingContent(
                services = services,
                language = language,
                onEditPrice = { service -> editingService = service },
                onToggleAvailability = { service ->
                    viewModel.updateServicePrice(service.id, service.price)
                }
            )

            2 -> CustomerReviewsContent(
                reviews = reviews,
                language = language
            )
        }
    }

    // Edit Price Dialog
    editingService?.let { service ->
        EditPriceDialog(
            service = service,
            language = language,
            onDismiss = { editingService = null },
            onSave = { newPrice ->
                viewModel.updateServicePrice(service.id, newPrice)
                editingService = null
            }
        )
    }
}

@Composable
fun BarberOrdersContent(
    bookings: List<BookingEntity>,
    selectedFilter: String,
    totalAdvance: Double,
    totalDue: Double,
    todayCount: Int,
    pendingCount: Int,
    language: AppLanguage,
    onFilterChange: (String) -> Unit,
    onConfirmClick: (BookingEntity) -> Unit,
    onStartClick: (BookingEntity) -> Unit,
    onCompleteClick: (BookingEntity) -> Unit,
    onCancelClick: (BookingEntity) -> Unit,
    onCallCustomer: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Business Metrics Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null,
                                tint = AccentGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val advCollectedLabel = if (language == AppLanguage.ENGLISH) "Advance (55%)"
                            else if (language == AppLanguage.HINDI) "अग्रिम जमा (५५%)"
                            else "অগ্রিম জমা (৫৫%)"
                            Text(
                                text = advCollectedLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "₹${totalAdvance.toInt()}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = AccentGreen
                            )
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = AmberPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val dueLabel = if (language == AppLanguage.ENGLISH) "Remaining Due"
                            else if (language == AppLanguage.HINDI) "बकाया राशि"
                            else "বাকি পাওনা"
                            Text(
                                text = dueLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "₹${totalDue.toInt()}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = AmberPrimary
                            )
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = AccentBronze,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = StringRes.todayBookings.tr(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$todayCount ${StringRes.personsUnit.tr(language)}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = AccentBronze
                            )
                        )
                    }
                }
            }
        }

        // Filter chips row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "ALL" to StringRes.filterAll.tr(language),
                    "TODAY" to StringRes.filterToday.tr(language),
                    "CONFIRMED" to StringRes.statusConfirmed.tr(language),
                    "IN_PROGRESS" to StringRes.statusInProgress.tr(language),
                    "COMPLETED" to StringRes.statusCompleted.tr(language)
                ).forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterChange(key) },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentBronze,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // List of booking cards
        if (bookings.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = StringRes.noBookingsInCategory.tr(language),
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(bookings) { booking ->
                BookingCard(
                    booking = booking,
                    language = language,
                    isBarberView = true,
                    onConfirmClick = { onConfirmClick(booking) },
                    onStartClick = { onStartClick(booking) },
                    onCompleteClick = { onCompleteClick(booking) },
                    onCancelClick = { onCancelClick(booking) },
                    onCallCustomer = { onCallCustomer(booking.customerPhone) }
                )
            }
        }
    }
}

@Composable
fun BarberPricingContent(
    services: List<ServiceItemEntity>,
    language: AppLanguage,
    onEditPrice: (ServiceItemEntity) -> Unit,
    onToggleAvailability: (ServiceItemEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        tint = AccentBronze,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = StringRes.ratesControlTitle.tr(language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = StringRes.ratesControlSubtitle.tr(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        items(services) { service ->
            val name = when (language) {
                AppLanguage.ENGLISH -> service.nameEn
                AppLanguage.HINDI -> service.nameHi
                AppLanguage.BENGALI -> service.nameBn
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val priceDesc = if (language == AppLanguage.ENGLISH) "Price: ₹${service.price.toInt()} | Pre-order Advance (55%): ₹${(service.price * 0.55).toInt()}"
                        else if (language == AppLanguage.HINDI) "मूल्य: ₹${service.price.toInt()} | अग्रिम (५५%): ₹${(service.price * 0.55).toInt()}"
                        else "বর্তমান সেবামূল্য: ₹${service.price.toInt()} | প্রি-অর্ডার অগ্রিম (৫৫%): ₹${(service.price * 0.55).toInt()}"
                        Text(
                            text = priceDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = AmberPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onEditPrice(service) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBronze),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = StringRes.editRateBtn.tr(language), fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditPriceDialog(
    service: ServiceItemEntity,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var priceInput by remember { mutableStateOf(service.price.toInt().toString()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .testTag("dialog_edit_price")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = StringRes.editPriceDialog.tr(language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                val localizedServiceName = when (language) {
                    AppLanguage.ENGLISH -> service.nameEn
                    AppLanguage.HINDI -> service.nameHi
                    AppLanguage.BENGALI -> service.nameBn
                }
                Text(
                    text = localizedServiceName,
                    style = MaterialTheme.typography.bodySmall,
                    color = AmberPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text(StringRes.newPriceLabel.tr(language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(StringRes.cancel.tr(language))
                    }

                    Button(
                        onClick = {
                            val parsed = priceInput.toDoubleOrNull() ?: service.price
                            onSave(parsed)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBronze)
                    ) {
                        Text(StringRes.savePrice.tr(language), color = Color.White)
                    }
                }
            }
        }
    }
}
