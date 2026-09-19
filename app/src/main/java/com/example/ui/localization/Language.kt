package com.example.ui.localization

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    BENGALI("bn", "Bengali", "বাংলা"),
    ENGLISH("en", "English", "English"),
    HINDI("hi", "Hindi", "हिन्दी")
}

object StringRes {
    // App & Header
    val appTitle = mapOf(
        AppLanguage.BENGALI to "ডিজিটাল সেলুন বুকিং",
        AppLanguage.ENGLISH to "Digital Salon Booking",
        AppLanguage.HINDI to "डिजिटल सैलून बुकिंग"
    )
    val appSubtitle = mapOf(
        AppLanguage.BENGALI to "স্মার্ট প্রি-অর্ডার ও সেলুন ম্যানেজমেন্ট",
        AppLanguage.ENGLISH to "Smart Pre-orders & Salon Hub",
        AppLanguage.HINDI to "स्मार्ट प्री-ऑर्डर और सैलून हब"
    )

    // Roles & Switcher
    val customerPanel = mapOf(
        AppLanguage.BENGALI to "কাস্টমার প্যানেল",
        AppLanguage.ENGLISH to "Customer Panel",
        AppLanguage.HINDI to "ग्राहक पैनल"
    )
    val barberPanel = mapOf(
        AppLanguage.BENGALI to "সেলুন প্যানেল",
        AppLanguage.ENGLISH to "Salon Panel",
        AppLanguage.HINDI to "सैलून पैनल"
    )
    val switchPanel = mapOf(
        AppLanguage.BENGALI to "প্যানেল পরিবর্তন",
        AppLanguage.ENGLISH to "Switch Panel",
        AppLanguage.HINDI to "पैनल बदलें"
    )

    // Auth & Profile
    val loginTitle = mapOf(
        AppLanguage.BENGALI to "প্রোফাইল লগইন",
        AppLanguage.ENGLISH to "Profile Login",
        AppLanguage.HINDI to "प्रोफ़ाइल लॉगिन"
    )
    val registerTitle = mapOf(
        AppLanguage.BENGALI to "নতুন অ্যাকাউন্ট তৈরি",
        AppLanguage.ENGLISH to "Create Account",
        AppLanguage.HINDI to "नया खाता बनाएं"
    )
    val phoneNumber = mapOf(
        AppLanguage.BENGALI to "মোবাইল নম্বর",
        AppLanguage.ENGLISH to "Phone Number",
        AppLanguage.HINDI to "फ़ोन नंबर"
    )
    val password = mapOf(
        AppLanguage.BENGALI to "পাসওয়ার্ড",
        AppLanguage.ENGLISH to "Password",
        AppLanguage.HINDI to "पासवर्ड"
    )
    val fullName = mapOf(
        AppLanguage.BENGALI to "আপনার নাম",
        AppLanguage.ENGLISH to "Full Name",
        AppLanguage.HINDI to "पूरा नाम"
    )
    val loginButton = mapOf(
        AppLanguage.BENGALI to "লগইন করুন",
        AppLanguage.ENGLISH to "Log In",
        AppLanguage.HINDI to "लॉग इन करें"
    )
    val registerButton = mapOf(
        AppLanguage.BENGALI to "রেজিস্টার করুন",
        AppLanguage.ENGLISH to "Register",
        AppLanguage.HINDI to "रजिस्टर करें"
    )
    val quickDemoCustomer = mapOf(
        AppLanguage.BENGALI to "কাস্টমার ডেমো লগইন",
        AppLanguage.ENGLISH to "Customer Demo Login",
        AppLanguage.HINDI to "ग्राहक डेमो लॉगिन"
    )
    val quickDemoBarber = mapOf(
        AppLanguage.BENGALI to "সেলুন ডেমো লগইন",
        AppLanguage.ENGLISH to "Salon Demo Login",
        AppLanguage.HINDI to "सैलून डेमो लॉगिन"
    )
    val loggedInAs = mapOf(
        AppLanguage.BENGALI to "লগইন আছেন:",
        AppLanguage.ENGLISH to "Logged in as:",
        AppLanguage.HINDI to "लॉग इन हैं:"
    )
    val logout = mapOf(
        AppLanguage.BENGALI to "লগআউট",
        AppLanguage.ENGLISH to "Logout",
        AppLanguage.HINDI to "लॉगआउट"
    )

    // Services Catalog
    val servicesTitle = mapOf(
        AppLanguage.BENGALI to "সেলুনের সেবাসমূহ ও মূল্যতালিকা",
        AppLanguage.ENGLISH to "Salon Services & Pricing",
        AppLanguage.HINDI to "सैलून सेवाएँ और मूल्य"
    )
    val currency = mapOf(
        AppLanguage.BENGALI to "₹",
        AppLanguage.ENGLISH to "₹",
        AppLanguage.HINDI to "₹"
    )
    val addService = mapOf(
        AppLanguage.BENGALI to "যোগ করুন +",
        AppLanguage.ENGLISH to "Add +",
        AppLanguage.HINDI to "जोड़ें +"
    )
    val selected = mapOf(
        AppLanguage.BENGALI to "সিলেক্টেড ✓",
        AppLanguage.ENGLISH to "Selected ✓",
        AppLanguage.HINDI to "चयनित ✓"
    )
    val durationMinutes = mapOf(
        AppLanguage.BENGALI to "মিনিট",
        AppLanguage.ENGLISH to "mins",
        AppLanguage.HINDI to "मिनट"
    )

    // Pre-order & Slot Booking
    val preOrderTitle = mapOf(
        AppLanguage.BENGALI to "প্রি-অর্ডার বুকিং",
        AppLanguage.ENGLISH to "Pre-Order Booking",
        AppLanguage.HINDI to "प्री-ऑर्डर बुकिंग"
    )
    val selectDate = mapOf(
        AppLanguage.BENGALI to "তারিখ নির্বাচন করুন",
        AppLanguage.ENGLISH to "Select Date",
        AppLanguage.HINDI to "तारीख चुनें"
    )
    val selectSlot = mapOf(
        AppLanguage.BENGALI to "টাইম স্লট পছন্দ করুন (ফাঁকা সময়)",
        AppLanguage.ENGLISH to "Choose Time Slot (Available Times)",
        AppLanguage.HINDI to "समय स्लॉट चुनें (उपलब्ध समय)"
    )
    val available = mapOf(
        AppLanguage.BENGALI to "ফাঁকা আছে",
        AppLanguage.ENGLISH to "Available",
        AppLanguage.HINDI to "उपलब्ध"
    )
    val booked = mapOf(
        AppLanguage.BENGALI to "বুকড",
        AppLanguage.ENGLISH to "Booked",
        AppLanguage.HINDI to "बुक है"
    )
    val today = mapOf(
        AppLanguage.BENGALI to "আজ",
        AppLanguage.ENGLISH to "Today",
        AppLanguage.HINDI to "आज"
    )
    val tomorrow = mapOf(
        AppLanguage.BENGALI to "আগামীকাল",
        AppLanguage.ENGLISH to "Tomorrow",
        AppLanguage.HINDI to "कल"
    )

