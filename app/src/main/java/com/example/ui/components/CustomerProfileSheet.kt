package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingEntity
import com.example.data.model.ServiceItemEntity
import com.example.data.model.UserEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringRes
import com.example.ui.localization.tr
import com.example.ui.theme.*
import com.example.ui.viewmodel.CustomerLocation
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerProfileSheet(
    currentUser: UserEntity?,
    language: AppLanguage,
    allBookings: List<BookingEntity>,
    services: List<ServiceItemEntity>,
    unreadNotifications: Int = 0,
    onDismiss: () -> Unit,
    onUpdateName: (String) -> Unit,
    onRequireLogin: () -> Unit,
    onQuickLogin: (phone: String, name: String) -> Unit,
    onLogout: () -> Unit,
    onViewBookings: () -> Unit,
    onOpenNotifications: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onSwitchToBarberPanel: () -> Unit,
    onOpenCreatorHub: () -> Unit,
    customerLocation: CustomerLocation = CustomerLocation(),
    onUpdateLocation: (address: String, locality: String, city: String, state: String, pincode: String) -> Unit = { _, _, _, _, _ -> }
) {
    val scrollState = rememberScrollState()
    var isEditingName by remember { mutableStateOf(false) }
    var editedName by remember(currentUser?.name) { mutableStateOf(currentUser?.name ?: "") }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    var isEditingLocation by remember { mutableStateOf(false) }
    var inputAddress by remember(customerLocation.address) { mutableStateOf(customerLocation.address) }
    var inputLocality by remember(customerLocation.locality) { mutableStateOf(customerLocation.locality) }
    var inputCity by remember(customerLocation.city) { mutableStateOf(customerLocation.city) }
    var inputState by remember(customerLocation.state) { mutableStateOf(customerLocation.state) }
    var inputPincode by remember(customerLocation.pincode) { mutableStateOf(customerLocation.pincode) }

    val customerPhone = currentUser?.phone ?: ""
    val customerBookings = remember(allBookings, customerPhone) {
        if (customerPhone.isBlank()) emptyList()
        else allBookings.filter { it.customerPhone == customerPhone }
    }

    val upcomingBooking = remember(customerBookings) {
        customerBookings.firstOrNull {
            it.status == "PENDING" || it.status == "CONFIRMED" || it.status == "IN_PROGRESS"
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = AmberPrimary) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(scrollState)
                .testTag("sheet_customer_profile")
        ) {
            // Header Row
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AmberPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = StringRes.customerProfileTitle.tr(language),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_close_profile")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Profile Hero Card
            if (currentUser != null) {
                // LOGGED-IN CUSTOMER CARD
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_logged_in_customer")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar Badge with Initials
                            val initial = currentUser.name.trim().take(1).uppercase()
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(AmberPrimary, AmberDark)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initial.ifEmpty { "C" },
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                if (isEditingName) {
                                    OutlinedTextField(
                                        value = editedName,
                                        onValueChange = { editedName = it },
                                        singleLine = true,
                                        label = { Text(StringRes.fullName.tr(language)) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_edit_name")
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                onUpdateName(editedName)
                                                isEditingName = false
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                                            modifier = Modifier.testTag("btn_save_profile_name")
                                        ) {
                                            Text(
                                                text = StringRes.saveChanges.tr(language),
                                                color = Color(0xFF0F172A),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                editedName = currentUser.name
                                                isEditingName = false
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Cancel", fontSize = 12.sp)
                                        }
                                    }
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = currentUser.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        IconButton(
                                            onClick = {
                                                editedName = currentUser.name
                                                isEditingName = true
                                            },
                                            modifier = Modifier
                                                .size(24.dp)
                                                .testTag("btn_edit_profile_name")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Name",
                                                tint = AmberPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "📱 ${currentUser.phone}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        color = AmberPrimary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = StringRes.memberTier.tr(language),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = AmberPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats Grid: Total Bookings, Loyalty Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${customerBookings.size}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AmberPrimary
                                    )
                                )
                                Text(
                                    text = StringRes.totalBookingsLabel.tr(language),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .height(36.dp)
                                    .width(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "55%",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AccentGreen
                                    )
                                )
                                Text(
                                    text = "Advance UPI",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .height(36.dp)
                                    .width(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "VIP Gold",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AccentBronze
                                    )
                                )
                                Text(
                                    text = "Status",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            } else {
                // GUEST / NOT LOGGED IN CARD
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_guest_customer")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccountCircle,
                                    contentDescription = null,
                                    tint = AmberPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = StringRes.guestWelcome.tr(language),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = StringRes.guestSubtitle.tr(language),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onRequireLogin,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_profile_signin")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = null,
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = StringRes.loginButton.tr(language),
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Upcoming Appointment Section
            Text(
                text = StringRes.upcomingAppointment.tr(language),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (upcomingBooking != null) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AmberPrimary.copy(alpha = 0.08f)
                    ),
                    border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = AmberPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${upcomingBooking.bookingDate} • ${upcomingBooking.timeSlot}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Surface(
                                color = when (upcomingBooking.status) {
                                    "CONFIRMED" -> AccentGreen.copy(alpha = 0.2f)
                                    "IN_PROGRESS" -> AmberPrimary.copy(alpha = 0.2f)
                                    else -> Color.Gray.copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = upcomingBooking.status,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = when (upcomingBooking.status) {
                                        "CONFIRMED" -> AccentGreen
                                        "IN_PROGRESS" -> AmberPrimary
                                        else -> MaterialTheme.colorScheme.onSurface
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = upcomingBooking.serviceNames,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Paid 55% Advance: ₹${upcomingBooking.advancePaid.toInt()} (Due: ₹${upcomingBooking.remainingDue.toInt()})",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )

                            TextButton(
                                onClick = onViewBookings,
                                modifier = Modifier.testTag("btn_profile_view_bookings")
                            ) {
                                Text(
                                    text = StringRes.viewAllBookings.tr(language),
                                    fontSize = 12.sp,
                                    color = AmberPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = AmberPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.EventAvailable,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = StringRes.noUpcomingBookings.tr(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Customer Location Details Section
            Text(
                text = "Customer Location & Address",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_customer_location")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(AmberPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = AmberPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Delivery & Service Location",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "India • Active Service Address",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                if (isEditingLocation) {
                                    onUpdateLocation(inputAddress, inputLocality, inputCity, inputState, inputPincode)
                                    isEditingLocation = false
                                } else {
                                    inputAddress = customerLocation.address
                                    inputLocality = customerLocation.locality
                                    inputCity = customerLocation.city
                                    inputState = customerLocation.state
                                    inputPincode = customerLocation.pincode
                                    isEditingLocation = true
                                }
                            },
                            modifier = Modifier.testTag("btn_edit_location")
                        ) {
                            Icon(
                                imageVector = if (isEditingLocation) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = if (isEditingLocation) "Save Location" else "Edit Location",
                                tint = AmberPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isEditingLocation) {
                        OutlinedTextField(
                            value = inputAddress,
                            onValueChange = { inputAddress = it },
                            label = { Text("Address / Flat / Road") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_customer_address")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = inputLocality,
                                onValueChange = { inputLocality = it },
                                label = { Text("Locality / Landmark") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_customer_locality")
                            )
                            OutlinedTextField(
                                value = inputCity,
                                onValueChange = { inputCity = it },
                                label = { Text("City") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_customer_city")
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = inputState,
                                onValueChange = { inputState = it },
                                label = { Text("State") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_customer_state")
                            )
                            OutlinedTextField(
                                value = inputPincode,
                                onValueChange = { inputPincode = it.filter { c -> c.isDigit() }.take(6) },
                                label = { Text("PIN Code (6 digits)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_customer_pincode")
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = {
                                    inputAddress = customerLocation.address
                                    inputLocality = customerLocation.locality
                                    inputCity = customerLocation.city
                                    inputState = customerLocation.state
                                    inputPincode = customerLocation.pincode
                                    isEditingLocation = false
                                }
                            ) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    onUpdateLocation(inputAddress, inputLocality, inputCity, inputState, inputPincode)
                                    isEditingLocation = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                                modifier = Modifier.testTag("btn_save_customer_location")
                            ) {
                                Text("Save Location", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = null,
                                        tint = AmberPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = customerLocation.address,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.NearMe,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${customerLocation.locality}, ${customerLocation.city}, ${customerLocation.state} - ${customerLocation.pincode}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Language & Notifications Quick Controls
            Text(
                text = "Language & Notification Settings",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppLanguage.values().forEach { lang ->
                    val isSelected = lang == language
                    val flag = when (lang) {
                        AppLanguage.ENGLISH -> "🇬🇧 EN"
                        AppLanguage.HINDI -> "🇮🇳 हिं"
                        AppLanguage.BENGALI -> "🇮🇳 বাং"
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { onLanguageChange(lang) },
                        label = { Text(flag, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberPrimary.copy(alpha = 0.2f),
                            selectedLabelColor = AmberPrimary
                        ),
                        modifier = Modifier.testTag("chip_lang_${lang.code}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Notification Center Quick Link
            Surface(
                onClick = onOpenNotifications,
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = AmberPrimary
                        )
                        Text(
                            text = "Salon Notifications",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (unreadNotifications > 0) {
                        Badge(
                            containerColor = AccentRed,
                            contentColor = Color.White
                        ) {
                            Text("$unreadNotifications new", fontSize = 11.sp)
                        }
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Salon Location & Contact Support
            Text(
                text = StringRes.salonSupport.tr(language),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = StringRes.salonAddressText.tr(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { /* Helpdesk call intent/action */ },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = StringRes.callSalon.tr(language),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Portal & System Options
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(14.dp))

            // Switch to Barber / Salon Partner Portal
            OutlinedButton(
                onClick = onSwitchToBarberPanel,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AccentBronze.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_profile_switch_staff")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCut,
                    contentDescription = null,
                    tint = AccentBronze,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = StringRes.switchStaffPortal.tr(language),
                    color = AccentBronze,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // App Creator Hub button
            TextButton(
                onClick = onOpenCreatorHub,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_profile_creator_hub")
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "App Creator & Multi-Salon Licensing Hub",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // Logout Option if logged in
            if (currentUser != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showLogoutConfirm = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentRed.copy(alpha = 0.15f),
                        contentColor = AccentRed
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_profile_logout")
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = AccentRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = StringRes.logout.tr(language),
                        fontWeight = FontWeight.Bold,
                        color = AccentRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Log Out Confirmation") },
            text = { Text("Are you sure you want to log out of your customer account?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        onLogout()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Log Out", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
