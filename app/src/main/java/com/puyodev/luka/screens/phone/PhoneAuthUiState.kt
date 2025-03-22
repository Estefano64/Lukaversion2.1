package com.puyodev.luka.screens.phone

data class PhoneAuthUiState(
    val phoneNumber: String = "",
    val verificationCode: String = "",
    val verificationId: String = "",
    val isCodeSent: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
) 