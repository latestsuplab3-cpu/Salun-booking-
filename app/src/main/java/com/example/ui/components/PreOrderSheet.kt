package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceItemEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringRes
import com.example.ui.localization.tr
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreOrderSheet(
    selectedServices: Set<ServiceItemEntity>,
    selectedDate: String,
    selectedSlot: String,
    slots: List<String>,
    language: AppLanguage,
    isSlotBooked: (String, String) -> Boolean,
    onDateChange: (String) -> Unit,
    onSlotChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirmBooking: () -> Unit
) {
    val total = selectedServices.sumOf { it.price }
    val advancePercent = 55.0
    val advance = Math.round((total * (advancePercent / 100.0)) * 100.0) / 100.0
    val remaining = Math.round((total - advance) * 100.0) / 100.0

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())

    val dateOptions = remember(language) {
        val list = mutableListOf<Pair<String, String>>()
        for (i in 0..4) {
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, i) }
            val formatted = dateFormat.format(cal.time)
            val dayName = when (i) {
                0 -> when (language) {
                    AppLanguage.ENGLISH -> "Today"
                    AppLanguage.HINDI -> "आज"
                    AppLanguage.BENGALI -> "আজ"
                }
                1 -> when (language) {
                    AppLanguage.ENGLISH -> "Tomorrow"
                    AppLanguage.HINDI -> "कल"
                    AppLanguage.BENGALI -> "আগামীকাল"
                }
                else -> displayFormat.format(cal.time)
            }
            val display = if (i <= 1) "$dayName (${displayFormat.format(cal.time)})" else dayName
            list.add(Pair(formatted, display))
        }
        list
    }

    // UPI Payment methods only (No bKash, no Nagad, no Card, no NetBanking)
    val paymentOptions = remember(language) {
        listOf(
            "UPI_QR" to StringRes.paymentUpiQr.tr(language),
            "GPAY" to StringRes.paymentGPay.tr(language),
            "PHONEPE" to StringRes.paymentPhonePe.tr(language),
            "PAYTM" to StringRes.paymentPaytmAnyUpi.tr(language)
        )
    }

    var selectedPaymentKey by remember { mutableStateOf("UPI_QR") }

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
                .testTag("sheet_pre_order")
        ) {
            // Title Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = StringRes.preOrderTitle.tr(language),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val servicesCountText = when (language) {
                        AppLanguage.ENGLISH -> "${selectedServices.size} services selected"
                        AppLanguage.HINDI -> "${selectedServices.size} सेवाएं चयनित"
                        AppLanguage.BENGALI -> "${selectedServices.size} টি সেবা নির্বাচিত"
                    }
                    Text(
                        text = servicesCountText,
                        style = MaterialTheme.typography.bodySmall,
                        color = AmberPrimary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step 1: Date selection
            Text(
                text = StringRes.step1Date.tr(language),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dateOptions.take(3).forEach { (dateStr, label) ->
                    val isSelected = dateStr == selectedDate
                    FilterChip(
                        selected = isSelected,
                        onClick = { onDateChange(dateStr) },
                        label = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberPrimary,
                            selectedLabelColor = Color(0xFF0F172A),
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = DarkBorder,
                            selectedBorderColor = AmberPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 2: Time slot selection
            Text(
                text = StringRes.step2Slot.tr(language),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                slots.chunked(3).forEach { rowSlots ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowSlots.forEach { slot ->
                            val isBooked = isSlotBooked(selectedDate, slot)
                            val isSelected = slot == selectedSlot

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(enabled = !isBooked) { onSlotChange(slot) }
                                    .testTag("slot_$slot"),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        isSelected -> AmberPrimary
                                        isBooked -> Color(0xFF332025)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                                border = if (isSelected) BorderStroke(1.5.dp, AmberDark) else null
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = slot,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = when {
                                            isSelected -> Color(0xFF0F172A)
                                            isBooked -> AccentRed
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isBooked) "✖ ${StringRes.booked.tr(language)}" else "● ${StringRes.available.tr(language)}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = when {
                                            isSelected -> Color(0xFF0F172A)
                                            isBooked -> AccentRed
                                            else -> AccentGreen
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Step 3: Advance 55% Payment Breakdown Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = StringRes.pricingBreakdown.tr(language),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Selected services breakdown
                    selectedServices.forEach { svc ->
                        val svcName = when (language) {
                            AppLanguage.ENGLISH -> svc.nameEn
                            AppLanguage.HINDI -> svc.nameHi
                            AppLanguage.BENGALI -> svc.nameBn
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• $svcName",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Text(
                                text = "₹${svc.price.toInt()}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = DarkBorder
                    )

                    // Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = StringRes.totalAmount.tr(language),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "₹$total",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Advance to Pay (55%)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = StringRes.advanceToPay.tr(language),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AmberPrimary
                                )
                            )
                        }
                        Text(
                            text = "₹$advance",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = AmberPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Remaining Due
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = StringRes.remainingDue.tr(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Text(
                            text = "₹$remaining",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "ℹ ${StringRes.advanceNotice.tr(language)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 4: UPI / QR Payment Options Only
            Text(
                text = StringRes.paymentMethodTitle.tr(language),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            // UPI method selector tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                paymentOptions.forEach { (key, label) ->
                    val isSelected = key == selectedPaymentKey
                    OutlinedButton(
                        onClick = { selectedPaymentKey = key },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) AmberPrimary.copy(alpha = 0.2f) else Color.Transparent
                        ),
                        border = BorderStroke(
                            1.2.dp,
                            if (isSelected) AmberPrimary else DarkBorder
                        ),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AmberPrimary else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Payment method details box (UPI QR or Direct UPI)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (selectedPaymentKey == "UPI_QR") {
                    // QR Code Scanner Display
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Stylized QR code box
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val s = size.width
                                val dot = s / 7f
                                // 3 corner position markers
                                drawRoundRect(
                                    color = Color.Black,
                                    topLeft = Offset(0f, 0f),
                                    size = Size(dot * 2.2f, dot * 2.2f),
                                    cornerRadius = CornerRadius(2f, 2f)
                                )
                                drawRoundRect(
                                    color = Color.Black,
                                    topLeft = Offset(s - dot * 2.2f, 0f),
                                    size = Size(dot * 2.2f, dot * 2.2f),
                                    cornerRadius = CornerRadius(2f, 2f)
                                )
                                drawRoundRect(
                                    color = Color.Black,
                                    topLeft = Offset(0f, s - dot * 2.2f),
                                    size = Size(dot * 2.2f, dot * 2.2f),
                                    cornerRadius = CornerRadius(2f, 2f)
                                )
                                // Pixel grid details
                                drawRect(color = Color.Black, topLeft = Offset(dot * 3f, dot * 3f), size = Size(dot * 1.2f, dot * 1.2f))
                                drawRect(color = Color.Black, topLeft = Offset(dot * 5f, dot * 3.5f), size = Size(dot, dot))
                                drawRect(color = Color.Black, topLeft = Offset(dot * 3.5f, dot * 5f), size = Size(dot, dot))
                                drawRect(color = Color.Black, topLeft = Offset(s - dot * 2f, s - dot * 2f), size = Size(dot * 1.5f, dot * 1.5f))
                            }
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan QR",
                                tint = Color(0xFF1E3A8A),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = StringRes.scanAndPayUpi.tr(language),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = StringRes.salonUpiId.tr(language) + " salonpay@upi",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = AmberPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "55% Advance: ₹$advance",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    // Direct UPI App Selected (PhonePe / Google Pay / Paytm)
                    val appName = when (selectedPaymentKey) {
                        "GPAY" -> "Google Pay"
                        "PHONEPE" -> "PhonePe"
                        else -> "Paytm / UPI"
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AmberPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = AmberPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$appName UPI",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (language == AppLanguage.ENGLISH) "Instant direct UPI advance payment of ₹$advance"
                                else if (language == AppLanguage.HINDI) "₹$advance का तत्काल सीधा UPI अग्रिम भुगतान"
                                else "তাৎক্ষণিক সরাসরি UPI অগ্রিম পেমেন্ট ₹$advance",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = TextSecondary
                            )
                        }

                        Surface(
                            color = AccentGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "100% UPI",
                                    fontSize = 9.sp,
                                    color = AccentGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Big CTA Button: Pay 55% & Book
            Button(
                onClick = onConfirmBooking,
                enabled = selectedSlot.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_confirm_preorder"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberPrimary,
                    disabledContainerColor = DarkBorder
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedSlot.isEmpty()) {
                            StringRes.selectSlotFirst.tr(language)
                        } else {
                            if (language == AppLanguage.ENGLISH) "Pay ₹$advance Advance & Book Slot"
                            else if (language == AppLanguage.HINDI) "₹$advance अग्रिम भुगतान कर स्लॉट बुक करें"
                            else "₹$advance অগ্রিম পে করে স্লট বুক করুন"
                        },
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = Color(0xFF0F172A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
