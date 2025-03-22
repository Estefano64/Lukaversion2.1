package com.puyodev.luka.screens.phone

import android.app.Activity
import androidx.compose.runtime.mutableStateOf
import com.puyodev.luka.LOGIN_SCREEN
import com.puyodev.luka.PAY_SCREEN
import com.puyodev.luka.R.string as AppText
import com.puyodev.luka.common.snackbar.SnackbarManager
import com.puyodev.luka.model.service.AccountService
import com.puyodev.luka.model.service.LogService
import com.puyodev.luka.screens.LukaViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : LukaViewModel(logService) {
    
    var uiState = mutableStateOf(PhoneAuthUiState())
        private set
    
    private val phoneNumber
        get() = uiState.value.phoneNumber
    
    private val verificationCode
        get() = uiState.value.verificationCode
    
    private val verificationId
        get() = uiState.value.verificationId
    
    fun onPhoneNumberChange(newValue: String) {
        uiState.value = uiState.value.copy(phoneNumber = newValue)
    }
    
    fun onVerificationCodeChange(newValue: String) {
        uiState.value = uiState.value.copy(verificationCode = newValue)
    }
    
    fun onSendVerificationCodeClick(activity: Activity) {
        if (phoneNumber.isEmpty()) {
            SnackbarManager.showMessage(AppText.generic_error)
            return
        }
        
        // Formato internacional del número de teléfono (+51123456789)
        val formattedPhoneNumber = formatPhoneNumber(phoneNumber)
        
        uiState.value = uiState.value.copy(isLoading = true, errorMessage = "")
        
        launchCatching {
            accountService.sendPhoneVerification(
                formattedPhoneNumber,
                activity
            ) { verificationId, exception ->
                if (exception != null) {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al enviar el código"
                    )
                } else if (verificationId != null) {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isCodeSent = true,
                        verificationId = verificationId
                    )
                } else {
                    // La verificación se completó automáticamente
                    uiState.value = uiState.value.copy(isLoading = false)
                }
            }
        }
    }
    
    fun onVerifyCodeClick(openAndPopUp: (String, String) -> Unit) {
        if (verificationCode.isEmpty() || verificationId.isEmpty()) {
            SnackbarManager.showMessage(AppText.generic_error)
            return
        }
        
        uiState.value = uiState.value.copy(isLoading = true, errorMessage = "")
        
        launchCatching {
            val success = accountService.verifyPhoneCode(verificationId, verificationCode)
            
            if (success) {
                openAndPopUp(PAY_SCREEN, LOGIN_SCREEN)
            } else {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al verificar el código"
                )
            }
        }
    }
    
    private fun formatPhoneNumber(phoneNumber: String): String {
        // Asegurarse de que el número tenga el formato internacional
        return if (phoneNumber.startsWith("+")) {
            phoneNumber
        } else {
            "+51$phoneNumber" // Agregar código de país (Perú)
        }
    }
} 