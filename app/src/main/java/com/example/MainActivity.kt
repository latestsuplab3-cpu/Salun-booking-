package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppCreatorPanelSheet
import com.example.ui.components.CustomerProfileSheet
import com.example.ui.components.NotificationDialog
import com.example.ui.components.SalonOwnerProfileSheet
import com.example.ui.components.TopAppBarWithLanguage
import com.example.ui.screens.AuthDialog
import com.example.ui.screens.BarberScreen
import com.example.ui.screens.CustomerLoginDialog
import com.example.ui.screens.CustomerScreen
import com.example.ui.screens.LoginSelectorScreen
import com.example.ui.screens.MasterAdminScreen
import com.example.ui.theme.SalonAppTheme
import com.example.ui.viewmodel.SalonViewModel
import com.google.firebase.auth.FirebaseAuth
class MainActivity : ComponentActivity() {private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) 
        auth = FirebaseAuth.getInstance()
        enableEdgeToEdge()
        setContent 
        SalonAppTheme{
        val viewModel: SalonViewModel = viewModel)
            SalonAppMain(viewModel) }
            }
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        //{ Code implementation for automatic verification success
    }val options = PhoneAuthOptions.newBuilder(auth)
    .setPhoneNumber("+91 9547625360")
    .setTimeout(60L, TimeUnit.SECONDS)
    .setActivity(this)
    .setCallbacks(callbacks)
    .build()
PhoneAuthProvider.verifyPhoneNumber(options)
    
PhoneAuthProvider.verifyPhoneNumber(options)


    override fun onVerificationFailed(e: FirebaseException) {
        // Code implementation for handling error cases
    }

    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
        // Code implementation for saving verification ID and prompting for OTP input
    }
}
        }
    }
}