    // Payment & Advance 55%
    val pricingBreakdown = mapOf(
        AppLanguage.BENGALI to "হিসাব ও অগ্রিম পরিশোধের নিয়ম",
        AppLanguage.ENGLISH to "Pricing & Advance Payment Rule",
        AppLanguage.HINDI to "मूल्य और अग्रिम भुगतान नियम"
    )
    val totalAmount = mapOf(
        AppLanguage.BENGALI to "মোট সেবামূল্য:",
        AppLanguage.ENGLISH to "Total Service Price:",
        AppLanguage.HINDI to "कुल सेवा शुल्क:"
    )
    val advanceToPay = mapOf(
        AppLanguage.BENGALI to "প্রি-অর্ডার অগ্রিম (৫৫% পে করুন):",
        AppLanguage.ENGLISH to "Pre-Order Advance (Pay 55% Now):",
        AppLanguage.HINDI to "प्री-ऑर्डर अग्रिम (55% भुगतान करें):"
    )
    val advanceNotice = mapOf(
        AppLanguage.BENGALI to "নিয়মানুযায়ী প্রি-অর্ডারে ৫৫% আগে দিতে হবে। বাকি টাকা চুল-দাড়ি কাটার পর সেলুনে দিবেন।",
        AppLanguage.ENGLISH to "As per policy, 55% advance pre-payment confirms your slot. Balance is due at salon.",
        AppLanguage.HINDI to "नियम अनुसार 55% अग्रिम भुगतान से स्लॉट पक्का होता है। शेष राशि सैलून पर देय है।"
    )
    val remainingDue = mapOf(
        AppLanguage.BENGALI to "সেলুনে বাকি প্রদেয়:",
        AppLanguage.ENGLISH to "Due at Salon:",
        AppLanguage.HINDI to "सैलून में बाकी देय:"
    )
    val confirmAndPay = mapOf(
        AppLanguage.BENGALI to "৫৫% অগ্রিম পে করে বুক করুন",
        AppLanguage.ENGLISH to "Pay 55% Advance & Book Slot",
        AppLanguage.HINDI to "55% अग्रिम देकर स्लॉट बुक करें"
    )
    val paymentSuccess = mapOf(
        AppLanguage.BENGALI to "অগ্রিম পেমেন্ট সফল! বুকিং কনফার্মড।",
        AppLanguage.ENGLISH to "Advance Paid Successfully! Booking Confirmed.",
        AppLanguage.HINDI to "अग्रिम भुगतान सफल! बुकिंग पक्की हुई।"
    )

    // Customer Bookings Tab
    val myBookings = mapOf(
        AppLanguage.BENGALI to "আমার বুকিং",
        AppLanguage.ENGLISH to "My Bookings",
        AppLanguage.HINDI to "मेरी बुकिंग"
    )
    val noBookings = mapOf(
        AppLanguage.BENGALI to "কোনো বুকিং পাওয়া যায়নি। এখনই প্রি-অর্ডার করুন!",
        AppLanguage.ENGLISH to "No bookings found yet. Pre-order now!",
        AppLanguage.HINDI to "कोई बुकिंग नहीं मिली। अभी प्री-ऑर्डर करें!"
    )
    val statusPending = mapOf(
        AppLanguage.BENGALI to "অপেক্ষমান (Pending)",
        AppLanguage.ENGLISH to "Pending Acceptance",
        AppLanguage.HINDI to "स्वीकृति लंबित"
    )
    val statusConfirmed = mapOf(
        AppLanguage.BENGALI to "নিশ্চিত (Confirmed)",
        AppLanguage.ENGLISH to "Confirmed",
        AppLanguage.HINDI to "पुष्ट (Confirmed)"
    )
    val statusInProgress = mapOf(
        AppLanguage.BENGALI to "চলছে (In Chair)",
        AppLanguage.ENGLISH to "In Progress",
        AppLanguage.HINDI to "प्रगति पर है"
    )
    val statusCompleted = mapOf(
        AppLanguage.BENGALI to "সম্পন্ন (Completed)",
        AppLanguage.ENGLISH to "Completed",
        AppLanguage.HINDI to "पूर्ण (Completed)"
    )
    val statusCancelled = mapOf(
        AppLanguage.BENGALI to "বাতিল (Cancelled)",
        AppLanguage.ENGLISH to "Cancelled",
        AppLanguage.HINDI to "रद्द (Cancelled)"
    )

    // Reviews
    val reviewsTab = mapOf(
        AppLanguage.BENGALI to "গ্রাহকদের রিভিউ",
        AppLanguage.ENGLISH to "Customer Reviews",
        AppLanguage.HINDI to "ग्राहक समीक्षाएं"
    )
    val writeReview = mapOf(
        AppLanguage.BENGALI to "রিভিউ দিন ★",
        AppLanguage.ENGLISH to "Write Review ★",
        AppLanguage.HINDI to "समीक्षा दें ★"
    )
    val ratingTitle = mapOf(
        AppLanguage.BENGALI to "আপনার অভিজ্ঞতা কেমন ছিল?",
        AppLanguage.ENGLISH to "How was your salon experience?",
        AppLanguage.HINDI to "आपका सैलून अनुभव कैसा रहा?"
    )
    val writeCommentHint = mapOf(
        AppLanguage.BENGALI to "চুল কাটা বা সেভ কেমন লাগলো লিখুন...",
        AppLanguage.ENGLISH to "Share your review about haircut or beard...",
        AppLanguage.HINDI to "बाल कटाई या शेव के बारे में अपनी राय लिखें..."
    )
    val submitReviewBtn = mapOf(
        AppLanguage.BENGALI to "রিভিউ জমা দিন",
        AppLanguage.ENGLISH to "Submit Review",
        AppLanguage.HINDI to "समीक्षा सबमिट करें"
    )
    val reviewSuccess = mapOf(
        AppLanguage.BENGALI to "আপনার মতামতের জন্য অনেক ধন্যবাদ!",
        AppLanguage.ENGLISH to "Thank you for your valuable feedback!",
        AppLanguage.HINDI to "आपकी बहुमूल्य समीक्षा के लिए धन्यवाद!"
    )

