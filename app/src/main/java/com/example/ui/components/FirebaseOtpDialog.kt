package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.auth.FirebaseAuthManager
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.StringRes
import com.example.ui.localization.tr
import com.example.ui.theme.*
import com.example.ui.viewmodel.SalonViewModel
import com.google.firebase.auth.PhoneAuthProvider

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}

@Composable
fun FirebaseOtpDialog(
    targetRole: String = "CUSTOMER", // "CUSTOMER", "BARBER", "MASTER"
    initialPhone: String = "",
    salonId: String? = null,
    language: AppLanguage,
    viewModel: SalonViewModel,
    onDismiss: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    var phoneInput by remember { mutableStateOf(initialPhone) }
    var nameInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }

    var isOtpSent by remember { mutableStateOf(false) }
    var verificationId by remember { mutableStateOf("") }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successNotice by remember { mutableStateOf<String?>(null) }

    val roleTitle = when (targetRole) {
        "BARBER" -> when (language) {
            AppLanguage.ENGLISH -> "Salon Owner OTP Login"
            AppLanguage.HINDI -> "सैलून ओनर OTP लॉगिन"
            else -> "সেলুন ওনার OTP লগইন"
        }
        "MASTER" -> when (language) {
            AppLanguage.ENGLISH -> "Master Admin OTP Login"
            AppLanguage.HINDI -> "मास्टर एडमिन OTP लॉगिन"
            else -> "মাস্টার এডমিন OTP লগইন"
        }
        else -> when (language) {
            AppLanguage.ENGLISH -> "Customer Phone OTP Login"
            AppLanguage.HINDI -> "ग्राहक फोन OTP लॉगिन"
            else -> "কাস্টমার ফোন OTP লগইন"
        }
    }

    Dialog(
        onDismissRequest = {
            if (!isLoading) onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight()
                .padding(vertical = 16.dp)
                .testTag("dialog_firebase_otp"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AmberPrimary, AmberDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isOtpSent) Icons.Default.MarkEmailRead else Icons.Default.PhoneAndroid,
                        contentDescription = "OTP Auth",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = roleTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isOtpSent) {
                        when (language) {
                            AppLanguage.ENGLISH -> "Enter 6-digit OTP sent to ${phoneInput.trim()}"
                            AppLanguage.HINDI -> "${phoneInput.trim()} पर भेजा गया 6 अंकों का OTP दर्ज करें"
                            else -> "${phoneInput.trim()} নম্বরে পাঠানো ৬-সংখ্যার OTP লিখুন"
                        }
                    } else {
                        when (language) {
                            AppLanguage.ENGLISH -> "Log in securely using Firebase SMS OTP"
                            AppLanguage.HINDI -> "Firebase SMS OTP से सुरक्षित लॉगिन करें"
                            else -> "Firebase SMS OTP দিয়ে সহজে ও নিরাপদে লগইন করুন"
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Error Banner
                if (errorMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AccentRed.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Error",
                                tint = AccentRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = AccentRed,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                            )
                        }
                    }
                }

                // Success Notice Banner
                if (successNotice != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        colors = CardDefaults.cardColors(containerColor = AccentGreen.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AccentGreen.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = AccentGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = successNotice ?: "",
                                color = AccentGreen,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                            )
                        }
                    }
                }

                if (!isOtpSent) {
                    // STEP 1: Phone Input & Send OTP
                    if (targetRole == "CUSTOMER") {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = {
                                Text(
                                    when (language) {
                                        AppLanguage.ENGLISH -> "Your Name (Optional)"
                                        AppLanguage.HINDI -> "आपका नाम (वैकल्पिक)"
                                        else -> "আপনার নাম (ঐচ্ছিক)"
                                    }
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = AmberPrimary)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_otp_name")
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = {
                            phoneInput = it
                            errorMessage = null
                        },
                        label = {
                            Text(
                                when (language) {
                                    AppLanguage.ENGLISH -> "Mobile Number (e.g. +91... or 10 digits)"
                                    AppLanguage.HINDI -> "मोबाइल नंबर (+91... या 10 अंक)"
                                    else -> "মোবাইল নম্বর (+91... বা 01...)"
                                }
                            )
                        },
                        placeholder = { Text("+91 9547625360") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = AmberPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_otp_phone")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Testing info tip
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = AmberPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (language) {
                                    AppLanguage.ENGLISH -> "Firebase Phone Auth sends an SMS code. For emulator testing, use pre-registered test numbers (e.g. +91 9547625360 with OTP 123456)."
                                    AppLanguage.HINDI -> "Firebase Phone Auth एक SMS कोड भेजता है। एमुलेटर में परीक्षण के लिए Firebase में पंजीकृत टेस्ट नंबर का उपयोग कर सकते हैं।"
                                    else -> "Firebase Phone Auth এসএমএস কোড পাঠায়। টেস্টিংয়ের জন্য Firebase Console-এ রেজিস্টার করা টেস্ট নম্বর (যেমন +91 9547625360 এবং OTP 123456) ব্যবহার করা যায়।"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val raw = phoneInput.trim()
                            if (raw.isBlank() || raw.length < 8) {
                                errorMessage = when (language) {
                                    AppLanguage.ENGLISH -> "Please enter a valid mobile number."
                                    AppLanguage.HINDI -> "कृपया मान्य मोबाइल नंबर दर्ज करें।"
                                    else -> "সঠিক মোবাইল নম্বর লিখুন।"
                                }
                                return@Button
                            }

                            if (activity == null) {
                                errorMessage = "Activity context not available. Please try again."
                                return@Button
                            }

                            isLoading = true
                            errorMessage = null
                            successNotice = null

                            FirebaseAuthManager.sendOtp(
                                activity = activity,
                                phoneNumber = raw,
                                onCodeSent = { vid, token ->
                                    isLoading = false
                                    verificationId = vid
                                    resendToken = token
                                    isOtpSent = true
                                    successNotice = when (language) {
                                        AppLanguage.ENGLISH -> "OTP sent successfully!"
                                        AppLanguage.HINDI -> "OTP सफलतापूर्वक भेजा गया!"
                                        else -> "OTP সফলভাবে পাঠানো হয়েছে!"
                                    }
                                },
                                onVerificationCompleted = { cred ->
                                    // Automatic instant verification
                                    isLoading = true
                                    val sms = cred.smsCode ?: ""
                                    if (sms.isNotBlank() && verificationId.isNotBlank()) {
                                        FirebaseAuthManager.verifyOtp(
                                            verificationId = verificationId,
                                            smsCode = sms,
                                            onSuccess = {
                                                isLoading = false
                                                viewModel.loginWithFirebaseOtpSuccess(
                                                    phone = raw,
                                                    expectedRole = targetRole,
                                                    name = nameInput.trim(),
                                                    salonId = salonId,
                                                    onSuccess = {
                                                        onLoginSuccess()
                                                        onDismiss()
                                                    }
                                                )
                                            },
                                            onError = { err ->
                                                isLoading = false
                                                errorMessage = err
                                            }
                                        )
                                    } else {
                                        isLoading = false
                                        viewModel.loginWithFirebaseOtpSuccess(
                                            phone = raw,
                                            expectedRole = targetRole,
                                            name = nameInput.trim(),
                                            salonId = salonId,
                                            onSuccess = {
                                                onLoginSuccess()
                                                onDismiss()
                                            }
                                        )
                                    }
                                },
                                onError = { err ->
                                    isLoading = false
                                    errorMessage = err
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_send_otp"),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = Color.Black
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Sending SMS...")
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (language) {
                                    AppLanguage.ENGLISH -> "Send OTP"
                                    AppLanguage.HINDI -> "OTP भेजें"
                                    else -> "OTP পাঠান"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // STEP 2: Enter OTP & Verify
                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = {
                            if (it.length <= 6) {
                                otpInput = it
                                errorMessage = null
                            }
                        },
                        label = {
                            Text(
                                when (language) {
                                    AppLanguage.ENGLISH -> "6-Digit OTP Code"
                                    AppLanguage.HINDI -> "6-अंकों का OTP कोड"
                                    else -> "৬-সংখ্যার ওটিপি কোড"
                                }
                            )
                        },
                        placeholder = { Text("123456") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AmberPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_otp_code")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val code = otpInput.trim()
                            if (code.length < 6) {
                                errorMessage = when (language) {
                                    AppLanguage.ENGLISH -> "Please enter the 6-digit OTP code."
                                    AppLanguage.HINDI -> "कृपया 6 अंकों का OTP दर्ज करें।"
                                    else -> "সম্পূর্ণ ৬-সংখ্যার OTP কোডটি লিখুন।"
                                }
                                return@Button
                            }

                            isLoading = true
                            errorMessage = null

                            FirebaseAuthManager.verifyOtp(
                                verificationId = verificationId,
                                smsCode = code,
                                onSuccess = { fbUser ->
                                    isLoading = false
                                    viewModel.loginWithFirebaseOtpSuccess(
                                        phone = phoneInput.trim(),
                                        expectedRole = targetRole,
                                        name = nameInput.trim(),
                                        salonId = salonId,
                                        onSuccess = {
                                            onLoginSuccess()
                                            onDismiss()
                                        }
                                    )
                                },
                                onError = { err ->
                                    isLoading = false
                                    errorMessage = err
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_verify_otp"),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = Color.Black
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Verifying...")
                        } else {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (language) {
                                    AppLanguage.ENGLISH -> "Verify & Log In"
                                    AppLanguage.HINDI -> "सत्यापित करें और लॉगिन करें"
                                    else -> "OTP যাচাই ও লগইন করুন"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                isOtpSent = false
                                otpInput = ""
                                errorMessage = null
                            },
                            enabled = !isLoading
                        ) {
                            Text(
                                text = when (language) {
                                    AppLanguage.ENGLISH -> "Change Number"
                                    AppLanguage.HINDI -> "नंबर बदलें"
                                    else -> "নম্বর পরিবর্তন"
                                },
                                fontSize = 13.sp
                            )
                        }

                        TextButton(
                            onClick = {
                                if (activity != null && resendToken != null) {
                                    isLoading = true
                                    errorMessage = null
                                    FirebaseAuthManager.resendOtp(
                                        activity = activity,
                                        phoneNumber = phoneInput.trim(),
                                        token = resendToken!!,
                                        onCodeSent = { vid, newToken ->
                                            isLoading = false
                                            verificationId = vid
                                            resendToken = newToken
                                            successNotice = "New OTP resent!"
                                        },
                                        onError = { err ->
                                            isLoading = false
                                            errorMessage = err
                                        }
                                    )
                                }
                            },
                            enabled = !isLoading && resendToken != null
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (language) {
                                    AppLanguage.ENGLISH -> "Resend OTP"
                                    AppLanguage.HINDI -> "पुनः भेजें"
                                    else -> "পুনরায় পাঠান"
                                },
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Close Button
                TextButton(
                    onClick = onDismiss,
                    enabled = !isLoading,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = when (language) {
                            AppLanguage.ENGLISH -> "Cancel"
                            AppLanguage.HINDI -> "रद्द करें"
                            else -> "বাতিল"
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