@Composable
fun SalonAppMain(viewModel: SalonViewModel) {
    val language by viewModel.currentLanguage.collectAsState()
    val activePanel by viewModel.activePanel.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentSalonLicense by viewModel.currentSalonLicense.collectAsState()
    val salonLicenses by viewModel.salonLicenses.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val feedbackMessage by viewModel.userFeedbackMessage.collectAsState()

    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showCustomerLoginDialog by remember { mutableStateOf(false) }
    var showSalonOwnerProfileSheet by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }
    var showCustomerProfileSheet by remember { mutableStateOf(false) }
    var showAppCreatorSheet by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (activePanel != "LOGIN_SELECTOR") {
                TopAppBarWithLanguage(
                    currentLanguage = language,
                    activePanel = activePanel,
                    unreadNotifications = unreadCount,
                    userName = currentUser?.name,
                    onLanguageChange = { viewModel.setLanguage(it) },
                    onSwitchPanel = {
                        viewModel.logoutToSelector()
                    },
                    onNotificationsClick = { showNotificationsDialog = true },
                    onProfileClick = {
                        if (activePanel == "CUSTOMER") {
                            if (currentUser != null && currentUser?.role == "CUSTOMER") {
                                showCustomerProfileSheet = true
                            } else {
                                showCustomerLoginDialog = true
                            }
                        } else {
                            showSalonOwnerProfileSheet = true
                        }
                    },
                    onOpenCreatorHub = { viewModel.setPanel("MASTER") }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (activePanel != "LOGIN_SELECTOR") innerPadding else PaddingValues(0.dp))
        ) {
            AnimatedContent(
                targetState = activePanel,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "PanelTransition"
            ) { panel ->
                when (panel) {
                    "LOGIN_SELECTOR" -> {
                        LoginSelectorScreen(
                            viewModel = viewModel,
                            onCustomerLoggedIn = {
                                viewModel.setPanel("CUSTOMER")
                            },
                            onSalonOwnerLoggedIn = {
                                viewModel.setPanel("BARBER")
                            },
                            onMasterLoggedIn = {
                                viewModel.setPanel("MASTER")
                            }
                        )
                    }
                    "BARBER" -> {
                        BarberScreen(
                            viewModel = viewModel,
                            onBackToCustomer = { viewModel.logoutToSelector() },
                            onOpenCreatorHub = { viewModel.setPanel("MASTER") },
                            onOpenProfile = { showSalonOwnerProfileSheet = true }
                        )
                    }
                    "MASTER" -> {
                        MasterAdminScreen(
                            viewModel = viewModel,
                            onLogoutToSelector = { viewModel.logoutToSelector() },
                            onLoginAsSalon = { viewModel.setPanel("BARBER") }
                        )
                    }
                    else -> {
                        CustomerScreen(
                            viewModel = viewModel,
                            onRequireLogin = { showCustomerLoginDialog = true }
                        )
                    }
                }
            }
        }
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        NotificationDialog(
            notifications = notifications,
            language = language,
            onDismiss = { showNotificationsDialog = false },
            onMarkAllAsRead = { viewModel.markAllNotificationsRead() },
            onNotificationClick = { id ->
                viewModel.markNotificationRead(id)
            }
        )
    }

    // Dedicated Customer Login Panel (Completely Separate from Salon Owner)
    if (showCustomerLoginDialog) {
        CustomerLoginDialog(
            currentUser = currentUser,
            language = language,
            onDismiss = { showCustomerLoginDialog = false },
            onLoginSubmit = { name, phone, pass, onError ->
                viewModel.loginWithPhonePassword(
                    phone = phone,
                    pass = pass,
                    expectedRole = "CUSTOMER",
                    name = name,
                    onSuccess = { showCustomerLoginDialog = false },
                    onError = onError
                )
            },
            onSwitchToSalonOwnerLogin = {
                showCustomerLoginDialog = false
                viewModel.setPanel("BARBER")
            },
            onLogout = {
                viewModel.logout()
            }
        )
    }

    // Auth / Profile Dialog (Dual Panel: Customer & Salon Owner)
    if (showAuthDialog) {
        AuthDialog(
            currentUser = currentUser,
            currentSalonLicense = currentSalonLicense,
            language = language,
            onDismiss = { showAuthDialog = false },
            onCustomerLogin = { name, phone, pass, onError ->
                viewModel.loginWithPhonePassword(
                    phone = phone,
                    pass = pass,
                    expectedRole = "CUSTOMER",
                    name = name,
                    onSuccess = { showAuthDialog = false },
                    onError = onError
                )
            },
            onSalonLogin = { salonId, phone, pass, onError ->
                viewModel.loginSalonOwner(
                    salonId = salonId,
                    phone = phone,
                    pass = pass,
                    onSuccess = {
                        showAuthDialog = false
                        viewModel.setPanel("BARBER")
                    },
                    onError = onError
                )
            },
            onOpenCreatorHub = {
                showAuthDialog = false
                showAppCreatorSheet = true
            },
            onQuickSwitchRole = { role ->
                viewModel.quickSwitchUser(role)
            },
            onLogout = {
                viewModel.logout()
            }
        )
    }

    // Dedicated Customer Profile Section Sheet
    if (showCustomerProfileSheet) {
        val allBookings by viewModel.allBookings.collectAsState()
        val services by viewModel.services.collectAsState()

        CustomerProfileSheet(
            currentUser = currentUser,
            language = language,
            allBookings = allBookings,
            services = services,
            unreadNotifications = unreadCount,
            onDismiss = { showCustomerProfileSheet = false },
            onUpdateName = { newName ->
                viewModel.updateCustomerProfile(newName) { }
            },
            onRequireLogin = {
                showCustomerProfileSheet = false
                showCustomerLoginDialog = true
            },
            onQuickLogin = { phone, name ->
                viewModel.loginWithPhonePassword(
                    phone = phone,
                    pass = "123456",
                    expectedRole = "CUSTOMER",
                    name = name,
                    onSuccess = { showCustomerProfileSheet = false },
                    onError = { }
                )
            },
            onLogout = {
                viewModel.logout()
            },
            onViewBookings = {
                showCustomerProfileSheet = false
            },
            onOpenNotifications = {
                showCustomerProfileSheet = false
                showNotificationsDialog = true
            },
            onLanguageChange = { lang ->
                viewModel.setLanguage(lang)
            },
            onSwitchToBarberPanel = {
                showCustomerProfileSheet = false
                viewModel.setPanel("BARBER")
            },
            onOpenCreatorHub = {
                showCustomerProfileSheet = false
                showAppCreatorSheet = true
            }
        )
    }

    // Dedicated Salon Owner Profile Section Sheet
    if (showSalonOwnerProfileSheet) {
        val allBookings by viewModel.allBookings.collectAsState()

        SalonOwnerProfileSheet(
            currentUser = currentUser,
            currentSalonLicense = currentSalonLicense,
            allBookings = allBookings,
            language = language,
            unreadNotifications = unreadCount,
            onDismiss = { showSalonOwnerProfileSheet = false },
            onRequireLogin = {
                showSalonOwnerProfileSheet = false
                viewModel.setPanel("BARBER")
            },
            onLogout = {
                viewModel.logoutSalonOwner()
                showSalonOwnerProfileSheet = false
            },
            onOpenCreatorHub = {
                showSalonOwnerProfileSheet = false
                showAppCreatorSheet = true
            },
            onOpenNotifications = {
                showSalonOwnerProfileSheet = false
                showNotificationsDialog = true
            },
            onLanguageChange = { lang ->
                viewModel.setLanguage(lang)
            },
            onSwitchToCustomerPanel = {
                showSalonOwnerProfileSheet = false
                viewModel.setPanel("CUSTOMER")
            },
            onUpdateProfile = { oName, sName, phone, addr ->
                val licId = currentSalonLicense?.salonId ?: "SALON-101"
                viewModel.updateSalonOwnerProfile(licId, oName, sName, phone, addr, onSuccess = {})
            },
            onQuickLoginOwner = {
                viewModel.quickSwitchUser("BARBER")
            }
        )
    }

    // App Creator / Admin Hub Sheet
    if (showAppCreatorSheet) {
        AppCreatorPanelSheet(
            licenses = salonLicenses,
            language = language,
            onDismiss = { showAppCreatorSheet = false },
            onIssueLicense = { salonName, ownerName, phone, pass, address, customId, onSuccess, onError ->
                viewModel.issueSalonLicense(
                    salonName = salonName,
                    ownerName = ownerName,
                    phone = phone,
                    pass = pass,
                    address = address,
                    customId = customId,
                    onSuccess = onSuccess,
                    onError = onError
                )
            },
            onToggleStatus = { salonId, currentActive ->
                viewModel.toggleSalonLicenseStatus(salonId, !currentActive)
            },
            onQuickLoginSalon = { salonId, phone, pass ->
                viewModel.loginSalonOwner(
                    salonId = salonId,
                    phone = phone,
                    pass = pass,
                    onSuccess = {
                        showAppCreatorSheet = false
                        viewModel.setPanel("BARBER")
                    },
                    onError = {}
                )
            }
        )
    }
}