    // Barber Panel Specific
    val barberOverview = mapOf(
        AppLanguage.BENGALI to "সেলুন ড্যাশবোর্ড ও অর্ডারসমূহ",
        AppLanguage.ENGLISH to "Salon Dashboard & Orders",
        AppLanguage.HINDI to "सैलून डैशबोर्ड और ऑर्डर"
    )
    val newOrdersNotification = mapOf(
        AppLanguage.BENGALI to "নতুন অর্ডারের নোটিফিকেশন",
        AppLanguage.ENGLISH to "New Order Alerts",
        AppLanguage.HINDI to "नए ऑर्डर अलर्ट"
    )
    val totalAdvanceCollected = mapOf(
        AppLanguage.BENGALI to "অগ্রিম জমা (৫৫%):",
        AppLanguage.ENGLISH to "Advance Collected (55%):",
        AppLanguage.HINDI to "अग्रिम प्राप्त (55%):"
    )
    val totalDueToCollect = mapOf(
        AppLanguage.BENGALI to "বাকি পাওনা আদায়যোগ্য:",
        AppLanguage.ENGLISH to "Pending Due to Collect:",
        AppLanguage.HINDI to "बाकी देय वसूली:"
    )
    val acceptBooking = mapOf(
        AppLanguage.BENGALI to "বুকিং নিশ্চিত করুন ✓",
        AppLanguage.ENGLISH to "Confirm Booking ✓",
        AppLanguage.HINDI to "स्वीकार करें ✓"
    )
    val startService = mapOf(
        AppLanguage.BENGALI to "সেবা শুরু করুন ✂",
        AppLanguage.ENGLISH to "Start Service ✂",
        AppLanguage.HINDI to "सेवा शुरू करें ✂"
    )
    val completeService = mapOf(
        AppLanguage.BENGALI to "সম্পন্ন ও বাকি টাকা গ্রহণ ✓",
        AppLanguage.ENGLISH to "Complete & Settle Due ✓",
        AppLanguage.HINDI to "पूर्ण व बाकी प्राप्त ✓"
    )
    val cancelBooking = mapOf(
        AppLanguage.BENGALI to "বাতিল করুন ✕",
        AppLanguage.ENGLISH to "Decline ✕",
        AppLanguage.HINDI to "अस्वीकार करें ✕"
    )
    val managePrices = mapOf(
        AppLanguage.BENGALI to "মূল্য ও রেট পরিবর্তন করুন",
        AppLanguage.ENGLISH to "Manage Rates & Pricing",
        AppLanguage.HINDI to "मूल्य व दरें प्रबंधित करें"
    )
    val editPriceDialog = mapOf(
        AppLanguage.BENGALI to "সেবার মূল্য আপডেট",
        AppLanguage.ENGLISH to "Update Service Rate",
        AppLanguage.HINDI to "सेवा शुल्क अपडेट करें"
    )
    val savePrice = mapOf(
        AppLanguage.BENGALI to "সংরক্ষণ করুন",
        AppLanguage.ENGLISH to "Save Rate",
        AppLanguage.HINDI to "सहेजें"
    )
    val callCustomer = mapOf(
        AppLanguage.BENGALI to "ফোন করুন",
        AppLanguage.ENGLISH to "Call Customer",
        AppLanguage.HINDI to "कॉल करें"
    )
    val cancel = mapOf(
        AppLanguage.BENGALI to "বাতিল",
        AppLanguage.ENGLISH to "Cancel",
        AppLanguage.HINDI to "रद्द करें"
    )
    val close = mapOf(
        AppLanguage.BENGALI to "বন্ধ করুন",
        AppLanguage.ENGLISH to "Close",
        AppLanguage.HINDI to "बंद करें"
    )
    val emptyNotifications = mapOf(
        AppLanguage.BENGALI to "নতুন কোনো নোটিফিকেশন নেই",
        AppLanguage.ENGLISH to "No new alerts",
        AppLanguage.HINDI to "कोई नया अलर्ट नहीं"
    )
    val preOrderLoginHeader = mapOf(
        AppLanguage.BENGALI to "প্রি-অর্ডার করার পূর্বে কাস্টমার লগইন",
        AppLanguage.ENGLISH to "Customer Login for Pre-Order",
        AppLanguage.HINDI to "प्री-ऑर्डर के लिए ग्राहक लॉगिन"
    )
    val preOrderLoginInstruction = mapOf(
        AppLanguage.BENGALI to "প্রি-অর্ডার বুকিং করতে আপনার নাম, মোবাইল নম্বর এবং পাসওয়ার্ড দিন। নতুন অ্যাকাউন্ট স্বয়ংক্রিয়ভাবে তৈরি হবে।",
        AppLanguage.ENGLISH to "Please provide your name, mobile number and password to place your pre-order. A new account will be created automatically if you are new.",
        AppLanguage.HINDI to "प्री-ऑर्डर के लिए अपना नाम, मोबाइल नंबर और पासवर्ड दर्ज करें।"
    )
    val namePlaceholder = mapOf(
        AppLanguage.BENGALI to "আপনার নাম লিখুন",
        AppLanguage.ENGLISH to "Enter your name",
        AppLanguage.HINDI to "अपना नाम दर्ज करें"
    )
    val phonePlaceholder = mapOf(
        AppLanguage.BENGALI to "মোবাইল নম্বর (যেমন: 9876543210)",
        AppLanguage.ENGLISH to "Mobile number (e.g. 9876543210)",
        AppLanguage.HINDI to "मोबाइल नंबर (उदा: 9876543210)"
    )
    val passwordPlaceholder = mapOf(
        AppLanguage.BENGALI to "পাসওয়ার্ড লিখুন",
        AppLanguage.ENGLISH to "Enter password",
        AppLanguage.HINDI to "पासवर्ड दर्ज करें"
    )
    val continuePreOrderBtn = mapOf(
        AppLanguage.BENGALI to "লগইন করে প্রি-অর্ডারে এগিয়ে যান",
        AppLanguage.ENGLISH to "Login & Continue Pre-Order",
        AppLanguage.HINDI to "लॉगिन कर प्री-ऑर्डर जारी रखें"
    )

