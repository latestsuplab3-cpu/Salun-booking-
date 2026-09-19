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
            cleaned.startsWith("0") -> "+91" + cleaned.removePrefix("0")
            cleaned.length == 10 -> "+91$cleaned" // India default (10 digits)
            else -> "+91$cleaned"
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
                val rawMsg = e.localizedMessage ?: ""
                val errorCode = when {
                    rawMsg.contains("17006", ignoreCase = true) || rawMsg.contains("blocked", ignoreCase = true) || rawMsg.contains("SMS unable to be sent until this region enabled", ignoreCase = true) ->
                        "ERROR_SMS_REGION_17006"
                    rawMsg.contains("INVALID_CERT_HASH", ignoreCase = true) || rawMsg.contains("certificate hash", ignoreCase = true) || rawMsg.contains("siteKey", ignoreCase = true) ->
                        "ERROR_INVALID_CERT_HASH"
                    e is FirebaseAuthInvalidCredentialsException -> 
                        "ERROR_INVALID_CREDENTIALS"
                    e is FirebaseTooManyRequestsException -> 
                        "ERROR_TOO_MANY_REQUESTS"
                    else -> rawMsg.ifBlank { "ERROR_UNKNOWN" }
                }
                onError(errorCode)
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
                onError(e.localizedMessage ?: "ERROR_RESEND_FAILED")
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
                            is FirebaseAuthInvalidCredentialsException -> "ERROR_INVALID_OTP"
                            else -> ex?.localizedMessage ?: "ERROR_VERIFY_FAILED"
                        }
                        onError(msg)
                    }
                }
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "ERROR_VERIFY_FAILED")
        }
    }
}
