package com.example.data.auth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

object FirebaseAuthManager {
    val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    fun normalizePhoneNumber(input: String): String {
        val cleaned = input.trim().replace(" ", "").replace("-", "")
        return when {
            cleaned.startsWith("+") -> cleaned
            cleaned.length == 10 -> "+91$cleaned" // India default
            cleaned.startsWith("01") && cleaned.length == 11 -> "+88$cleaned" // Bangladesh default
            else -> "+$cleaned"
        }
    }

    fun sendOtp(
        activity: Activity,
        phoneNumber: String,
        onCodeSent: (verificationId: String, resendToken: PhoneAuthProvider.ForceResendingToken) -> Unit,
        onVerificationCompleted: (credential: PhoneAuthCredential) -> Unit,
        onError: (String) -> Unit
    ) {
        val formattedNumber = normalizePhoneNumber(phoneNumber)

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                onVerificationCompleted(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                val message = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "ভুল ফোন নম্বর ফরম্যাট! (Invalid phone number format)"
                    is FirebaseTooManyRequestsException -> "অনেক বেশি অনুরোধ করা হয়েছে। কিছুক্ষণ পর চেষ্টা করুন।"
                    else -> e.localizedMessage ?: "OTP পাঠানোর সময় ত্রুটি হয়েছে।"
                }
                onError(message)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                onCodeSent(verificationId, token)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(formattedNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun resendOtp(
        activity: Activity,
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken,
        onCodeSent: (verificationId: String, resendToken: PhoneAuthProvider.ForceResendingToken) -> Unit,
        onError: (String) -> Unit
    ) {
        val formattedNumber = normalizePhoneNumber(phoneNumber)

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {
                onError(e.localizedMessage ?: "OTP পুনরায় পাঠানোর সময় সমস্যা হয়েছে।")
            }

            override fun onCodeSent(
                verificationId: String,
                newToken: PhoneAuthProvider.ForceResendingToken
            ) {
                onCodeSent(verificationId, newToken)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(formattedNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .setForceResendingToken(token)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(
        verificationId: String,
        smsCode: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, smsCode.trim())
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess(auth.currentUser)
                    } else {
                        val ex = task.exception
                        val msg = when (ex) {
                            is FirebaseAuthInvalidCredentialsException -> "ভুল ওটিপি কোড! অনুগ্রহ করে সঠিক কোড দিন।"
                            else -> ex?.localizedMessage ?: "OTP যাচাইকরণ ব্যর্থ হয়েছে।"
                        }
                        onError(msg)
                    }
                }
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "OTP যাচাই করতে সমস্যা হয়েছে।")
        }
    }
}