    // Dual Login & Salon ID & Creator Panel strings
    val customerLoginTab = mapOf(
        AppLanguage.BENGALI to "কাস্টমার লগইন",
        AppLanguage.ENGLISH to "Customer Login",
        AppLanguage.HINDI to "ग्राहक लॉगिन"
    )
    val salonOwnerLoginTab = mapOf(
        AppLanguage.BENGALI to "সেলুন ওনার লগইন",
        AppLanguage.ENGLISH to "Salon Owner Login",
        AppLanguage.HINDI to "सैलून मालिक लॉगिन"
    )
    val salonIdLabel = mapOf(
        AppLanguage.BENGALI to "সেলুন আইডি (Salon ID)",
        AppLanguage.ENGLISH to "Salon License ID",
        AppLanguage.HINDI to "सैलून आईडी (Salon ID)"
    )
    val salonIdPlaceholder = mapOf(
        AppLanguage.BENGALI to "যেমন: SALON-101 (তৈরিদাতা কর্তৃক প্রদত্ত)",
        AppLanguage.ENGLISH to "e.g. SALON-101 (provided by App Creator)",
        AppLanguage.HINDI to "जैसे: SALON-101 (निर्माता द्वारा प्रदत्त)"
    )
    val salonOwnerLoginNotice = mapOf(
        AppLanguage.BENGALI to "সেলুন প্যানেল এক্সেস করতে অ্যাপ তৈরিদাতা কর্তৃক প্রদত্ত সেলুন আইডি, আপনার রেজিস্টার্ড মোবাইল নম্বর ও পাসওয়ার্ড প্রদান করুন।",
        AppLanguage.ENGLISH to "To access Salon Panel, enter your official Salon ID provided by App Creator, along with your phone number and password.",
        AppLanguage.HINDI to "सैलून पैनल के लिए ऐप निर्माता द्वारा दी गई सैलून आईडी, फोन और पासवर्ड दर्ज करें।"
    )
    val loginSalonBtn = mapOf(
        AppLanguage.BENGALI to "সেলুন ড্যাশবোর্ডে প্রবেশ করুন",
        AppLanguage.ENGLISH to "Verify ID & Enter Dashboard",
        AppLanguage.HINDI to "आईडी सत्यापित करें और प्रवेश करें"
    )
    val logoutBtn = mapOf(
        AppLanguage.BENGALI to "লগ আউট",
        AppLanguage.ENGLISH to "Log Out",
        AppLanguage.HINDI to "लॉग आउट"
    )
    val appCreatorHub = mapOf(
        AppLanguage.BENGALI to "অ্যাপ তৈরিদাতার প্যানেল (মাস্টার অ্যাডমিন)",
        AppLanguage.ENGLISH to "App Creator Hub (Master Admin)",
        AppLanguage.HINDI to "ऐप निर्माता हब (मास्टर एडमिन)"
    )
    val appCreatorSubtitle = mapOf(
        AppLanguage.BENGALI to "নতুন সেলুনের জন্য আইডি তৈরি ও অনুমোদন করুন",
        AppLanguage.ENGLISH to "Generate and issue official Salon IDs to Salon Owners",
        AppLanguage.HINDI to "सैलून मालिकों के लिए आधिकारिक आईडी बनाएं और जारी करें"
    )
    val issueNewSalonId = mapOf(
        AppLanguage.BENGALI to "নতুন সেলুন আইডি প্রদান করুন",
        AppLanguage.ENGLISH to "Issue New Salon ID",
        AppLanguage.HINDI to "नई सैलून आईडी जारी करें"
    )
    val salonNameLabel = mapOf(
        AppLanguage.BENGALI to "সেলুনের নাম",
        AppLanguage.ENGLISH to "Salon Name",
        AppLanguage.HINDI to "सैलून का नाम"
    )
    val ownerNameLabel = mapOf(
        AppLanguage.BENGALI to "মালিকের নাম",
        AppLanguage.ENGLISH to "Owner Name",
        AppLanguage.HINDI to "मालिक का नाम"
    )
    val activeLicensesList = mapOf(
        AppLanguage.BENGALI to "অনুমোদিত সেলুন আইডি সমূহের তালিকা",
        AppLanguage.ENGLISH to "Authorized Salon IDs & Licenses",
        AppLanguage.HINDI to "अधिकृत सैलून आईडी सूची"
    )

    // Payment Methods & UPI & QR
    val paymentMethodTitle = mapOf(
        AppLanguage.BENGALI to "পেমেন্ট মাধ্যম বেছে নিন (৫৫% ডিজিটাল অগ্রিম):",
        AppLanguage.ENGLISH to "Choose Payment Method (55% Digital Advance):",
        AppLanguage.HINDI to "भुगतान विधि चुनें (55% डिजिटल अग्रिम):"
    )
    val paymentUpiQr = mapOf(
        AppLanguage.BENGALI to "UPI QR কোড",
        AppLanguage.ENGLISH to "UPI QR Code",
        AppLanguage.HINDI to "UPI QR कोड"
    )
    val paymentGPay = mapOf(
        AppLanguage.BENGALI to "Google Pay",
        AppLanguage.ENGLISH to "Google Pay",
        AppLanguage.HINDI to "Google Pay"
    )
    val paymentPhonePe = mapOf(
        AppLanguage.BENGALI to "PhonePe",
        AppLanguage.ENGLISH to "PhonePe",
        AppLanguage.HINDI to "PhonePe"
    )
    val paymentPaytmAnyUpi = mapOf(
        AppLanguage.BENGALI to "Paytm / যেকোনো UPI",
        AppLanguage.ENGLISH to "Paytm / Any UPI",
        AppLanguage.HINDI to "Paytm / कोई भी UPI"
    )
    val scanAndPayUpi = mapOf(
        AppLanguage.BENGALI to "যেকোনো UPI অ্যাপ দিয়ে স্ক্যান করে পে করুন (Google Pay, PhonePe, Paytm)",
        AppLanguage.ENGLISH to "Scan & Pay using any UPI App (Google Pay, PhonePe, Paytm)",
        AppLanguage.HINDI to "किसी भी UPI ऐप से स्कैन करके भुगतान करें (Google Pay, PhonePe, Paytm)"
    )
    val salonUpiId = mapOf(
        AppLanguage.BENGALI to "সেলুন ভেরিফায়েড UPI আইডি:",
        AppLanguage.ENGLISH to "Salon Verified UPI ID:",
        AppLanguage.HINDI to "सैलून सत्यापित UPI आईडी:"
    )

    // Steps & Indicators
    val step1Date = mapOf(
        AppLanguage.BENGALI to "১. তারিখ নির্বাচন করুন",
        AppLanguage.ENGLISH to "1. Select Date",
        AppLanguage.HINDI to "1. तारीख चुनें"
    )
    val step2Slot = mapOf(
        AppLanguage.BENGALI to "২. ফাঁকা টাইম স্লট পছন্দ করুন",
        AppLanguage.ENGLISH to "2. Choose Available Time Slot",
        AppLanguage.HINDI to "2. उपलब्ध समय स्लॉट चुनें"
    )
    val step3Breakdown = mapOf(
        AppLanguage.BENGALI to "৩. ৫৫% অগ্রিম পেমেন্টের হিসাব",
        AppLanguage.ENGLISH to "3. Advance 55% Payment Breakdown",
        AppLanguage.HINDI to "3. 55% अग्रिम भुगतान विवरण"
    )
    val advancePayNotice = mapOf(
        AppLanguage.BENGALI to "এখনই ৫৫% অগ্রিম পরিশোধ",
        AppLanguage.ENGLISH to "Pay 55% Advance Now",
        AppLanguage.HINDI to "अभी 55% अग्रिम भुगतान"
    )
    val dueAtSalonNotice = mapOf(
        AppLanguage.BENGALI to "সেলুনে সেবা গ্রহণের পর প্রদেয়",
        AppLanguage.ENGLISH to "Remaining Balance Due at Salon",
        AppLanguage.HINDI to "सैलून में सेवा के बाद शेष देय"
    )
    val selectSlotFirst = mapOf(
        AppLanguage.BENGALI to "প্রথমে একটি টাইম স্লট নির্বাচন করুন",
        AppLanguage.ENGLISH to "Please select a time slot first",
        AppLanguage.HINDI to "कृपया पहले एक समय स्लॉट चुनें"
    )
    val payAdvanceAndBook = mapOf(
        AppLanguage.BENGALI to "অগ্রিম পে করে স্লট বুক করুন",
        AppLanguage.ENGLISH to "Pay Advance & Book Slot",
        AppLanguage.HINDI to "अग्रिम भुगतान कर स्लॉट बुक करें"
    )

