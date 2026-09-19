package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.ServiceItemEntity
import com.example.data.model.UserEntity
import com.example.ui.components.CustomerPreOrderLoginSheet
import com.example.ui.components.FirebaseOtpDialog
import com.example.ui.components.PreOrderSheet
import com.example.ui.components.ReviewDialog
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringRes
import com.example.ui.localization.tr
import com.example.ui.theme.*
import com.example.ui.viewmodel.SalonViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CustomerScreen(
    viewModel: SalonViewModel,
    onRequireLogin: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsState()
    val services by viewModel.services.collectAsState()
    val allBookings by viewModel.allBookings.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isSyncingSupabase by viewModel.isSyncingSupabase.collectAsState()
    val selectedServices by viewModel.selectedServices.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedSlot by viewModel.selectedSlot.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showPreOrderSheet by remember { mutableStateOf(false) }
    var showPreOrderLoginSheet by remember { mutableStateOf(false) }
    var showFirebaseOtpSheet by remember { mutableStateOf(false) }
    var reviewBookingTarget by remember { mutableStateOf<BookingEntity?>(null) }

    // Filter customer's own bookings
    val customerPhone = currentUser?.phone ?: ""
    val customerBookings = remember(allBookings, customerPhone) {
        if (customerPhone.isBlank()) emptyList()
        else allBookings.filter { it.customerPhone == customerPhone }
    }

    val (totalPrice, advanceToPay, remainingDue) = viewModel.calculateBookingSummary()

    Scaffold(
        bottomBar = {
            // Sticky Cart / Pre-Order Action Bar when services are selected
            AnimatedVisibility(
                visible = selectedServices.isNotEmpty() && selectedTab == 0,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 16.dp,
                    border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            val itemsLabel = if (language == AppLanguage.ENGLISH) "${selectedServices.size} Services | Total: ₹${totalPrice.toInt()}"
                            else if (language == AppLanguage.HINDI) "${selectedServices.size} सेवाएँ | कुल: ₹${totalPrice.toInt()}"
                            else "${selectedServices.size} টি সেবা | মোট: ₹${totalPrice.toInt()}"
                            Text(
                                text = itemsLabel,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val advLabel = if (language == AppLanguage.ENGLISH) "55% Advance: ₹${advanceToPay.toInt()}"
                            else if (language == AppLanguage.HINDI) "५५% अग्रिम: ₹${advanceToPay.toInt()}"
                            else "৫৫% অগ্রিম পে: ₹${advanceToPay.toInt()}"
                            Text(
                                text = advLabel,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AmberPrimary
                                )
                            )
                        }

                        Button(
                            onClick = {
                                if (currentUser == null) {
                                    showPreOrderLoginSheet = true
                                } else {
                                    showPreOrderSheet = true
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                            modifier = Modifier.testTag("btn_proceed_preorder")
                        ) {
                            Text(
                                text = StringRes.preOrderBookingBtn.tr(language),
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs Bar: Services, My Bookings, Reviews
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = AmberPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            val title = StringRes.servicesTitle.tr(language)
                            Text(
                                text = if (title.length > 12) title.take(12) + "..." else title,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    },
                    modifier = Modifier.testTag("tab_services")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${StringRes.myBookings.tr(language)} (${customerBookings.size})",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    },
                    modifier = Modifier.testTag("tab_my_bookings")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = StringRes.reviewsTab.tr(language),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    },
                    modifier = Modifier.testTag("tab_reviews")
                )
            }

            when (selectedTab) {
                0 -> ServicesListContent(
                    services = services,
                    selectedServices = selectedServices,
                    language = language,
                    onToggleService = { viewModel.toggleService(it) },
                    onDirectBook = { service ->
                        viewModel.toggleService(service)
                        if (currentUser == null) {
                            showPreOrderLoginSheet = true
                        } else {
                            showPreOrderSheet = true
                        }
                    }
                )
                1 -> CustomerBookingsContent(
                    bookings = customerBookings,
                    currentUser = currentUser,
                    language = language,
                    isSyncing = isSyncingSupabase,
                    onSyncSupabase = { viewModel.syncNowToSupabase() },
                    onRequireLogin = { showPreOrderLoginSheet = true },
                    onReviewClick = { booking -> reviewBookingTarget = booking }
                )
                2 -> CustomerReviewsContent(
                    reviews = reviews,
                    language = language
                )
            }
        }
    }

    // Customer Pre-Order Login Sheet (Requires Name, Phone Number, Password)
    if (showPreOrderLoginSheet) {
        CustomerPreOrderLoginSheet(
            selectedServices = selectedServices,
            totalPrice = totalPrice,
            advanceToPay = advanceToPay,
            remainingDue = remainingDue,
            language = language,
            onDismiss = { showPreOrderLoginSheet = false },
            onLoginSuccess = {
                showPreOrderLoginSheet = false
                showPreOrderSheet = true
            },
            onLoginSubmit = { name, phone, pass, onError ->
                viewModel.loginOrRegisterCustomer(
                    name = name,
                    phone = phone,
                    pass = pass,
                    onSuccess = {
                        showPreOrderLoginSheet = false
                        showPreOrderSheet = true
                    },
                    onError = onError
                )
            },
            onQuickDemoCustomer = {
                viewModel.quickSwitchUser("CUSTOMER")
                showPreOrderLoginSheet = false
                showPreOrderSheet = true
            },
            onOpenOtpLogin = {
                showPreOrderLoginSheet = false
                showFirebaseOtpSheet = true
            }
        )
    }

    if (showFirebaseOtpSheet) {
        FirebaseOtpDialog(
            targetRole = "CUSTOMER",
            language = language,
            viewModel = viewModel,
            onDismiss = { showFirebaseOtpSheet = false },
            onLoginSuccess = {
                showFirebaseOtpSheet = false
                showPreOrderSheet = true
            }
        )
    }

    // Pre-Order Modal Sheet
    if (showPreOrderSheet && selectedServices.isNotEmpty()) {
        PreOrderSheet(
            selectedServices = selectedServices,
            selectedDate = selectedDate,
            selectedSlot = selectedSlot,
            slots = viewModel.standardSlots,
            language = language,
            isSlotBooked = { date, slot -> viewModel.isSlotBooked(date, slot) },
            onDateChange = { viewModel.setDate(it) },
            onSlotChange = { viewModel.setSlot(it) },
            onDismiss = { showPreOrderSheet = false },
            onConfirmBooking = {
                viewModel.submitPreOrderBooking(
                    onSuccess = {
                        showPreOrderSheet = false
                        selectedTab = 1 // Switch to My Bookings tab
                    },
                    onError = { /* handled via feedback */ }
                )
            }
        )
    }

    // Review Dialog
    reviewBookingTarget?.let { booking ->
        ReviewDialog(
            bookingId = booking.id,
            serviceName = booking.serviceNames,
            language = language,
            onDismiss = { reviewBookingTarget = null },
            onSubmit = { rating, comment ->
                viewModel.addCustomerReview(booking.id, rating, comment, booking.serviceNames) {
                    reviewBookingTarget = null
                }
            }
        )
    }
}

@Composable
fun ServicesListContent(
    services: List<ServiceItemEntity>,
    selectedServices: Set<ServiceItemEntity>,
    language: AppLanguage,
    onToggleService: (ServiceItemEntity) -> Unit,
    onDirectBook: (ServiceItemEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = AmberPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = StringRes.heroRating.tr(language),
                                    fontSize = 11.sp,
                                    color = AmberPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                color = AccentGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = StringRes.heroOpen.tr(language),
                                    fontSize = 11.sp,
                                    color = AccentGreen,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = StringRes.heroTitle.tr(language),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = StringRes.heroSubtitle.tr(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Services header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = StringRes.servicesTitle.tr(language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = StringRes.advanceBadge.tr(language),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = AmberPrimary
                )
            }
        }

        // List of services
        items(services) { service ->
            val isSelected = selectedServices.any { it.id == service.id }
            val name = when (language) {
                AppLanguage.ENGLISH -> service.nameEn
                AppLanguage.HINDI -> service.nameHi
                AppLanguage.BENGALI -> service.nameBn
            }
            val desc = when (language) {
                AppLanguage.ENGLISH -> service.descEn
                AppLanguage.HINDI -> service.descHi
                AppLanguage.BENGALI -> service.descBn
            }

            // Calculate 55% advance for preview
            val advance55 = (service.price * 0.55).toInt()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleService(service) }
                    .testTag("service_card_${service.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) AmberPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.2.dp,
                    if (isSelected) AmberPrimary else DarkBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Category Icon
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when (service.category) {
                                    "HAIR" -> Color(0xFF1E3A8A)
                                    "BEARD" -> Color(0xFF78350F)
                                    "FACIAL" -> Color(0xFF134E4A)
                                    "COMBO" -> Color(0xFF581C87)
                                    else -> Color(0xFF334155)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (service.category) {
                                "HAIR" -> Icons.Default.ContentCut
                                "BEARD" -> Icons.Default.Face
                                "FACIAL" -> Icons.Default.Spa
                                "COMBO" -> Icons.Default.AutoAwesome
                                else -> Icons.Default.CleanHands
                            },
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "₹${service.price.toInt()}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AmberPrimary
                                )
                            )
                            Surface(
                                color = DarkSurfaceElevated,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                val advText = if (language == AppLanguage.ENGLISH) "Advance: ₹$advance55"
                                else if (language == AppLanguage.HINDI) "अग्रिम: ₹$advance55"
                                else "অগ্রিম: ₹$advance55"
                                Text(
                                    text = advText,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "⏱ ${service.durationMinutes} ${StringRes.durationMinutes.tr(language)}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextMuted
                            )
                        }
                    }

                    // Toggle Button
                    IconButton(
                        onClick = { onToggleService(service) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) AmberPrimary else DarkSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = "Select",
                            tint = if (isSelected) Color(0xFF0F172A) else AmberPrimary
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
fun CustomerBookingsContent(
    bookings: List<BookingEntity>,
    currentUser: UserEntity?,
    language: AppLanguage,
    isSyncing: Boolean = false,
    onSyncSupabase: () -> Unit = {},
    onRequireLogin: () -> Unit,
    onReviewClick: (BookingEntity) -> Unit
) {
    if (currentUser == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = AmberPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = StringRes.loginToViewBookingsTitle.tr(language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = StringRes.loginToViewBookingsSubtitle.tr(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRequireLogin,
                        colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_booking_login")
                    ) {
                        Text(
                            text = StringRes.loginRegisterBtn.tr(language),
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    } else if (bookings.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = TextMuted
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = StringRes.noBookings.tr(language),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onSyncSupabase,
                    enabled = !isSyncing,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AccentGreen)
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            color = AccentGreen,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cloud Syncing...", fontSize = 12.sp, color = AccentGreen)
                    } else {
                        Icon(Icons.Default.CloudSync, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Supabase Cloud Sync", fontSize = 12.sp, color = AccentGreen, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Appointment History",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedButton(
                        onClick = onSyncSupabase,
                        enabled = !isSyncing,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        border = BorderStroke(1.dp, AccentGreen.copy(alpha = 0.5f))
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                color = AccentGreen,
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Syncing...", fontSize = 11.sp, color = AccentGreen)
                        } else {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Supabase Sync", fontSize = 11.sp, color = AccentGreen, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            items(bookings) { booking ->
                BookingCard(
                    booking = booking,
                    language = language,
                    isBarberView = false,
                    onReviewClick = { onReviewClick(booking) }
                )
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: BookingEntity,
    language: AppLanguage,
    isBarberView: Boolean,
    onReviewClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onStartClick: () -> Unit = {},
    onCompleteClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onCallCustomer: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            when (booking.status) {
                "CONFIRMED" -> AccentGreen.copy(alpha = 0.5f)
                "IN_PROGRESS" -> AmberPrimary.copy(alpha = 0.5f)
                "COMPLETED" -> AccentCyan.copy(alpha = 0.4f)
                "CANCELLED" -> AccentRed.copy(alpha = 0.4f)
                else -> DarkBorder
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_card_${booking.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: ID & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${StringRes.bookingNumber.tr(language)}${booking.id}",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    color = when (booking.status) {
                        "CONFIRMED" -> AccentGreen.copy(alpha = 0.2f)
                        "IN_PROGRESS" -> AmberPrimary.copy(alpha = 0.2f)
                        "COMPLETED" -> AccentCyan.copy(alpha = 0.2f)
                        "CANCELLED" -> AccentRed.copy(alpha = 0.2f)
                        else -> AmberPrimary.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = when (booking.status) {
                            "CONFIRMED" -> StringRes.statusConfirmed.tr(language)
                            "IN_PROGRESS" -> StringRes.statusInProgress.tr(language)
                            "COMPLETED" -> StringRes.statusCompleted.tr(language)
                            "CANCELLED" -> StringRes.statusCancelled.tr(language)
                            else -> StringRes.statusPending.tr(language)
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (booking.status) {
                            "CONFIRMED" -> AccentGreen
                            "IN_PROGRESS" -> AmberPrimary
                            "COMPLETED" -> AccentCyan
                            "CANCELLED" -> AccentRed
                            else -> AmberPrimary
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Customer Name & Services
            Text(
                text = booking.serviceNames,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (isBarberView) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "👤 ${booking.customerName} (${booking.customerPhone})",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Date and Slot pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = booking.bookingDate,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = booking.timeSlot,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pricing details box (55% Advance vs Remaining Due)
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val totalLabel = if (language == AppLanguage.ENGLISH) "Total: ₹${booking.totalPrice.toInt()}"
                        else if (language == AppLanguage.HINDI) "कुल: ₹${booking.totalPrice.toInt()}"
                        else "মোট: ₹${booking.totalPrice.toInt()}"
                        Text(
                            text = totalLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        val advanceLabel = if (language == AppLanguage.ENGLISH) "Advance (55%): ₹${booking.advancePaid.toInt()} ✓"
                        else if (language == AppLanguage.HINDI) "अग्रिम (५५%): ₹${booking.advancePaid.toInt()} ✓"
                        else "অগ্রিম (৫৫%): ₹${booking.advancePaid.toInt()} ✓"
                        Text(
                            text = advanceLabel,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentGreen
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = StringRes.remainingDue.tr(language),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Text(
                            text = "₹${booking.remainingDue.toInt()}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AmberPrimary
                            )
                        )
                    }
                }
            }

            // Barber actions or Customer Review action
            if (isBarberView) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCallCustomer,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(StringRes.callCustomer.tr(language), fontSize = 11.sp)
                    }

                    when (booking.status) {
                        "PENDING" -> {
                            Button(
                                onClick = onConfirmClick,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                modifier = Modifier.weight(1.4f)
                            ) {
                                Text(
                                    text = StringRes.acceptBooking.tr(language),
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                        }
                        "CONFIRMED" -> {
                            Button(
                                onClick = onStartClick,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                                modifier = Modifier.weight(1.4f)
                            ) {
                                Text(
                                    text = StringRes.startService.tr(language),
                                    fontSize = 11.sp,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }
                        "IN_PROGRESS" -> {
                            Button(
                                onClick = onCompleteClick,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                modifier = Modifier.weight(1.5f)
                            ) {
                                Text(
                                    text = StringRes.completeService.tr(language),
                                    fontSize = 11.sp,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }
                        else -> {
                            // Completed or Cancelled - No action needed
                        }
                    }
                }
            } else {
                // Customer View: If completed and not yet reviewed
                if (booking.status == "COMPLETED" && !booking.isReviewed) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onReviewClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_review_booking_${booking.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = null,
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = StringRes.writeReview.tr(language),
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerReviewsContent(
    reviews: List<ReviewEntity>,
    language: AppLanguage
) {
    val avgRating = if (reviews.isNotEmpty()) {
        reviews.map { it.rating }.average()
    } else 5.0
    val timeFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Overall Rating Summary Header Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f", avgRating),
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                            color = AmberPrimary
                        )
                        Row {
                            (1..5).forEach { _ ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AmberPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${reviews.size} ${StringRes.customerReviewsCount.tr(language)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = StringRes.featurePreorder.tr(language), fontSize = 12.sp, color = AccentGreen)
                        Text(text = StringRes.featureAdvance.tr(language), fontSize = 12.sp, color = AccentGreen)
                        Text(text = StringRes.featureStaff.tr(language), fontSize = 12.sp, color = AccentGreen)
                        Text(text = StringRes.featureAmbiance.tr(language), fontSize = 12.sp, color = AccentGreen)
                    }
                }
            }
        }

        items(reviews) { review ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = review.customerName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row {
                            (1..5).forEach { star ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (star <= review.rating) AmberPrimary else DarkBorder,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${StringRes.serviceLabel.tr(language)} ${review.serviceName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"${review.comment}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = timeFormat.format(Date(review.createdAt)),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = TextMuted
                    )
                }
            }
        }
    }
}
