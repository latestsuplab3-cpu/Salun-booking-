package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Salon Booking", appName)
  }

  @Test
  fun `verify customer profile localization strings`() {
    assertEquals("Customer Profile", com.example.ui.localization.StringRes.customerProfileTitle[com.example.ui.localization.AppLanguage.ENGLISH])
    assertEquals("গ্রাহক প্রোফাইল", com.example.ui.localization.StringRes.customerProfileTitle[com.example.ui.localization.AppLanguage.BENGALI])
    assertEquals("ग्राहक प्रोफ़ाइल", com.example.ui.localization.StringRes.customerProfileTitle[com.example.ui.localization.AppLanguage.HINDI])
  }

  @Test
  fun `verify salon owner portal localization strings`() {
    assertEquals("Salon Owner Portal", com.example.ui.localization.StringRes.salonOwnerLoginTitle[com.example.ui.localization.AppLanguage.ENGLISH])
    assertEquals("সেলুন ওনার লগইন পোর্টাল", com.example.ui.localization.StringRes.salonOwnerLoginTitle[com.example.ui.localization.AppLanguage.BENGALI])
    assertEquals("सैलून मालिक पोर्टल", com.example.ui.localization.StringRes.salonOwnerLoginTitle[com.example.ui.localization.AppLanguage.HINDI])
  }

  @Test
  fun `verify customer login panel localization strings`() {
    assertEquals("Customer Login Panel", com.example.ui.localization.StringRes.customerLoginPanelTitle[com.example.ui.localization.AppLanguage.ENGLISH])
    assertEquals("কাস্টমার লগইন প্যানেল", com.example.ui.localization.StringRes.customerLoginPanelTitle[com.example.ui.localization.AppLanguage.BENGALI])
    assertEquals("ग्राहक लॉगिन पैनल", com.example.ui.localization.StringRes.customerLoginPanelTitle[com.example.ui.localization.AppLanguage.HINDI])
  }

  @Test
  fun `verify salon owner profile section localization strings`() {
    assertEquals("Salon Owner Profile", com.example.ui.localization.StringRes.salonOwnerProfileTitle[com.example.ui.localization.AppLanguage.ENGLISH])
    assertEquals("সেলুন মালিক প্রোফাইল", com.example.ui.localization.StringRes.salonOwnerProfileTitle[com.example.ui.localization.AppLanguage.BENGALI])
    assertEquals("सैलून मालिक प्रोफ़ाइल", com.example.ui.localization.StringRes.salonOwnerProfileTitle[com.example.ui.localization.AppLanguage.HINDI])
    assertEquals("Edit Profile Details", com.example.ui.localization.StringRes.editOwnerProfileBtn[com.example.ui.localization.AppLanguage.ENGLISH])
    assertEquals("প্রোফাইল বিবরণ এডিট করুন", com.example.ui.localization.StringRes.editOwnerProfileBtn[com.example.ui.localization.AppLanguage.BENGALI])
    assertEquals("प्रोफ़ाइल विवरण संपादित करें", com.example.ui.localization.StringRes.editOwnerProfileBtn[com.example.ui.localization.AppLanguage.HINDI])
  }

  @Test
  fun `verify login selector 3 options localization strings`() {
    assertEquals("1. Customer Panel Login", com.example.ui.localization.StringRes.customerPortalOptionTitle[com.example.ui.localization.AppLanguage.ENGLISH])
    assertEquals("১. কাস্টমার প্যানেল লগইন", com.example.ui.localization.StringRes.customerPortalOptionTitle[com.example.ui.localization.AppLanguage.BENGALI])
    assertEquals("1. ग्राहक पोर्टल लॉगिन", com.example.ui.localization.StringRes.customerPortalOptionTitle[com.example.ui.localization.AppLanguage.HINDI])

    assertEquals("2. Salon Owner Login", com.example.ui.localization.StringRes.salonOwnerPortalOptionTitle[com.example.ui.localization.AppLanguage.ENGLISH])
    assertEquals("২. সেলুন ওনার লগইন", com.example.ui.localization.StringRes.salonOwnerPortalOptionTitle[com.example.ui.localization.AppLanguage.BENGALI])
    assertEquals("2. सैलून मालिक लॉगिन", com.example.ui.localization.StringRes.salonOwnerPortalOptionTitle[com.example.ui.localization.AppLanguage.HINDI])

    assertEquals("3. Master / Creator Login", com.example.ui.localization.StringRes.masterPortalOptionTitle[com.example.ui.localization.AppLanguage.ENGLISH])
    assertEquals("৩. মাস্টার লগইন (অ্যাপ ক্রিয়েটর)", com.example.ui.localization.StringRes.masterPortalOptionTitle[com.example.ui.localization.AppLanguage.BENGALI])
    assertEquals("3. मास्टर / क्रिएटर लॉगिन", com.example.ui.localization.StringRes.masterPortalOptionTitle[com.example.ui.localization.AppLanguage.HINDI])
  }

  @Test
  fun `verify Indian Rupee currency across all language modes`() {
    assertEquals("₹", com.example.ui.localization.StringRes.currency[com.example.ui.localization.AppLanguage.ENGLISH])
    assertEquals("₹", com.example.ui.localization.StringRes.currency[com.example.ui.localization.AppLanguage.BENGALI])
    assertEquals("₹", com.example.ui.localization.StringRes.currency[com.example.ui.localization.AppLanguage.HINDI])
  }
}