    // Hero & Badges
    val heroRating = mapOf(
        AppLanguage.BENGALI to "★ ৪.৯ রেটিং (১২০+ রিভিউ)",
        AppLanguage.ENGLISH to "★ 4.9 Rating (120+ Reviews)",
        AppLanguage.HINDI to "★ 4.9 रेटिंग (120+ समीक्षाएं)"
    )
    val heroOpen = mapOf(
        AppLanguage.BENGALI to "খোলা আছে (Open)",
        AppLanguage.ENGLISH to "Open Now",
        AppLanguage.HINDI to "अभी खुला है"
    )
    val heroTitle = mapOf(
        AppLanguage.BENGALI to "ডিজিটাল হেয়ার কাট ও গ্রুমিং স্টুডিও",
        AppLanguage.ENGLISH to "Digital Haircut & Grooming Studio",
        AppLanguage.HINDI to "डिजिटल हेयर कट और ग्रूमिंग स्टूडियो"
    )
    val heroSubtitle = mapOf(
        AppLanguage.BENGALI to "আপনার সুবিধাজনক সময় বুক করুন। ৫৫% অগ্রিম দিয়ে নিশ্চিন্তে স্লট নিশ্চিত করুন।",
        AppLanguage.ENGLISH to "Book your convenient appointment slot. 55% advance pre-payment secures your place.",
        AppLanguage.HINDI to "अपना सुविधाजनक समय बुक करें। 55% अग्रिम से स्लॉट पक्का करें।"
    )
    val advanceBadge = mapOf(
        AppLanguage.BENGALI to "৫৫% অগ্রিম প্রি-অর্ডার",
        AppLanguage.ENGLISH to "55% Advance Pre-Order",
        AppLanguage.HINDI to "55% अग्रिम प्री-ऑर्डर"
    )

    // Login prompt
    val loginToViewBookingsTitle = mapOf(
        AppLanguage.BENGALI to "বুকিং দেখতে লগইন করুন",
        AppLanguage.ENGLISH to "Login to View Your Bookings",
        AppLanguage.HINDI to "अपनी बुकिंग देखने के लिए लॉगिन करें"
    )
    val loginToViewBookingsSubtitle = mapOf(
        AppLanguage.BENGALI to "আপনার নাম, মোবাইল নম্বর ও পাসওয়ার্ড দিয়ে লগইন করে নিজের সকল বুকিং স্ট্যাটাস দেখুন।",
        AppLanguage.ENGLISH to "Sign in with your phone number and password to track all your scheduled appointments.",
        AppLanguage.HINDI to "अपनी सभी नियुक्तियां देखने के लिए फोन नंबर और पासवर्ड से लॉगिन करें।"
    )
    val loginRegisterBtn = mapOf(
        AppLanguage.BENGALI to "লগইন / রেজিস্টার করুন",
        AppLanguage.ENGLISH to "Login / Register",
        AppLanguage.HINDI to "लॉगिन / रजिस्टर करें"
    )
    val preOrderBookingBtn = mapOf(
        AppLanguage.BENGALI to "প্রি-অর্ডার বুকিং করুন",
        AppLanguage.ENGLISH to "Book Pre-Order Now",
        AppLanguage.HINDI to "प्री-ऑर्डर बुक करें"
    )

    // Barber Screen
    val todayBookings = mapOf(
        AppLanguage.BENGALI to "আজকের বুকিং",
        AppLanguage.ENGLISH to "Today's Bookings",
        AppLanguage.HINDI to "आज की बुकिंग"
    )
    val filterAll = mapOf(
        AppLanguage.BENGALI to "সবগুলো",
        AppLanguage.ENGLISH to "All",
        AppLanguage.HINDI to "सभी"
    )
    val filterToday = mapOf(
        AppLanguage.BENGALI to "আজকের",
        AppLanguage.ENGLISH to "Today",
        AppLanguage.HINDI to "आज"
    )
    val noBookingsInCategory = mapOf(
        AppLanguage.BENGALI to "এই ক্যাটাগরিতে কোনো বুকিং নেই",
        AppLanguage.ENGLISH to "No bookings in this category",
        AppLanguage.HINDI to "इस श्रेणी में कोई बुकिंग नहीं है"
    )
    val ratesControlTitle = mapOf(
        AppLanguage.BENGALI to "সেবার মূল্য ও রেট কন্ট্রোল প্যানেল",
        AppLanguage.ENGLISH to "Service Pricing & Rates Management",
        AppLanguage.HINDI to "सेवा मूल्य और दर प्रबंधन"
    )
    val ratesControlSubtitle = mapOf(
        AppLanguage.BENGALI to "এখানে যেকোনো সেবার রেট পরিবর্তন করুন। কাস্টমাররা সাথে সাথে নতুন রেট দেখতে পাবে।",
        AppLanguage.ENGLISH to "Adjust rates for any service (Haircut, Beard, Facial). Customers will instantly see updated prices.",
        AppLanguage.HINDI to "किसी भी सेवा की दरें बदलें। ग्राहक तुरंत अद्यतन मूल्य देखेंगे।"
    )
    val editRateBtn = mapOf(
        AppLanguage.BENGALI to "রেট বদলান",
        AppLanguage.ENGLISH to "Edit Rate",
        AppLanguage.HINDI to "दर बदलें"
    )
    val newPriceLabel = mapOf(
        AppLanguage.BENGALI to "নতুন মূল্য (₹)",
        AppLanguage.ENGLISH to "New Price (₹)",
        AppLanguage.HINDI to "नया मूल्य (₹)"
    )

    // Features in Review Tab
    val featurePreorder = mapOf(
        AppLanguage.BENGALI to "✓ প্রি-অর্ডার সুবিধা",
        AppLanguage.ENGLISH to "✓ Pre-order Convenience",
        AppLanguage.HINDI to "✓ प्री-ऑर्डर सुविधा"
    )
    val featureAdvance = mapOf(
        AppLanguage.BENGALI to "✓ ৫৫% সহজ UPI অগ্রিম পেমেন্ট",
        AppLanguage.ENGLISH to "✓ Seamless 55% Advance UPI Payment",
        AppLanguage.HINDI to "✓ आसान 55% अग्रिम UPI भुगतान"
    )
    val featureStaff = mapOf(
        AppLanguage.BENGALI to "✓ দক্ষ ও যত্নশীল হেয়ার ড্রেসার",
        AppLanguage.ENGLISH to "✓ Skilled & Professional Hairstylists",
        AppLanguage.HINDI to "✓ कुशल और अनुभवी हेयर ड्रेसर"
    )
    val featureAmbiance = mapOf(
        AppLanguage.BENGALI to "✓ পরিচ্ছন্ন ও এসি এনভায়রনমেন্ট",
        AppLanguage.ENGLISH to "✓ Clean & AC Hygienic Environment",
        AppLanguage.HINDI to "✓ स्वच्छ और एसी वातावरण"
    )
    val serviceLabel = mapOf(
        AppLanguage.BENGALI to "সেবা:",
        AppLanguage.ENGLISH to "Service:",
        AppLanguage.HINDI to "सेवा:"
    )
    val customerReviewsCount = mapOf(
        AppLanguage.BENGALI to "টি কাস্টমার রিভিউ",
        AppLanguage.ENGLISH to "Customer Reviews",
        AppLanguage.HINDI to "ग्राहक समीक्षाएं"
    )
    val bookingNumber = mapOf(
        AppLanguage.BENGALI to "বুকিং #",
        AppLanguage.ENGLISH to "Booking #",
        AppLanguage.HINDI to "बुकिंग #"
    )
    val personsUnit = mapOf(
        AppLanguage.BENGALI to "জন",
        AppLanguage.ENGLISH to "clients",
        AppLanguage.HINDI to "ग्राहक"
    )

    // Customer Profile Section
    val customerProfileTitle = mapOf(
        AppLanguage.BENGALI to "গ্রাহক প্রোফাইল",
        AppLanguage.ENGLISH to "Customer Profile",
        AppLanguage.HINDI to "ग्राहक प्रोफ़ाइल"
    )
    val profile = mapOf(
        AppLanguage.BENGALI to "প্রোফাইল",
        AppLanguage.ENGLISH to "Profile",
        AppLanguage.HINDI to "प्रोफ़ाइल"
    )
    val guestWelcome = mapOf(
        AppLanguage.BENGALI to "স্বাগতম, সম্মানিত গ্রাহক!",
        AppLanguage.ENGLISH to "Welcome, Valued Client!",
        AppLanguage.HINDI to "स्वागत है, सम्मानित ग्राहक!"
    )
    val guestSubtitle = mapOf(
        AppLanguage.BENGALI to "আপনার বুকিং ট্র্যাক করতে ও প্রিমিয়াম সেলুন সেবা পেতে সাইন ইন করুন।",
        AppLanguage.ENGLISH to "Sign in to track appointments, manage pre-orders, and enjoy VIP perks.",
        AppLanguage.HINDI to "अपॉइंटमेंट ट्रैक करने, प्री-ऑर्डर प्रबंधित करने और वीआईपी लाभ पाने के लिए साइन इन करें।"
    )
    val editName = mapOf(
        AppLanguage.BENGALI to "নাম পরিবর্তন",
        AppLanguage.ENGLISH to "Edit Name",
        AppLanguage.HINDI to "नाम बदलें"
    )
    val saveChanges = mapOf(
        AppLanguage.BENGALI to "সংরক্ষণ করুন",
        AppLanguage.ENGLISH to "Save Changes",
        AppLanguage.HINDI to "परिवर्तन सहेजें"
    )
    val memberTier = mapOf(
        AppLanguage.BENGALI to "👑 রয়্যাল ভিআইপি সদস্য",
        AppLanguage.ENGLISH to "👑 Royal VIP Member",
        AppLanguage.HINDI to "👑 रॉयल वीआईपी सदस्य"
    )
    val totalBookingsLabel = mapOf(
        AppLanguage.BENGALI to "মোট বুকিং",
        AppLanguage.ENGLISH to "Total Bookings",
        AppLanguage.HINDI to "कुल बुकिंग"
    )
    val upcomingAppointment = mapOf(
        AppLanguage.BENGALI to "আসন্ন অ্যাপয়েন্টমেন্ট",
        AppLanguage.ENGLISH to "Upcoming Appointment",
        AppLanguage.HINDI to "आगामी अपॉइंटमेंट"
    )
    val noUpcomingBookings = mapOf(
        AppLanguage.BENGALI to "বর্তমানে কোনো আসন্ন বুকিং নেই",
        AppLanguage.ENGLISH to "No upcoming appointments scheduled",
        AppLanguage.HINDI to "वर्तमान में कोई आगामी अपॉइंटमेंट नहीं है"
    )
    val salonSupport = mapOf(
        AppLanguage.BENGALI to "সেলুন হেল্পডেস্ক ও লোকেশন",
        AppLanguage.ENGLISH to "Salon Helpdesk & Location",
        AppLanguage.HINDI to "सैलून हेल्पडेस्क और स्थान"
    )
    val callSalon = mapOf(
        AppLanguage.BENGALI to "কল করুন",
        AppLanguage.ENGLISH to "Call Salon",
        AppLanguage.HINDI to "कॉल करें"
    )
    val switchStaffPortal = mapOf(
        AppLanguage.BENGALI to "সেলুন স্টাফ / বার্বার মোড",
        AppLanguage.ENGLISH to "Salon Staff / Partner Mode",
        AppLanguage.HINDI to "सैलून स्टाफ / पार्टनर मोड"
    )
    val salonAddressText = mapOf(
        AppLanguage.BENGALI to "৪২ এমজি রোড, ব্রিগেড জংশন, বেঙ্গালুরু • সকাল ৯টা - রাত ৯টা",
        AppLanguage.ENGLISH to "42 MG Road, Brigade Junction, Bengaluru • 09:00 AM - 09:00 PM",
        AppLanguage.HINDI to "42 एमजी रोड, ब्रिगेड जंक्शन, बेंगलुरु • सुबह 09:00 - रात 09:00"
    )
    val viewAllBookings = mapOf(
        AppLanguage.BENGALI to "সব বুকিং দেখুন",
        AppLanguage.ENGLISH to "View All Bookings",
        AppLanguage.HINDI to "सभी बुकिंग देखें"
    )

    // Dedicated Salon Owner Login Screen
    val salonOwnerLoginTitle = mapOf(
        AppLanguage.BENGALI to "সেলুন ওনার লগইন পোর্টাল",
        AppLanguage.ENGLISH to "Salon Owner Portal",
        AppLanguage.HINDI to "सैलून मालिक पोर्टल"
    )
    val salonOwnerLoginSubtitle = mapOf(
        AppLanguage.BENGALI to "সেলুন আইডি, রেজিস্টার্ড মোবাইল নম্বর ও পাসওয়ার্ড দিয়ে নিরাপদে ড্যাশবোর্ডে প্রবেশ করুন।",
        AppLanguage.ENGLISH to "Secure merchant access using official Salon ID, registered phone number & password.",
        AppLanguage.HINDI to "आधिकारिक सैलून आईडी, पंजीकृत फोन नंबर और पासवर्ड के साथ सुरक्षित रूप से प्रवेश करें।"
    )
    val salonIdRequirementHint = mapOf(
        AppLanguage.BENGALI to "প্রতিটি অনুমোদিত সেলুনের জন্য তৈরিদাতা কর্তৃক ৩-স্তরের প্রমাণীকরণ আবশ্যক।",
        AppLanguage.ENGLISH to "3-Factor secure authentication required for authorized salon partners.",
        AppLanguage.HINDI to "अधिकृत सैलून भागीदारों के लिए 3-स्तरीय सुरक्षा प्रमाणीकरण आवश्यक है।"
    )
    val quickDemoCredentialsLabel = mapOf(
        AppLanguage.BENGALI to "⚡ ডেমো একাউন্ট (১-ট্যাপ ফিল):",
        AppLanguage.ENGLISH to "⚡ Quick Demo Logins (1-Tap Fill):",
        AppLanguage.HINDI to "⚡ त्वरित डेमो लॉगिन (1-टैप):"
    )
    val backToCustomerPortal = mapOf(
        AppLanguage.BENGALI to "কাস্টমার অ্যাপয়েন্টমেন্টে ফিরে যান",
        AppLanguage.ENGLISH to "Back to Customer Booking",
        AppLanguage.HINDI to "ग्राहक बुकिंग पर वापस जाएं"
    )
    val authenticatingOwner = mapOf(
        AppLanguage.BENGALI to "তথ্য যাচাই করা হচ্ছে...",
        AppLanguage.ENGLISH to "Verifying Salon Credentials...",
        AppLanguage.HINDI to "क्रेडेंशियल सत्यापित हो रहे हैं..."
    )
    val ownerPhonePlaceholder = mapOf(
        AppLanguage.BENGALI to "যেমন: 9876543210",
        AppLanguage.ENGLISH to "e.g. 9876543210",
        AppLanguage.HINDI to "जैसे: 9876543210"
    )
    val salonIdHelperText = mapOf(
        AppLanguage.BENGALI to "অ্যাপ তৈরিদাতা কর্তৃক প্রদত্ত অনন্য সেলুন লাইসেন্স কোড (যেমন: SALON-101)",
        AppLanguage.ENGLISH to "Unique Salon ID issued by App Creator (e.g. SALON-101)",
        AppLanguage.HINDI to "ऐप निर्माता द्वारा जारी अद्वितीय सैलून आईडी (उदा: SALON-101)"
    )

    // Separate Customer Login Panel strings
    val customerLoginPanelTitle = mapOf(
        AppLanguage.BENGALI to "কাস্টমার লগইন প্যানেল",
        AppLanguage.ENGLISH to "Customer Login Panel",
        AppLanguage.HINDI to "ग्राहक लॉगिन पैनल"
    )
    val customerLoginPanelSubtitle = mapOf(
        AppLanguage.BENGALI to "অ্যাপয়েন্টমেন্ট বুকিং, প্রি-অর্ডার ও ভিআইপি রিওয়ার্ড দেখতে মোবাইল নম্বর দিয়ে সাইন ইন করুন।",
        AppLanguage.ENGLISH to "Sign in with your phone & password to book appointments and track loyalty rewards.",
        AppLanguage.HINDI to "अपॉइंटमेंट बुक करने और लॉयल्टी रिवार्ड्स देखने के लिए फोन व पासवर्ड से लॉगिन करें।"
    )
    val switchToSalonOwnerLogin = mapOf(
        AppLanguage.BENGALI to "💈 সেলুন ওনার লগইন প্যানেলে যান →",
        AppLanguage.ENGLISH to "💈 Salon Owner? Switch to Salon Owner Login →",
        AppLanguage.HINDI to "💈 सैलून मालिक? सैलून मालिक लॉगिन पर जाएं →"
    )
    val quickDemoCustomersLabel = mapOf(
        AppLanguage.BENGALI to "⚡ ডেমো কাস্টমার একাউন্ট (১-ট্যাপ ফিল):",
        AppLanguage.ENGLISH to "⚡ Quick Customer Demo (1-Tap Fill):",
        AppLanguage.HINDI to "⚡ त्वरित ग्राहक डेमो (1-टैप):"
    )

    // Salon Owner Profile Section strings
    val salonOwnerProfileTitle = mapOf(
        AppLanguage.BENGALI to "সেলুন মালিক প্রোফাইল",
        AppLanguage.ENGLISH to "Salon Owner Profile",
        AppLanguage.HINDI to "सैलून मालिक प्रोफ़ाइल"
    )
    val salonOwnerProfileSubtitle = mapOf(
        AppLanguage.BENGALI to "অফিসিয়াল পার্টনার ও সেলুন লাইসেন্স বিবরণ",
        AppLanguage.ENGLISH to "Official Merchant Credentials & License Status",
        AppLanguage.HINDI to "आधिकारिक व्यापारी क्रेडेंशियल व लाइसेंस स्थिति"
    )
    val salonLicenseDetails = mapOf(
        AppLanguage.BENGALI to "লাইসেন্স ও সেলুন বিবরণ",
        AppLanguage.ENGLISH to "License & Store Information",
        AppLanguage.HINDI to "लाइसेंस व स्टोर जानकारी"
    )
    val registeredPhoneLabel = mapOf(
        AppLanguage.BENGALI to "রেজিস্টার্ড মোবাইল",
        AppLanguage.ENGLISH to "Registered Business Phone",
        AppLanguage.HINDI to "पंजीकृत व्यवसाय फोन"
    )
    val salonAddressLabel = mapOf(
        AppLanguage.BENGALI to "সেলুনের ঠিকানা",
        AppLanguage.ENGLISH to "Salon Location Address",
        AppLanguage.HINDI to "सैलून का पता"
    )
    val performanceSummary = mapOf(
        AppLanguage.BENGALI to "সেলুন পারফরম্যান্স সারাংশ",
        AppLanguage.ENGLISH to "Salon Activity Summary",
        AppLanguage.HINDI to "सैलून गतिविधि सारांश"
    )
    val logoutSalonOwnerBtn = mapOf(
        AppLanguage.BENGALI to "সেলুন ওনার অ্যাকাউন্ট লগআউট",
        AppLanguage.ENGLISH to "Logout Salon Owner Account",
        AppLanguage.HINDI to "सैलून मालिक खाता लॉग आउट"
    )
    val languageLabel = mapOf(
        AppLanguage.BENGALI to "ভাষা নির্বাচন করুন",
        AppLanguage.ENGLISH to "Select Language",
        AppLanguage.HINDI to "भाषा चुनें"
    )
    val editOwnerProfileBtn = mapOf(
        AppLanguage.BENGALI to "প্রোফাইল বিবরণ এডিট করুন",
        AppLanguage.ENGLISH to "Edit Profile Details",
        AppLanguage.HINDI to "प्रोफ़ाइल विवरण संपादित करें"
    )
    val editOwnerProfileTitle = mapOf(
        AppLanguage.BENGALI to "সেলুন মালিকের বিবরণ আপডেট করুন",
        AppLanguage.ENGLISH to "Update Salon Owner Details",
        AppLanguage.HINDI to "सैलून मालिक विवरण अपडेट करें"
    )
    val saveProfileChangesBtn = mapOf(
        AppLanguage.BENGALI to "সংরক্ষণ করুন",
        AppLanguage.ENGLISH to "Save Changes",
        AppLanguage.HINDI to "परिवर्तन सहेजें"
    )
    val operatingHoursLabel = mapOf(
        AppLanguage.BENGALI to "কাজের সময়",
        AppLanguage.ENGLISH to "Operating Hours",
        AppLanguage.HINDI to "कार्य समय"
    )
    val operatingHoursValue = mapOf(
        AppLanguage.BENGALI to "সকাল ১০:০০ - রাত ০৮:০০ (প্রতিদিন)",
        AppLanguage.ENGLISH to "10:00 AM - 08:00 PM (Everyday)",
        AppLanguage.HINDI to "सुबह 10:00 - रात 08:00 (प्रतिदिन)"
    )
    val quickLoginAsOwnerBtn = mapOf(
        AppLanguage.BENGALI to "মালিক হিসেবে সরাসরি লগইন করুন",
        AppLanguage.ENGLISH to "Quick Log In as Owner",
        AppLanguage.HINDI to "मालिक के रूप में सीधा लॉगिन करें"
    )
    val merchantStatusActive = mapOf(
        AppLanguage.BENGALI to "অ্যাক্টিভ ও ভেরিফাইড পার্টনার",
        AppLanguage.ENGLISH to "Active & Verified Merchant",
        AppLanguage.HINDI to "सक्रिय और सत्यापित व्यापारी"
    )

    // Centralized LoginSelector Screen Strings
    val loginSelectorTitle = mapOf(
        AppLanguage.BENGALI to "স্টাইল মাস্টার সেলুন সিস্টেম",
        AppLanguage.ENGLISH to "Style Master Salon System",
        AppLanguage.HINDI to "स्टाइल मास्टर सैलून सिस्टम"
    )
    val loginSelectorSubtitle = mapOf(
        AppLanguage.BENGALI to "প্রবেশ করতে আপনার লগইন প্যানেল নির্বাচন করুন",
        AppLanguage.ENGLISH to "Select your login portal to proceed",
        AppLanguage.HINDI to "आगे बढ़ने के लिए अपना लॉगिन पोर्टल चुनें"
    )
    val chooseLoginPortal = mapOf(
        AppLanguage.BENGALI to "তিনটি স্বতন্ত্র লগইন অপশন",
        AppLanguage.ENGLISH to "3 Distinct Login Options",
        AppLanguage.HINDI to "3 अलग-अलग लॉगिन विकल्प"
    )
    val customerPortalOptionTitle = mapOf(
        AppLanguage.BENGALI to "১. কাস্টমার প্যানেল লগইন",
        AppLanguage.ENGLISH to "1. Customer Panel Login",
        AppLanguage.HINDI to "1. ग्राहक पोर्टल लॉगिन"
    )
    val customerPortalOptionDesc = mapOf(
        AppLanguage.BENGALI to "গ্রাহকরা ফোন ও পাসওয়ার্ড দিয়ে প্রবেশ করে চুল কাটা ও গ্রুমিং প্রি-অর্ডার বুকিং করবেন",
        AppLanguage.ENGLISH to "Customers log in with phone & password to book hair cuts, styling & pay advance deposit",
        AppLanguage.HINDI to "ग्राहक फ़ोन और पासवर्ड से लॉगिन करके बाल कटाई, ग्रूमिंग और अग्रिम बुकिंग करेंगे"
    )
    val salonOwnerPortalOptionTitle = mapOf(
        AppLanguage.BENGALI to "২. সেলুন ওনার লগইন",
        AppLanguage.ENGLISH to "2. Salon Owner Login",
        AppLanguage.HINDI to "2. सैलून मालिक लॉगिन"
    )
    val salonOwnerPortalOptionDesc = mapOf(
        AppLanguage.BENGALI to "সেলুন মালিকরা তাদের সেলুন আইডি, ফোন নম্বর ও পাসওয়ার্ড দিয়ে নিজস্ব সেলুন ম্যানেজ করবেন",
        AppLanguage.ENGLISH to "Salon owners log in with Salon ID, phone & password to manage chairs, queue & UPI earnings",
        AppLanguage.HINDI to "सैलून मालिक अपने सैलून आईडी, फ़ोन और पासवर्ड से अपनी कुर्सियां, कतार और खाता प्रबंधित करेंगे"
    )
    val masterPortalOptionTitle = mapOf(
        AppLanguage.BENGALI to "৩. মাস্টার লগইন (অ্যাপ ক্রিয়েটর)",
        AppLanguage.ENGLISH to "3. Master / Creator Login",
        AppLanguage.HINDI to "3. मास्टर / क्रिएटर लॉगिन"
    )
    val masterPortalOptionDesc = mapOf(
        AppLanguage.BENGALI to "অ্যাপ ক্রিয়েটরদের জন্য নিজস্ব পার্সোনাল অ্যাডমিন আইডি ও পাসওয়ার্ড দিয়ে লাইসেন্স ইস্যু ও নিয়ন্ত্রণ",
        AppLanguage.ENGLISH to "App creators log in with master personal ID & password to issue Salon IDs and control licenses",
        AppLanguage.HINDI to "ऐप क्रिएटर अपने व्यक्तिगत आईडी और पासवर्ड से नए सैलून आईडी जारी और नियंत्रित करेंगे"
    )
    val masterAdminIdLabel = mapOf(
        AppLanguage.BENGALI to "মাস্টার ক্রিয়েটর আইডি",
        AppLanguage.ENGLISH to "Master Creator ID",
        AppLanguage.HINDI to "मास्टर क्रिएटर आईडी"
    )
    val masterPasswordLabel = mapOf(
        AppLanguage.BENGALI to "মাস্টার সিক্রেট পাসওয়ার্ড",
        AppLanguage.ENGLISH to "Master Secret Password",
        AppLanguage.HINDI to "मास्टर सीक्रेट पासवर्ड"
    )
    val masterLoginBtn = mapOf(
        AppLanguage.BENGALI to "মাস্টার হাব প্রবেশ করুন",
        AppLanguage.ENGLISH to "Enter Master Admin Hub",
        AppLanguage.HINDI to "मास्टर हब में प्रवेश करें"
    )
    val backToPortalSelection = mapOf(
        AppLanguage.BENGALI to "← লগইন প্যানেল পরিবর্তনে ফিরে যান",
        AppLanguage.ENGLISH to "← Back to Portal Selection",
        AppLanguage.HINDI to "← पोर्टल चयन पर वापस जाएँ"
    )
    val quickDemoFill = mapOf(
        AppLanguage.BENGALI to "⚡ ডেমো অটো-ফিল",
        AppLanguage.ENGLISH to "⚡ Quick Demo Fill",
        AppLanguage.HINDI to "⚡ त्वरित डेमो भरें"
    )
    val customerPortalLabel = mapOf(
        AppLanguage.BENGALI to "কাস্টমার প্যানেল",
        AppLanguage.ENGLISH to "Customer Panel",
        AppLanguage.HINDI to "ग्राहक पोर्टल"
    )
    val salonOwnerPortalLabel = mapOf(
        AppLanguage.BENGALI to "সেলুন ওনার প্যানেল",
        AppLanguage.ENGLISH to "Salon Owner Panel",
        AppLanguage.HINDI to "सैलून मालिक पोर्टल"
    )
    val customerName = mapOf(
        AppLanguage.BENGALI to "আপনার পুরো নাম",
        AppLanguage.ENGLISH to "Full Name",
        AppLanguage.HINDI to "पूरा नाम"
    )
    val masterPortalLabel = mapOf(
        AppLanguage.BENGALI to "মাস্টার ক্রিয়েটর হাব",
        AppLanguage.ENGLISH to "Master Creator Hub",
        AppLanguage.HINDI to "मास्टर क्रिएटर हब"
    )
    val exitToPortalSelection = mapOf(
        AppLanguage.BENGALI to "লগআউট ও পোর্টাল পরিবর্তন",
        AppLanguage.ENGLISH to "Logout & Switch Portal",
        AppLanguage.HINDI to "लॉग आउट और पोर्टल बदलें"
    )
}

fun Map<AppLanguage, String>.tr(lang: AppLanguage): String = this[lang] ?: this[AppLanguage.ENGLISH] ?: ""

